package java_cup;

import java_cup.runtime.Symbol;

public class ErrorManager extends AbstractErrorManager {
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
		super.emit_fatal(message);
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
		super.emit_fatal(message, sym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_warning(java.lang.String)
	 */
	public void emit_warning(String message) {
		System.err.println("Warning : " + message);
		super.emit_warning(message);
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
		super.emit_warning(message, sym);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.IErrorManager#emit_error(java.lang.String)
	 */
	public void emit_error(String message) {
		System.err.println("Error : " + message);
		super.emit_error(message);
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
		super.emit_error(message, sym);
	}

}
