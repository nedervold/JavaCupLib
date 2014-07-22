package java_cup;

import java.io.PrintStream;
import java.util.Enumeration;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.lr_parser;

public class Factories {
	/**
	 * Helper routine to optionally return a plural or non-plural ending. Used
	 * only by emit_summary().
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

	public parse_action_table action_table;

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

	public void build_parser(final PrintStream pp,
			final IErrorManager errorManager, final Emitter emitter,
			final Options options, final ITimings timings) throws internal_error {
		nonTerminalFactory
				.build_parser(pp, productionFactory, options, timings);

		/* build the LR viable prefix recognition machine */
		pp.println("  Building state machine...");

		start_state = lalrStateFactory.build_machine(errorManager,
				terminalFactory, emitter.start_production());
		timings.endStateMachine();

		/* build the LR parser action and reduce-goto tables */
		pp.println("  Filling in tables...");

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

		timings.endTables();

		/* check and warn for non-reduced productions */
		pp.println("  Checking for non-reduced productions...");

		action_table.check_reductions(errorManager, terminalFactory,
				productionFactory, emitter);

		timings.endReducedChecking();

		/*
		 * if we have more conflicts than we expected issue a message and die
		 */
		if (emitter.num_conflicts() > options.expect_conflicts) {
			errorManager
					.emit_error("*** More conflicts encountered than expected "
							+ "-- parser generation aborted");
			// indicate the problem.
			// we'll die on return, after clean up.
		}

		timings.endBuild();
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

	public void dump(final PrintStream ps, final Options options)
			throws internal_error {
		/* do requested dumps */
		if (options.opt_dump_grammar) {
			dump_grammar(ps);
		}
		if (options.opt_dump_states) {
			dump_machine(ps);
		}
		if (options.opt_dump_tables) {
			dump_tables(ps);
		}
	}

	/** Produce a human readable dump of the grammar. */
	public void dump_grammar(final PrintStream ps) throws internal_error {
		ps.println("===== Terminals =====");
		for (int tidx = 0, cnt = 0; tidx < terminalFactory.number(); tidx++, cnt++) {
			ps.print("[" + tidx + "]" + terminalFactory.find(tidx).name() + " ");
			if ((cnt + 1) % 5 == 0) {
				ps.println();
			}
		}
		ps.println();
		ps.println();

		ps.println("===== Non terminals =====");
		for (int nidx = 0, cnt = 0; nidx < nonTerminalFactory.number(); nidx++, cnt++) {
			ps.print("[" + nidx + "]" + nonTerminalFactory.find(nidx).name()
					+ " ");
			if ((cnt + 1) % 5 == 0) {
				ps.println();
			}
		}
		ps.println();
		ps.println();

		ps.println("===== Productions =====");
		for (int pidx = 0; pidx < productionFactory.number(); pidx++) {
			final production prod = productionFactory.find(pidx);
			ps.print("[" + pidx + "] " + prod.lhs().the_symbol().name()
					+ " ::= ");
			for (int i = 0; i < prod.rhs_length(); i++) {
				if (prod.rhs(i).is_action()) {
					ps.print("{action} ");
				} else {
					ps.print(((symbol_part) prod.rhs(i)).the_symbol().name()
							+ " ");
				}
			}
			ps.println();
		}
		ps.println();
	}

	/**
	 * Produce a (semi-) human readable dump of the complete viable prefix
	 * recognition state machine.
	 */
	public void dump_machine(final PrintStream ps) {
		final lalr_state ordered[] = new lalr_state[lalrStateFactory.number()];

		/* put the states in sorted order for a nicer display */
		for (final Enumeration<lalr_state> s = lalrStateFactory.all(); s
				.hasMoreElements();) {
			final lalr_state st = s.nextElement();
			ordered[st.index()] = st;
		}

		ps.println("===== Viable Prefix Recognizer =====");
		for (int i = 0; i < lalrStateFactory.number(); i++) {
			if (ordered[i] == start_state) {
				ps.print("START ");
			}
			ps.println(ordered[i]);
			ps.println("-------------------");
		}
	}

	/** Produce a (semi-) human readable dumps of the parse tables */
	public void dump_tables(final PrintStream ps) {
		ps.println(action_table);
		ps.println(reduce_table);
	}

	public void emit_summary(final PrintStream ps,
			final boolean output_produced, final Emitter emitter,
			final IErrorManager errorManager, final Options options,
			final ITimings timings) {

		if (!options.no_summary) {
			ps.println("------- " + version.title_str
					+ " Parser Generation Summary -------");

			/* error and warning count */
			final int errorCount = errorManager.getErrorCount();
			final int warningCount = errorManager.getWarningCount();
			ps.println("  " + errorCount + " error" + plural(errorCount)
					+ " and " + warningCount + " warning"
					+ plural(warningCount));

			/* basic stats */
			final int terminalCount = terminalFactory.number();
			final int nonTerminalCount = nonTerminalFactory.number();
			final int productionCount = productionFactory.number();
			final int stateCount = lalrStateFactory.number();

			ps.print("  " + terminalCount + " terminal" + plural(terminalCount)
					+ ", ");
			ps.print(nonTerminalCount + " non-terminal"
					+ plural(nonTerminalCount) + ", and ");
			ps.println(productionCount + " production"
					+ plural(productionCount) + " declared, ");
			ps.println("  producing " + stateCount + " unique parse states.");

			/* unused symbols */
			final int unusedTerminalCount = emitter.unused_term();
			final int unusedNonTerminalCount = emitter.unused_non_term();
			ps.println("  " + unusedTerminalCount + " terminal"
					+ plural(unusedTerminalCount) + " declared but not used.");
			ps.println("  " + unusedNonTerminalCount + " non-terminal"
					+ plural(unusedTerminalCount) + " declared but not used.");

			/* productions that didn't reduce */
			final int unreducedProductionCount = emitter.not_reduced();
			ps.println("  " + unreducedProductionCount + " production"
					+ plural(unreducedProductionCount) + " never reduced.");

			/* conflicts */
			final int conflictCount = emitter.num_conflicts();
			ps.println("  " + conflictCount + " conflict"
					+ plural(conflictCount) + " detected" + " ("
					+ options.expect_conflicts + " expected).");

			/* code location */
			if (output_produced) {
				ps.println("  Code written to \"" + emitter.parser_class_name()
						+ ".java\", and \"" + emitter.symbol_const_class_name()
						+ ".java\".");
			} else {
				ps.println("  No code produced.");
			}

			if (options.opt_show_timing) {
				timings.show_times(ps, emitter);
			}

			ps.println("---------------------------------------------------- ("
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
			 * something threw an exception. catch it and emit a message so we
			 * have a line number to work with, then re-throw it
			 */
			errorManager.emit_error("Internal error: Unexpected exception");
			throw e;
		}
	}
}