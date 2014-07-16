package java_cup;

import java.io.PrintWriter;
import java.util.Stack;

public interface Emitter {

	/** whether or not to emit code for left and right values */
	public abstract boolean lr_values();

	public abstract boolean locations();

	public abstract void set_lr_values(boolean b);

	public abstract void set_locations(boolean b);

	public abstract void set_genericlabels(boolean b);

	public abstract void set_xmlactions(boolean b);

	// Hm Added clear to clear all static fields
	public abstract void clear();

	/**
	 * Build a string with the standard prefix.
	 * 
	 * @param str
	 *            string to prefix.
	 */
	public abstract String pre(String str);

	/**
	 * Emit a package spec if the user wants one.
	 * 
	 * @param out
	 *            stream to produce output on.
	 */
	public abstract void emit_package(PrintWriter out);

	/**
	 * Emit code for the symbol constant class, optionally including non terms,
	 * if they have been requested.
	 * 
	 * @param out
	 *            stream to produce output on.
	 * @param emit_non_terms
	 *            do we emit constants for non terminals?
	 * @param sym_interface
	 *            should we emit an interface, rather than a class?
	 */
	public abstract void symbols(PrintWriter out, boolean emit_non_terms,
			boolean sym_interface);

	/**
	 * Emit the parser subclass with embedded tables.
	 * 
	 * @param out
	 *            stream to produce output on.
	 * @param action_table
	 *            internal representation of the action table.
	 * @param reduce_table
	 *            internal representation of the reduce-goto table.
	 * @param start_st
	 *            start state of the parse machine.
	 * @param start_prod
	 *            start production of the grammar.
	 * @param compact_reduces
	 *            do we use most frequent reduce as default?
	 * @param suppress_scanner
	 *            should scanner be suppressed for compatibility?
	 */
	public abstract void parser(PrintWriter out,
			parse_action_table action_table, parse_reduce_table reduce_table,
			int start_st, production start_prod, boolean compact_reduces,
			boolean suppress_scanner) throws internal_error;

	public abstract boolean is_xmlactions();

	public abstract String getAction_code();

	public abstract void setAction_code(String action_code);

	public abstract String getParser_code();

	public abstract void setParser_code(String parser_code);

	public abstract String getInit_code();

	public abstract void setInit_code(String init_code);

	public abstract String getScan_code();

	public abstract void setScan_code(String scan_code);

	public abstract long getSymbols_time();

	public abstract void setSymbols_time(long symbols_time);

	public abstract long getParser_time();

	public abstract void setParser_time(long parser_time);

	public abstract long getAction_code_time();

	public abstract void setAction_code_time(long action_code_time);

	public abstract long getProduction_table_time();

	public abstract void setProduction_table_time(long production_table_time);

	public abstract long getAction_table_time();

	public abstract void setAction_table_time(long action_table_time);

	public abstract long getGoto_table_time();

	public abstract void setGoto_table_time(long goto_table_time);

	public abstract String getClass_type_argument();

	public abstract void setClass_type_argument(String class_type_argument);

	public abstract Stack<String> getImport_list();

	public abstract void setImport_list(Stack<String> import_list);

	public abstract int getNot_reduced();

	public abstract void setNot_reduced(int not_reduced);

	public abstract boolean isNowarn();

	public abstract void setNowarn(boolean nowarn);

	public abstract int getNum_conflicts();

	public abstract void setNum_conflicts(int num_conflicts);

	public abstract String getPackage_name();

	public abstract void setPackage_name(String package_name);

	public abstract String getParser_class_name();

	public abstract void setParser_class_name(String parser_class_name);

	public abstract production getStart_production();

	public abstract void setStart_production(production start_production);

	public abstract String getSymbol_const_class_name();

	public abstract void setSymbol_const_class_name(
			String symbol_const_class_name);

	public abstract int getUnused_non_term();

	public abstract void setUnused_non_term(int unused_non_term);

	public abstract int getUnused_term();

	public abstract void setUnused_term(int unused_term);

}