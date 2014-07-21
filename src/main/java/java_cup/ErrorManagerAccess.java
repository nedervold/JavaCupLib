package java_cup;

public class ErrorManagerAccess {

	protected static IErrorManager errorManager;

	static {
		errorManager = new ErrorManager();
	}

	public static IErrorManager getManager() {
		return errorManager;
	}

	public ErrorManagerAccess() {
		super();
	}

}