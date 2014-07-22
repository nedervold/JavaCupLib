package java_cup;

import java_cup.runtime.Symbol;

public interface IErrorManager {

	public int getFatalCount();

	public int getErrorCount();

	public boolean areErrors();

	public int getWarningCount();

	public void emit_fatal(String message);

	public void emit_fatal(String message, Symbol sym);

	public void emit_warning(String message);

	public void emit_warning(String message, Symbol sym);

	public void emit_error(String message);

	public void emit_error(String message, Symbol sym);

	public void exitIfErrors(int status);

	// // // //

	public void internalFatalError(internal_error e);

	public void parserError(String message);

	public void parserError(String message, Symbol sym);

	public void parserFatalError(String message, Symbol sym);

	public void parserWarning(String message);

	public void reduceReduceConflict(TerminalFactory terminalFactory,
			lalr_state st, lalr_item itm1, lalr_item itm2)
			throws internal_error;

	public void shiftReduceConflict(lalr_state st,
			TerminalFactory terminalFactory, lalr_item red_itm, int conflict_sym)
			throws internal_error;

	public void tooManyConflicts(int expected, int actual);

	public void unrecognizedToken(String message);

	public void unreducedProduction(production prod) throws internal_error;

	public void unusedNonTerminal(non_terminal nt);

	public void unusedTerminal(terminal t);

}