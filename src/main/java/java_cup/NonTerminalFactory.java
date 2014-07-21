package java_cup;

import java.util.Enumeration;
import java.util.Hashtable;

public  class NonTerminalFactory {

	public NonTerminalFactory(IErrorManager errorManager, TerminalFactory terminalFactory) {
		super();
		this.errorManager = errorManager;
		this.terminalFactory = terminalFactory;
	}

	private final IErrorManager errorManager;
	private final TerminalFactory terminalFactory;

	private void register(non_terminal nt) {
		/* add to set of all non terminals and check for duplicates */
		Object conflict = _all.put(nt.name(), nt);
		if (conflict != null)
			// can't throw an exception here because these are used in static
			// initializers, so we crash instead
			// was:
			// throw new
			// internal_error("Duplicate non-terminal ("+nm+") created");
			(new internal_error("Duplicate non-terminal (" + nt.name()
					+ ") created")).crash(errorManager);

		/* assign a unique index */
		next_index++;

		/* add to by_index set */
		_all_by_index.put(new Integer(nt.index()), nt);
	}

	/**
	 * Table of all non-terminals -- elements are stored using name strings as
	 * the key
	 */
	protected Hashtable<String, non_terminal> _all = new Hashtable<String, non_terminal>();

	public void clear() {
		_all.clear();
		_all_by_index.clear();
		next_index = 0;
		next_nt = 0;
	}

	/** Access to all non-terminals. */
	public Enumeration<non_terminal> all() {
		return _all.elements();
	}

	/** lookup a non terminal by name string */
	public non_terminal find(String with_name) {
		if (with_name == null)
			return null;
		else
			return (non_terminal) _all.get(with_name);
	}

	/** Table of all non terminals indexed by their index number. */
	protected Hashtable<Integer, non_terminal> _all_by_index = new Hashtable<Integer, non_terminal>();

	/** Lookup a non terminal by index. */
	public non_terminal find(int indx) {
		return _all_by_index.get(indx);
	}

	/** Total number of non-terminals. */
	public int number() {
		return _all.size();
	}

	/** Static counter to assign unique indexes. */
	protected int next_index = 0;
	/** Static counter for creating unique non-terminal names */
	protected int next_nt = 0;
	/** special non-terminal for start symbol */
	private non_terminal START_nt;
	public non_terminal START_nt() {
		if (START_nt == null){
			START_nt = createNonTerminal("$START");
		}
		return START_nt;
	}
	/**
	 * Method for creating a new uniquely named hidden non-terminal using the
	 * given string as a base for the name (or "NT$" if null is passed).
	 * 
	 * @param prefix
	 *            base name to construct unique name from.
	 */
	non_terminal create_new(String prefix) throws internal_error {
		return create_new(prefix, null); // TUM 20060608 embedded actions patch
	}

	/** routine for creating a new uniquely named hidden non-terminal */
	non_terminal create_new() throws internal_error {
		return create_new(null);
	}

	/**
	 * TUM 20060608 bugfix for embedded action codes
	 */
	protected non_terminal create_new(String prefix, String type)
			throws internal_error {
		if (prefix == null)
			prefix = "NT$";
		return createNonTerminal(prefix + next_nt++, type);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Compute nullability of all non-terminals. */
	public void compute_nullability(ProductionFactory productionFactory)
			throws internal_error {
		boolean change = true;
		non_terminal nt;
		production prod;

		/* repeat this process until there is no change */
		while (change) {
			/* look for a new change */
			change = false;

			/* consider each non-terminal */
			for (Enumeration<non_terminal> e = all(); e.hasMoreElements();) {
				nt = e.nextElement();

				/* only look at things that aren't already marked nullable */
				if (!nt.nullable()) {
					if (nt.looks_nullable()) {
						nt._nullable = true;
						change = true;
					}
				}
			}
		}

		/* do one last pass over the productions to finalize all of them */
		for (Enumeration<production> e = productionFactory.all(); e
				.hasMoreElements();) {
			prod = (production) e.nextElement();
			prod.set_nullable(prod.check_nullable());
		}
	}

	/**
	 * Compute first sets for all non-terminals. This assumes nullability has
	 * already computed.
	 */
	public void compute_first_sets() throws internal_error {
		boolean change = true;
		Enumeration<non_terminal> n;
		Enumeration<production> p;
		non_terminal nt;
		production prod;
		terminal_set prod_first;

		/* repeat this process until we have no change */
		while (change) {
			/* look for a new change */
			change = false;

			/* consider each non-terminal */
			for (n = all(); n.hasMoreElements();) {
				nt = (non_terminal) n.nextElement();

				/* consider every production of that non terminal */
				for (p = nt.productions(); p.hasMoreElements();) {
					prod = (production) p.nextElement();

					/* get the updated first of that production */
					prod_first = prod.check_first_set();

					/* if this going to add anything, add it */
					if (!prod_first.is_subset_of(nt._first_set)) {
						change = true;
						nt._first_set.add(prod_first);
					}
				}
			}
		}
	}

	public non_terminal createNonTerminal(String nm, String tp) {
		non_terminal nt = new non_terminal(terminalFactory, nm, tp, next_index);
		register(nt);
		return nt;
	}

	public non_terminal createNonTerminal(String nm) {
		non_terminal nt = new non_terminal(terminalFactory, nm, next_index);
		register(nt);
		return nt;
	}



}