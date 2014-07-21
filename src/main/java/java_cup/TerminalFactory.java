package java_cup;

import java.util.Enumeration;
import java.util.Hashtable;

public  class TerminalFactory {

	private void register(terminal t) {
		/* add to set of all terminals and check for duplicates */
		Object conflict = _all.put(t.name(), t);
		if (conflict != null)
			// can't throw an execption here because this is used in static
			// initializers, so we do a crash instead
			// was:
			// throw new internal_error("Duplicate terminal (" + nm +
			// ") created");
			(new internal_error("Duplicate terminal (" + t.name() + ") created"))
					.crash();

		/* assign a unique index */
		next_index++;

		/* add to by_index set */
		_all_by_index.put(new Integer(t.index()), t);
	}

	/**
	 * Table of all terminals. Elements are stored using name strings as the key
	 */
	protected Hashtable<String, terminal> _all = new Hashtable<String, terminal>();

	public void clear() {
		_all.clear();
		_all_by_index.clear();
		next_index = 0;
		EOF = createTerminal("EOF");
		error = createTerminal("error");
	}

	/** Access to all terminals. */
	public Enumeration<terminal> all() {
		return _all.elements();
	}

	/** Lookup a terminal by name string. */
	public terminal find(String with_name) {
		if (with_name == null)
			return null;
		else
			return _all.get(with_name);
	}

	/** Table of all terminals indexed by their index number. */
	protected Hashtable<Integer, terminal> _all_by_index = new Hashtable<Integer, terminal>();

	/** Lookup a terminal by index. */
	public terminal find(int indx) {
		return _all_by_index.get(indx);
	}

	/** Total number of terminals. */
	public int number() {
		return _all.size();
	}

	public terminal createTerminal(String nm, String tp,
			int precedence_side, int precedence_num) {
		terminal result = new terminal(nm, tp, precedence_side, precedence_num,
				next_index);
		register(result);
		return result;
	}

	public terminal createTerminal(String nm, String tp) {
		terminal result = new terminal(nm, tp, next_index);
		register(result);
		return result;
	}

	public terminal createTerminal(String nm) {
		terminal result = new terminal(nm, next_index);
		register(result);
		return result;
	}

	/** Static counter to assign unique index. */
	protected int next_index = 0;
	/** Special terminal for end of input. */
	public terminal EOF = createTerminal("EOF");
	/** special terminal used for error recovery */
	public terminal error = createTerminal("error");

}