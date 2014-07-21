package java_cup;

import java.io.File;
import java.util.Stack;

public interface Emitter {

	public abstract String action_code();

	public abstract long action_code_time();

	public abstract long action_table_time();

	// Hm Added clear to clear all static fields
	public abstract void clear();

	public void emit_parser(ProductionFactory productionFactory, File dest_dir, parse_action_table action_table,
			parse_reduce_table reduce_table, lalr_state start_state,
			boolean include_non_terms, boolean opt_compact_red,
			boolean suppress_scanner, boolean sym_interface)
			throws internal_error;

	public abstract long goto_table_time();

	public abstract Stack<String> import_list();

	public abstract String init_code();

	public abstract boolean locations();

	/** whether or not to emit code for left and right values */
	public abstract boolean lr_values();

	public abstract int not_reduced();

	public abstract boolean nowarn();

	public abstract int num_conflicts();

	public abstract String parser_class_name();

	public abstract String parser_code();

	public abstract long parser_time();

	/**
	 * Build a string with the standard prefix.
	 * 
	 * @param str
	 *            string to prefix.
	 */
	public abstract String pre(String str);

	public abstract long production_table_time();

	public abstract String scan_code();

	public abstract void set_action_code(String action_code);

	public abstract void set_action_code_time(long action_code_time);

	public abstract void set_class_type_argument(String class_type_argument);

	public abstract void set_genericlabels(boolean b);

	public abstract void set_init_code(String init_code);

	public abstract void set_locations(boolean b);

	public abstract void set_lr_values(boolean b);

	public abstract void set_nowarn(boolean nowarn);

	public abstract void set_num_conflicts(int num_conflicts);

	public abstract void set_package_name(String package_name);

	public abstract void set_parser_class_name(String parser_class_name);

	public abstract void set_parser_code(String parser_code);

	public abstract void set_scan_code(String scan_code);

	public abstract void set_start_production(production start_production);

	public abstract void set_symbol_const_class_name(
			String symbol_const_class_name);

	public abstract void set_symbols_time(long symbols_time);

	public abstract void set_unused_non_term(int unused_term);

	public abstract void set_unused_term(int unused_term);

	public abstract void set_xmlactions(boolean b);

	public abstract production start_production();

	public abstract String symbol_const_class_name();

	public abstract long symbols_time();

	public abstract int unused_non_term();

	public abstract int unused_term();

	public abstract boolean xmlactions();

}