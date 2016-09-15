package all53e;
import java.awt.* ;
import java.lang.* ;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.io.* ;

/////////////////////////////////////////////////////////////////////////
//
//               %%%% MyConstants %%%%
//
/////////////////////////////////////////////////////////////////////////
class MyConstants
{   
    // time to wait for a socket connection
	public static int ConnectionTO = 3000;
    // history file
 	public static FileWriter session ;
    // run parameters hashtable
 	public static Properties pairs = null;
    // system parameters : current date/time, user directory, file separator
    public static Properties SP;
    // maximum strings to be intercepted simultaneous - per address 
    public static int ToIntercept = 5 ; 
    // statistics file
	public static FileWriter statFile = null;
	public static long currentTime;

    // button definitions
    public static final String [] buttonFunctions =
    {
      "?",
      "file",
      "wait",
      "loop",
      "for",
      "inc",
      "intercept",
      "button",
      "buttonwait",
      "if",
      "open",
      "cs",
      "break",
      "expr",
      "sif",
      "display",
      "print",
      "store",
      "get",
      "end",
      "general"
    };
    public static final String terminate = "end";
    public static final int fileFunction = 1;
    public static final int waitFunction = 2;
    public static final int loopFunction = 3;
    public static final int forFunction = 4;
    public static final int incFunction = 5;
    public static final int interceptFunction = 6;
    public static final int buttonFunction = 7;
    public static final int buttonWFunction = 8;
    public static final int ifFunction = 9;
    public static final int openFunction = 10;
    public static final int csFunction = 11;
    public static final int breakFunction = 12;
    public static final int exprFunction = 13;
    public static final int ifsFunction = 14;
    public static final int displayFunction = 15;
    public static final int printFunction = 16;
    public static final int storeFunction = 17;
    public static final int getFunction = 18;
    public static final int noneFunction = 19;
    public static Hashtable<String, Integer> buttonDictionary;
	public static final int bDlength = buttonFunctions.length;
    public static final int max_functions_per_button = 150;
    // alias definitions
	public static final int ips = 66;
    public static Hashtable<String, String> alias = null;
    public static String [] potential;
    public static Hashtable<String,String > reversealias = null;
    // connections definitions
	public static String [] IP_addresses_names;
	public static ipdef [] ipconnections;
    public static String ip_file_path = "";
    // global
    public static ButtonPanel bp;
	public static TextWindow tw;
	public static TextWindowRun twr;
	public static InputWindow iw;
    // audit
	public static auditStatus [] connections;

    // random
    public static long seed = 1;
    public static Random random;
    //debug
    public static boolean printdebug_flag = false;
    public static boolean Userdebug_flag = false;
    public static boolean ipdefdebug_flag = false;
    public static boolean buttondebug_flag = false;
    public static boolean execdebug_flag = false;
    public static boolean counterdebug_flag = false;
    // constants
	public static int iconsN = 20 ;  
	public static ButtonIcons BI = null;  
    public static final int selftest = 0;
    public static final int swtest = 1;
    public static final int hwtest = 2;
    public static final int datatest = 3;
    public static final int usertest = 4;
    public static final int pkgtest = 5;
    public static final boolean not_setPanel = false;
    public static final boolean setPanel = true;
	public static final String addressfile = "ip_addr_conv";
	public static final String buttonfile1 = "selfButtons.txt";
	public static final String buttonfile1save = "selfButtons.txt.save";
	public static final String buttonfile2 = "swButtons.txt";
	public static final String buttonfile2save = "swButtons.txt.save";
	public static final String buttonfile3 = "hwButtons.txt";
	public static final String buttonfile3save = "hwButtons.txt.save";
	public static final String buttonfile4 = "dataButtons.txt";
	public static final String buttonfile4save = "dataButtons.txt.save";
	public static String buttonfile5 = "userButtons.txt";
	public static String buttonfile5save = "userButtons.txt.save";
	public static String buttonfile6 = "pkgButtons.txt";
	public static String buttonfile6save = "pkgButtons.txt.save";
    public static int Screen_x = 930 ;
    public static int Screen_y = 900 ;
    public static int button_y = 10;
    
    public static int output_x = Screen_x / 30;
    public static int output_y = Screen_y / 4;
    public static int input_x = Screen_x / 2;
    public static int input_y = Screen_y / 4;
    public static int button_x = ( Screen_x / 4 ) * 3;    
    public static int button_wx = Screen_x / 3;
    public static int button_wy = Screen_y / 3;
    public static int unit_type = 0;
    public static final Color background = Color.lightGray ;
    // block types for button
    public static final int noneType = -1;
    public static final int simpleType = 0;
    public static final int loopType = 2;
    public static final int incType = 1;
    public static final int forType = 3;
    public static final int ifType = 4;
    public static final int ifTypeNo = 5;
    public static final int endType = 6;
    public static final int buttonType = 7;
    public static final int interceptType = 8;
    public static final int exprType = 9;
    public static final int ifsType = 10;
    
    public static final int palete = 11;
    public static final Color cyan = new Color ( 153, 153, 204 );
    public static final Color pink = Color.pink;
    public static final Color green = Color.green;
    public static final Color orange = Color.orange.darker();
    public static final Color blue = new Color ( 0,255, 255 );
    public static final Color magenta = new Color ( 140, 50, 180 );    
    public static final Color bcl [] =
    {
       green,
       blue,     
       cyan,
       magenta,
       orange,
       pink,
       Color.red,
       Color.yellow,
       Color.magenta,
       Color.orange,
       Color.cyan
    };

    public static final String bclName [] =
    {
        "green",
		"blue",        
        "cyan0",
        "magenta1",
        "brown",
        "pink",
        "red",
        "yellow",
        "magent0",
        "orange",
        "cyan1"
    };

    public static final int Not_answer = -1;
	public static final int Error = 0;
	public static final int Inactive = 1;
	public static final int Standby = 3;
	public static final int Active = 2;
	public static final String auditCommand =    "/application/user/audit";
	public static final String auditAnswer =    "auditforjava";
    public static final int ipport = 20001 ;
 /**********************************************************************/
    public static void trace (  String tt )
    {
   	    long crt = System.currentTimeMillis() - currentTime;
   	    System.out.println ("trace : " + crt + " " + tt );
	}
    public static int ComplexType ( String s )
    {
	    int type = simpleType;
		if ( s == null )
		{
			 type = noneType;
		}
		else
		{
	        if ( s.startsWith ("loop ") )
	          type = loopType;
	        if ( s.startsWith ("inc ") )
	          type = incType;
	        if ( s.startsWith ("for ") )
	          type = forType;
	        if ( s.startsWith ("if ") )
	          type = ifType;
	        if ( s.startsWith ("intercept ") )
	          type = interceptType;
	        if ( s.startsWith ("expr ") )
	          type = exprType;
	        if ( s.startsWith ("sif ") )
	          type = ifsType;	      
	        if ( s.startsWith ("?") )
	          type = ifTypeNo;
            if ( s.startsWith ("END") )
              type = endType;
        }

	return type;
    }
    /*****************************************************/
    //  return the equivalent of a variable 
    //
    /*****************************************************/
    public static String lookForPair ( String ss )
    {
        String ret = ss ;

        String equivalent = pairs.getProperty ( ss ) ;
        if ( equivalent != null ) ret = equivalent ;

        return ( ret ) ; 
    }
}  // MyConstants class

