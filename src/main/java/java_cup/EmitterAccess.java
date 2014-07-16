package java_cup;

public class EmitterAccess {

	private static Emitter _instance = new cup_emit();

	public static Emitter instance() {
		return _instance;
	}

	public EmitterAccess() {
		super();
	}

}