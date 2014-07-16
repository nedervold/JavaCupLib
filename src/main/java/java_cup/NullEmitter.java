package java_cup;

import java.io.PrintWriter;

public class NullEmitter extends AbstractEmitter {

	public void emit_package(PrintWriter out) {
		// do nothing
	}

	public void parser(PrintWriter out, parse_action_table action_table,
			parse_reduce_table reduce_table, int start_st,
			production start_prod, boolean compact_reduces,
			boolean suppress_scanner) throws internal_error {
		// do nothing
	}

	public void symbols(PrintWriter out, boolean emit_non_terms,
			boolean sym_interface) {
		// do nothing
	}

}
