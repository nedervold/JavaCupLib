package java_cup;


/** This class represents a terminal symbol in the grammar.  Each terminal 
 *  has a textual name, an index, and a string which indicates the type of 
 *  object it will be implemented with at runtime (i.e. the class of object 
 *  that will be returned by the scanner and pushed on the parse stack to 
 *  represent it). 
 *
 * @version last updated: 7/3/96
 * @author  Frank Flannery
 */
public class terminal extends symbol {

  /*-----------------------------------------------------------*/
  /*--- Constructor(s) ----------------------------------------*/
  /*-----------------------------------------------------------*/

  /** Full constructor.
   * @param nm the name of the terminal.
   * @param tp the type of the terminal.
   */
	protected terminal(String nm, String tp, int precedence_side, int precedence_num, int next_index) 
    {
      /* superclass does most of the work */
      super(nm, tp);

      /* assign a unique index */
      _index = next_index;

      /* set the precedence */
      _precedence_num = precedence_num;
      _precedence_side = precedence_side;
    }

	

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Constructor for non-precedented terminal
    */ 

  protected terminal(String nm, String tp, int next_index) 
    {
      this(nm, tp, assoc.no_prec, -1, next_index);
    }

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Constructor with default type. 
   * @param nm the name of the terminal.
   */
  protected terminal(String nm, int next_index) 
    {
      this(nm, null, next_index);
    }

  /*-----------------------------------------------------------*/
  /*-------------------  Class Variables  ---------------------*/
  /*-----------------------------------------------------------*/

  private int _precedence_num;
  private int _precedence_side;

  /*-----------------------------------------------------------*/
  /*--- (Access to) Static (Class) Variables ------------------*/
  /*-----------------------------------------------------------*/

  


  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
 
  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  

  /*-----------------------------------------------------------*/
  /*--- General Methods ---------------------------------------*/
  /*-----------------------------------------------------------*/

  /** Report this symbol as not being a non-terminal. */
  public boolean is_non_term() 
    {
      return false;
    }

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** Convert to a string. */
  public String toString()
    {
      return super.toString() + "[" + index() + "]";
    }

  /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/

  /** get the precedence of a terminal */
  public int precedence_num() {
    return _precedence_num;
  }
  public int precedence_side() {
    return _precedence_side;
  }

  /** set the precedence of a terminal */
  public void set_precedence(int p, int new_prec) {
    _precedence_side = p;
    _precedence_num = new_prec;
  }

  /*-----------------------------------------------------------*/

}
