package java_cup;

import java.io.PrintStream;

public interface ITimings {

	public abstract void endAll();

	public abstract void endBuild();

	public abstract void endCheck();

	public abstract void endDump();

	public abstract void endEmit();

	public abstract void endFirstSets();

	public abstract void endNullability();

	public abstract void endParsing();

	public abstract void endPreliminaries();

	public abstract void endReducedChecking();

	public abstract void endStateMachine();

	public abstract void endTables();

	public abstract void show_times(PrintStream ps, Emitter emitter);

	public abstract void start();

}