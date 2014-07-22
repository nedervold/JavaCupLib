package java_cup;

import java.io.IOException;
import java.util.Enumeration;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.lr_parser;

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

public class Main extends Timings {

	public static class Factories {
		/**
		 * Helper routine to optionally return a plural or non-plural ending.
		 * Used only by emit_summary().
		 * 
		 * @param val
		 *            the numerical value determining plurality.
		 */
		private static String plural(final int val) {
			if (val == 1) {
				return "";
			} else {
				return "s";
			}
		}

		public void check_unused(final IErrorManager errorManager,
				final Emitter emit) {
			terminal term;
			non_terminal nt;

			/* check for unused terminals */
			for (final Enumeration<terminal> t = terminalFactory.all(); t
					.hasMoreElements();) {
				term = t.nextElement();

				/* don't issue a message for EOF */
				if (term == terminalFactory.EOF) {
					continue;
				}

				/* or error */
				if (term == terminalFactory.error) {
					continue;
				}

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
			for (final Enumeration<non_terminal> n = nonTerminalFactory.all(); n
					.hasMoreElements();) {
				nt = n.nextElement();

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

		public parse_action_table action_table;

		/** Produce a human readable dump of the grammar. */
		public void dump_grammar() throws internal_error {
			TerminalFactory terminalFactory = this.terminalFactory;
			NonTerminalFactory nonTerminalFactory = this.nonTerminalFactory;
			ProductionFactory productionFactory = this.productionFactory;
			System.err.println("===== Terminals =====");
			for (int tidx = 0, cnt = 0; tidx < terminalFactory.number(); tidx++, cnt++) {
				System.err.print("[" + tidx + "]"
						+ terminalFactory.find(tidx).name() + " ");
				if ((cnt + 1) % 5 == 0) {
					System.err.println();
				}
			}
			System.err.println();
			System.err.println();

			System.err.println("===== Non terminals =====");
			for (int nidx = 0, cnt = 0; nidx < nonTerminalFactory.number(); nidx++, cnt++) {
				System.err.print("[" + nidx + "]"
						+ nonTerminalFactory.find(nidx).name() + " ");
				if ((cnt + 1) % 5 == 0) {
					System.err.println();
				}
			}
			System.err.println();
			System.err.println();

			System.err.println("===== Productions =====");
			for (int pidx = 0; pidx < productionFactory.number(); pidx++) {
				final production prod = productionFactory.find(pidx);
				System.err.print("[" + pidx + "] "
						+ prod.lhs().the_symbol().name() + " ::= ");
				for (int i = 0; i < prod.rhs_length(); i++) {
					if (prod.rhs(i).is_action()) {
						System.err.print("{action} ");
					} else {
						System.err.print(((symbol_part) prod.rhs(i))
								.the_symbol().name() + " ");
					}
				}
				System.err.println();
			}
			System.err.println();
		}

		/**
		 * Produce a (semi-) human readable dump of the complete viable prefix
		 * recognition state machine.
		 */
		public void dump_machine() {
			LalrStateFactory lalrStateFactory = this.lalrStateFactory;
			final lalr_state ordered[] = new lalr_state[lalrStateFactory
					.number()];

			/* put the states in sorted order for a nicer display */
			for (final Enumeration<lalr_state> s = lalrStateFactory.all(); s
					.hasMoreElements();) {
				final lalr_state st = s.nextElement();
				ordered[st.index()] = st;
			}

			System.err.println("===== Viable Prefix Recognizer =====");
			for (int i = 0; i < lalrStateFactory.number(); i++) {
				if (ordered[i] == this.start_state) {
					System.err.print("START ");
				}
				System.err.println(ordered[i]);
				System.err.println("-------------------");
			}
		}

		public  void dump(final Options options)
				throws internal_error {
			/* do requested dumps */
			if (options.opt_dump_grammar) {
				dump_grammar();
			}
			if (options.opt_dump_states) {
				dump_machine();
			}
			if (options.opt_dump_tables) {
				dump_tables();
			}
		}

		/** Produce a (semi-) human readable dumps of the parse tables */
		public void dump_tables() {
			System.err.println(this.action_table);
			System.err.println(this.reduce_table);
		}

		public final LalrStateFactory lalrStateFactory;

		public final NonTerminalFactory nonTerminalFactory;

		public final ProductionFactory productionFactory;

		/** Resulting reduce-goto table. */
		public parse_reduce_table reduce_table;

		/** Start state in the overall state machine. */
		public lalr_state start_state;

		public final TerminalFactory terminalFactory;

		public Factories(final IErrorManager errorManager, final Emitter emitter) {
			lalrStateFactory = new LalrStateFactory();

			terminalFactory = new TerminalFactory(errorManager);

			nonTerminalFactory = new NonTerminalFactory(errorManager,
					terminalFactory);

			productionFactory = new ProductionFactory(errorManager,
					terminalFactory, nonTerminalFactory, emitter);
		}

		public void build_parser(final IErrorManager errorManager,
				final Emitter emitter, final Options options,
				final Timings timings) throws internal_error {
			nonTerminalFactory
					.build_parser(productionFactory, options, timings);

			/* build the LR viable prefix recognition machine */
			if (options.opt_do_debug || options.print_progress) {
				System.err.println("  Building state machine...");
			}
			start_state = lalrStateFactory.build_machine(errorManager,
					terminalFactory, emitter.start_production());

			timings.machine_end = System.currentTimeMillis();

			/* build the LR parser action and reduce-goto tables */
			if (options.opt_do_debug || options.print_progress) {
				System.err.println("  Filling in tables...");
			}
			action_table = new parse_action_table(terminalFactory,
					lalrStateFactory.number());
			reduce_table = new parse_reduce_table(nonTerminalFactory,
					lalrStateFactory.number());
			for (final Enumeration<lalr_state> st = lalrStateFactory.all(); st
					.hasMoreElements();) {
				final lalr_state lst = st.nextElement();
				lst.build_table_entries(errorManager, terminalFactory, emitter,
						action_table, reduce_table);
			}

			timings.table_end = System.currentTimeMillis();

			/* check and warn for non-reduced productions */
			if (options.opt_do_debug || options.print_progress) {
				System.err.println("  Checking for non-reduced productions...");
			}
			action_table.check_reductions(errorManager, terminalFactory,
					productionFactory, emitter);

			timings.reduce_check_end = System.currentTimeMillis();

			/*
			 * if we have more conflicts than we expected issue a message and
			 * die
			 */
			if (emitter.num_conflicts() > options.expect_conflicts) {
				errorManager
						.emit_error("*** More conflicts encountered than expected "
								+ "-- parser generation aborted");
				// indicate the problem.
				// we'll die on return, after clean up.
			}

			timings.build_end = System.currentTimeMillis();
		}

		private final lr_parser createParser(final IErrorManager errorManager,
				final Emitter emit) {
			final ComplexSymbolFactory csf = new ComplexSymbolFactory();
			final Lexer lexer = new Lexer(csf);
			lexer.errorManager = errorManager;
			final parser result = new parser(lexer, csf);
			result.errorManager = errorManager;
			result.emitter = emit;
			result.productionFactory = productionFactory;
			result.nonTerminalFactory = nonTerminalFactory;
			result.terminalFactory = terminalFactory;
			return result;
		}

		public void emit_summary(final boolean output_produced,
				final Emitter emitter, final IErrorManager errorManager,
				final Options options, final Timings timings) {

			if (!options.no_summary) {

				System.err.println("------- " + version.title_str
						+ " Parser Generation Summary -------");

				/* error and warning count */
				System.err.println("  " + errorManager.getErrorCount()
						+ " error" + plural(errorManager.getErrorCount())
						+ " and " + errorManager.getWarningCount() + " warning"
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
						+ plural(emitter.unused_term())
						+ " declared but not used.");
				System.err.println("  " + emitter.unused_non_term()
						+ " non-terminal" + plural(emitter.unused_term())
						+ " declared but not used.");

				/* productions that didn't reduce */
				System.err.println("  " + emitter.not_reduced() + " production"
						+ plural(emitter.not_reduced()) + " never reduced.");

				/* conflicts */
				System.err.println("  " + emitter.num_conflicts() + " conflict"
						+ plural(emitter.num_conflicts()) + " detected" + " ("
						+ options.expect_conflicts + " expected).");

				/* code location */
				if (output_produced) {
					System.err.println("  Code written to \""
							+ emitter.parser_class_name() + ".java\", and \""
							+ emitter.symbol_const_class_name() + ".java\".");
				} else {
					System.err.println("  No code produced.");
				}

				if (options.opt_show_timing) {
					timings.show_times(emitter);
				}

				System.err
						.println("---------------------------------------------------- ("
								+ version.title_str + ")");
			}
		}

		public void parse_grammar_spec(final boolean do_debug,
				final IErrorManager errorManager, final Emitter emit)
				throws java.lang.Exception {
			/* create a parser and parse with it */
			final java_cup.runtime.lr_parser parser_obj = createParser(
					errorManager, emit);

			try {
				if (do_debug) {
					parser_obj.debug_parse();
				} else {
					parser_obj.parse();
				}
			} catch (final Exception e) {
				/*
				 * something threw an exception. catch it and emit a message so
				 * we have a line number to work with, then re-throw it
				 */
				errorManager.emit_error("Internal error: Unexpected exception");
				throw e;
			}
		}
	}

	/* Additional timing information is also collected in emit */

	private static boolean emit(final Factories factories,
			final Options options, final Emitter emitter,
			final IErrorManager errorManager) throws internal_error {
		/* output the generated code, if # of conflicts permits */
		if (errorManager.getErrorCount() != 0) {
			// conflicts! don't emit code, don't dump tables.
			options.opt_dump_tables = false;
			return false;
		} else { // everything's okay, emit parser.
			if (options.print_progress) {
				System.err.println("Writing parser...");
			}
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
		if (options.print_progress) {
			System.err.println("Opening files...");
		}

		prelim_end = System.currentTimeMillis();

		/* parse spec into internal data structures */
		if (options.print_progress) {
			System.err.println("Parsing specification from standard input...");
		}

		final Factories factories = new Factories(errorManager, emitter);
		factories.parse_grammar_spec(options.opt_do_debug, errorManager,
				emitter);

		parse_end = System.currentTimeMillis();

		/* don't proceed unless we are error free */
		if (errorManager.getErrorCount() == 0) {
			/* check for unused bits */
			if (options.print_progress) {
				System.err.println("Checking specification...");
			}
			factories.check_unused(errorManager, emitter);

			check_end = System.currentTimeMillis();

			/* build the state machine and parse tables */
			if (options.print_progress) {
				System.err.println("Building parse tables...");
			}

			factories.build_parser(errorManager, emitter, options, this);

			did_output = emit(factories, options, emitter, errorManager);
		}
		/* fix up the times to make the summary easier */
		emit_end = System.currentTimeMillis();

		factories.dump(options);

		dump_end = System.currentTimeMillis();

		/* close input/output files */
		if (options.print_progress) {
			System.err.println("Closing files...");
		}

		/* produce a summary if desired */
		if (!options.no_summary) {
			final_time = System.currentTimeMillis();
			factories.emit_summary(did_output, emitter, errorManager, options,
					this);
		}

		/*
		 * If there were errors during the run, exit with non-zero status
		 * (makefile-friendliness). --CSA
		 */
		if (errorManager.getErrorCount() != 0) {
			System.exit(100);
		}
	}

}
