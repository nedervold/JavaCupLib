package java_cup;

import java_cup.runtime.Symbol;

public class ErrorManager implements IErrorManager {
	private int errors = 0;
	private int warnings = 0;
	private int fatals = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#getFatalCount()
	 */
	public int getFatalCount() {
		return fatals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#getErrorCount()
	 */
	public int getErrorCount() {
		return errors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#getWarningCount()
	 */
	public int getWarningCount() {
		return warnings;
	}

	protected ErrorManager() {
	}

	// TODO: migrate to java.util.logging
	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_fatal(java.lang.String)
	 */
	public void emit_fatal(String message) {
		System.err.println("Fatal : " + message);
		fatals++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_fatal(java.lang.String,
	 * java_cup.runtime.Symbol)
	 */
	public void emit_fatal(String message, Symbol sym) {
		// System.err.println("Fatal at ("+sym.left+"/"+sym.right+")@"+convSymbol(sym)+" : "+message);
		System.err.println("Fatal: " + message + " @ " + sym);
		fatals++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_warning(java.lang.String)
	 */
	public void emit_warning(String message) {
		System.err.println("Warning : " + message);
		warnings++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_warning(java.lang.String,
	 * java_cup.runtime.Symbol)
	 */
	public void emit_warning(String message, Symbol sym) {
		// System.err.println("Warning at ("+sym.left+"/"+sym.right+")@"+convSymbol(sym)+" : "+message);
		System.err.println("Fatal: " + message + " @ " + sym);
		warnings++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_error(java.lang.String)
	 */
	public void emit_error(String message) {
		System.err.println("Error : " + message);
		errors++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_error(java.lang.String,
	 * java_cup.runtime.Symbol)
	 */
	public void emit_error(String message, Symbol sym) {
		// System.err.println("Error at ("+sym.left+"/"+sym.right+")@"+convSymbol(sym)+" : "+message);
		System.err.println("Error: " + message + " @ " + sym);
		errors++;
	}

	public void exitIfErrors(int status) {
		if (getErrorCount() != 0) {
			System.exit(status);
		}
	}

}
