package java_cup;

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class NonTerminalFactory  {

	private static void register(non_terminal nt) {
		/* add to set of all non terminals and check for duplicates */
		Object conflict = _all.put(nt.name(), nt);
		if (conflict != null)
			// can't throw an exception here because these are used in static
			// initializers, so we crash instead
			// was:
			// throw new
			// internal_error("Duplicate non-terminal ("+nm+") created");
			(new internal_error("Duplicate non-terminal (" + nt.name()
					+ ") created")).crash();
	
		/* assign a unique index */
		next_index++;
	
		/* add to by_index set */
		_all_by_index.put(new Integer(nt.index()), nt);
	}

	/** Table of all non-terminals -- elements are stored using name strings 
	   *  as the key 
	   */
	protected static Hashtable _all = new Hashtable();

	public static void clear() {
	      _all.clear();
	      _all_by_index.clear();
	      next_index=0;
	      next_nt=0;
	  }

	/** Access to all non-terminals. */
	public static Enumeration all() {return _all.elements();}

	/** lookup a non terminal by name string */
	public static non_terminal find(String with_name) {
	  if (with_name == null)
	    return null;
	  else 
	    return (non_terminal)_all.get(with_name);
	}

	/** Table of all non terminals indexed by their index number. */
	protected static Hashtable _all_by_index = new Hashtable();

	/** Lookup a non terminal by index. */
	public static non_terminal find(int indx) {
	  Integer the_indx = new Integer(indx);
	
	  return (non_terminal)_all_by_index.get(the_indx);
	}

	/** Total number of non-terminals. */
	public static int number() {return _all.size();}

	/** Static counter to assign unique indexes. */
	protected static int next_index = 0;
	/** Static counter for creating unique non-terminal names */
	protected static int next_nt = 0;
	/** special non-terminal for start symbol */
	public static final non_terminal START_nt = createNonTerminal("$START");

	/** Method for creating a new uniquely named hidden non-terminal using 
	   *  the given string as a base for the name (or "NT$" if null is passed).
	   * @param prefix base name to construct unique name from. 
	   */
	static non_terminal create_new(String prefix) throws internal_error {
	  return create_new(prefix,null); // TUM 20060608 embedded actions patch
	}

	/** static routine for creating a new uniquely named hidden non-terminal */
	static non_terminal create_new() throws internal_error { 
	  return create_new(null); 
	}

	/**
	 * TUM 20060608 bugfix for embedded action codes
	 */
	protected static non_terminal create_new(String prefix, String type)
			throws internal_error {
			        if (prefix==null) prefix = "NT$";
			        return createNonTerminal(prefix + next_nt++, type);
			    }
			  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

	/** Compute nullability of all non-terminals. */
	public static void compute_nullability() throws internal_error {
	  boolean      change = true;
	  non_terminal nt;
	  Enumeration  e;
	  production   prod;
	
	  /* repeat this process until there is no change */
	  while (change)
	{
	  /* look for a new change */
	  change = false;
	
	  /* consider each non-terminal */
	  for (e=all(); e.hasMoreElements(); )
	    {
	      nt = (non_terminal)e.nextElement();
	
	      /* only look at things that aren't already marked nullable */
	      if (!nt.nullable())
		{
		  if (nt.looks_nullable())
		    {
		      nt._nullable = true;
		      change = true;
		    }
		}
	    }
	}
	
	  /* do one last pass over the productions to finalize all of them */
	  for (e=production.all(); e.hasMoreElements(); )
	{
	  prod = (production)e.nextElement();
	  prod.set_nullable(prod.check_nullable());
	}
	}

	/** Compute first sets for all non-terminals.  This assumes nullability has
	   *  already computed.
	   */
	public static void compute_first_sets() throws internal_error {
	  boolean      change = true;
	  Enumeration  n;
	  Enumeration  p;
	  non_terminal nt;
	  production   prod;
	  terminal_set prod_first;
	
	  /* repeat this process until we have no change */
	  while (change)
	{
	  /* look for a new change */
	  change = false;
	
	  /* consider each non-terminal */
	  for (n = all(); n.hasMoreElements(); )
	    {
	      nt = (non_terminal)n.nextElement();
	
	      /* consider every production of that non terminal */
	      for (p = nt.productions(); p.hasMoreElements(); )
		{
		  prod = (production)p.nextElement();
	
		  /* get the updated first of that production */
		  prod_first = prod.check_first_set();
	
		  /* if this going to add anything, add it */
		  if (!prod_first.is_subset_of(nt._first_set))
		    {
		      change = true;
		      nt._first_set.add(prod_first);
		    }
		}
	    }
	}
	}

	public static non_terminal createNonTerminal(String nm, String tp) {
		non_terminal nt = new non_terminal(nm, tp, next_index);
	    register(nt);
	    return nt;
	}

	public static non_terminal createNonTerminal(String nm) {
		non_terminal nt = new non_terminal(nm, next_index);
		register(nt);
		return nt;
	}


}