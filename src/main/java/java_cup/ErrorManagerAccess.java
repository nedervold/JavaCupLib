package java_cup;

public class ErrorManagerAccess {

	protected static ErrorManager errorManager;

	public static ErrorManager getManager() { return errorManager; }

	static {
        errorManager = new ErrorManager();
    }

    public ErrorManagerAccess() {
		super();
	}

}