package java_cup;

import java_cup.runtime.Symbol;

public interface IErrorManager {

	public abstract int getFatalCount();

	public abstract int getErrorCount();

	public abstract int getWarningCount();

	//TODO: migrate to java.util.logging
	/**
	 * Error message format: 
	 * ERRORLEVEL at (LINE/COLUMN)@SYMBOL: MESSAGE
	 * ERRORLEVEL : MESSAGE
	 **/
	public abstract void emit_fatal(String message);

	public abstract void emit_fatal(String message, Symbol sym);

	public abstract void emit_warning(String message);

	public abstract void emit_warning(String message, Symbol sym);

	public abstract void emit_error(String message);

	public abstract void emit_error(String message, Symbol sym);

}