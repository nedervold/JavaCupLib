package java_cup;

import java.io.IOException;
import java.io.PrintStream;

/**
 * This class serves as the main driver for the JavaCup system. It accepts user
 * options and coordinates overall control flow. The main flow of control
 * includes the following activities:
 * <ul>
 * <li>Parse user supplied arguments and options.
 * <li>Open output files.
 * <li>Parse the specification from standard input.
 * <li>Check for unused terminals, non-terminals, and productions.
 * <li>Build the state machine, tables, etc.
 * <li>Output the generated code.
 * <li>Close output files.
 * <li>Print a summary if requested.
 * </ul>
 * 
 * Options to the main program include:
 * <dl>
 * <dt>-package name
 * <dd>specify package generated classes go in [default none]
 * <dt>-parser name
 * <dd>specify parser class name [default "parser"]
 * <dt>-symbols name
 * <dd>specify name for symbol constant class [default "sym"]
 * <dt>-interface
 * <dd>emit symbol constant <i>interface</i>, rather than class
 * <dt>-nonterms
 * <dd>put non terminals in symbol constant class
 * <dt>-expect #
 * <dd>number of conflicts expected/allowed [default 0]
 * <dt>-compact_red
 * <dd>compact tables by defaulting to most frequent reduce
 * <dt>-nowarn
 * <dd>don't warn about useless productions, etc.
 * <dt>-nosummary
 * <dd>don't print the usual summary of parse states, etc.
 * <dt>-progress
 * <dd>print messages to indicate progress of the system
 * <dt>-time
 * <dd>print time usage summary
 * <dt>-dump_grammar
 * <dd>produce a dump of the symbols and grammar
 * <dt>-dump_states
 * <dd>produce a dump of parse state machine
 * <dt>-dump_tables
 * <dd>produce a dump of the parse tables
 * <dt>-dump
 * <dd>produce a dump of all of the above
 * <dt>-debug
 * <dd>turn on debugging messages within JavaCup
 * <dt>-nopositions
 * <dd>don't generate the positions code
 * <dt>-locations
 * <dd>generate handles xleft/xright for symbol positions in actions
 * <dt>-noscanner
 * <dd>don't refer to java_cup.runtime.Scanner in the parser (for compatibility
 * with old runtimes)
 * <dt>-version
 * <dd>print version information for JavaCUP and halt.
 * </dl>
 * 
 * @version last updated: 7/3/96
 * @author Frank Flannery
 */

public class Main {

	/* Additional timing information is also collected in emit */

	private static boolean emit(ProgressPrinter pp, final Factories factories,
			final Options options, final Emitter emitter,
			final IErrorManager errorManager) throws internal_error {
		/* output the generated code, if # of conflicts permits */
		if (errorManager.getErrorCount() != 0) {
			// conflicts! don't emit code, don't dump tables.
			options.opt_dump_tables = false;
			return false;
		} else { // everything's okay, emit parser.
			pp.printProgress("Writing parser...");

			emitter.emit_parser(factories, options);
			return true;
		}
	}

	public static void main(final String argv[]) throws internal_error,
			IOException, Exception {
		new Main(argv);
	}

	/** Resulting parse action table. */
	private final Emitter emitter = new cup_emit();

	private final IErrorManager errorManager = new ErrorManager();

	/** Input file. This is a buffered version of System.in. */

	private final Options options;

	/**
	 * The main driver for the system.
	 * 
	 * @param argv
	 *            an array of strings containing command line arguments.
	 */
	public Main(final String argv[]) throws internal_error,
			java.io.IOException, java.lang.Exception {
		final PrintStream ps = System.err;
		boolean did_output = false;
		Timings timings = new Timings();
		timings.start_time = System.currentTimeMillis();

		/* process user options and arguments */
		options = new Options(argv, emitter);

		ProgressPrinter pp = options.print_progress ? new PrintStreamProgressPrinter(
				ps) : new NullProgressPrinter();
		ProgressPrinter pp2 = (options.opt_do_debug || options.print_progress) ? new PrintStreamProgressPrinter(
				ps) : new NullProgressPrinter();

		/*
		 * frankf 6/18/96 hackish, yes, but works
		 */
		emitter.set_lr_values(options.lr_values);
		emitter.set_locations(options.locations);
		emitter.set_xmlactions(options.xmlactions);
		emitter.set_genericlabels(options.genericlabels);
		/* open output set_xmlactionsfiles */

		pp.printProgress("Opening files...");

		timings.prelim_end = System.currentTimeMillis();

		/* parse spec into internal data structures */
		pp.printProgress("Parsing specification from standard input...");

		final Factories factories = new Factories(errorManager, emitter);
		factories.parse_grammar_spec(options.opt_do_debug, errorManager,
				emitter);

		timings.parse_end = System.currentTimeMillis();

		/* don't proceed unless we are error free */
		if (errorManager.getErrorCount() == 0) {
			/* check for unused bits */
			pp.printProgress("Checking specification...");
			factories.check_unused(errorManager, emitter);

			timings.check_end = System.currentTimeMillis();

			/* build the state machine and parse tables */
			pp.printProgress("Building parse tables...");

			factories.build_parser(pp2, errorManager, emitter, options, timings);

			did_output = emit(pp, factories, options, emitter, errorManager);
		}
		/* fix up the times to make the summary easier */
		timings.emit_end = System.currentTimeMillis();

		factories.dump(ps, options);

		timings.dump_end = System.currentTimeMillis();

		/* close input/output files */
		pp.printProgress("Closing files...");

		timings.final_time = System.currentTimeMillis();

		/* produce a summary if desired */
		factories.emit_summary(ps, did_output, emitter, errorManager, options,
				timings);

		/*
		 * If there were errors during the run, exit with non-zero status
		 * (makefile-friendliness). --CSA
		 */
		errorManager.exit_on_errors(100);
	}

	static class NullProgressPrinter implements ProgressPrinter {
		public void printProgress(String msg) {
			// Do nothing
		}
	}

	static class PrintStreamProgressPrinter implements ProgressPrinter {
		public PrintStreamProgressPrinter(PrintStream ps) {
			super();
			this.ps = ps;
		}

		final PrintStream ps;

		public void printProgress(String msg) {
			ps.println(msg);
		}
	}

}
