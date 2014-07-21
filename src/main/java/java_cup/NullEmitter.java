package java_cup;

import java.io.File;

public class NullEmitter extends AbstractEmitter {

	public void emit_parser(final ProductionFactory productionFactory,
			final File dest_dir, final parse_action_table action_table,
			final parse_reduce_table reduce_table,
			final lalr_state start_state, final boolean include_non_terms,
			final boolean opt_compact_red, final boolean suppress_scanner,
			final boolean sym_interface) {
		// do nothing
	}

}
