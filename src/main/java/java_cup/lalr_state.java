package java_cup;

import java.util.Enumeration;

/**
 * This class represents a state in the LALR viable prefix recognition machine.
 * A state consists of an LALR item set and a set of transitions to other states
 * under terminal and non-terminal symbols. Each state represents a potential
 * configuration of the parser. If the item set of a state includes an item such
 * as:
 * 
 * <pre>
 *    [A ::= B * C d E , {a,b,c}]
 * </pre>
 * 
 * this indicates that when the parser is in this state it is currently looking
 * for an A of the given form, has already seen the B, and would expect to see
 * an a, b, or c after this sequence is complete. Note that the parser is
 * normally looking for several things at once (represented by several items).
 * In our example above, the state would also include items such as:
 * 
 * <pre>
 *    [C ::= * X e Z, {d}]
 *    [X ::= * f, {e}]
 * </pre>
 * 
 * to indicate that it was currently looking for a C followed by a d (which
 * would be reduced into a C, matching the first symbol in our production
 * above), and the terminal f followed by e.
 * <p>
 * 
 * At runtime, the parser uses a viable prefix recognition machine made up of
 * these states to parse. The parser has two operations, shift and reduce. In a
 * shift, it consumes one Symbol and makes a transition to a new state. This
 * corresponds to "moving the dot past" a terminal in one or more items in the
 * state (these new shifted items will then be found in the state at the end of
 * the transition). For a reduce operation, the parser is signifying that it is
 * recognizing the RHS of some production. To do this it first "backs up" by
 * popping a stack of previously saved states. It pops off the same number of
 * states as are found in the RHS of the production. This leaves the machine in
 * the same state is was in when the parser first attempted to find the RHS.
 * From this state it makes a transition based on the non-terminal on the LHS of
 * the production. This corresponds to placing the parse in a configuration
 * equivalent to having replaced all the symbols from the the input
 * corresponding to the RHS with the symbol on the LHS.
 * 
 * @see java_cup.lalr_item
 * @see java_cup.lalr_item_set
 * @see java_cup.lalr_transition
 * @version last updated: 7/3/96
 * @author Frank Flannery
 * 
 */

public class lalr_state {
	/*-----------------------------------------------------------*/
	/*--- Constructor(s) ----------------------------------------*/
	/*-----------------------------------------------------------*/

	/**
	 * Constructor for building a state from a set of items.
	 * 
	 * @param itms
	 *            the set of items that makes up this state.
	 */
	protected lalr_state(lalr_item_set itms) throws internal_error {
		/* store the items */
		_items = itms;
	}

	/*-----------------------------------------------------------*/
	/*--- (Access to) Instance Variables ------------------------*/
	/*-----------------------------------------------------------*/

	/** The item set for this state. */
	protected lalr_item_set _items;

	/** The item set for this state. */
	public lalr_item_set items() {
		return _items;
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** List of transitions out of this state. */
	protected lalr_transition _transitions = null;

	/** List of transitions out of this state. */
	public lalr_transition transitions() {
		return _transitions;
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Index of this state in the parse tables */
	protected int _index;

	/** Index of this state in the parse tables */
	public int index() {
		return _index;
	}

	protected void setIndex(int i) {
		_index = i;
	}

	/*-----------------------------------------------------------*/
	/*--- General Methods ---------------------------------------*/
	/*-----------------------------------------------------------*/

	/**
	 * Add a transition out of this state to another.
	 * 
	 * @param on_sym
	 *            the symbol the transition is under.
	 * @param to_st
	 *            the state the transition goes to.
	 */
	public void add_transition(symbol on_sym, lalr_state to_st)
			throws internal_error {
		lalr_transition trans;

		/* create a new transition object and put it in our list */
		trans = new lalr_transition(on_sym, to_st, _transitions);
		_transitions = trans;
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Propagate lookahead sets out of this state. This recursively propagates
	 * to all items that have propagation links from some item in this state.
	 */
	protected void propagate_lookaheads() throws internal_error {
		/* recursively propagate out from each item in the state */
		for (Enumeration<lalr_item> itm = items().all(); itm.hasMoreElements();)
			((lalr_item) itm.nextElement()).propagate_lookaheads(null);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Fill in the parse table entries for this state. There are two parse
	 * tables that encode the viable prefix recognition machine, an action table
	 * and a reduce-goto table. The rows in each table correspond to states of
	 * the machine. The columns of the action table are indexed by terminal
	 * symbols and correspond to either transitions out of the state (shift
	 * entries) or reductions from the state to some previous state saved on the
	 * stack (reduce entries). All entries in the action table that are not
	 * shifts or reduces, represent errors. The reduce-goto table is indexed by
	 * non terminals and represents transitions out of a state on that
	 * non-terminal.
	 * <p>
	 * Conflicts occur if more than one action needs to go in one entry of the
	 * action table (this cannot happen with the reduce-goto table). Conflicts
	 * are resolved by always shifting for shift/reduce conflicts and choosing
	 * the lowest numbered production (hence the one that appeared first in the
	 * specification) in reduce/reduce conflicts. All conflicts are reported and
	 * if more conflicts are detected than were declared by the user, code
	 * generation is aborted.
	 * 
	 * @param act_table
	 *            the action table to put entries in.
	 * @param reduce_table
	 *            the reduce-goto table to put entries in.
	 */
	public void build_table_entries(IErrorManager errorManager,
			TerminalFactory terminalFactory, Emitter emit,
			parse_action_table act_table, parse_reduce_table reduce_table)
			throws internal_error {
		parse_action_row our_act_row;
		parse_reduce_row our_red_row;
		lalr_item itm;
		parse_action act, other_act;
		symbol sym;
		terminal_set conflict_set = new terminal_set(terminalFactory);

		/* pull out our rows from the tables */
		our_act_row = act_table.under_state[index()];
		our_red_row = reduce_table.under_state[index()];

		/* consider each item in our state */
		for (Enumeration<lalr_item> i = items().all(); i.hasMoreElements();) {
			itm = (lalr_item) i.nextElement();

			/* if its completed (dot at end) then reduce under the lookahead */
			if (itm.dot_at_end()) {
				act = new reduce_action(itm.the_production());

				/* consider each lookahead symbol */
				for (int t = 0; t < terminalFactory.number(); t++) {
					/* skip over the ones not in the lookahead */
					if (!itm.lookahead().contains(t))
						continue;

					/* if we don't already have an action put this one in */
					if (our_act_row.under_term[t].kind() == parse_action.ERROR) {
						our_act_row.under_term[t] = act;
					} else {
						/* we now have at least one conflict */
						terminal term = terminalFactory.find(t);
						other_act = our_act_row.under_term[t];

						/* if the other act was not a shift */
						if ((other_act.kind() != parse_action.SHIFT)
								&& (other_act.kind() != parse_action.NONASSOC)) {
							/* if we have lower index hence priority, replace it */
							if (itm.the_production().index() < ((reduce_action) other_act)
									.reduce_with().index()) {
								/* replace the action */
								our_act_row.under_term[t] = act;
							}
						} else {
							/* Check precedences,see if problem is correctable */
							if (fix_with_precedence(terminalFactory,
									itm.the_production(), t, our_act_row, act)) {
								term = null;
							}
						}
						if (term != null) {

							conflict_set.add(term);
						}
					}
				}
			}
		}

		/* consider each outgoing transition */
		for (lalr_transition trans = transitions(); trans != null; trans = trans
				.next()) {
			/* if its on an terminal add a shift entry */
			sym = trans.on_symbol();
			if (!sym.is_non_term()) {
				act = new shift_action(trans.to_state());

				/* if we don't already have an action put this one in */
				if (our_act_row.under_term[sym.index()].kind() == parse_action.ERROR) {
					our_act_row.under_term[sym.index()] = act;
				} else {
					/* we now have at least one conflict */
					production p = ((reduce_action) our_act_row.under_term[sym
							.index()]).reduce_with();

					/* shift always wins */
					if (!fix_with_precedence(terminalFactory, p, sym.index(),
							our_act_row, act)) {
						our_act_row.under_term[sym.index()] = act;
						conflict_set.add(terminalFactory.find(sym.index()));
					}
				}
			} else {
				/* for non terminals add an entry to the reduce-goto table */
				our_red_row.under_non_term[sym.index()] = trans.to_state();
			}
		}

		/* if we end up with conflict(s), report them */
		if (!conflict_set.empty())
			report_conflicts(errorManager, terminalFactory, emit, conflict_set);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Procedure that attempts to fix a shift/reduce error by using precedences.
	 * --frankf 6/26/96
	 * 
	 * if a production (also called rule) or the lookahead terminal has a
	 * precedence, then the table can be fixed. if the rule has greater
	 * precedence than the terminal, a reduce by that rule in inserted in the
	 * table. If the terminal has a higher precedence, it is shifted. if they
	 * have equal precedence, then the associativity of the precedence is used
	 * to determine what to put in the table: if the precedence is left
	 * associative, the action is to reduce. if the precedence is right
	 * associative, the action is to shift. if the precedence is non
	 * associative, then it is a syntax error.
	 * 
	 * @param p
	 *            the production
	 * @param term_index
	 *            the index of the lokahead terminal
	 * @param parse_action_row
	 *            a row of the action table
	 * @param act
	 *            the rule in conflict with the table entry
	 */

	protected boolean fix_with_precedence(TerminalFactory terminalFactory,
			production p, int term_index, parse_action_row table_row,
			parse_action act)

	throws internal_error {

		terminal term = terminalFactory.find(term_index);

		/* if the production has a precedence number, it can be fixed */
		if (p.precedence_num() > assoc.no_prec) {

			/* if production precedes terminal, put reduce in table */
			if (p.precedence_num() > term.precedence_num()) {
				table_row.under_term[term_index] = insert_reduce(
						table_row.under_term[term_index], act);
				return true;
			}

			/* if terminal precedes rule, put shift in table */
			else if (p.precedence_num() < term.precedence_num()) {
				table_row.under_term[term_index] = insert_shift(
						table_row.under_term[term_index], act);
				return true;
			} else { /* they are == precedence */

				/*
				 * equal precedences have equal sides, so only need to look at
				 * one: if it is right, put shift in table
				 */
				if (term.precedence_side() == assoc.right) {
					table_row.under_term[term_index] = insert_shift(
							table_row.under_term[term_index], act);
					return true;
				}

				/* if it is left, put reduce in table */
				else if (term.precedence_side() == assoc.left) {
					table_row.under_term[term_index] = insert_reduce(
							table_row.under_term[term_index], act);
					return true;
				}

				/*
				 * if it is nonassoc, we're not allowed to have two nonassocs of
				 * equal precedence in a row, so put in NONASSOC
				 */
				else if (term.precedence_side() == assoc.nonassoc) {
					table_row.under_term[term_index] = new nonassoc_action();
					return true;
				} else {
					/* something really went wrong */
					throw new internal_error(
							"Unable to resolve conflict correctly");
				}
			}
		}
		/*
		 * check if terminal has precedence, if so, shift, since rule does not
		 * have precedence
		 */
		else if (term.precedence_num() > assoc.no_prec) {
			table_row.under_term[term_index] = insert_shift(
					table_row.under_term[term_index], act);
			return true;
		}

		/*
		 * otherwise, neither the rule nor the terminal has a precedence, so it
		 * can't be fixed.
		 */
		return false;
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/*
	 * given two actions, and an action type, return the action of that action
	 * type. give an error if they are of the same action, because that should
	 * never have tried to be fixed
	 */
	protected parse_action insert_action(parse_action a1, parse_action a2,
			int act_type) throws internal_error {
		if ((a1.kind() == act_type) && (a2.kind() == act_type)) {
			throw new internal_error("Conflict resolution of bogus actions");
		} else if (a1.kind() == act_type) {
			return a1;
		} else if (a2.kind() == act_type) {
			return a2;
		} else {
			throw new internal_error("Conflict resolution of bogus actions");
		}
	}

	/* find the shift in the two actions */
	protected parse_action insert_shift(parse_action a1, parse_action a2)
			throws internal_error {
		return insert_action(a1, a2, parse_action.SHIFT);
	}

	/* find the reduce in the two actions */
	protected parse_action insert_reduce(parse_action a1, parse_action a2)
			throws internal_error {
		return insert_action(a1, a2, parse_action.REDUCE);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Produce warning messages for all conflicts found in this state. */
	protected void report_conflicts(IErrorManager errorManager,
			TerminalFactory terminalFactory, Emitter emit,
			terminal_set conflict_set) throws internal_error {
		lalr_item itm, compare;
		boolean after_itm;

		/* consider each element */
		for (Enumeration<lalr_item> itms = items().all(); itms
				.hasMoreElements();) {
			itm = (lalr_item) itms.nextElement();

			/* clear the S/R conflict set for this item */

			/* if it results in a reduce, it could be a conflict */
			if (itm.dot_at_end()) {
				/* not yet after itm */
				after_itm = false;

				/* compare this item against all others looking for conflicts */
				for (Enumeration<lalr_item> comps = items().all(); comps
						.hasMoreElements();) {
					compare = (lalr_item) comps.nextElement();

					/* if this is the item, next one is after it */
					if (itm == compare)
						after_itm = true;

					/* only look at it if its not the same item */
					if (itm != compare) {
						/* is it a reduce */
						if (compare.dot_at_end()) {
							/* only look at reduces after itm */
							if (after_itm)
								/* does the comparison item conflict? */
								if (compare.lookahead().intersects(
										itm.lookahead()))
									/* report a reduce/reduce conflict */
									report_reduce_reduce(errorManager,
											terminalFactory, emit, itm, compare);
						}
					}
				}
				/* report S/R conflicts under all the symbols we conflict under */
				for (int t = 0; t < terminalFactory.number(); t++)
					if (conflict_set.contains(t))
						report_shift_reduce(errorManager, terminalFactory,
								emit, itm, t);
			}
		}
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Produce a warning message for one reduce/reduce conflict.
	 * 
	 * @param itm1
	 *            first item in conflict.
	 * @param itm2
	 *            second item in conflict.
	 */
	protected void report_reduce_reduce(IErrorManager errorManager,
			TerminalFactory terminalFactory, Emitter emit, lalr_item itm1,
			lalr_item itm2) throws internal_error {

		/* count the conflict */
		emit.set_num_conflicts(emit.num_conflicts() + 1);

		errorManager.reduceReduceConflict(terminalFactory, this, itm1, itm2);
	}


	/**
	 * Produce a warning message for one shift/reduce conflict.
	 * 
	 * @param red_itm
	 *            the item with the reduce.
	 * @param conflict_sym
	 *            the index of the symbol conflict occurs under.
	 */
	protected void report_shift_reduce(IErrorManager errorManager,
			TerminalFactory terminalFactory, Emitter emit, lalr_item red_itm,
			int conflict_sym) throws internal_error {
		/* count the conflict */
		emit.set_num_conflicts(emit.num_conflicts() + 1);

		errorManager.shiftReduceConflict(this, terminalFactory, red_itm,
				conflict_sym);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Equality comparison. */
	public boolean equals(lalr_state other) {
		/* we are equal if our item sets are equal */
		return other != null && items().equals(other.items());
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Generic equality comparison. */
	public boolean equals(Object other) {
		if (!(other instanceof lalr_state))
			return false;
		else
			return equals((lalr_state) other);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Produce a hash code. */
	public int hashCode() {
		/* just use the item set hash code */
		return items().hashCode();
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Convert to a string. */
	public String toString() {
		String result;
		lalr_transition tr;

		/* dump the item set */
		result = "lalr_state [" + index() + "]: " + _items + "\n";

		/* do the transitions */
		for (tr = transitions(); tr != null; tr = tr.next()) {
			result += tr;
			result += "\n";
		}

		return result;
	}

	/*-----------------------------------------------------------*/
}
