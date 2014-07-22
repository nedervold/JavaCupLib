package java_cup;

import java.util.Enumeration;

import java_cup.runtime.Symbol;

public class ErrorManager extends AbstractErrorManager {

	private static String reduceReduceMessage(
			final TerminalFactory terminalFactory, final lalr_state st,
			final lalr_item itm1, final lalr_item itm2) throws internal_error {
		boolean comma_flag = false;
		String message = "*** Reduce/Reduce conflict found in state #"
				+ st.index() + "\n" + "  between " + itm1.to_simple_string()
				+ "\n" + "  and     " + itm2.to_simple_string() + "\n"
				+ "  under symbols: {";
		for (int t = 0; t < terminalFactory.number(); t++) {
			if (itm1.lookahead().contains(t) && itm2.lookahead().contains(t)) {
				if (comma_flag) {
					message += ", ";
				} else {
					comma_flag = true;
				}
				message += terminalFactory.find(t).name();
			}
		}
		message += "}\n  Resolved in favor of ";
		if (itm1.the_production().index() < itm2.the_production().index()) {
			message += "the first production.\n";
		} else {
			message += "the second production.\n";
		}
		return message;
	}

	private static String shiftReduceMessage(final lalr_state st,
			final TerminalFactory terminalFactory, final lalr_item red_itm,
			final int conflict_sym) throws internal_error {
		/* emit top part of message including the reduce item */
		String message = "*** Shift/Reduce conflict found in state #"
				+ st.index() + "\n" + "  between " + red_itm.to_simple_string()
				+ "\n";

		/* find and report on all items that shift under our conflict symbol */
		for (final Enumeration<lalr_item> itms = st.items().all(); itms
				.hasMoreElements();) {
			final lalr_item itm = itms.nextElement();

			/* only look if its not the same item and not a reduce */
			if (itm != red_itm && !itm.dot_at_end()) {
				/* is it a shift on our conflicting terminal */
				final symbol shift_sym = itm.symbol_after_dot();
				if (!shift_sym.is_non_term()
						&& shift_sym.index() == conflict_sym) {
					/* yes, report on it */
					message += "  and     " + itm.to_simple_string() + "\n";
				}
			}
		}
		message += "  under symbol "
				+ terminalFactory.find(conflict_sym).name() + "\n"
				+ "  Resolved in favor of shifting.\n";
		return message;
	}

	protected ErrorManager() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_error(java.lang.String)
	 */
	@Override
	public void emit_error(final String message) {
		System.err.println("Error : " + message);
		super.emit_error(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_error(java.lang.String,
	 * java_cup.runtime.Symbol)
	 */
	@Override
	public void emit_error(final String message, final Symbol sym) {
		// System.err.println("Error at ("+sym.left+"/"+sym.right+")@"+convSymbol(sym)+" : "+message);
		System.err.println("Error: " + message + " @ " + sym);
		super.emit_error(message, sym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_fatal(java.lang.String)
	 */
	@Override
	public void emit_fatal(final String message) {
		System.err.println("Fatal : " + message);
		super.emit_fatal(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_fatal(java.lang.String,
	 * java_cup.runtime.Symbol)
	 */
	@Override
	public void emit_fatal(final String message, final Symbol sym) {
		// System.err.println("Fatal at ("+sym.left+"/"+sym.right+")@"+convSymbol(sym)+" : "+message);
		System.err.println("Fatal: " + message + " @ " + sym);
		super.emit_fatal(message, sym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_warning(java.lang.String)
	 */
	@Override
	public void emit_warning(final String message) {
		System.err.println("Warning : " + message);
		super.emit_warning(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_warning(java.lang.String,
	 * java_cup.runtime.Symbol)
	 */
	@Override
	public void emit_warning(final String message, final Symbol sym) {
		// System.err.println("Warning at ("+sym.left+"/"+sym.right+")@"+convSymbol(sym)+" : "+message);
		System.err.println("Fatal: " + message + " @ " + sym);
		super.emit_warning(message, sym);
	}

	public void internalFatalError(final internal_error e) {
		emit_fatal("JavaCUP Internal Error Detected: " + e.getMessage());
	}

	public void parserError(final String message) {
		emit_error(message);
	}

	public void parserError(final String message, final Symbol sym) {
		emit_error(message, sym);
	}

	public void parserFatalError(final String message, final Symbol sym) {
		emit_fatal(message
				+ "\nCan't recover from previous error(s), giving up.", sym);
	}

	public void parserWarning(final String message) {
		emit_warning(message);
	}

	public void reduceReduceConflict(final TerminalFactory terminalFactory,
			final lalr_state st, final lalr_item itm1, final lalr_item itm2)
			throws internal_error {
		final String message = reduceReduceMessage(terminalFactory, st, itm1,
				itm2);
		emit_warning(message);
	}

	public void shiftReduceConflict(final lalr_state st,
			final TerminalFactory terminalFactory, final lalr_item red_itm,
			final int conflict_sym) throws internal_error {
		final String message = shiftReduceMessage(st, terminalFactory, red_itm,
				conflict_sym);

		emit_warning(message);
	}

	public void tooManyConflicts(int expected, int actual) {
		emit_error("*** More conflicts encountered than expected "
				+ "-- parser generation aborted");
	}

	public void unrecognizedToken(final String message) {
		emit_warning(message);
	}

	public void unreducedProduction(final production prod)
			throws internal_error {
		emit_warning("*** Production \"" + prod.to_simple_string()
				+ "\" never reduced");
	}

	public void unusedNonTerminal(final non_terminal nt) {
		emit_warning("Non terminal \"" + nt.name()
				+ "\" was declared but never used");
	}

	public void unusedTerminal(final terminal term) {
		emit_warning("Terminal \"" + term.name()
				+ "\" was declared but never used");
	}

}
