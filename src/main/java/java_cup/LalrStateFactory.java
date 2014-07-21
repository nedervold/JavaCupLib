package java_cup;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

public class LalrStateFactory {

	/** Collection of all states. */
	protected static Hashtable<lalr_item_set, lalr_state> _all = new Hashtable<lalr_item_set, lalr_state>();

	/** Collection of all states. */
	public static Enumeration<lalr_state> all() {
		return _all.elements();
	}

	public static void clear() {
		_all.clear();
		_all_kernels.clear();
		next_index = 0;
	}

	/** Indicate total number of states there are. */
	public static int number() {
		return _all.size();
	}

	/**
	 * Hash table to find states by their kernels (i.e, the original, unclosed,
	 * set of items -- which uniquely define the state). This table stores state
	 * objects using (a copy of) their kernel item sets as keys.
	 */
	protected static Hashtable<lalr_item_set, lalr_state> _all_kernels = new Hashtable<lalr_item_set, lalr_state>();

	/**
	 * Find and return state with a given a kernel item set (or null if not
	 * found). The kernel item set is the subset of items that were used to
	 * originally create the state. These items are formed by "shifting the dot"
	 * within items of other states that have a transition to this one. The
	 * remaining elements of this state's item set are added during closure.
	 * 
	 * @param itms
	 *            the kernel set of the state we are looking for.
	 */
	public static lalr_state find_state(lalr_item_set itms) {
		if (itms == null)
			return null;
		else
			return (lalr_state) _all.get(itms);
	}

	/** Static counter for assigning unique state indexes. */
	protected static int next_index = 0;

	/**
	 * Helper routine for debugging -- produces a dump of the given state onto
	 * System.out.
	 */
	protected static void dump_state(lalr_state st) throws internal_error {
		lalr_item_set itms;
		lalr_item itm;
		production_part part;

		if (st == null) {
			System.out.println("NULL lalr_state");
			return;
		}

		System.out.println("lalr_state [" + st.index() + "] {");
		itms = st.items();
		for (Enumeration<lalr_item> e = itms.all(); e.hasMoreElements();) {
			itm =  e.nextElement();
			System.out.print("  [");
			System.out.print(itm.the_production().lhs().the_symbol().name());
			System.out.print(" ::= ");
			for (int i = 0; i < itm.the_production().rhs_length(); i++) {
				if (i == itm.dot_pos())
					System.out.print("(*) ");
				part = itm.the_production().rhs(i);
				if (part.is_action())
					System.out.print("{action} ");
				else
					System.out.print(((symbol_part) part).the_symbol().name()
							+ " ");
			}
			if (itm.dot_at_end())
				System.out.print("(*) ");
			System.out.println("]");
		}
		System.out.println("}");
	}

	/**
	 * Propagate lookahead sets through the constructed viable prefix
	 * recognizer. When the machine is constructed, each item that results in
	 * the creation of another such that its lookahead is included in the
	 * other's will have a propagate link set up for it. This allows additions
	 * to the lookahead of one item to be included in other items that it was
	 * used to directly or indirectly create.
	 */
	protected static void propagate_all_lookaheads() throws internal_error {
		/* iterate across all states */
		for (Enumeration st = all(); st.hasMoreElements();) {
			/* propagate lookaheads out of that state */
			((lalr_state) st.nextElement()).propagate_lookaheads();
		}
	}

	/**
	 * Build an LALR viable prefix recognition machine given a start production.
	 * This method operates by first building a start state from the start
	 * production (based on a single item with the dot at the beginning and EOF
	 * as expected lookahead). Then for each state it attempts to extend the
	 * machine by creating transitions out of the state to new or existing
	 * states. When considering extension from a state we make a transition on
	 * each symbol that appears before the dot in some item. For example, if we
	 * have the items:
	 * 
	 * <pre>
	 *    [A ::= a b * X c, {d,e}]
	 *    [B ::= a b * X d, {a,b}]
	 * </pre>
	 * 
	 * in some state, then we would be making a transition under X to a new
	 * state. This new state would be formed by a "kernel" of items
	 * corresponding to moving the dot past the X. In this case:
	 * 
	 * <pre>
	 *    [A ::= a b X * c, {d,e}]
	 *    [B ::= a b X * Y, {a,b}]
	 * </pre>
	 * 
	 * The full state would then be formed by "closing" this kernel set of items
	 * so that it included items that represented productions of things the
	 * parser was now looking for. In this case we would items corresponding to
	 * productions of Y, since various forms of Y are expected next when in this
	 * state (see lalr_item_set.compute_closure() for details on closure).
	 * <p>
	 * 
	 * The process of building the viable prefix recognizer terminates when no
	 * new states can be added. However, in order to build a smaller number of
	 * states (i.e., corresponding to LALR rather than canonical LR) the state
	 * building process does not maintain full loookaheads in all items.
	 * Consequently, after the machine is built, we go back and propagate
	 * lookaheads through the constructed machine using a call to
	 * propagate_all_lookaheads(). This makes use of propagation links
	 * constructed during the closure and transition process.
	 * 
	 * @param start_prod
	 *            the start production of the grammar
	 * @see java_cup.lalr_item_set#compute_closure
	 * @see java_cup.lalr_state#propagate_all_lookaheads
	 */
	public static lalr_state build_machine(production start_prod)
			throws internal_error {
		lalr_state start_state;
		lalr_item_set start_items;
		lalr_item_set new_items;
		lalr_item_set linked_items;
		lalr_item_set kernel;
		Stack work_stack = new Stack();
		lalr_state st, new_st;
		symbol_set outgoing;
		lalr_item itm, new_itm, existing, fix_itm;
		symbol sym, sym2;
		Enumeration i, s, fix;

		/* sanity check */
		if (start_prod == null)
			throw new internal_error(
					"Attempt to build viable prefix recognizer using a null production");

		/* build item with dot at front of start production and EOF lookahead */
		start_items = new lalr_item_set();

		itm = new lalr_item(start_prod);
		itm.lookahead().add(TerminalFactory.EOF);

		start_items.add(itm);

		/* create copy the item set to form the kernel */
		kernel = new lalr_item_set(start_items);

		/* create the closure from that item set */
		start_items.compute_closure();

		/* build a state out of that item set and put it in our work set */
		start_state = createLalrState(start_items);
		work_stack.push(start_state);

		/* enter the state using the kernel as the key */
		_all_kernels.put(kernel, start_state);

		/* continue looking at new states until we have no more work to do */
		while (!work_stack.empty()) {
			/* remove a state from the work set */
			st = (lalr_state) work_stack.pop();

			/* gather up all the symbols that appear before dots */
			outgoing = new symbol_set();
			for (i = st.items().all(); i.hasMoreElements();) {
				itm = (lalr_item) i.nextElement();

				/* add the symbol before the dot (if any) to our collection */
				sym = itm.symbol_after_dot();
				if (sym != null)
					outgoing.add(sym);
			}

			/* now create a transition out for each individual symbol */
			for (s = outgoing.all(); s.hasMoreElements();) {
				sym = (symbol) s.nextElement();

				/* will be keeping the set of items with propagate links */
				linked_items = new lalr_item_set();

				/*
				 * gather up shifted versions of all the items that have this
				 * symbol before the dot
				 */
				new_items = new lalr_item_set();
				for (i = st.items().all(); i.hasMoreElements();) {
					itm = (lalr_item) i.nextElement();

					/* if this is the symbol we are working on now, add to set */
					sym2 = itm.symbol_after_dot();
					if (sym.equals(sym2)) {
						/* add to the kernel of the new state */
						new_items.add(itm.shift());

						/* remember that itm has propagate link to it */
						linked_items.add(itm);
					}
				}

				/* use new items as state kernel */
				kernel = new lalr_item_set(new_items);

				/* have we seen this one already? */
				new_st = (lalr_state) _all_kernels.get(kernel);

				/* if we haven't, build a new state out of the item set */
				if (new_st == null) {
					/* compute closure of the kernel for the full item set */
					new_items.compute_closure();

					/* build the new state */
					new_st = createLalrState(new_items);

					/* add the new state to our work set */
					work_stack.push(new_st);

					/* put it in our kernel table */
					_all_kernels.put(kernel, new_st);
				}
				/* otherwise relink propagation to items in existing state */
				else {
					/* walk through the items that have links to the new state */
					for (fix = linked_items.all(); fix.hasMoreElements();) {
						fix_itm = (lalr_item) fix.nextElement();

						/* look at each propagate link out of that item */
						for (int l = 0; l < fix_itm.propagate_items().size(); l++) {
							/* pull out item linked to in the new state */
							new_itm = (lalr_item) fix_itm.propagate_items()
									.elementAt(l);

							/* find corresponding item in the existing state */
							existing = new_st.items().find(new_itm);

							/* fix up the item so it points to the existing set */
							if (existing != null)
								fix_itm.propagate_items().setElementAt(
										existing, l);
						}
					}
				}

				/* add a transition from current state to that state */
				st.add_transition(sym, new_st);
			}
		}

		/* all done building states */

		/* propagate complete lookahead sets throughout the states */
		propagate_all_lookaheads();

		return start_state;
	}

	public static lalr_state createLalrState(lalr_item_set itms)
			throws internal_error {
		/* don't allow null or duplicate item sets */
		if (itms == null)
			throw new internal_error(
					"Attempt to construct an LALR state from a null item set");

		if (find_state(itms) != null)
			throw new internal_error(
					"Attempt to construct a duplicate LALR state");
		lalr_state result = new lalr_state(itms);

		/* assign a unique index */
		result.setIndex(next_index++);

		/* add to the global collection, keyed with its item set */
		_all.put(itms, result);

		return result;
	}

	public LalrStateFactory() {
		super();
	}

}