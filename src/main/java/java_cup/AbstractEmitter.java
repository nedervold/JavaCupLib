package java_cup;

import java.io.PrintStream;
import java.util.Stack;

public abstract class AbstractEmitter implements Emitter {

	public void show_times(PrintStream ps, long total_time){
		if (symbols_time() != 0) {
			ps.println("        Symbols      "
					+ Timings.timestr(symbols_time(), total_time));
		}
		if (parser_time() != 0) {
			ps.println("        Parser class "
					+ Timings.timestr(parser_time(), total_time));
		}
		if (action_code_time() != 0) {
			ps.println("          Actions    "
					+ Timings.timestr(action_code_time(), total_time));
		}
		if (production_table_time() != 0) {
			ps.println("          Prod table "
					+ Timings.timestr(production_table_time(), total_time));
		}
		if (action_table_time() != 0) {
			ps.println("          Action tab "
					+ Timings.timestr(action_table_time(), total_time));
		}
		if (goto_table_time() != 0) {
			ps.println("          Reduce tab "
					+ Timings.timestr(goto_table_time(), total_time));
		}
	}

	protected boolean _locations;

	protected boolean _lr_values;

	protected boolean _xmlactions;

	/** User declarations for direct inclusion in user action class. */
	protected String action_code = null;

	/** Time to produce action code class. */
	protected long action_code_time = 0;

	/** Time to produce the action table. */
	protected long action_table_time = 0;

	/**
	 * TUM changes; proposed by Henning Niss 20050628: Type arguments for class
	 * declaration
	 */
	protected String class_type_argument = null;

	/** Time to produce the reduce-goto table. */
	protected long goto_table_time = 0;

	/** List of imports (Strings containing class names) to go with actions. */
	protected Stack<String> import_list = new Stack<String>();

	/** User code for user_init() which is called during parser initialization. */
	protected String init_code = null;

	/** Count of the number on non-reduced productions found. */
	protected int not_reduced = 0;

	/** Do we skip warnings? */
	private boolean nowarn = false;
	/** Number of conflict found while building tables. */
	protected int num_conflicts = 0;

	/** Package that the resulting code goes into (null is used for unnamed). */
	protected String package_name = null;

	/** Name of the generated parser class. */
	protected String parser_class_name = "parser";

	/** User declarations for direct inclusion in parser class. */
	protected String parser_code = null;

	/** Time to produce parser class. */
	protected long parser_time = 0;

	/** Time to produce the production table. */
	protected long production_table_time = 0;

	/** User code for scan() which is called to get the next Symbol. */
	protected String scan_code = null;

	/** The start production of the grammar. */
	protected production start_production = null;

	/** Name of the generated class for symbol constants. */
	protected String symbol_const_class_name = "sym";

	/** Time to produce symbol constant class. */
	protected long symbols_time = 0;

	/** Count of unused non terminals. */
	protected int unused_non_term = 0;

	/** Count of unused terminals. */
	protected int unused_term = 0;

	protected boolean _genericlabels;

	/** The prefix placed on names that pollute someone else's name space. */
	private final String prefix = "CUP$";

	public AbstractEmitter() {
		super();
		_genericlabels = false;
		_xmlactions = false;
		_locations = false;
		_lr_values = true;
		action_code = null;
		import_list = new Stack<String>();
		init_code = null;
		not_reduced = 0;
		num_conflicts = 0;
		package_name = null;
		parser_class_name = "parser";
		parser_code = null;
		scan_code = null;
		start_production = null;
		symbol_const_class_name = "sym";
		unused_non_term = 0;
		unused_term = 0;
	}

	public boolean xmlactions() {
		return _xmlactions;
	}

	public String action_code() {
		return action_code;
	}

	public long action_code_time() {
		return action_code_time;
	}

	public long action_table_time() {
		return action_table_time;
	}

	public long goto_table_time() {
		return goto_table_time;
	}

	public Stack<String> import_list() {
		return import_list;
	}

	public String init_code() {
		return init_code;
	}

	public boolean locations() {
		return _locations;
	}

	public boolean lr_values() {
		return _lr_values;
	}

	public int not_reduced() {
		return not_reduced;
	}

	public boolean nowarn() {
		return nowarn;
	}

	public int num_conflicts() {
		return num_conflicts;
	}

	public String parser_class_name() {
		return parser_class_name;
	}

	public String parser_code() {
		return parser_code;
	}

	public long parser_time() {
		return parser_time;
	}

	public long production_table_time() {
		return production_table_time;
	}

	public String scan_code() {
		return scan_code;
	}

	public void set_action_code(final String action_code) {
		this.action_code = action_code;
	}

	public void set_action_code_time(final long action_code_time) {
		this.action_code_time = action_code_time;
	}

	public void set_class_type_argument(final String class_type_argument) {
		this.class_type_argument = class_type_argument;
	}

	public void set_init_code(final String init_code) {
		this.init_code = init_code;
	}

	public void set_locations(final boolean b) {
		_locations = b;
	}

	public void set_lr_values(final boolean b) {
		_lr_values = b;
	}

	public void set_nowarn(final boolean nowarn) {
		this.nowarn = nowarn;
	}

	public void set_num_conflicts(final int num_conflicts) {
		this.num_conflicts = num_conflicts;
	}

	public void set_package_name(final String package_name) {
		this.package_name = package_name;
	}

	public void set_parser_class_name(final String parser_class_name) {
		this.parser_class_name = parser_class_name;
	}

	public void set_parser_code(final String parser_code) {
		this.parser_code = parser_code;
	}

	public void set_scan_code(final String scan_code) {
		this.scan_code = scan_code;
	}

	public void set_start_production(final production start_production) {
		this.start_production = start_production;
	}

	public void set_symbol_const_class_name(final String symbol_const_class_name) {
		this.symbol_const_class_name = symbol_const_class_name;
	}

	public void set_symbols_time(final long symbols_time) {
		this.symbols_time = symbols_time;
	}

	public void set_unused_non_term(final int unused_non_term) {
		this.unused_non_term = unused_non_term;
	}

	public void set_unused_term(final int unused_term) {
		this.unused_term = unused_term;
	}

	public void set_xmlactions(final boolean b) {
		_xmlactions = b;
		if (!b) {
			return;
		}
		_locations = true;
		_lr_values = true;
	}

	public production start_production() {
		return start_production;
	}

	public String symbol_const_class_name() {
		return symbol_const_class_name;
	}

	public long symbols_time() {
		return symbols_time;
	}

	public int unused_non_term() {
		return unused_non_term;
	}

	public int unused_term() {
		return unused_term;
	}

	public void set_genericlabels(boolean b) {
		_genericlabels = b;
	}

	public String pre(String str) {
		return prefix + parser_class_name + "$" + str;
	}

}