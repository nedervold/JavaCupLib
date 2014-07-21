package java_cup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Enumeration;

import java_cup.runtime.ComplexSymbolFactory;

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

	private final Options options;
	
	/*----------------------------------------------------------------------*/
	/* Timing data (not all of these time intervals are mutually exclusive) */
	/*----------------------------------------------------------------------*/
	/** Timing data -- when did we start */
	protected long start_time = 0;
	/** Timing data -- when did we end preliminaries */
	protected long prelim_end = 0;
	/** Timing data -- when did we end parsing */
	protected long parse_end = 0;
	/** Timing data -- when did we end checking */
	protected long check_end = 0;
	/** Timing data -- when did we end dumping */
	protected long dump_end = 0;
	/** Timing data -- when did we end state and table building */
	protected long build_end = 0;
	/** Timing data -- when did we end nullability calculation */
	protected long nullability_end = 0;
	/** Timing data -- when did we end first set calculation */
	protected long first_end = 0;
	/** Timing data -- when did we end state machine construction */
	protected long machine_end = 0;
	/** Timing data -- when did we end table construction */
	protected long table_end = 0;
	/** Timing data -- when did we end checking for non-reduced productions */
	protected long reduce_check_end = 0;
	/** Timing data -- when did we finish emitting code */
	protected long emit_end = 0;
	/** Timing data -- when were we completely done */
	protected long final_time = 0;

	/* Additional timing information is also collected in emit */

	/* Factories */
	private IErrorManager errorManager = new ErrorManager();;
	private LalrStateFactory lalrStateFactory = new LalrStateFactory();
	private Emitter emitter = new cup_emit();
	private TerminalFactory terminalFactory = new TerminalFactory(errorManager);
	private NonTerminalFactory nonTerminalFactory = new NonTerminalFactory(
			errorManager, terminalFactory);
	private ProductionFactory productionFactory = new ProductionFactory(
			errorManager, terminalFactory, nonTerminalFactory, emitter);

	/*-----------------------------------------------------------*/
	/*--- Main Program ------------------------------------------*/
	/*-----------------------------------------------------------*/

	public static void main(String argv[]) throws internal_error, IOException,
			Exception {
		new Main(argv);
	}

	/**
	 * The main driver for the system.
	 * 
	 * @param argv
	 *            an array of strings containing command line arguments.
	 */
	public Main(String argv[]) throws internal_error, java.io.IOException,
			java.lang.Exception {
		boolean did_output = false;

		start_time = System.currentTimeMillis();

		/* process user options and arguments */
		options = new Options(argv, emitter);

		/*
		 * frankf 6/18/96 hackish, yes, but works
		 */
		emitter.set_lr_values(options.lr_values);
		emitter.set_locations(options.locations);
		emitter.set_xmlactions(options.xmlactions);
		emitter.set_genericlabels(options.genericlabels);
		/* open output set_xmlactionsfiles */
		if (options.print_progress)
			System.err.println("Opening files...");
		/* use a buffered version of standard input */
		input_file = new BufferedInputStream(System.in);

		prelim_end = System.currentTimeMillis();

		/* parse spec into internal data structures */
		if (options.print_progress)
			System.err.println("Parsing specification from standard input...");
		parse_grammar_spec(options.opt_do_debug, errorManager, emitter, terminalFactory,
				nonTerminalFactory, productionFactory);

		parse_end = System.currentTimeMillis();

		/* don't proceed unless we are error free */
		if (errorManager.getErrorCount() == 0) {
			/* check for unused bits */
			if (options.print_progress)
				System.err.println("Checking specification...");
			check_unused(errorManager, emitter, terminalFactory, nonTerminalFactory);

			check_end = System.currentTimeMillis();

			/* build the state machine and parse tables */
			if (options.print_progress)
				System.err.println("Building parse tables...");
			build_parser();

			build_end = System.currentTimeMillis();

			/* output the generated code, if # of conflicts permits */
			if (errorManager.getErrorCount() != 0) {
				// conflicts! don't emit code, don't dump tables.
				options.opt_dump_tables = false;
			} else { // everything's okay, emit parser.
				if (options.print_progress)
					System.err.println("Writing parser...");
				emitter.emit_parser(terminalFactory, nonTerminalFactory,
						productionFactory, options.dest_dir, action_table,
						reduce_table, start_state, options.include_non_terms,
						options.opt_compact_red, options.suppress_scanner, options.sym_interface);
				did_output = true;
			}
		}
		/* fix up the times to make the summary easier */
		emit_end = System.currentTimeMillis();

		/* do requested dumps */
		if (options.opt_dump_grammar)
			dump_grammar(terminalFactory, nonTerminalFactory, productionFactory);
		if (options.opt_dump_states)
			dump_machine(lalrStateFactory, start_state);
		if (options.opt_dump_tables)
			dump_tables(action_table, reduce_table);

		dump_end = System.currentTimeMillis();

		/* close input/output files */
		if (options.print_progress)
			System.err.println("Closing files...");
		if (input_file != null)
			input_file.close();

		/* produce a summary if desired */
		if (!options.no_summary)
			emit_summary(did_output);

		/*
		 * If there were errors during the run, exit with non-zero status
		 * (makefile-friendliness). --CSA
		 */
		if (errorManager.getErrorCount() != 0)
			System.exit(100);
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/*-------*/
	/* Files */
	/*-------*/

	/** Input file. This is a buffered version of System.in. */
	protected BufferedInputStream input_file;

	

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Parse the grammar specification from standard input. This produces sets
	 * of terminal, non-terminals, and productions which can be accessed via
	 * variables of the respective classes, as well as the setting of various
	 * variables (mostly in the emit class) for small user supplied items such
	 * as the code to scan with.
	 */
	protected static void parse_grammar_spec(boolean do_debug,
			IErrorManager errorManager, Emitter emit,
			TerminalFactory terminalFactory,
			NonTerminalFactory nonTerminalFactory,
			ProductionFactory productionFactory) throws java.lang.Exception {
		/* create a parser and parse with it */
		java_cup.runtime.lr_parser parser_obj = createParser(errorManager,
				emit, terminalFactory, nonTerminalFactory, productionFactory);

		try {
			if (do_debug)
				parser_obj.debug_parse();
			else
				parser_obj.parse();
		} catch (Exception e) {
			/*
			 * something threw an exception. catch it and emit a message so we
			 * have a line number to work with, then re-throw it
			 */
			errorManager.emit_error("Internal error: Unexpected exception");
			throw e;
		}
	}

	private static java_cup.runtime.lr_parser createParser(
			IErrorManager errorManager, Emitter emit,
			TerminalFactory terminalFactory,
			NonTerminalFactory nonTerminalFactory,
			ProductionFactory productionFactory) {
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		Lexer lexer = new Lexer(csf);
		lexer.errorManager = errorManager;
		parser result = new parser(lexer, csf);
		result.errorManager = errorManager;
		result.emitter = emit;
		result.productionFactory = productionFactory;
		result.nonTerminalFactory = nonTerminalFactory;
		result.terminalFactory = terminalFactory;
		return result;
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Check for unused symbols. Unreduced productions get checked when tables
	 * are created.
	 */
	protected static void check_unused(IErrorManager errorManager, Emitter emit,
			TerminalFactory terminalFactory, NonTerminalFactory nonTerminalFactory) {
		terminal term;
		non_terminal nt;

		/* check for unused terminals */
		for (Enumeration<terminal> t = terminalFactory.all(); t
				.hasMoreElements();) {
			term = (terminal) t.nextElement();

			/* don't issue a message for EOF */
			if (term == terminalFactory.EOF)
				continue;

			/* or error */
			if (term == terminalFactory.error)
				continue;

			/* is this one unused */
			if (term.use_count() == 0) {
				/* count it and warn if we are doing warnings */
				emit.set_unused_term(emit.unused_term() + 1);
				if (!emit.nowarn()) {
					errorManager.emit_warning("Terminal \"" + term.name()
							+ "\" was declared but never used");
				}
			}
		}

		/* check for unused non terminals */
		for (Enumeration<non_terminal> n = nonTerminalFactory.all(); n
				.hasMoreElements();) {
			nt = (non_terminal) n.nextElement();

			/* is this one unused */
			if (nt.use_count() == 0) {
				/* count and warn if we are doing warnings */
				emit.set_unused_non_term(emit.unused_non_term() + 1);
				if (!emit.nowarn()) {
					errorManager.emit_warning("Non terminal \"" + nt.name()
							+ "\" was declared but never used");
				}
			}
		}

	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . */
	/* . . Internal Results of Generating the Parser . . */
	/* . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Start state in the overall state machine. */
	protected lalr_state start_state;

	/** Resulting parse action table. */
	protected parse_action_table action_table;

	/** Resulting reduce-goto table. */
	protected parse_reduce_table reduce_table;

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Build the (internal) parser from the previously parsed specification.
	 * This includes:
	 * <ul>
	 * <li>Computing nullability of non-terminals.
	 * <li>Computing first sets of non-terminals and productions.
	 * <li>Building the viable prefix recognizer machine.
	 * <li>Filling in the (internal) parse tables.
	 * <li>Checking for unreduced productions.
	 * </ul>
	 */
	protected void build_parser() throws internal_error {
		/* compute nullability of all non terminals */
		if (options.opt_do_debug || options.print_progress)
			System.err.println("  Computing non-terminal nullability...");
		nonTerminalFactory.compute_nullability(productionFactory);

		nullability_end = System.currentTimeMillis();

		/* compute first sets of all non terminals */
		if (options.opt_do_debug || options.print_progress)
			System.err.println("  Computing first sets...");
		nonTerminalFactory.compute_first_sets();

		first_end = System.currentTimeMillis();

		/* build the LR viable prefix recognition machine */
		if (options.opt_do_debug || options.print_progress)
			System.err.println("  Building state machine...");
		start_state = lalrStateFactory.build_machine(errorManager,
				terminalFactory, emitter.start_production());

		machine_end = System.currentTimeMillis();

		/* build the LR parser action and reduce-goto tables */
		if (options.opt_do_debug || options.print_progress)
			System.err.println("  Filling in tables...");
		action_table = new parse_action_table(terminalFactory,
				lalrStateFactory.number());
		reduce_table = new parse_reduce_table(nonTerminalFactory,
				lalrStateFactory.number());
		for (Enumeration<lalr_state> st = lalrStateFactory.all(); st
				.hasMoreElements();) {
			lalr_state lst = (lalr_state) st.nextElement();
			lst.build_table_entries(errorManager, terminalFactory, emitter,
					action_table, reduce_table);
		}

		table_end = System.currentTimeMillis();

		/* check and warn for non-reduced productions */
		if (options.opt_do_debug || options.print_progress)
			System.err.println("  Checking for non-reduced productions...");
		action_table.check_reductions(errorManager, terminalFactory,
				productionFactory, emitter);

		reduce_check_end = System.currentTimeMillis();

		/* if we have more conflicts than we expected issue a message and die */
		if (emitter.num_conflicts() > options.expect_conflicts) {
			errorManager
					.emit_error("*** More conflicts encountered than expected "
							+ "-- parser generation aborted");
			// indicate the problem.
			// we'll die on return, after clean up.
		}
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Helper routine to optionally return a plural or non-plural ending.
	 * 
	 * @param val
	 *            the numerical value determining plurality.
	 */
	protected static String plural(int val) {
		if (val == 1)
			return "";
		else
			return "s";
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Emit a long summary message to standard error (System.err) which
	 * summarizes what was found in the specification, how many states were
	 * produced, how many conflicts were found, etc. A detailed timing summary
	 * is also produced if it was requested by the user.
	 * 
	 * @param output_produced
	 *            did the system get far enough to generate code.
	 */
	protected void emit_summary(boolean output_produced) {
		final_time = System.currentTimeMillis();

		if (options.no_summary)
			return;

		System.err.println("------- " + version.title_str
				+ " Parser Generation Summary -------");

		/* error and warning count */
		System.err.println("  " + errorManager.getErrorCount() + " error"
				+ plural(errorManager.getErrorCount()) + " and "
				+ errorManager.getWarningCount() + " warning"
				+ plural(errorManager.getWarningCount()));

		/* basic stats */
		System.err.print("  " + terminalFactory.number() + " terminal"
				+ plural(terminalFactory.number()) + ", ");
		System.err.print(nonTerminalFactory.number() + " non-terminal"
				+ plural(nonTerminalFactory.number()) + ", and ");
		System.err.println(productionFactory.number() + " production"
				+ plural(productionFactory.number()) + " declared, ");
		System.err.println("  producing " + lalrStateFactory.number()
				+ " unique parse states.");

		/* unused symbols */
		System.err.println("  " + emitter.unused_term() + " terminal"
				+ plural(emitter.unused_term()) + " declared but not used.");
		System.err.println("  " + emitter.unused_non_term() + " non-terminal"
				+ plural(emitter.unused_term()) + " declared but not used.");

		/* productions that didn't reduce */
		System.err.println("  " + emitter.not_reduced() + " production"
				+ plural(emitter.not_reduced()) + " never reduced.");

		/* conflicts */
		System.err.println("  " + emitter.num_conflicts() + " conflict"
				+ plural(emitter.num_conflicts()) + " detected" + " ("
				+ options.expect_conflicts + " expected).");

		/* code location */
		if (output_produced)
			System.err.println("  Code written to \""
					+ emitter.parser_class_name() + ".java\", and \""
					+ emitter.symbol_const_class_name() + ".java\".");
		else
			System.err.println("  No code produced.");

		if (options.opt_show_timing)
			show_times();

		System.err
				.println("---------------------------------------------------- ("
						+ version.title_str + ")");
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Produce the optional timing summary as part of an overall summary. */
	protected void show_times() {
		long total_time = final_time - start_time;

		System.err
				.println(". . . . . . . . . . . . . . . . . . . . . . . . . ");
		System.err.println("  Timing Summary");
		System.err.println("    Total time       "
				+ timestr(final_time - start_time, total_time));
		System.err.println("      Startup        "
				+ timestr(prelim_end - start_time, total_time));
		System.err.println("      Parse          "
				+ timestr(parse_end - prelim_end, total_time));
		if (check_end != 0)
			System.err.println("      Checking       "
					+ timestr(check_end - parse_end, total_time));
		if (check_end != 0 && build_end != 0)
			System.err.println("      Parser Build   "
					+ timestr(build_end - check_end, total_time));
		if (nullability_end != 0 && check_end != 0)
			System.err.println("        Nullability  "
					+ timestr(nullability_end - check_end, total_time));
		if (first_end != 0 && nullability_end != 0)
			System.err.println("        First sets   "
					+ timestr(first_end - nullability_end, total_time));
		if (machine_end != 0 && first_end != 0)
			System.err.println("        State build  "
					+ timestr(machine_end - first_end, total_time));
		if (table_end != 0 && machine_end != 0)
			System.err.println("        Table build  "
					+ timestr(table_end - machine_end, total_time));
		if (reduce_check_end != 0 && table_end != 0)
			System.err.println("        Checking     "
					+ timestr(reduce_check_end - table_end, total_time));
		if (emit_end != 0 && build_end != 0)
			System.err.println("      Code Output    "
					+ timestr(emit_end - build_end, total_time));
		if (emitter.symbols_time() != 0)
			System.err.println("        Symbols      "
					+ timestr(emitter.symbols_time(), total_time));
		if (emitter.parser_time() != 0)
			System.err.println("        Parser class "
					+ timestr(emitter.parser_time(), total_time));
		if (emitter.action_code_time() != 0)
			System.err.println("          Actions    "
					+ timestr(emitter.action_code_time(), total_time));
		if (emitter.production_table_time() != 0)
			System.err.println("          Prod table "
					+ timestr(emitter.production_table_time(), total_time));
		if (emitter.action_table_time() != 0)
			System.err.println("          Action tab "
					+ timestr(emitter.action_table_time(), total_time));
		if (emitter.goto_table_time() != 0)
			System.err.println("          Reduce tab "
					+ timestr(emitter.goto_table_time(), total_time));

		System.err.println("      Dump Output    "
				+ timestr(dump_end - emit_end, total_time));
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

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
	protected static String timestr(long time_val, long total_time) {
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

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Produce a human readable dump of the grammar. */
	public static void dump_grammar(TerminalFactory terminalFactory,
			NonTerminalFactory nonTerminalFactory,
			ProductionFactory productionFactory) throws internal_error {
		System.err.println("===== Terminals =====");
		for (int tidx = 0, cnt = 0; tidx < terminalFactory.number(); tidx++, cnt++) {
			System.err.print("[" + tidx + "]"
					+ terminalFactory.find(tidx).name() + " ");
			if ((cnt + 1) % 5 == 0)
				System.err.println();
		}
		System.err.println();
		System.err.println();

		System.err.println("===== Non terminals =====");
		for (int nidx = 0, cnt = 0; nidx < nonTerminalFactory.number(); nidx++, cnt++) {
			System.err.print("[" + nidx + "]"
					+ nonTerminalFactory.find(nidx).name() + " ");
			if ((cnt + 1) % 5 == 0)
				System.err.println();
		}
		System.err.println();
		System.err.println();

		System.err.println("===== Productions =====");
		for (int pidx = 0; pidx < productionFactory.number(); pidx++) {
			production prod = productionFactory.find(pidx);
			System.err.print("[" + pidx + "] " + prod.lhs().the_symbol().name()
					+ " ::= ");
			for (int i = 0; i < prod.rhs_length(); i++)
				if (prod.rhs(i).is_action())
					System.err.print("{action} ");
				else
					System.err.print(((symbol_part) prod.rhs(i)).the_symbol()
							.name() + " ");
			System.err.println();
		}
		System.err.println();
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/**
	 * Produce a (semi-) human readable dump of the complete viable prefix
	 * recognition state machine.
	 */
	public static void dump_machine(LalrStateFactory lalrStateFactory,
			lalr_state start_state) {
		lalr_state ordered[] = new lalr_state[lalrStateFactory.number()];

		/* put the states in sorted order for a nicer display */
		for (Enumeration<lalr_state> s = lalrStateFactory.all(); s
				.hasMoreElements();) {
			lalr_state st = (lalr_state) s.nextElement();
			ordered[st.index()] = st;
		}

		System.err.println("===== Viable Prefix Recognizer =====");
		for (int i = 0; i < lalrStateFactory.number(); i++) {
			if (ordered[i] == start_state)
				System.err.print("START ");
			System.err.println(ordered[i]);
			System.err.println("-------------------");
		}
	}

	/* . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . */

	/** Produce a (semi-) human readable dumps of the parse tables */
	public static void dump_tables(parse_action_table action_table,
			parse_reduce_table reduce_table) {
		System.err.println(action_table);
		System.err.println(reduce_table);
	}

	/*-----------------------------------------------------------*/

}
