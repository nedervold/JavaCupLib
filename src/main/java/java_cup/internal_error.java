package java_cup;

/** Exception subclass for reporting internal errors in JavaCup. */
public class internal_error extends Exception {
	/** Constructor with a message */
	public internal_error(String msg) {
		super(msg);
	}

	/**
	 * Method called to do a forced error exit on an internal error for cases
	 * when we can't actually throw the exception.
	 */
	public void crash(IErrorManager errorManager) {
		errorManager.internalFatalError(this);
		printStackTrace();
		System.exit(-1);
	}
}
