package java_cup;

import java.util.Enumeration;
import java.util.Hashtable;

public class ProductionFactory {

	/**
	 * Table of all productions. Elements are stored using their index as the
	 * key.
	 */
	protected static Hashtable<Integer, production> _all = new Hashtable<Integer, production>();

	/** Access to all productions. */
	public static Enumeration<production> all() {
		return _all.elements();
	}

	/** Lookup a production by index. */
	public static production find(int indx) {
		return _all.get(indx);
	}

	public static void clear() {
		_all.clear();
		next_index = 0;
	}

	/** Total number of productions. */
	public static int number() {
		return _all.size();
	}

	/** Static counter for assigning unique index numbers. */
	protected static int next_index;

	/**
	 * Determine if a given character can be a label id starter.
	 * 
	 * @param c
	 *            the character in question.
	 */
	protected static boolean is_id_start(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');

		// later need to handle non-8-bit chars here
	}

	/**
	 * Determine if a character can be in a label id.
	 * 
	 * @param c
	 *            the character in question.
	 */
	protected static boolean is_id_char(char c) {
		return is_id_start(c) || (c >= '0' && c <= '9');
	}

	public static production createProduction(non_terminal lhs_sym,
			production_part[] rhs_parts, int rhs_l) throws internal_error {
		return createProduction(lhs_sym, rhs_parts, rhs_l, null);
	}

	public static production createProduction(non_terminal lhs_sym,
			production_part[] rhs_parts, int rhs_l, int prec_num, int prec_side)
			throws internal_error {
		production result = createProduction(lhs_sym, rhs_parts, rhs_l, null);

		/* set the precedence */
		result.set_precedence_num(prec_num);
		result.set_precedence_side(prec_side);
		return result;
	}

	public static production createProduction(non_terminal lhs_sym,
			production_part[] rhs_parts, int rhs_l, String action_str)
			throws internal_error {
		production result = new production(lhs_sym, rhs_parts, rhs_l,
				action_str);

		ProductionFactory.register(lhs_sym, result);
		return result;
	}

	public static production createProduction(non_terminal lhs_sym,
			production_part[] rhs_parts, int rhs_l, String action_str,
			int prec_num, int prec_side) throws internal_error {
		production result = createProduction(lhs_sym, rhs_parts, rhs_l,
				action_str);

		/* set the precedence */
		result.set_precedence_num(prec_num);
		result.set_precedence_side(prec_side);
		return result;
	}

	public static action_production createActionProduction(production base,
			non_terminal lhs_sym, production_part[] rhs_parts, int rhs_len,
			String action_str, int indexOfIntermediateResult)
			throws internal_error {
		action_production result = new action_production(base, lhs_sym, rhs_parts, rhs_len,
				action_str, indexOfIntermediateResult);

		ProductionFactory.register(lhs_sym, result);
		return result;
	}

	private static void register(non_terminal lhs_sym, production prod)
			throws internal_error {
		/* assign an index */
		prod.setIndex(next_index++);

		/* put us in the global collection of productions */
		_all.put(new Integer(prod.index()), prod);

		/* put us in the production list of the lhs non terminal */
		lhs_sym.add_production(prod);
	}

	public ProductionFactory() {
		super();
	}

}