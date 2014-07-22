package java_cup;

import java.util.Enumeration;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.lr_parser;

public class Factories {
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