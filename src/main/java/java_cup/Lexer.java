/* The following code was generated by JFlex 1.3.5 on 7/21/14 10:20 PM */

package java_cup;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import java.lang.Error;
import java.io.InputStreamReader;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.3.5
 * on 7/21/14 10:20 PM from the specification file
 * <tt>file:/Users/nedervold/JavaCupLib/flex/Lexer.jflex</tt>
 */
public class Lexer implements sym, java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  final public static int YYEOF = -1;

  /** initial size of the lookahead buffer */
  final private static int YY_BUFFERSIZE = 16384;

  /** lexical states */
  final public static int CODESEG = 1;
  final public static int YYINITIAL = 0;

  /** 
   * Translates characters to character classes
   */
  final private static String yycmap_packed = 
    "\11\7\1\3\1\2\1\0\1\3\1\1\16\7\4\0\1\3\3\0"+
    "\1\6\1\21\4\0\1\5\1\0\1\12\1\0\1\13\1\4\12\7"+
    "\1\17\1\11\1\27\1\20\1\26\1\10\1\0\32\6\1\15\1\0"+
    "\1\16\1\0\1\6\1\0\1\31\1\6\1\25\1\40\1\24\1\46"+
    "\1\33\1\45\1\34\1\6\1\32\1\43\1\35\1\41\1\36\1\22"+
    "\1\6\1\23\1\42\1\37\1\50\1\6\1\44\1\47\2\6\1\30"+
    "\1\14\1\51\1\0\41\7\2\0\4\6\4\0\1\6\2\0\1\7"+
    "\7\0\1\6\4\0\1\6\5\0\27\6\1\0\37\6\1\0\u01ca\6"+
    "\4\0\14\6\16\0\5\6\7\0\1\6\1\0\1\6\21\0\160\7"+
    "\5\6\1\0\2\6\2\0\4\6\10\0\1\6\1\0\3\6\1\0"+
    "\1\6\1\0\24\6\1\0\123\6\1\0\213\6\1\0\5\7\2\0"+
    "\236\6\11\0\46\6\2\0\1\6\7\0\47\6\11\0\55\7\1\0"+
    "\1\7\1\0\2\7\1\0\2\7\1\0\1\7\10\0\33\6\5\0"+
    "\3\6\15\0\4\7\7\0\1\6\4\0\13\7\5\0\53\6\37\7"+
    "\4\0\2\6\1\7\143\6\1\0\1\6\10\7\1\0\6\7\2\6"+
    "\2\7\1\0\4\7\2\6\12\7\3\6\2\0\1\6\17\0\1\7"+
    "\1\6\1\7\36\6\33\7\2\0\131\6\13\7\1\6\16\0\12\7"+
    "\41\6\11\7\2\6\4\0\1\6\5\0\26\6\4\7\1\6\11\7"+
    "\1\6\3\7\1\6\5\7\22\0\31\6\3\7\244\0\4\7\66\6"+
    "\3\7\1\6\22\7\1\6\7\7\12\6\2\7\2\0\12\7\1\0"+
    "\7\6\1\0\7\6\1\0\3\7\1\0\10\6\2\0\2\6\2\0"+
    "\26\6\1\0\7\6\1\0\1\6\3\0\4\6\2\0\1\7\1\6"+
    "\7\7\2\0\2\7\2\0\3\7\1\6\10\0\1\7\4\0\2\6"+
    "\1\0\3\6\2\7\2\0\12\7\4\6\7\0\1\6\5\0\3\7"+
    "\1\0\6\6\4\0\2\6\2\0\26\6\1\0\7\6\1\0\2\6"+
    "\1\0\2\6\1\0\2\6\2\0\1\7\1\0\5\7\4\0\2\7"+
    "\2\0\3\7\3\0\1\7\7\0\4\6\1\0\1\6\7\0\14\7"+
    "\3\6\1\7\13\0\3\7\1\0\11\6\1\0\3\6\1\0\26\6"+
    "\1\0\7\6\1\0\2\6\1\0\5\6\2\0\1\7\1\6\10\7"+
    "\1\0\3\7\1\0\3\7\2\0\1\6\17\0\2\6\2\7\2\0"+
    "\12\7\1\0\1\6\17\0\3\7\1\0\10\6\2\0\2\6\2\0"+
    "\26\6\1\0\7\6\1\0\2\6\1\0\5\6\2\0\1\7\1\6"+
    "\7\7\2\0\2\7\2\0\3\7\10\0\2\7\4\0\2\6\1\0"+
    "\3\6\2\7\2\0\12\7\1\0\1\6\20\0\1\7\1\6\1\0"+
    "\6\6\3\0\3\6\1\0\4\6\3\0\2\6\1\0\1\6\1\0"+
    "\2\6\3\0\2\6\3\0\3\6\3\0\14\6\4\0\5\7\3\0"+
    "\3\7\1\0\4\7\2\0\1\6\6\0\1\7\16\0\12\7\11\0"+
    "\1\6\7\0\3\7\1\0\10\6\1\0\3\6\1\0\27\6\1\0"+
    "\12\6\1\0\5\6\3\0\1\6\7\7\1\0\3\7\1\0\4\7"+
    "\7\0\2\7\1\0\2\6\6\0\2\6\2\7\2\0\12\7\22\0"+
    "\2\7\1\0\10\6\1\0\3\6\1\0\27\6\1\0\12\6\1\0"+
    "\5\6\2\0\1\7\1\6\7\7\1\0\3\7\1\0\4\7\7\0"+
    "\2\7\7\0\1\6\1\0\2\6\2\7\2\0\12\7\1\0\2\6"+
    "\17\0\2\7\1\0\10\6\1\0\3\6\1\0\51\6\2\0\1\6"+
    "\7\7\1\0\3\7\1\0\4\7\1\6\10\0\1\7\10\0\2\6"+
    "\2\7\2\0\12\7\12\0\6\6\2\0\2\7\1\0\22\6\3\0"+
    "\30\6\1\0\11\6\1\0\1\6\2\0\7\6\3\0\1\7\4\0"+
    "\6\7\1\0\1\7\1\0\10\7\22\0\2\7\15\0\60\6\1\7"+
    "\2\6\7\7\4\0\10\6\10\7\1\0\12\7\47\0\2\6\1\0"+
    "\1\6\2\0\2\6\1\0\1\6\2\0\1\6\6\0\4\6\1\0"+
    "\7\6\1\0\3\6\1\0\1\6\1\0\1\6\2\0\2\6\1\0"+
    "\4\6\1\7\2\6\6\7\1\0\2\7\1\6\2\0\5\6\1\0"+
    "\1\6\1\0\6\7\2\0\12\7\2\0\2\6\42\0\1\6\27\0"+
    "\2\7\6\0\12\7\13\0\1\7\1\0\1\7\1\0\1\7\4\0"+
    "\2\7\10\6\1\0\44\6\4\0\24\7\1\0\2\7\5\6\13\7"+
    "\1\0\44\7\11\0\1\7\71\0\53\6\24\7\1\6\12\7\6\0"+
    "\6\6\4\7\4\6\3\7\1\6\3\7\2\6\7\7\3\6\4\7"+
    "\15\6\14\7\1\6\17\7\2\0\46\6\12\0\53\6\1\0\1\6"+
    "\3\0\u0149\6\1\0\4\6\2\0\7\6\1\0\1\6\1\0\4\6"+
    "\2\0\51\6\1\0\4\6\2\0\41\6\1\0\4\6\2\0\7\6"+
    "\1\0\1\6\1\0\4\6\2\0\17\6\1\0\71\6\1\0\4\6"+
    "\2\0\103\6\2\0\3\7\40\0\20\6\20\0\125\6\14\0\u026c\6"+
    "\2\0\21\6\1\0\32\6\5\0\113\6\3\0\3\6\17\0\15\6"+
    "\1\0\4\6\3\7\13\0\22\6\3\7\13\0\22\6\2\7\14\0"+
    "\15\6\1\0\3\6\1\0\2\7\14\0\64\6\40\7\3\0\1\6"+
    "\3\0\2\6\1\7\2\0\12\7\41\0\3\7\2\0\12\7\6\0"+
    "\130\6\10\0\51\6\1\7\1\6\5\0\106\6\12\0\35\6\3\0"+
    "\14\7\4\0\14\7\12\0\12\7\36\6\2\0\5\6\13\0\54\6"+
    "\4\0\21\7\7\6\2\7\6\0\12\7\46\0\27\6\5\7\4\0"+
    "\65\6\12\7\1\0\35\7\2\0\13\7\6\0\12\7\15\0\1\6"+
    "\130\0\5\7\57\6\21\7\7\6\4\0\12\7\21\0\11\7\14\0"+
    "\3\7\36\6\12\7\3\0\2\6\12\7\6\0\46\6\16\7\14\0"+
    "\44\6\24\7\10\0\12\7\3\0\3\6\12\7\44\6\122\0\3\7"+
    "\1\0\25\7\4\6\1\7\4\6\1\7\15\0\300\6\47\7\25\0"+
    "\4\7\u0116\6\2\0\6\6\2\0\46\6\2\0\6\6\2\0\10\6"+
    "\1\0\1\6\1\0\1\6\1\0\1\6\1\0\37\6\2\0\65\6"+
    "\1\0\7\6\1\0\1\6\3\0\3\6\1\0\7\6\3\0\4\6"+
    "\2\0\6\6\4\0\15\6\5\0\3\6\1\0\7\6\16\0\5\7"+
    "\32\0\5\7\20\0\2\6\23\0\1\6\13\0\5\7\5\0\6\7"+
    "\1\0\1\6\15\0\1\6\20\0\15\6\3\0\32\6\26\0\15\7"+
    "\4\0\1\7\3\0\14\7\21\0\1\6\4\0\1\6\2\0\12\6"+
    "\1\0\1\6\3\0\5\6\6\0\1\6\1\0\1\6\1\0\1\6"+
    "\1\0\4\6\1\0\13\6\2\0\4\6\5\0\5\6\4\0\1\6"+
    "\21\0\51\6\u0a77\0\57\6\1\0\57\6\1\0\205\6\6\0\4\6"+
    "\3\7\16\0\46\6\12\0\66\6\11\0\1\6\17\0\1\7\27\6"+
    "\11\0\7\6\1\0\7\6\1\0\7\6\1\0\7\6\1\0\7\6"+
    "\1\0\7\6\1\0\7\6\1\0\7\6\1\0\40\7\57\0\1\6"+
    "\u01d5\0\3\6\31\0\11\6\6\7\1\0\5\6\2\0\5\6\4\0"+
    "\126\6\2\0\2\7\2\0\3\6\1\0\132\6\1\0\4\6\5\0"+
    "\51\6\3\0\136\6\21\0\33\6\65\0\20\6\u0200\0\u19b6\6\112\0"+
    "\u51cc\6\64\0\u048d\6\103\0\56\6\2\0\u010d\6\3\0\20\6\12\7"+
    "\2\6\24\0\57\6\1\7\14\0\2\7\1\0\31\6\10\0\120\6"+
    "\2\7\45\0\11\6\2\0\147\6\2\0\4\6\1\0\2\6\16\0"+
    "\12\6\120\0\10\6\1\7\3\6\1\7\4\6\1\7\27\6\5\7"+
    "\20\0\1\6\7\0\64\6\14\0\2\7\62\6\21\7\13\0\12\7"+
    "\6\0\22\7\6\6\3\0\1\6\4\0\12\7\34\6\10\7\2\0"+
    "\27\6\15\7\14\0\35\6\3\0\4\7\57\6\16\7\16\0\1\6"+
    "\12\7\46\0\51\6\16\7\11\0\3\6\1\7\10\6\2\7\2\0"+
    "\12\7\6\0\27\6\3\0\1\6\1\7\4\0\60\6\1\7\1\6"+
    "\3\7\2\6\2\7\5\6\2\7\1\6\1\7\1\6\30\0\3\6"+
    "\43\0\6\6\2\0\6\6\2\0\6\6\11\0\7\6\1\0\7\6"+
    "\221\0\43\6\10\7\1\0\2\7\2\0\12\7\6\0\u2ba4\6\14\0"+
    "\27\6\4\0\61\6\u2104\0\u012e\6\2\0\76\6\2\0\152\6\46\0"+
    "\7\6\14\0\5\6\5\0\1\6\1\7\12\6\1\0\15\6\1\0"+
    "\5\6\1\0\1\6\1\0\2\6\1\0\2\6\1\0\154\6\41\0"+
    "\u016b\6\22\0\100\6\2\0\66\6\50\0\15\6\3\0\20\7\20\0"+
    "\7\7\14\0\2\6\30\0\3\6\31\0\1\6\6\0\5\6\1\0"+
    "\207\6\2\0\1\7\4\0\1\6\13\0\12\7\7\0\32\6\4\0"+
    "\1\6\1\0\32\6\13\0\131\6\3\0\6\6\2\0\6\6\2\0"+
    "\6\6\2\0\3\6\3\0\2\6\3\0\2\6\22\0\3\7\4\0";

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = yy_unpack_cmap(yycmap_packed);

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    42,    84,   126,    84,   168,    84,   210,    84,    84, 
       84,    84,    84,    84,    84,   252,   294,   336,   378,   420, 
      462,    84,    84,   504,   546,   588,   630,   672,   714,   756, 
      798,    84,   840,   882,   924,   966,  1008,  1050,  1092,  1134, 
     1176,  1218,    84,  1260,  1302,  1344,  1386,  1428,  1470,  1512, 
     1554,  1596,  1638,    84,  1680,    84,  1722,  1764,  1806,  1848, 
     1890,  1932,  1974,  2016,  2058,  2100,  2142,  2184,  2226,  2268, 
     2310,  2352,  2394,  2436,  2478,  2520,  2562,  2604,  2646,   210, 
     2688,  2730,   210,  2772,  2814,  2856,   210,  2898,  2940,   210, 
      210,    84,  2982,  3024,  3066,   210,  3108,  3150,  3192,  3234, 
     3276,  3318,   210,   210,  3360,   210,  3402,  3444,   210,   210, 
     3486,  3528,  3570,  3612,   210,   210,  3654,  3696,  3738,  3780, 
      210,   210,  3822,  3864,  3906,   210,  3948,   210
  };

  /** 
   * The packed transition table of the DFA (part 0)
   */
  final private static String yy_packed0 = 
    "\1\3\1\4\2\5\1\6\1\7\1\10\1\3\1\11"+
    "\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\3"+
    "\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30"+
    "\1\31\2\10\1\32\2\10\1\33\1\10\1\34\1\35"+
    "\1\36\1\37\4\10\1\3\17\40\1\41\32\40\54\0"+
    "\1\5\53\0\1\42\1\43\52\0\2\10\12\0\4\10"+
    "\3\0\20\10\20\0\1\44\54\0\1\45\35\0\2\10"+
    "\12\0\1\10\1\46\2\10\3\0\1\47\17\10\7\0"+
    "\2\10\12\0\4\10\3\0\3\10\1\50\14\10\7\0"+
    "\2\10\12\0\4\10\3\0\16\10\1\51\1\10\7\0"+
    "\2\10\12\0\4\10\3\0\5\10\1\52\12\10\20\0"+
    "\1\53\40\0\2\10\12\0\3\10\1\54\3\0\20\10"+
    "\7\0\2\10\12\0\4\10\3\0\4\10\1\55\3\10"+
    "\1\56\7\10\7\0\2\10\12\0\2\10\1\57\1\10"+
    "\3\0\20\10\7\0\2\10\12\0\4\10\3\0\5\10"+
    "\1\60\12\10\7\0\2\10\12\0\3\10\1\61\3\0"+
    "\6\10\1\62\10\10\1\63\7\0\2\10\12\0\2\10"+
    "\1\64\1\10\3\0\20\10\7\0\2\10\12\0\4\10"+
    "\3\0\3\10\1\65\14\10\52\0\1\66\1\42\1\4"+
    "\1\5\47\42\5\43\1\67\44\43\20\0\1\70\54\0"+
    "\1\71\34\0\2\10\12\0\2\10\1\72\1\10\3\0"+
    "\20\10\7\0\2\10\12\0\1\10\1\73\1\10\1\74"+
    "\3\0\20\10\7\0\2\10\12\0\4\10\3\0\2\10"+
    "\1\75\15\10\7\0\2\10\12\0\4\10\3\0\6\10"+
    "\1\76\11\10\7\0\2\10\12\0\4\10\3\0\7\10"+
    "\1\77\10\10\7\0\2\10\12\0\4\10\3\0\6\10"+
    "\1\100\11\10\7\0\2\10\12\0\1\101\3\10\3\0"+
    "\20\10\7\0\2\10\12\0\4\10\3\0\3\10\1\102"+
    "\14\10\7\0\2\10\12\0\1\10\1\103\2\10\3\0"+
    "\20\10\7\0\2\10\12\0\4\10\3\0\10\10\1\104"+
    "\7\10\7\0\2\10\12\0\4\10\3\0\1\105\17\10"+
    "\7\0\2\10\12\0\4\10\3\0\1\106\17\10\7\0"+
    "\2\10\12\0\1\107\3\10\3\0\20\10\7\0\2\10"+
    "\12\0\4\10\3\0\15\10\1\110\2\10\7\0\2\10"+
    "\12\0\4\10\3\0\6\10\1\111\11\10\1\0\4\43"+
    "\1\5\1\67\44\43\24\0\1\112\33\0\2\10\12\0"+
    "\3\10\1\113\3\0\20\10\7\0\2\10\12\0\4\10"+
    "\3\0\11\10\1\114\6\10\7\0\2\10\12\0\4\10"+
    "\3\0\1\10\1\115\16\10\7\0\2\10\12\0\4\10"+
    "\3\0\14\10\1\116\3\10\7\0\2\10\12\0\2\10"+
    "\1\117\1\10\3\0\20\10\7\0\2\10\12\0\2\10"+
    "\1\120\1\10\3\0\20\10\7\0\2\10\12\0\4\10"+
    "\3\0\3\10\1\121\14\10\7\0\2\10\12\0\4\10"+
    "\3\0\5\10\1\122\12\10\7\0\2\10\12\0\4\10"+
    "\3\0\6\10\1\123\11\10\7\0\2\10\12\0\4\10"+
    "\3\0\4\10\1\124\13\10\7\0\2\10\12\0\4\10"+
    "\3\0\1\125\5\10\1\126\11\10\7\0\2\10\12\0"+
    "\4\10\3\0\10\10\1\127\7\10\7\0\2\10\12\0"+
    "\1\10\1\130\2\10\3\0\20\10\7\0\2\10\12\0"+
    "\2\10\1\131\1\10\3\0\20\10\7\0\2\10\12\0"+
    "\4\10\3\0\6\10\1\132\11\10\7\0\2\10\12\0"+
    "\4\10\3\0\14\10\1\133\3\10\26\0\1\134\32\0"+
    "\2\10\12\0\2\10\1\135\1\10\3\0\20\10\7\0"+
    "\2\10\12\0\2\10\1\136\1\10\3\0\20\10\7\0"+
    "\2\10\12\0\4\10\3\0\1\137\17\10\7\0\2\10"+
    "\12\0\4\10\3\0\6\10\1\140\11\10\7\0\2\10"+
    "\12\0\4\10\3\0\10\10\1\141\7\10\7\0\2\10"+
    "\12\0\4\10\3\0\5\10\1\142\12\10\7\0\2\10"+
    "\12\0\1\10\1\143\2\10\3\0\20\10\7\0\2\10"+
    "\12\0\4\10\3\0\3\10\1\144\14\10\7\0\2\10"+
    "\12\0\4\10\3\0\11\10\1\145\6\10\7\0\2\10"+
    "\12\0\2\10\1\146\1\10\3\0\20\10\7\0\2\10"+
    "\12\0\4\10\3\0\6\10\1\147\11\10\7\0\2\10"+
    "\12\0\1\10\1\150\2\10\3\0\20\10\7\0\2\10"+
    "\12\0\4\10\3\0\7\10\1\151\10\10\7\0\2\10"+
    "\12\0\1\10\1\152\2\10\3\0\20\10\7\0\2\10"+
    "\12\0\4\10\3\0\2\10\1\153\15\10\7\0\2\10"+
    "\12\0\4\10\3\0\7\10\1\154\10\10\7\0\2\10"+
    "\12\0\4\10\3\0\10\10\1\155\7\10\7\0\2\10"+
    "\12\0\4\10\3\0\6\10\1\156\11\10\7\0\2\10"+
    "\12\0\4\10\3\0\10\10\1\157\7\10\7\0\2\10"+
    "\12\0\4\10\3\0\11\10\1\160\6\10\7\0\2\10"+
    "\12\0\1\10\1\161\2\10\3\0\20\10\7\0\2\10"+
    "\12\0\2\10\1\162\1\10\3\0\20\10\7\0\2\10"+
    "\12\0\2\10\1\163\1\10\3\0\20\10\7\0\2\10"+
    "\12\0\4\10\3\0\11\10\1\164\6\10\7\0\2\10"+
    "\12\0\4\10\3\0\1\165\17\10\7\0\2\10\12\0"+
    "\4\10\3\0\5\10\1\166\12\10\7\0\2\10\12\0"+
    "\4\10\3\0\4\10\1\167\13\10\7\0\2\10\12\0"+
    "\4\10\3\0\10\10\1\170\7\10\7\0\2\10\12\0"+
    "\4\10\3\0\12\10\1\171\5\10\7\0\2\10\12\0"+
    "\3\10\1\172\3\0\20\10\7\0\2\10\12\0\4\10"+
    "\3\0\3\10\1\173\14\10\7\0\2\10\12\0\3\10"+
    "\1\174\3\0\20\10\7\0\2\10\12\0\4\10\3\0"+
    "\10\10\1\175\7\10\7\0\2\10\12\0\2\10\1\176"+
    "\1\10\3\0\20\10\7\0\2\10\12\0\4\10\3\0"+
    "\1\177\17\10\7\0\2\10\12\0\4\10\3\0\12\10"+
    "\1\200\5\10\1\0";

  /** 
   * The transition table of the DFA
   */
  final private static int yytrans [] = yy_unpack();


  /* error codes */
  final private static int YY_UNKNOWN_ERROR = 0;
  final private static int YY_ILLEGAL_STATE = 1;
  final private static int YY_NO_MATCH = 2;
  final private static int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  final private static String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private final static byte YY_ATTRIBUTE[] = {
     0,  0,  9,  1,  9,  1,  9,  1,  9,  9,  9,  9,  9,  9,  9,  1, 
     1,  1,  1,  1,  1,  9,  9,  1,  1,  1,  1,  1,  1,  1,  1,  9, 
     1,  0,  0,  0,  0,  1,  1,  1,  1,  1,  9,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  9,  0,  9,  0,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  9,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[YY_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn; 

  /** 
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL = true;

  /** yy_atEOF == true <=> the scanner is at the EOF */
  private boolean yy_atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean yy_eof_done;

  /* user code: */
    public Lexer(ComplexSymbolFactory sf){
	this(new InputStreamReader(System.in));
        symbolFactory = sf;
    }
    protected IErrorManager errorManager;
    private StringBuffer sb;
    private ComplexSymbolFactory symbolFactory;
    private int csline,cscolumn;
    public Symbol symbol(String name, int code){
	return symbolFactory.newSymbol(name, code,new Location(yyline+1,yycolumn+1-yylength()),new Location(yyline+1,yycolumn+1));
    }
    public Symbol symbol(String name, int code, String lexem){
	return symbolFactory.newSymbol(name, code, new Location(yyline+1, yycolumn +1), new Location(yyline+1,yycolumn+yylength()), lexem);
    }
    protected void emit_warning(String message){
	errorManager.unrecognizedToken("Scanner at " + (yyline+1) + "(" + (yycolumn+1) + "): " + message);
    }
    protected void emit_error(String message){
	errorManager.emit_error("Scanner at " + (yyline+1) + "(" + (yycolumn+1) +  "): " + message);
    }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the split, compressed DFA transition table.
   *
   * @return the unpacked transition table
   */
  private static int [] yy_unpack() {
    int [] trans = new int[3990];
    int offset = 0;
    offset = yy_unpack(yy_packed0, offset, trans);
    return trans;
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the index of the last entry
   */
  private static int yy_unpack(String packed, int offset, int [] trans) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] yy_unpack_cmap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 2200) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   IOException  if any I/O-Error occurs
   */
  private boolean yy_refill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if (numRead < 0) {
      return true;
    }
    else {
      yy_endRead+= numRead;  
      return false;
    }
  }


  /**
   * Closes the input stream.
   */
  final public void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */

    if (yy_reader != null)
      yy_reader.close();
  }


  /**
   * Closes the current stream, and resets the
   * scanner to read from a new input stream.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>YY_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  final public void yyreset(java.io.Reader reader) throws java.io.IOException {
    yyclose();
    yy_reader = reader;
    yy_atBOL  = true;
    yy_atEOF  = false;
    yy_endRead = yy_startRead = 0;
    yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    yy_lexical_state = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  final public int yystate() {
    return yy_lexical_state;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  final public void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  final public String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  final public char yycharat(int pos) {
    return yy_buffer[yy_startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  final public int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    String message;
    try {
      message = YY_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number)  {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void yy_do_eof() throws java.io.IOException {
    if (!yy_eof_done) {
      yy_eof_done = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int yy_input;
    int yy_action;

    // cached fields:
    int yy_currentPos_l;
    int yy_startRead_l;
    int yy_markedPos_l;
    int yy_endRead_l = yy_endRead;
    char [] yy_buffer_l = yy_buffer;
    char [] yycmap_l = yycmap;

    int [] yytrans_l = yytrans;
    int [] yy_rowMap_l = yy_rowMap;
    byte [] yy_attr_l = YY_ATTRIBUTE;

    while (true) {
      yy_markedPos_l = yy_markedPos;

      boolean yy_r = false;
      for (yy_currentPos_l = yy_startRead; yy_currentPos_l < yy_markedPos_l;
                                                             yy_currentPos_l++) {
        switch (yy_buffer_l[yy_currentPos_l]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          yy_r = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          yy_r = true;
          break;
        case '\n':
          if (yy_r)
            yy_r = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          yy_r = false;
          yycolumn++;
        }
      }

      if (yy_r) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean yy_peek;
        if (yy_markedPos_l < yy_endRead_l)
          yy_peek = yy_buffer_l[yy_markedPos_l] == '\n';
        else if (yy_atEOF)
          yy_peek = false;
        else {
          boolean eof = yy_refill();
          yy_markedPos_l = yy_markedPos;
          yy_buffer_l = yy_buffer;
          if (eof) 
            yy_peek = false;
          else 
            yy_peek = yy_buffer_l[yy_markedPos_l] == '\n';
        }
        if (yy_peek) yyline--;
      }
      yy_action = -1;

      yy_startRead_l = yy_currentPos_l = yy_currentPos = 
                       yy_startRead = yy_markedPos_l;

      yy_state = yy_lexical_state;


      yy_forAction: {
        while (true) {

          if (yy_currentPos_l < yy_endRead_l)
            yy_input = yy_buffer_l[yy_currentPos_l++];
          else if (yy_atEOF) {
            yy_input = YYEOF;
            break yy_forAction;
          }
          else {
            // store back cached positions
            yy_currentPos  = yy_currentPos_l;
            yy_markedPos   = yy_markedPos_l;
            boolean eof = yy_refill();
            // get translated positions and possibly new buffer
            yy_currentPos_l  = yy_currentPos;
            yy_markedPos_l   = yy_markedPos;
            yy_buffer_l      = yy_buffer;
            yy_endRead_l     = yy_endRead;
            if (eof) {
              yy_input = YYEOF;
              break yy_forAction;
            }
            else {
              yy_input = yy_buffer_l[yy_currentPos_l++];
            }
          }
          int yy_next = yytrans_l[ yy_rowMap_l[yy_state] + yycmap_l[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = yy_attr_l[yy_state];
          if ( (yy_attributes & 1) == 1 ) {
            yy_action = yy_state; 
            yy_markedPos_l = yy_currentPos_l; 
            if ( (yy_attributes & 8) == 8 ) break yy_forAction;
          }

        }
      }

      // store back cached position
      yy_markedPos = yy_markedPos_l;

      switch (yy_action) {

        case 22: 
          {  return symbol("LT",LT);                       }
        case 129: break;
        case 21: 
          {  return symbol("GT",GT);                       }
        case 130: break;
        case 14: 
          {  return symbol("RBRACK",RBRACK);               }
        case 131: break;
        case 13: 
          {  return symbol("LBRACK",LBRACK);               }
        case 132: break;
        case 8: 
          {  return symbol("QESTION",QUESTION);            }
        case 133: break;
        case 114: 
          {  return symbol("PACKAGE",PACKAGE);             }
        case 134: break;
        case 115: 
          {  return symbol("EXTENDS",EXTENDS);             }
        case 135: break;
        case 42: 
          {  sb = new StringBuffer(); csline=yyline+1; cscolumn=yycolumn+1; yybegin(CODESEG);     }
        case 136: break;
        case 3: 
        case 4: 
          {                                                }
        case 137: break;
        case 31: 
        case 32: 
          {  sb.append(yytext());  }
        case 138: break;
        case 7: 
        case 17: 
        case 18: 
        case 19: 
        case 20: 
        case 24: 
        case 25: 
        case 26: 
        case 27: 
        case 28: 
        case 29: 
        case 30: 
        case 37: 
        case 38: 
        case 39: 
        case 40: 
        case 41: 
        case 43: 
        case 44: 
        case 45: 
        case 46: 
        case 47: 
        case 48: 
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 57: 
        case 58: 
        case 59: 
        case 60: 
        case 61: 
        case 62: 
        case 63: 
        case 64: 
        case 65: 
        case 66: 
        case 68: 
        case 69: 
        case 70: 
        case 71: 
        case 72: 
        case 74: 
        case 75: 
        case 76: 
        case 77: 
        case 78: 
        case 80: 
        case 81: 
        case 83: 
        case 84: 
        case 85: 
        case 87: 
        case 88: 
        case 92: 
        case 93: 
        case 94: 
        case 96: 
        case 97: 
        case 98: 
        case 99: 
        case 100: 
        case 101: 
        case 104: 
        case 106: 
        case 107: 
        case 110: 
        case 111: 
        case 112: 
        case 113: 
        case 116: 
        case 117: 
        case 118: 
        case 119: 
        case 122: 
        case 123: 
        case 124: 
        case 126: 
          {  return symbol("ID",ID,yytext());              }
        case 139: break;
        case 2: 
        case 5: 
        case 16: 
        case 23: 
          {  emit_warning("Unrecognized character '" +yytext()+"' -- ignored");  }
        case 140: break;
        case 53: 
          {  yybegin(YYINITIAL); return symbolFactory.newSymbol("CODE_STRING",CODE_STRING, new Location(csline, cscolumn),new Location(yyline+1,yycolumn+1+yylength()), sb.toString());  }
        case 141: break;
        case 91: 
          {  return symbol("PERCENT_PREC",PERCENT_PREC);   }
        case 142: break;
        case 6: 
          {  return symbol("STAR",STAR);                   }
        case 143: break;
        case 11: 
          {  return symbol("DOT",DOT);                     }
        case 144: break;
        case 90: 
          {  return symbol("WITH",WITH);		        }
        case 145: break;
        case 103: 
          {  return symbol("SUPER",SUPER);                 }
        case 146: break;
        case 12: 
          {  return symbol("BAR",BAR);                     }
        case 147: break;
        case 102: 
          {  return symbol("START",START);		        }
        case 148: break;
        case 109: 
          {  return symbol("IMPORT",IMPORT);	        }
        case 149: break;
        case 120: 
          {  return symbol("PARSER",TERMINAL);	        }
        case 150: break;
        case 55: 
          {  return symbol("COLON_COLON_EQUALS",COLON_COLON_EQUALS);    }
        case 151: break;
        case 67: 
          {  return symbol("NON",NON);		        }
        case 152: break;
        case 108: 
          {  return symbol("ACTION",ACTION);	        }
        case 153: break;
        case 125: 
          {  return symbol("PRECEDENCE",PRECEDENCE);       }
        case 154: break;
        case 127: 
          {  return symbol("NONTERMINAL",NONTERMINAL);     }
        case 155: break;
        case 82: 
          {  return symbol("INIT",INIT);		        }
        case 156: break;
        case 95: 
          {  return symbol("RIGHT",RIGHT);		        }
        case 157: break;
        case 89: 
          {  return symbol("LEFT",LEFT);		        }
        case 158: break;
        case 9: 
          {  return symbol("SEMI",SEMI);                   }
        case 159: break;
        case 105: 
          {  return symbol("PARSER",PARSER);	        }
        case 160: break;
        case 79: 
          {  return symbol("CODE",CODE);		        }
        case 161: break;
        case 15: 
          {  return symbol("COLON",COLON);                 }
        case 162: break;
        case 10: 
          {  return symbol("COMMA",COMMA);                 }
        case 163: break;
        case 121: 
          {  return symbol("NONASSOC",NONASSOC);           }
        case 164: break;
        case 86: 
          {  return symbol("SCAN",SCAN);		        }
        case 165: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
              {     return symbolFactory.newSymbol("EOF",sym.EOF);
 }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }


}
