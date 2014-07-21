package java_cup;

import java.io.File;
import java.io.FileInputStream;

public class Options {

	/** User option -- do we print progress messages. */
	public boolean print_progress = false;
	/** User option -- do we produce a dump of the state machine */
	public boolean opt_dump_states = false;
	/** User option -- do we produce a dump of the parse tables */
	public boolean opt_dump_tables = false;
	/** User option -- do we produce a dump of the grammar */
	public boolean opt_dump_grammar = false;
	/** User option -- do we show timing information as a part of the summary */
	public boolean opt_show_timing = false;
	/** User option -- do we run produce extra debugging messages */
	public boolean opt_do_debug = false;
	/**
	 * User option -- do we compact tables by making most common reduce the
	 * default action
	 */
	public boolean opt_compact_red = false;
	/**
	 * User option -- should we include non terminal symbol numbers in the
	 * symbol constant class.
	 */
	public boolean include_non_terms = false;
	/** User option -- do not print a summary. */
	public boolean no_summary = false;
	/** User option -- number of conflicts to expect */
	public int expect_conflicts = 0;
	/** User option -- should generator generate code for left/right values? */
	public boolean lr_values = true;
	public boolean locations = false;
	public boolean xmlactions = false;
	public boolean genericlabels = false;
	/** User option -- should symbols be put in a class or an interface? [CSA] */
	public boolean sym_interface = false;
	/**
	 * User option -- should generator suppress references to
	 * java_cup.runtime.Scanner for compatibility with old runtimes?
	 */
	public boolean suppress_scanner = false;

	/**
	 * Print a "usage message" that described possible command line options,
	 * then exit.
	 * 
	 * @param message
	 *            a specific error message to preface the usage message by.
	 */
	private static void usage(String message) {
		System.err.println();
		System.err.println(message);
		System.err.println();
		System.err
				.println(version.title_str
						+ "\n"
						+ "Usage: "
						+ version.program_name
						+ " [options] [filename]\n"
						+ "  and expects a specification file on standard input if no filename is given.\n"
						+ "  Legal options include:\n"
						+ "    -package name  specify package generated classes go in [default none]\n"
						+ "    -destdir name  specify the destination directory, to store the generated files in\n"
						+ "    -parser name   specify parser class name [default \"parser\"]\n"
						+ "    -typearg args  specify type arguments for parser class\n"
						+ "    -symbols name  specify name for symbol constant class [default \"sym\"]\n"
						+ "    -interface     put symbols in an interface, rather than a class\n"
						+ "    -nonterms      put non terminals in symbol constant class\n"
						+ "    -expect #      number of conflicts expected/allowed [default 0]\n"
						+ "    -compact_red   compact tables by defaulting to most frequent reduce\n"
						+ "    -nowarn        don't warn about useless productions, etc.\n"
						+ "    -nosummary     don't print the usual summary of parse states, etc.\n"
						+ "    -nopositions   don't propagate the left and right token position values\n"
						+ "    -locations     generate handles xleft/xright for symbol positions in actions\n"
						+ "    -xmlactions    make the generated parser yield its parse tree as XML\n"
						+ "    -genericlabels automatically generate labels to all symbols in XML mode\n"
						+ "    -noscanner     don't refer to java_cup.runtime.Scanner\n"
						+ "    -progress      print messages to indicate progress of the system\n"
						+ "    -time          print time usage summary\n"
						+ "    -dump_grammar  produce a human readable dump of the symbols and grammar\n"
						+ "    -dump_states   produce a dump of parse state machine\n"
						+ "    -dump_tables   produce a dump of the parse tables\n"
						+ "    -dump          produce a dump of all of the above\n"
						+ "    -version       print the version information for CUP and exit\n");
		System.exit(1);
	}

	/** Output directory. */
	public File dest_dir = null;

	/**
	 * Parse command line options and arguments to set various user-option flags
	 * and variables.
	 * 
	 * @param argv
	 *            the command line arguments to be parsed.
	 */
	private void parse_args(String argv[], Emitter emitter) {
		int len = argv.length;
		int i;
	
		/* parse the options */
		for (i = 0; i < len; i++) {
			/* try to get the various options */
			if (argv[i].equals("-package")) {
				/* must have an arg */
				if (++i >= len || argv[i].startsWith("-")
						|| argv[i].endsWith(".cup"))
					usage("-package must have a name argument");
	
				/* record the name */
				emitter.set_package_name(argv[i]);
			} else if (argv[i].equals("-destdir")) {
				/* must have an arg */
				if (++i >= len || argv[i].startsWith("-")
						|| argv[i].endsWith(".cup"))
					usage("-destdir must have a name argument");
				/* record the name */
				dest_dir = new java.io.File(argv[i]);
			} else if (argv[i].equals("-parser")) {
				/* must have an arg */
				if (++i >= len || argv[i].startsWith("-")
						|| argv[i].endsWith(".cup"))
					usage("-parser must have a name argument");
	
				/* record the name */
				emitter.set_parser_class_name(argv[i]);
			} else if (argv[i].equals("-symbols")) {
				/* must have an arg */
				if (++i >= len || argv[i].startsWith("-")
						|| argv[i].endsWith(".cup"))
					usage("-symbols must have a name argument");
	
				/* record the name */
				emitter.set_symbol_const_class_name(argv[i]);
			} else if (argv[i].equals("-nonterms")) {
				include_non_terms = true;
			} else if (argv[i].equals("-expect")) {
				/* must have an arg */
				if (++i >= len || argv[i].startsWith("-")
						|| argv[i].endsWith(".cup"))
					usage("-expect must have a name argument");
	
				/* record the number */
				try {
					expect_conflicts = Integer.parseInt(argv[i]);
				} catch (NumberFormatException e) {
					usage("-expect must be followed by a decimal integer");
				}
			} else if (argv[i].equals("-compact_red"))
				opt_compact_red = true;
			else if (argv[i].equals("-nosummary"))
				no_summary = true;
			else if (argv[i].equals("-nowarn"))
				emitter.set_nowarn(true);
			else if (argv[i].equals("-dump_states"))
				opt_dump_states = true;
			else if (argv[i].equals("-dump_tables"))
				opt_dump_tables = true;
			else if (argv[i].equals("-progress"))
				print_progress = true;
			else if (argv[i].equals("-dump_grammar"))
				opt_dump_grammar = true;
			else if (argv[i].equals("-dump"))
				opt_dump_states = opt_dump_tables = opt_dump_grammar = true;
			else if (argv[i].equals("-time"))
				opt_show_timing = true;
			else if (argv[i].equals("-debug"))
				opt_do_debug = true;
			/* frankf 6/18/96 */
			else if (argv[i].equals("-nopositions"))
				lr_values = false;
			else if (argv[i].equals("-locations"))
				locations = true;
			else if (argv[i].equals("-xmlactions"))
				xmlactions = true;
			else if (argv[i].equals("-genericlabels"))
				genericlabels = true;
			/* CSA 12/21/97 */
			else if (argv[i].equals("-interface"))
				sym_interface = true;
			/* CSA 23-Jul-1999 */
			else if (argv[i].equals("-noscanner"))
				suppress_scanner = true;
			/* CSA 23-Jul-1999 */
			else if (argv[i].equals("-version")) {
				System.out.println(version.title_str);
				System.exit(1);
			}
			/* TUM changes; suggested by Henning Niss 20050628 */
			else if (argv[i].equals("-typearg")) {
				if (++i >= len || argv[i].startsWith("-")
						|| argv[i].endsWith(".cup"))
					usage("-symbols must have a name argument");
	
				/* record the typearg */
				emitter.set_class_type_argument(argv[i]);
			}
	
			/* CSA 24-Jul-1999; suggestion by Jean Vaucher */
			else if (!argv[i].startsWith("-") && i == len - 1) {
				/* use input from file. */
				try {
					System.setIn(new FileInputStream(argv[i]));
				} catch (java.io.FileNotFoundException e) {
					usage("Unable to open \"" + argv[i] + "\" for input");
				}
			} else {
				usage("Unrecognized option \"" + argv[i] + "\"");
			}
		}
	}

	public Options() {
		super();
	}

	public Options(String[] argv, Emitter emitter){
		this();
		parse_args(argv, emitter);
	}
}