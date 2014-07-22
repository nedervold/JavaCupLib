package java_cup;

import java.util.Enumeration;

import java_cup.runtime.Symbol;

public abstract class AbstractErrorManager implements IErrorManager {


	private int errors = 0;

	private int fatals = 0;

	private int warnings = 0;

	protected AbstractErrorManager() {
		super();
	}

	public final boolean areErrors() {
		return getErrorCount() != 0;
	}

	public void emit_error(final String message) {
		errors++;
	}

	public void emit_error(final String message, final Symbol sym) {
		errors++;
	}

	public void emit_fatal(final String message) {
		fatals++;
	}

	public void emit_fatal(final String message, final Symbol sym) {
		fatals++;
	}

	public void emit_warning(final String message) {
		warnings++;
	}

	public void emit_warning(final String message, final Symbol sym) {
		warnings++;
	}

	public final void exitIfErrors(final int status) {
		if (areErrors()) {
			System.exit(status);
		}
	}

	public final int getErrorCount() {
		return errors;
	}

	public final int getFatalCount() {
		return fatals;
	}

	public final int getWarningCount() {
		return warnings;
	}
}