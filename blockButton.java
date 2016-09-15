package all53e;
import java.awt.*;
import java.lang.*;
import javax.swing.*;
import java.util.* ;

/************************************************************************/
//
// build the inner blocks of the functions
//
/************************************************************************/
class blockButton
{
    public int blocktype;
    public String command;
    public Vector<blockButton> vs;

	public blockButton ( )
	{
        blocktype = MyConstants.noneType;
        command = "";
        vs = new Vector<blockButton> ( 0,1 );
	}
// builds a simple ( final ) block - without any blocks into its vector
    public blockButton ( int tt, String fbase )
    {
        blocktype = tt;
        command = fbase;
        vs = new Vector<blockButton> ( 0,1 );
        if ( MyConstants.buttondebug_flag ) 
        {      
        	Utils.printList ( this );
		}                   
    }
/*   each functions terminates with ';'
     loop/for/inc functions -after the last function ( terminated, of course with ; )
     has '@' to indicate the end of the block
     if function has before the TRUE block ( after address ) ';'
     the TRUE block terminates with '?' after its last function ( which obviously termiates with ';'
     the FALSE block terminates with '@' after its last function ( which obviously termiates with ';'
*/
    // builds blocks connected to the vector of a parent block
    // 'if','sif' functions build 2 blocks ( one for tes and one for no )
	public blockButton ( int type, String f, String fbase, int complexO )
	{
        String component [];
        int component_type [];
        int complex = complexO;

        vs = new Vector<blockButton> ( 0,1 );
        blocktype = type;
        command = fbase ;
        debug ( false, "type=" + type + " initial=" + f  + " fbase=" + fbase );
        if ( ComplexType ( f ) != MyConstants.noneType )
        {
			component = f.split(";");
            boolean work = true;
            component_type = new int [ component.length ];
            // split the body of the complex function into functions
            for ( int k =0; k < component.length; k ++ )
            {
	            component_type [ k ] = ComplexType ( component [ k ] );
	            debug ( false, "component=" + component [ k ] + " type=" + component_type [ k ] );
            }
            // run along all its functions and build the correspondent blocks
            for ( int k =0; k < component.length; k ++ )
            {
	            switch ( component_type [ k ] )
                {
	            case MyConstants.simpleType:
	            case MyConstants.interceptType:
	            case MyConstants.exprType:
	            	if ( ! component [ k ].trim().equals("") )
                    	vs.add ( new blockButton( component_type [ k ], component [ k ] ) );
	                break;
	                
                case MyConstants.noneType:
	            case MyConstants.endType:
	                break;
	                
	            case MyConstants.loopType:
	            case MyConstants.incType:
	            case MyConstants.forType:
                    complex ++ ;
                    if ( complex > 2 )
                    {
                          JOptionPane.showMessageDialog( new JFrame(), "Nesting of complex function is overloaded", "Error", JOptionPane.ERROR_MESSAGE );
                          return;
                    }
                    StringTokenizer stt = new StringTokenizer ( component [ k ], " ", false );
                    // take the mnemonic of the function
                    String r = stt.nextToken() ;
                    int tt = ComplexType ( r );
                    // f1 = function + parameter
                    String f1 = r + " " + stt.nextToken() + " ";
                    r = "";
                    // build the body of the function into string r
                    while ( stt.hasMoreTokens() )
                    {
                    	r = r + stt.nextToken() + " ";
                	}
                    // add 'end' delimiter
                    r = r + " ;";
                    debug ( false, "f1=" + f1 + "#r=" + r );
                    int y = k + 1;
                    // add the other functions of the block, until end
                    while ( component_type [ y ] != MyConstants.endType )
                    {
	                    r = r + component [ y ++ ] + ";";
                    }
                    // set loop index to the first comonent not taken yet
                    k = y;
                    debug(false,"k="+k+" r=" + r + "#" );
                    // add he block
                    vs.add ( new blockButton ( tt, r, f1, complex ) );
	                break;
	                
	            case MyConstants.ifsType:	            	                        
	            case MyConstants.ifType:	            
                    complex ++ ;
                    if ( complex > 2 )
                    {
                          JOptionPane.showMessageDialog( new JFrame(), "Nesting of complex function is overloaded", "Error", JOptionPane.ERROR_MESSAGE );
                          return;
                    }
                    stt = new StringTokenizer ( component [ k ], " ", false );
                    r = stt.nextToken() ;
                    tt = ComplexType ( r );
                    // function + limits
                    f1 = component [ k ];
                    debug ( false, "f1=" + f1 + "#" );
                    y = k + 1;
                    r = "";
                    // add the other functions of the block, until 'no' block
                    while ( component_type [ y ] != MyConstants.ifTypeNo ) 
                    {
	                    r = r + component [ y ++ ] + ";";
                    }
                    // set loop index to 'no' component
                    k = y - 1;
                    debug(false,"if type k=" + k + " r=" + r + "#" );
                    // the 'yes' block may be empty
                    if ( r.equals("") ) r = null;
                    // add the block
                    vs.add ( new blockButton ( tt, r, f1, complex ) );
	                break;
	                
	            case MyConstants.ifTypeNo:
                    r = "";
                    y = k + 1;
                    // add the other functions of the block, until 'end' block
                    while ( component_type [ y ] != MyConstants.endType )
                    {
	                    r = r + component [ y ++ ] + ";";
                    }
                    // set loop index to the first comonent not taken yet
                    k = y ;
                    debug(false,"if no type k=" + k + " r=" + r + "#" );
                    // the 'no' block may be empty
                    if ( r.equals("") ) r = null;
                    // add the block
                    vs.add ( new blockButton ( MyConstants.ifTypeNo, r, "?" , complex) );
	            	break;   
                } // switch
            } // for
        }
        if ( MyConstants.buttondebug_flag ) 
        {   
            debug ( false, "final block\n" );
        	Utils.printList ( this );
		}               
    }
    private int ComplexType ( String s )
    {
		int type = MyConstants.simpleType;
		if ( s == null )
		{
			 type = MyConstants.noneType;
		}
		else
		{
			if ( s.equals ( " " ) )
				type = MyConstants.noneType;				
			if ( s.startsWith ("loop") )
		  		type = MyConstants.loopType;
			if ( s.startsWith ("inc") )
		  		type = MyConstants.incType;
			if ( s.startsWith ("for") )
		  		type = MyConstants.forType;
			if ( s.startsWith ("if") )
		  		type = MyConstants.ifType;
			if ( s.startsWith ("sif") )
		  		type = MyConstants.ifsType;
			if ( s.startsWith ("intercept") )
		  		type = MyConstants.interceptType;
			if ( s.startsWith ("expr") )
		  		type = MyConstants.exprType;
			if ( s.startsWith ("@") )
		  		type = MyConstants.endType;
			if ( s.startsWith ("?") )
		  		type = MyConstants.ifTypeNo;
		}
		return type;
	}

    private static void debug ( boolean doflag, String str )
    {
        if ( ( doflag ) || ( MyConstants.buttondebug_flag ) )
        {
            System.out.println( "blockButton : " + str ) ;
        }
    }
    public void clear()
    {
		vs.removeAllElements();
	}
}
