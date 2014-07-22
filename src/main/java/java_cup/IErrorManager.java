package java_cup;

import java_cup.runtime.Symbol;

public interface IErrorManager {

	public int getFatalCount();

	public int getErrorCount();

	public boolean areErrors();

	public int getWarningCount();

	// TODO: migrate to java.util.logging
	/**
	 * Error message format: ERRORLEVEL at (LINE/COLUMN)@SYMBOL: MESSAGE
	 * ERRORLEVEL : MESSAGE
	 **/
	public void emit_fatal(String message);

	public void emit_fatal(String message, Symbol sym);

	public void emit_warning(String message);

	public void emit_warning(String message, Symbol sym);

	public void emit_error(String message);

	public void emit_error(String message, Symbol sym);

	public void exitIfErrors(int status);

	// // // //

	public void reduceReduceConflict(TerminalFactory terminalFactory,
			lalr_state st, lalr_item itm1, lalr_item itm2)
			throws internal_error;

	public void shiftReduceConflict(lalr_state st,
			TerminalFactory terminalFactory, lalr_item red_itm, int conflict_sym)
			throws internal_error;

	public void unrecognizedToken(String message);

	public void unreducedProduction(production prod) throws internal_error;

	public void unusedNonTerminal(non_terminal nt);

	public void unusedTerminal(terminal t);

	public void parserWarning(String message);

	public void parserError(String message);

	public void parserError(String message, Symbol sym);
}