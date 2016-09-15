package all53e;
import java.awt.*;
import java.lang.*;
import javax.swing.*;
import java.util.* ;
import java.io.*;
import java.util.StringTokenizer;
/************************************************************************/
//
// implemets users' button definition and execution
//      --> JButton ub
//      --> vs  - vector of functions ( simple or blockButton ( vector for complex functions )
//      --> execButton ( Thread ) 
//      --> vector of execResult ( results )
//           every execution build a new execResult block 

/************************************************************************/
class userButton
{
// the 'vs' contains the first level functions of the button
// the next levels are build into blockButton and are members of the vector of the
// block of the primary level function
    public Vector<blockButton> vs ;
    public DefineButton adr;
	public JButton ub;
	public String button_name;
    public Vector<execResult> results ;
    private Color button_color = MyConstants.background;
    private Color default_color = MyConstants.background;
    private ImageIcon button_icon = null;
    public execButton exec = null;
	public int buttonIcon = -1;
	public int buttonColor = -1;

	public userButton ( )
	{
        vs = new Vector<blockButton> ( 0,1 );
        results = new Vector<execResult> ( 0,1 );
	}
	public userButton ( String name, DefineButton a, Color bcolor )
	{
		adr = a;
		button_name = name;
        vs = new Vector<blockButton> ( 0,1 );
        results = new Vector<execResult> ( 0,1 );
 	    ub = new JButton ( button_name );
 	    button_color = bcolor;
 	    for ( int i =0; i < MyConstants.bcl.length; i ++)
 	    {
	 	    if ( button_color == MyConstants.bcl [ i ] )
	 	    {
		 	    buttonColor = i ;
		 	    break;
	 	    } 
	    }
 	    button_icon = null;
 	    ub.setBackground(button_color);
        // create the button's thread
		exec = new execButton( this, adr );
        // wait until an execution request
		exec.start();
	}
	public userButton ( String name, DefineButton a, ImageIcon icon, String iconF )
	{
		adr = a;
		button_name = name;
        vs = new Vector<blockButton> ( 0,1 );
        results = new Vector<execResult> ( 0,1 );
  	    button_icon = icon; 
		for ( int i =0; i < MyConstants.iconsN; i ++)
 	    {
	 	    if ( iconF == MyConstants.BI.filenames [ i ] )
	 	    {
		 	    buttonIcon = i ;
		 	    break;
	 	    } 
	    }  	          
 	    ub = new JButton ( button_name, button_icon );
 	    button_color = MyConstants.background;
 	    ub.setBackground(button_color);
        // create the button's thread
		exec = new execButton( this, adr );
        // wait until an execution request
		exec.start();
	}
	
	public userButton ( String name, DefineButton a )
	{
		adr = a;
		button_name = name;
        vs = new Vector<blockButton> ( 0,1 );
        results = new Vector<execResult> ( 0,1 );
 	    ub = new JButton ( button_name );
 	    button_color = MyConstants.background;
  	    button_icon = null;	    
 	    ub.setBackground(button_color);
        // create the button's thread
		exec = new execButton( this, adr );
        // wait until an execution request
		exec.start();
	}	
	public userButton ( userButton p )
	{
		button_name = p.button_name;
		button_color = p.button_color;
		button_icon = p.button_icon;
		buttonColor = p.buttonColor;
		buttonIcon = p.buttonIcon;
        vs = new Vector<blockButton> ( 0,1 );
        for ( int k = 0; k < p.vs.size() ; k ++ )
        {
            vs.add( ( blockButton )p.vs.get( k ) );
        }
	}
	public void setColor ( Color bcolor )
	{
		button_color = bcolor;
		for ( int i =0; i < MyConstants.bcl.length; i ++)
 	    {
	 	    if ( button_color == MyConstants.bcl [ i ] )
	 	    {
		 	    buttonColor = i ;
		 	    break;
	 	    } 
	    }
 	    ub.setBackground(button_color);		
	}
	public void setIcon ( ImageIcon f, String fname )
	{
		ub.setIcon ( f ) ;
		if ( MyConstants.BI != null )
		{
		  for ( int i =0; i < MyConstants.iconsN; i ++)
 	      {
	 	    if ( fname == MyConstants.BI.filenames [ i ] )
	 	    {
		 	    buttonIcon = i ;
		 	    break;
	 	    }
            else MyConstants.BI.addIconsFromDefinition ( fname, f );
	      }	
        }
        else
        {
	        MyConstants.BI = new ButtonIcons ();
	        MyConstants.BI.addIconsFromDefinition ( fname, f );
        }	
	}
	public void retIcon()
	{
		ub.setIcon ( null ) ;
		buttonIcon = -1;
	}
	public void setDefaultColor ( Color dflt )
	{
		default_color = dflt;
	}
	public void retColor ( )
	{
		button_color = default_color;
		for ( int i =0; i < MyConstants.bcl.length; i ++)
 	    {
	 	    if ( button_color == MyConstants.bcl [ i ] )
	 	    {
		 	    buttonColor = i ;
		 	    break;
	 	    } 
	    }
 	    ub.setBackground(button_color);		
	}
	public Color getColor()
	{
		return button_color;
	}
    // add to the blocks vector 2 blocks : one for the 'yes' part of the function
    // and one for the 'no' part of the function
private void SetIfBlock ( String function, String f1, int type )
{
    // function is the entire string describeing the 'if', 'sif'function
    // r is the part of the function containing the 'yes' and 'no' blocks
    // f1 is the condition part :
    // for 'if' is the wait time, the text to be checked and the address 
    // from whom we expect the text
    // for 'sif' is the condition to check
    String r = function.substring ( f1.length() + 1, function.length() );
    debug ( false, "f=" +function + "f1=" + f1 + "#r=" + r );
    // the 'yes' part will finish with '?'
    // it may be empty ( means the string begins with '?' )
    StringTokenizer z = new StringTokenizer ( r, "?", false );
    if ( r.startsWith ( "?" ) )
    {
        vs.add ( new blockButton( type, null, f1, 1 ) );
    }
    else
    {
        String yes = z.nextToken().trim();
        if ( ( yes.indexOf ("if ") == -1 ) && ( yes.indexOf ("sif ") == -1 ) )
        {
            // look for the 'end' of the last complex function under the 'yes' block
            int ll = yes.lastIndexOf ( "@" ); 
            if ( ll != -1 ) 
            {
                // found - look for the common delimiter after the end of the last
                ll = yes.lastIndexOf( ";" );                 	
                yes = yes.substring( 0, ll ) ;
            }
            // add the 'yes' block
            vs.add ( new blockButton ( type, yes, f1, 1 ) );
        }
        else
        {
            // DOES NOT support 'if' under 'if'
            vs.add ( new blockButton( type, null, f1, 1 ) );
        }
    }
    if ( z.hasMoreTokens() )
    {
        // build the 'no' part - the rest of the string
        String no = z.nextToken().trim();
        if ( no.startsWith(";") ) // ???????
        {
            no = no.substring( 1, no.length() );
        }                   	
        vs.add ( new blockButton ( MyConstants.ifTypeNo, no, "?", 1 ) );
    }
    else
    {
        // the 'no' part is empty
        vs.add ( new blockButton( MyConstants.ifTypeNo, null, "?", 1 ) );
    }
}
// build the blocks vector of a function
public boolean addFunction ( String function )
{
    String f;
    int type;
    StringTokenizer stc ;

    if ( ( vs.size() < MyConstants.max_functions_per_button ) && ( function != null ) )
    {
        // ';' is the delimiter of a primitive function
        stc = new StringTokenizer ( function, ";", false ) ;
        if ( stc.countTokens() >= 1 )
        {
            // f is the mnemonic of the function and parameters ( if any )
            f = stc.nextToken() ;
            type = MyConstants.ComplexType ( f );
            debug( false,"function="+function+"#f="+f);
            switch ( type )
            {
                case MyConstants.simpleType:
                case MyConstants.exprType:
                case MyConstants.interceptType:
                    vs.add ( new blockButton ( type, f ) );
                    break;
                
            case MyConstants.ifType:
                    // look for parameters
                    StringTokenizer stt = new StringTokenizer ( f, " ", false ) ;
                    // first parameter is supposed to be waiting time
                    // f1 is the mnemonic
     	            String f1 = stt.nextToken() + " " ;
                    // check if it is a random value ( between [ ] may be spaces !!! )
                    int ii = f.indexOf('[') ;
                    if ( ii != -1 )
                    {
                        // yes - copy all the string includeing the text to be checked
                        f1 = f1 + f.substring( ii, f.lastIndexOf('"' ) + 1 ) + " " ;
                    }
                    else
                    {
                        // no - take next ( which is the time ) and the text
                        f1 = f1 + stt.nextToken() + " " + f.substring( f.indexOf('"'), f.lastIndexOf('"' ) + 1 ) + " " ;               	
                    }
                    // add to the string the address
                    f1 = f1 + f.substring(f1.length(), f.length());
                    // build and add the 'yes' and 'no' blocks
                    SetIfBlock ( function, f1, type ) ;
                    break;
                
                case MyConstants.ifsType:
                    // look for parameters
                    stt = new StringTokenizer ( f, " ", false ) ;
                    // f1 is the mnemonic
     	            f1 = stt.nextToken() + " " ;           
                    // add the condition to be checked
                    f1 = f1 + f.substring( f.indexOf('"'), f.lastIndexOf('"' ) + 1 ) ;               	
                    // build and add the 'yes' and 'no' blocks
                    SetIfBlock ( function, f1, type ) ;
                    break;
                
                default: // other complex functions ( loop, inc, for )
                    stt = new StringTokenizer ( f, " ", false ) ;
                    // f1 is the mnemonic
     	            f1 = stt.nextToken() + " " ;           
                    // look for parameters
                    if ( stt.hasMoreTokens() )
                    {
                        f1 = f1 + stt.nextToken() ; 
                    }
                    f1 = f1 + " ";
                    // r is the string of the lock under this complex function
                    String r = function.substring ( f1.length(), function.length() );
                    vs.add ( new blockButton ( type, r, f1, 1 ) );
                    break;
            }
        }
        return true;
    }
    else
    {
		JOptionPane.showMessageDialog( new Frame(), "Button exceed number of function " + vs.size(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}

    public void clear()
    {
    	button_name = "";
    	vs.removeAllElements();
    }

    private static void debug ( boolean doflag, String str )
    {
        if ( ( doflag ) || ( MyConstants.buttondebug_flag ) )
        {
            System.out.println( "userButton : " + str ) ;
        }
    }
}
