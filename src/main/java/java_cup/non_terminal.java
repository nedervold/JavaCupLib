package java_cup;

import java.util.Hashtable;
import java.util.Enumeration;

/** This class represents a non-terminal symbol in the grammar.  Each
 *  non terminal has a textual name, an index, and a string which indicates
 *  the type of object it will be implemented with at runtime (i.e. the class
 *  of object that will be pushed on the parse stack to represent it). 
 *
 * @version last updated: 11/25/95
 * @author  Scott Hudson
 */

public class non_terminal extends symbol {

  /*-----------------------------------------------------------*/
  /*--- Constructor(s) ----------------------------------------*/
  /*-----------------------------------------------------------*/

  /** Full constructor.
   * @param nm  the name of the non terminal.
   * @param tp  the type string for the non terminal.
   */
  protected non_terminal(String nm, String tp, int next_index) 
    {
      /* super class does most of the work */
      super(nm, tp);
      /* assign a unique index */
      _index = next_index;
   }

	

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Constructor with default type. 
   * @param nm  the name of the non terminal.
   */
  protected non_terminal(String nm, int next_index) 
    {
      this(nm, null, next_index);
    }

  /*-----------------------------------------------------------*/
  /*--- (Access to) Static (Class) Variables ------------------*/
  /*-----------------------------------------------------------*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** flag non-terminals created to embed action productions */
  public boolean is_embedded_action = false; /* added 24-Mar-1998, CSA */

  /*-----------------------------------------------------------*/
  /*--- Static Methods ----------------------------------------*/
  /*-----------------------------------------------------------*/
	 
  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*-----------------------------------------------------------*/
  /*--- (Access to) Instance Variables ------------------------*/
  /*-----------------------------------------------------------*/

  /** Table of all productions with this non terminal on the LHS. */
  protected Hashtable _productions = new Hashtable(11);

  /** Access to productions with this non terminal on the LHS. */
  public Enumeration productions() {return _productions.elements();}

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Total number of productions with this non terminal on the LHS. */
  public int num_productions() {return _productions.size();}

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Add a production to our set of productions. */
  public void add_production(production prod) throws internal_error
    {
      /* catch improper productions */
      if (prod == null || prod.lhs() == null || prod.lhs().the_symbol() != this)
	throw new internal_error(
	  "Attempt to add invalid production to non terminal production table");

      /* add it to the table, keyed with itself */
      _productions.put(prod,prod);
    }

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Nullability of this non terminal. */
  protected boolean _nullable;

  /** Nullability of this non terminal. */
  public boolean nullable() {return _nullable;}

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** First set for this non-terminal. */
  protected terminal_set _first_set = new terminal_set();

  /** First set for this non-terminal. */
  public terminal_set first_set() {return _first_set;}

  /*-----------------------------------------------------------*/
  /*--- General Methods ---------------------------------------*/
  /*-----------------------------------------------------------*/

  /** Indicate that this symbol is a non-terminal. */
  public boolean is_non_term() 
    {
      return true;
    }

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Test to see if this non terminal currently looks nullable. */
  protected boolean looks_nullable() throws internal_error
    {
      /* look and see if any of the productions now look nullable */
      for (Enumeration e = productions(); e.hasMoreElements(); )
	/* if the production can go to empty, we are nullable */
	if (((production)e.nextElement()).check_nullable())
	  return true;

      /* none of the productions can go to empty, so we are not nullable */
      return false;
    }

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** convert to string */
  public String toString()
    {
      return super.toString() + "[" + index() + "]" + (nullable() ? "*" : "");
    }

  /*-----------------------------------------------------------*/
}
