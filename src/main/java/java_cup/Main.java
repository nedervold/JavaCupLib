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

	public static void main(final String argv[]) throws internal_error,
			IOException, Exception {
		new Main(argv);
	}

	/** Resulting parse action table. */
	private final Emitter emitter = new cup_emit();

	private final IErrorManager errorManager = new ErrorManager();

	private final Options options;

	/**
	 * The main driver for the system.
	 * 
	 * @param argv
	 *            an array of strings containing command line arguments.
	 */
	public Main(final String argv[]) throws internal_error,
			java.io.IOException, java.lang.Exception {
		/* process user options and arguments */
		options = new Options(argv, emitter);
		run();
	}

	private boolean buildParser(final Factories factories,
			final PrintStream progressStream,
			final PrintStream progressDebugStream, final ITimings timings)
			throws internal_error {
		return factories.checkBuildAndEmitParser(errorManager, emitter,
				options, progressStream, progressDebugStream, timings);
	}

	private void dump(final PrintStream dumpStream, final ITimings timings,
			final Factories factories) throws internal_error {
		factories.dump(dumpStream, options, timings);
	}

	private void parseGrammarSpecification(final ITimings timings,
			final Factories factories) throws Exception {
		factories.parse_grammar_spec(options.opt_do_debug, errorManager,
				emitter, timings);
	}

	private void run() throws Exception, internal_error {
		final NullPrintStream nps = new NullPrintStream();
		final PrintStream ps = System.err;
		final PrintStream dumpStream = ps;
		final PrintStream summaryStream = ps;
		final PrintStream progressStream = options.print_progress ? ps : nps;
		final PrintStream progressDebugStream = options.opt_do_debug
				|| options.print_progress ? ps : nps;

		final ITimings timings = new Timings();

		/*
		 * frankf 6/18/96 hackish, yes, but works
		 */
		emitter.set_lr_values(options.lr_values);
		emitter.set_locations(options.locations);
		emitter.set_xmlactions(options.xmlactions);
		emitter.set_genericlabels(options.genericlabels);
		/* open output set_xmlactionsfiles */

		progressStream.println("Opening files...");
		timings.endPreliminaries();

		/* parse spec into internal data structures */
		progressStream.println("Parsing specification from standard input...");

		final Factories factories = new Factories(errorManager, emitter);

		parseGrammarSpecification(timings, factories);

		final boolean did_output = buildParser(factories, progressStream,
				progressDebugStream, timings);

		dump(dumpStream, timings, factories);

		/* close input/output files */
		progressStream.println("Closing files...");

		timings.endAll();

		summarize(summaryStream, did_output, timings, factories);

		/*
		 * If there were errors during the run, exit with non-zero status
		 * (makefile-friendliness). --CSA
		 */
		errorManager.exitIfErrors(100);
	}

	private void summarize(final PrintStream summaryStream,
			final boolean did_output, final ITimings timings,
			final Factories factories) {
		/* produce a summary if desired */
		factories.emit_summary(summaryStream, did_output, emitter,
				errorManager, options, timings);
	}

}
