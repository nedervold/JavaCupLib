package java_cup;

public class Timings {

	public  void show_times(Emitter emitter) {
		final long total_time = final_time - start_time;
		
		System.err
				.println(". . . . . . . . . . . . . . . . . . . . . . . . . ");
		System.err.println("  Timing Summary");
		System.err.println("    Total time       "
				+ Main.timestr(final_time - start_time, total_time));
		System.err.println("      Startup        "
				+ Main.timestr(prelim_end - start_time, total_time));
		System.err.println("      Parse          "
				+ Main.timestr(parse_end - prelim_end, total_time));
		if (check_end != 0) {
			System.err
					.println("      Checking       "
							+ Main.timestr(check_end - parse_end,
									total_time));
		}
		if (check_end != 0 && build_end != 0) {
			System.err
					.println("      Parser Build   "
							+ Main.timestr(build_end - check_end,
									total_time));
		}
		if (nullability_end != 0 && check_end != 0) {
			System.err.println("        Nullability  "
					+ Main.timestr(nullability_end - check_end,
							total_time));
		}
		if (first_end != 0 && nullability_end != 0) {
			System.err.println("        First sets   "
					+ Main.timestr(first_end - nullability_end,
							total_time));
		}
		if (machine_end != 0 && first_end != 0) {
			System.err.println("        State build  "
					+ Main.timestr(machine_end - first_end,
							total_time));
		}
		if (table_end != 0 && machine_end != 0) {
			System.err.println("        Table build  "
					+ Main.timestr(table_end - machine_end, total_time));
		}
		if (reduce_check_end != 0 && table_end != 0) {
			System.err.println("        Checking     "
					+ Main.timestr(reduce_check_end - table_end, total_time));
		}
		if (emit_end != 0 && build_end != 0) {
			System.err.println("      Code Output    "
					+ Main.timestr(emit_end - build_end, total_time));
		}
		
		emitter.show_times(total_time);
		
		System.err.println("      Dump Output    "
				+ Main.timestr(dump_end - emit_end, total_time));
	}

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
	public static String timestr(long time_val, long total_time) {
		boolean neg;
		long ms = 0;
		long sec = 0;
		long percent10;
		String pad;

		/* work with positives only */
		neg = time_val < 0;
		if (neg)
			time_val = -time_val;

		/* pull out seconds and ms */
		ms = time_val % 1000;
		sec = time_val / 1000;

		/* construct a pad to blank fill seconds out to 4 places */
		if (sec < 10)
			pad = "   ";
		else if (sec < 100)
			pad = "  ";
		else if (sec < 1000)
			pad = " ";
		else
			pad = "";

		/* calculate 10 times the percentage of total */
		percent10 = (time_val * 1000) / total_time;

		/* build and return the output string */
		return (neg ? "-" : "") + pad + sec + "." + ((ms % 1000) / 100)
				+ ((ms % 100) / 10) + (ms % 10) + "sec" + " (" + percent10 / 10
				+ "." + percent10 % 10 + "%)";
	}

	/** Timing data -- when did we start */
	public long start_time = 0;
	/** Timing data -- when did we end preliminaries */
	public long prelim_end = 0;
	/** Timing data -- when did we end parsing */
	public long parse_end = 0;
	/** Timing data -- when did we end checking */
	public long check_end = 0;
	/** Timing data -- when did we end dumping */
	public long dump_end = 0;
	/** Timing data -- when did we end state and table building */
	public long build_end = 0;
	/** Timing data -- when did we end nullability calculation */
	public long nullability_end = 0;
	/** Timing data -- when did we end first set calculation */
	public long first_end = 0;
	/** Timing data -- when did we end state machine construction */
	public long machine_end = 0;
	/** Timing data -- when did we end table construction */
	public long table_end = 0;
	/** Timing data -- when did we end checking for non-reduced productions */
	public long reduce_check_end = 0;
	/** Timing data -- when did we finish emitting code */
	public long emit_end = 0;
	/** Timing data -- when were we completely done */
	public long final_time = 0;

	public Timings() {
		super();
	}

}