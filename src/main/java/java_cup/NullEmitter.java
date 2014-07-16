package java_cup;

import java.io.File;
import java.io.PrintWriter;

public class NullEmitter extends AbstractEmitter {

	public void close_files() {
		// do nothing
	}

	public void emit_parser(final parse_action_table action_table,
			final parse_reduce_table reduce_table,
			final lalr_state start_state, final boolean include_non_terms,
			final boolean opt_compact_red, final boolean suppress_scanner,
			final boolean sym_interface) {
		// do nothing
	}

	public void emit_package(final PrintWriter out) {
		// do nothing
	}

	public void open_files(File dest_dir) {
		// do nothing
	}

	public void parser(final PrintWriter out,
			final parse_action_table action_table,
			final parse_reduce_table reduce_table, final int start_st,
			final production start_prod, final boolean compact_reduces,
			final boolean suppress_scanner) throws internal_error {
		// do nothing
	}

	public void symbols(final PrintWriter out, final boolean emit_non_terms,
			final boolean sym_interface) {
		// do nothing
	}

}
