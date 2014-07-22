package java_cup;

import java.io.PrintStream;

public class Timings implements ITimings {

	/**
	 * Helper routine to format a decimal based display of seconds and
	 * percentage of total time given counts of milliseconds. Note: this is
	 * broken for use with some instances of negative time (since we don't use
	 * any negative time here, we let if be for now).
	 * 
	 * @param time_val
	 *            the value being formatted (in ms).
	 * @param total_time
	 *            total time percentages are calculated against (in ms).
	 */
	public static String timestr(long time_val, final long total_time) {
		boolean neg;
		long ms = 0;
		long sec = 0;
		long percent10;
		String pad;

		/* work with positives only */
		neg = time_val < 0;
		if (neg) {
			time_val = -time_val;
		}

		/* pull out seconds and ms */
		ms = time_val % 1000;
		sec = time_val / 1000;

		/* construct a pad to blank fill seconds out to 4 places */
		if (sec < 10) {
			pad = "   ";
		} else if (sec < 100) {
			pad = "  ";
		} else if (sec < 1000) {
			pad = " ";
		} else {
			pad = "";
		}

		/* calculate 10 times the percentage of total */
		percent10 = time_val * 1000 / total_time;

		/* build and return the output string */
		return (neg ? "-" : "") + pad + sec + "." + ms % 1000 / 100 + ms % 100
				/ 10 + ms % 10 + "sec" + " (" + percent10 / 10 + "."
				+ percent10 % 10 + "%)";
	}

	/** Timing data -- when did we end state and table building */
	private long build_end = 0;
	/** Timing data -- when did we end checking */
	private long check_end = 0;

	/** Timing data -- when did we end dumping */
	private long dump_end = 0;
	/** Timing data -- when did we finish emitting code */
	private long emit_end = 0;
	/** Timing data -- when were we completely done */
	private long final_time = 0;
	/** Timing data -- when did we end first set calculation */
	private long first_end = 0;
	/** Timing data -- when did we end state machine construction */
	private long machine_end = 0;
	/** Timing data -- when did we end nullability calculation */
	private long nullability_end = 0;
	/** Timing data -- when did we end parsing */
	private long parse_end = 0;
	/** Timing data -- when did we end preliminaries */
	private long prelim_end = 0;
	/** Timing data -- when did we end checking for non-reduced productions */
	private long reduce_check_end = 0;
	/** Timing data -- when did we start */
	private long start_time = 0;
	/** Timing data -- when did we end table construction */
	private long table_end = 0;

	public Timings() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endAll()
	 */
	public void endAll() {
		final_time = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endBuild()
	 */
	public void endBuild() {
		build_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endCheck()
	 */
	public void endCheck() {
		check_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endDump()
	 */
	public void endDump() {
		dump_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endEmit()
	 */
	public void endEmit() {
		emit_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endFirstSets()
	 */
	public void endFirstSets() {
		first_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endNullability()
	 */
	public void endNullability() {
		nullability_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endParsing()
	 */
	public void endParsing() {
		parse_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endPreliminaries()
	 */
	public void endPreliminaries() {
		prelim_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endReducedChecking()
	 */
	public void endReducedChecking() {
		reduce_check_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endStateMachine()
	 */
	public void endStateMachine() {
		machine_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#endTables()
	 */
	public void endTables() {
		table_end = System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#show_times(java.io.PrintStream, java_cup.Emitter)
	 */
	public void show_times(PrintStream ps, final Emitter emitter) {
		final long total_time = final_time - start_time;

		ps.println(". . . . . . . . . . . . . . . . . . . . . . . . . ");
		ps.println("  Timing Summary");
		ps.println("    Total time       "
				+ timestr(final_time - start_time, total_time));
		ps.println("      Startup        "
				+ timestr(prelim_end - start_time, total_time));
		ps.println("      Parse          "
				+ timestr(parse_end - prelim_end, total_time));
		if (check_end != 0) {
			ps.println("      Checking       "
					+ timestr(check_end - parse_end, total_time));
		}
		if (check_end != 0 && build_end != 0) {
			ps.println("      Parser Build   "
					+ timestr(build_end - check_end, total_time));
		}
		if (nullability_end != 0 && check_end != 0) {
			ps.println("        Nullability  "
					+ timestr(nullability_end - check_end, total_time));
		}
		if (first_end != 0 && nullability_end != 0) {
			ps.println("        First sets   "
					+ timestr(first_end - nullability_end, total_time));
		}
		if (machine_end != 0 && first_end != 0) {
			ps.println("        State build  "
					+ timestr(machine_end - first_end, total_time));
		}
		if (table_end != 0 && machine_end != 0) {
			ps.println("        Table build  "
					+ timestr(table_end - machine_end, total_time));
		}
		if (reduce_check_end != 0 && table_end != 0) {
			ps.println("        Checking     "
					+ timestr(reduce_check_end - table_end, total_time));
		}
		if (emit_end != 0 && build_end != 0) {
			ps.println("      Code Output    "
					+ timestr(emit_end - build_end, total_time));
		}

		emitter.show_times(ps, total_time);

		ps.println("      Dump Output    "
				+ timestr(dump_end - emit_end, total_time));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java_cup.ITimings#start()
	 */
	public void start() {
		start_time = System.currentTimeMillis();
	}

}