package all53e;
import java.awt.*;
import java.lang.*;
import javax.swing.*;
import java.util.* ;
import java.io.*;
import java.util.StringTokenizer;
/************************************************************************/
//
//   class execute a button's functions
//
/************************************************************************/
class returnParameters
{
    public int start;
    public int stop;
    public int step;
    public String common;
    public String address;
    public boolean error;

    public returnParameters()
    {
        start = 0;
        stop = 0;
        step = 0;
        common = "";
        address = "";
        error = true;
    }
}
class execButton extends Thread
{
	private final int breakCode = 2;
	private final int okCode = 1;
	private final int errorCode = 0;
	
	private DefineButton adr;
	private boolean okCondition = false;
	private execResult execstat ;
    private execButton caller;
    private int generalError ;
    private String paramB = "";

    public boolean is_executeing;
    public boolean deleted;
    public boolean stopButton;
    public userButton parent;

/************************************************************************/
//
//   constructor - connect this thread to an user button for the tester
//
/************************************************************************/
	public execButton ( userButton ub, DefineButton a )
	{
		parent = ub;
        adr = a ;
        okCondition = false;
        execstat = null;
        caller = null;
        deleted = false;
        is_executeing = false;
        stopButton = false;
	}
	// who activated this thread : null is from mouse action ( primary execution )
	// otherwise from a buttonWait function, and is the thread executinf this function
	
/*****************************************************/
	public void Set ( execButton caller )
	{ 
         this.caller = caller ;
	}
/*****************************************************/
    public int getError ()
    {
/*        return ( generalError ) ; */
        return ( okCode ) ;
    }
/********************************************************************/
private boolean continueButton ( int error )    
{
	if ( error == breakCode ) return ( false ) ; 
/*	if ( error != okCode ) return ( false ) ; */
	return ( true ) ;
}
/************************************************************************/
//
//   run function of the thread
//   execute a function from the button's body; gives to other threads the chance
//
/************************************************************************/
public synchronized void run ( )
{
	while ( ! deleted && ! is_executeing )
	{
        // wait until notified to start
        // avoid executing of a button which is already in execution
        // avoid executing of a button which is deleted ( from another button )
        try
        {
			wait(); 
		}
	    catch ( InterruptedException e )
	    {
	        debug ( true, "Interrupted exception from button execution(0)" );
	    }
        // registed it into history file
		try
		{
            if ( MyConstants.session != null )
			    MyConstants.session.write("Execute : " + adr.i_am() + " / " + parent.button_name + "\n" );
		}
		catch ( IOException e )
		{
		}
        debug ( false,"Execute button : " + parent.button_name  );
        is_executeing = true;
        stopButton = false;
        okCondition = false;
        Color color = parent.ub.getBackground ( );
        // chage its color
        parent.ub.setBackground ( Color.white ) ;
        // attach a result block
        execstat = new execResult(); 
        // register the parameter 
        if ( ! paramB.equals ("") )
        {
            execstat.SetNotification ( paramB,"parameter" );
            debug ( false, "parameter =" + paramB ) ;
        }
 //     Utils.printList ( new blockButton ( parent.vs ) );
        int error = executeFunction ( parent.vs );
        this.yield();
        // return the original color
        parent.ub.setBackground ( color ) ;
        if ( execstat.IsValid() )
        {
            // result has a meaing ==> check if passed or failed
             execstat.conclude();
             // add the result to its own statistics or to its caller ( another button )
             if ( caller == null )
            	 parent.results.add ( execstat );
             else
            	 caller.parent.results.add ( execstat );
        } 
        // in case another button called 'me' to execute a buttonwait function
        // I need to tell him I finished      
        paramB = "";
        if ( caller != null )
        {
            synchronized ( caller )
            {
                caller.notify();
            }
        }
        is_executeing = false;
    }
 }
/************************************************************************/
//
//   set the "if" true condition
//
/************************************************************************/
 public void notify( String notification, boolean wakeup )
 {
 	okCondition = true;
 	execstat.SetNotification ( notification,"notification" );
    // it came from if ==> notify
    // it came from intercept without time ==> do not wake
    if ( wakeup )
    {
 	    synchronized ( this )
        {
            this.notify();
        }
    }
 }
 /********************************************************************/
 //
 //  main execution procedure - run along the entire primary block vector
 //
 /********************************************************************/
 private int executeFunction ( Vector<blockButton> vs )
 {
	int error = okCode;
    for ( int k = 0; ( k < vs.size() ) && continueButton ( error ); k ++ )
    {
        blockButton block = ( blockButton )vs.get ( k );
        // stop_button is set when break button from the button frame is pressed
        if ( ! stopButton )
        {
	        switch ( block.blocktype )
	        {
                // if, sif functions has 2 blocks ( yes and no )
		    case MyConstants.ifType:
		    case MyConstants.ifsType:
		    	k++;
		    	blockButton temp = ( blockButton ) vs.get ( k );		        
	        	error = executeFunction ( block, temp );
	        	break;
	        default:
	        	error = executeFunction ( block, null );	        
	        	break;
        	}	
        }
        else
        {
            break;
        }
	}
	generalError = error;
    return error;
 }
 /********************************************************************/
 //
 //  execute a block function
 //
 /********************************************************************/
 private int executeFunction ( blockButton block, blockButton temp )
 {
    int error = okCode;
    int type = block.blocktype;
    debug ( false,"executeFunction : " + type );
    switch ( type )
    {
    case MyConstants.exprType :
        error = executeExpression ( block.command );
        break;
                
    case MyConstants.simpleType :
        error = executeSimple ( block.command );
        break;
                
    case MyConstants.interceptType :
        error = executeIntercept ( block.command );
        break;
                
    case MyConstants.loopType :
        error = executeLoop ( block.command, block.vs );
        break;
                
    case MyConstants.forType :
        error = executeFor ( block.command, block.vs );
        break;
                
    case MyConstants.incType :
        error = executeInc ( block.command, block.vs );
        break;
                
    case MyConstants.ifsType :
    case MyConstants.ifType :
        error = executeIf ( type, block.command, block.vs, temp.vs );
        break;
                
    }
    debug ( false,"end executeFunction : " + type + " error = " + error );

    return error;
}
/********************************************************************/
// called for functions under a 'for' function
// address is the current one
/********************************************************************/
 private int executeFunction ( Vector<blockButton> vs, String address )
 {
    int error = okCode;
    for ( int k = 0; ( k < vs.size() ) && continueButton ( error ); k ++ )
    {
        blockButton block = ( blockButton )vs.get ( k );
        debug ( false,"executeFunction with address : " + block.blocktype + " " + address + " " + vs.size() );
        switch ( block.blocktype )
        {
		case MyConstants.simpleType :
            error = executeSimple ( block.command, address );
            break;

        case MyConstants.exprType :
            if ( ! block.command.trim().startsWith ( "#" ) )
            {
                error = executeExpression ( block.command );
            }
         	break;
        
         case MyConstants.interceptType :
            String ffor = address + " " + block.command;
            if ( ! ffor.trim().startsWith ( "#" ) )
            {
                error = executeIntercept ( ffor );
        	}
            break;
            
        case MyConstants.loopType :
        	error = executeLoop ( block.command, block.vs, address );
        	break;
        	
        case MyConstants.forType :
        	error = 1;
        	JOptionPane.showMessageDialog( new Frame(), "'for' function under another 'for' is not supported", "Error", JOptionPane.ERROR_MESSAGE );
        	break;
        	
        case MyConstants.incType :
        	error = executeInc ( block.command, block.vs, address );
        	break;
        	
        case MyConstants.ifType :
        case MyConstants.ifsType :
        	k ++ ;
            blockButton temp = ( blockButton )vs.get( k ) ;
            error = executeIf ( block.blocktype, block.command, block.vs, temp.vs, address );
        	break;
        }
    }
    return error;
}
/********************************************************************/
// called for function under a 'for' under 'inc' 
//
/********************************************************************/
private int executeFunction ( Vector<blockButton> vs, String address, int kk, String common, String variable )
{
    int error = okCode;
    for ( int k = 0; ( k < vs.size() ) && continueButton ( error ); k ++ )
    {
        blockButton block = ( blockButton )vs.get ( k );
        debug ( false,"executeFunction with address and padding : " + block.blocktype + " " + address + " " + kk );
		String str = block.command;
        switch ( block.blocktype )
        {
        case MyConstants.exprType :
        	if ( ! str.trim().startsWith ( "#" ) )
        	{
                error = executeExpression ( str );
            }
        	break;
		case MyConstants.simpleType :
        	String ffor = str;
        	if ( ! ffor.trim().startsWith ( "#" ) )
        	{
                if ( ! ffor.trim().startsWith ( "wait " ) && ! ffor.trim().equals ( "break" ) )
                {
                    ffor = address + " " + ffor;
                }
                ffor = ExamineInc ( ffor, kk, common, variable );
            	error = executeSimple ( ffor );
        	}
        	break;
		case MyConstants.interceptType :
        	ffor = str;
        	if ( ! ffor.trim().startsWith ( "#" ) )
        	{
                ffor = address + " " + ffor;
                ffor = ExamineInc ( ffor, kk, common, variable );
            	error = executeIntercept ( ffor );
        	}
        	break;
        default:
            JOptionPane.showMessageDialog( new Frame(), "More than 2 complex nested functions : " + block.blocktype, "Error", JOptionPane.ERROR_MESSAGE );
            error = errorCode;
            break;
	    }
        debug ( false,"end executeFunction with address and padding : " + block.blocktype + " error = " + error );
    }
    return error;
}
/********************************************************************/
// called for function under a 'inc' function
//
/********************************************************************/
 private int executeFunction ( Vector<blockButton> vs, int kk, String common, String variable )
 {
    int error = okCode;
    for ( int k = 0; ( k < vs.size() ) && continueButton ( error ); k ++ )
    {
        blockButton block = ( blockButton )vs.get ( k );
        debug ( false,"executeFunction with padding : " );
		String str = block.command;
        switch ( block.blocktype )
        {
        case MyConstants.exprType :
        	if ( ! str.trim().startsWith ( "#" ) )
        	{
                error = executeExpression ( str );
            }
        	break;

		case MyConstants.simpleType :
        	String ffor = ExamineInc ( str, kk, common, variable );
            error = executeSimple ( ffor );
        	break;
        	
		case MyConstants.interceptType :
        	ffor = ExamineInc ( str, kk, common, variable );
            error = executeIntercept ( ffor );
        	break;
        	
        case MyConstants.loopType :
        	error = executeLoop ( str, block.vs, kk, common, variable );
        	break;
        	
        case MyConstants.forType :
		    error = executeFor ( str, block.vs, kk, common, variable );
        	break;
        	
        case MyConstants.incType :
        	error = executeInc( str, block.vs, kk, common, variable );
        	break;
        	
        case MyConstants.ifsType :
        case MyConstants.ifType :
        	k ++ ;
        	blockButton temp =  ( blockButton ) vs.get( k );
        	error = executeIf ( block.blocktype, str, block.vs, temp.vs, kk, common, variable );
        	break;
        }
    }
    return error;
}
/********************************************************************/
// simple loop - extract the loop counter from the command
// substitute it and translate it to number
// call execution for its block vector above number of times
/********************************************************************/
private int executeLoop ( String command, Vector<blockButton> blk )
{
    int error = okCode;

     debug ( false,"executeLoop : " + command );
     StringTokenizer stt = new StringTokenizer ( command, " ", false ) ;
     String f = stt.nextToken();
     String aa = command.substring( f.length(), command.length() ).trim() ;
     int howmany ;
     aa = MyConstants.lookForPair ( aa );
     try
     {
       howmany = Integer.parseInt ( aa );
     }
     catch ( NumberFormatException ex )
     {
         JOptionPane.showMessageDialog( new Frame(), "loop counter is not number", "Error", JOptionPane.ERROR_MESSAGE );
         return ( errorCode ) ;
     }
     for ( int t = 0; ( t < howmany ) && continueButton ( error ); t ++ )
     {
         execstat.SetIndex ( t, "loop" );					
         error = executeFunction ( blk );
     } // for
     
     return error;
}
/********************************************************************/
// loop under 'inc'
// substitute it and translate it to number
// call execution for its block vector above number of times
// pass the 'inc' parameters futher
/********************************************************************/

private int executeLoop ( String command, Vector<blockButton> vs, int kk, String common, String variable )
{
    int error = okCode;

     debug ( false,"executeLoop under inc : " + command + " kk=" + kk + " common=" + common + " variable=" + variable );
     StringTokenizer stt = new StringTokenizer ( command, " ", false ) ;
     String f = stt.nextToken();
     String aa = command.substring( f.length(), command.length() ).trim() ;
     int howmany ;
     aa = MyConstants.lookForPair ( aa );
     try
     {
        howmany = Integer.parseInt ( aa );
     }
     catch ( NumberFormatException ex )
     {
         JOptionPane.showMessageDialog( new Frame(), "loop counter is not number", "Error", JOptionPane.ERROR_MESSAGE );
         return ( errorCode ) ;
     }
     for ( int t = 0; ( t < howmany ) && continueButton ( error ); t ++ )
     {
        execstat.SetIndex ( t, "loop" );					
        error = executeFunction ( vs , kk, common, variable );
     } // for
     return error;
}
/********************************************************************/
// loop under 'for'
// substitute it and translate it to number
// call execution for its block vector above number of times
// pass the 'for' parameter futher
/********************************************************************/
private int executeLoop ( String command, Vector<blockButton> vs, String address )
{
    int error = okCode;

    debug ( false,"executeLoop under for : " + command + " address=" + address );
    StringTokenizer stt = new StringTokenizer ( command, " ", false ) ;
    String f = stt.nextToken();
    String aa = command.substring( f.length(), command.length() ).trim() ;
    int howmany ;
    aa = MyConstants.lookForPair ( aa );
    try
    {
      howmany = Integer.parseInt ( aa );
    }
    catch ( NumberFormatException ex )
    {
        JOptionPane.showMessageDialog( new Frame(), "loop counter is not number", "Error", JOptionPane.ERROR_MESSAGE );
        return ( errorCode ) ;
    }
    for ( int t = 0; ( t < howmany ) && continueButton ( error ); t ++ )
    {
        execstat.SetIndex ( t, "loop" );					
        error = executeFunction ( vs , address );
    } // for

    return error;
}
/********************************************************************/
// retreive all the nedeed parameters from the command line
// of a 'for' function  : start address, end address
// and the common part of the addresses
// the suffix of an address MUST be 2 charaters
/********************************************************************/
private returnParameters translateFor ( String command )
{
 String f, aa, start, stop ;
	
 returnParameters rp = new returnParameters();
 StringTokenizer stt = new StringTokenizer ( command, " ", false ) ;
 if ( stt.countTokens() == 2 ) // 'for' and xx-yy
 {
   f = stt.nextToken();
   aa = stt.nextToken();
   // split the start and stop strings
   stt = new StringTokenizer ( aa, "-", false ) ;
   if ( stt.countTokens() == 2 )
   {
    start = stt.nextToken().trim();
    stop = stt.nextToken().trim();
    // substitute them
    start = MyConstants.lookForPair( start );
    stop = MyConstants.lookForPair ( stop );
    // 
    int l = start.length();
    int stopI = 0;
    int startI = 0;
    // take the common part of the address
    String common = start.substring ( 0, l - 2 );
    // take the suffix of start as start index of the loop
    String suffix = start.substring ( l - 2, l );
    try
    {
        startI = Integer.parseInt ( suffix );
        l = stop.length();
        // take the suffix of stop as stop index of the loop
        suffix = stop.substring ( l - 2, l );
        stopI = Integer.parseInt ( suffix );
        // for the start and stop - in case a translate file is defined
        if ( MyConstants.alias != null )
        {
            // translate the start
            start = ( String ) MyConstants.alias.get ( start );
            // in case the start is not available -
            // look towards stop, for the first available address
            while ( ( start == null ) && ( startI < stopI ) )
            {
                startI++;
                start = new String ( common + String.valueOf ( startI ) );
                start = ( String ) MyConstants.alias.get ( start );
            }
            if ( start != null )
            {
                // translate the stop
                stop = ( String ) MyConstants.alias.get ( stop );
                // in case the original stop is not available
                // look towards start for the first (from the end) available stop
                while ( ( stop == null ) && ( startI < stopI ) )
                {
                    if ( stopI > 0 )
                    {
                        stopI--;
                        suffix = String.valueOf ( stopI );
                        if ( suffix.length() < 2 )
                        {
                            suffix = "0" + suffix;
                        }
                        stop = new String ( common + suffix );
                        stop = ( String ) MyConstants.alias.get ( stop );
                    }
                    else break;
                } // while
            } // if
        } // if address
        rp.start = startI;
        rp.stop = stopI;
        rp.error = false;
        rp.common = common;
    }
    catch ( NumberFormatException ex )
    {
        JOptionPane.showMessageDialog( new Frame(), "some of 'for' parameters is not number", "Error", JOptionPane.ERROR_MESSAGE );
    }
	    	
   }
   else rp.error = true;
 }
 else rp.error = true;
 
 return rp;
}
/********************************************************************/
// simple for - extract the parameters from the command
// call execution for its block vector each time with the current address
/********************************************************************/
private int executeFor ( String command, Vector<blockButton> vs )
{
    int error = okCode;
    int startI = 0;
    int stopI = 0;
    String common = "";

    debug ( false, "executeFor : " + command );
    returnParameters rp = translateFor ( command );
    if ( ! rp.error )
    {
        startI = rp.start;
        stopI = rp.stop;
        common = rp.common;
 // from start to stop and execute the entire body for each of the addresses
        for ( int k = startI; ( k <= stopI ) && continueButton ( error ); k ++ )
        {
            // take an address; build it as string padding it with the common part
            String suffix = String.valueOf ( k );
            if ( suffix.length() < 2 )
            {
                suffix = "0" + suffix;
            }
            String address = new String ( common + suffix );
		   	debug ( false, "executeFor : " + address ) ;            
	   		execstat.SetAddress ( address );
            error = executeFunction ( vs, address );	               
        } // for
    } // if
    else
    {
        error = errorCode;
    }
    
    return error;
}
/********************************************************************/
// for under 'inc' - extract the parameters from the command
// call execution for its block vector each time with the current address
// and the inc parameters
/********************************************************************/
private int executeFor ( String command, Vector<blockButton> vs, int kk, String padding, String variable )
{
    int error = okCode;
    int startI = 0;
    int stopI = 0;
    String common = "";

    debug ( false, "executeFor under inc : " + command + " kk=" + kk + " padding=" + padding + " variable=" + variable );
    returnParameters rp = translateFor ( command );
    if ( ! rp.error )
    {
        startI = rp.start;
        stopI = rp.stop;
        common = rp.common;
        for ( int k = startI; ( k <= stopI ) && continueButton ( error ); k ++ )
        {
            String suffix = String.valueOf ( k );
            if ( suffix.length() < 2 )
            {
                suffix = "0" + suffix;
            }
            String address = new String ( common + suffix );
		   	debug ( false, "executeFor under inc : " + address ) ;            
            execstat.SetAddress ( address );
            error = executeFunction ( vs, address, kk, padding, variable );
        } // for
    } // if
    else
    {
        error = errorCode;
    }
     return error;
}
/********************************************************************/
// retreive all the nedeed parameters from the command line
// of a 'inc' function  : start,end and step and if exist the variable name
/********************************************************************/
private returnParameters translateInc ( String command, String vari )
{
    String variable = vari;
    String start = "";
    String stop = "";
    String step = "";
    String common = "";
    String f, aa ;

    returnParameters rp = new returnParameters();
    StringTokenizer stt = new StringTokenizer ( command, " ", false ) ;
    if ( stt.countTokens() == 2 )
    {
        f = stt.nextToken(); // inc
        aa = stt.nextToken(); // limists and step
        stt = new StringTokenizer ( aa, "-", false ) ;
        if ( stt.countTokens() == 3 )
        {
            start = stt.nextToken().trim();
            int index = start.indexOf ( "=" ) ;
            // check if the variable is defined
            // if not 'i' is defualt variable
            if ( index != -1 )
            {
                // yes, it is defined
                // retreive it
                // clear it from the original command
                variable = start.substring( 0, index ).trim();
                start = start.substring( index + 1, start.length() ).trim();
            }
            debug( false,"variable=" + variable + "#start=" + start ) ;
            // substitute - if need - all the parameters
            start = MyConstants.lookForPair( start );
            stop = MyConstants.lookForPair ( stt.nextToken().trim() );
	        step = MyConstants.lookForPair ( stt.nextToken().trim() ); 
            try
            {
                rp.start = Integer.parseInt ( start );
                rp.stop = Integer.parseInt ( stop );
                rp.step = Integer.parseInt ( step );
                rp.error = false;
                rp.common = start;
                rp.address = variable ;
            }
            catch ( NumberFormatException ex )
            {
                JOptionPane.showMessageDialog( new Frame(), "some of 'for' parameters is not number", "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
        else rp.error = true;
    }
    else rp.error = true;
    return( rp );
}
/********************************************************************/
// simple inc - extract the parameters from the command
// call execution for its block vector each time with the next
// value of the incremental variable
/********************************************************************/
private int executeInc ( String command, Vector<blockButton> vs )
{
  int error = okCode;
  String variable = "i";
  int stepI = 0;
  int startIi = 0;
  int stopIi = 0;
  String common = "";

  debug ( false, "executeInc " + command );
  returnParameters rp = translateInc ( command, variable );
  if ( ! rp.error )
  {
     startIi = rp.start;
     stopIi = rp.stop;
     stepI = rp.step;
     common = rp.common;
     variable = rp.address;
     String finc = "";
     for ( int kk = startIi; ( kk <= stopIi ) && continueButton ( error ); kk += stepI )
     {
          execstat.SetIndex ( kk, "inc" ) ;
		  for ( int t = 0; t < vs.size(); t ++ )
		  {
		  	blockButton block = ( blockButton )vs.get ( t );
		  	switch ( block.blocktype )
            {
            case MyConstants.simpleType:
		  	    finc = ExamineInc ( block.command, kk, common, variable );
                error = executeSimple ( finc );
                break;
            case MyConstants.interceptType:
		  	    finc = ExamineInc ( block.command, kk, common, variable );
                error = executeIntercept ( finc );
                break;
            case MyConstants.exprType:
		  	    finc = ExamineInc ( block.command, kk, common, variable );
                error = executeExpression ( finc );
                break;
            case MyConstants.loopType:
		        error = executeLoop ( block.command, block.vs, kk, common, variable );
		  	    break;
            case MyConstants.incType:
		        error = executeInc ( block.command, block.vs, kk, common, variable );
		  	    break;
            case MyConstants.forType:
		        error = executeFor ( block.command, block.vs, kk, common, variable );
		  	    break;                
            case MyConstants.ifType:
            case MyConstants.ifsType:
            	t++;
            	blockButton temp = ( blockButton ) vs.get(t);
       			error = executeIf ( block.blocktype, block.command, block.vs, temp.vs, kk, common, variable );
		  	    break;
            }
		  } // for
     } // for
   }
   else error = errorCode;
     
   return error;
}
/********************************************************************/
// inc under 'inc' - extract the parameters from the command ( second inc )
// the first inc parameters are passed as parameters from caller
// call execution for its block vector each time with the next
// value of the second incremental variable
// before execution replace the both variables with their actual values
/********************************************************************/
private int executeInc ( String command, Vector<blockButton> vs, int kk2, String common2, String variable2 )
{
    int error = okCode;
    String variable = "i";
    int startIi = 0;
    int stopIi = 0;
    int stepI = 0;
    String common = "";

     debug ( false, "executeInc under inc : " + command + " kk2=" + kk2 + " common2=" + common2 + " variable2=" + variable2 );
     returnParameters rp = translateInc ( command, variable );
     if ( ! rp.error )
     {
        startIi = rp.start;
        stopIi = rp.stop;
        stepI = rp.step;
        common = rp.common;
        variable = rp.address;
        for ( int kk = startIi; ( kk <= stopIi ) && continueButton ( error ); kk += stepI )
        {
            execstat.SetIndex ( kk, "inc" ) ;
	       	for ( int t = 0; t < vs.size(); t ++ )
	       	{
	       		blockButton block = ( blockButton )vs.get ( t );
	      		String finc ;
                switch ( block.blocktype )
                {
                case MyConstants.simpleType :
                    finc = ExamineInc ( block.command, kk, common, variable );
                    finc = ExamineInc ( finc, kk2, common2, variable2 ) ;
	       		    error = executeSimple ( finc  );
                    break;
        
                case MyConstants.interceptType :
                    finc = ExamineInc ( block.command, kk, common, variable );
                    finc = ExamineInc ( finc, kk2, common2, variable2 ) ;
	       		    error = executeIntercept ( finc  );
                    break;
        
                case MyConstants.exprType :
                    finc = ExamineInc ( block.command, kk, common, variable );
                    finc = ExamineInc ( finc, kk2, common2, variable2 ) ;
	       		    error = executeExpression ( finc  );
                    break;
        
                default:
                    error = errorCode;
                    JOptionPane.showMessageDialog( new Frame(), "More than 2 complex nested functions : " + block.blocktype, "Error", JOptionPane.ERROR_MESSAGE );
                    break;
                }
	       	} // for
        } // for
     }
     return error;
}

/********************************************************************/
// inc under 'for' - extract the parameters from the command 
// call execution for its block vector each time with the next
// value of the incremental variable and the received address
// before execution replace the variable with their actual values
/********************************************************************/

private int executeInc ( String command, Vector<blockButton> vs, String address )
{
    int error = okCode;
    String variable = "i";
    int stepI = 0;
    int startIi = 0;
    int stopIi = 0;
    String common = "";

    debug ( false,"executeInc under for : " + command + " address=" + address );
    returnParameters rp = translateInc ( command, variable );
    if ( ! rp.error )
    {
         startIi = rp.start;
         stopIi = rp.stop;
         stepI = rp.step;
         common = rp.common;
         variable = rp.address;
         debug ( false,"executeInc under for : " + startIi + " " + stopIi );
         for ( int kk = startIi; (kk <= stopIi ) && continueButton ( error ); kk += stepI )
         {
             execstat.SetIndex ( kk, "inc" );					
			 for ( int y = 0; y < vs.size(); y ++ )
			 {
                blockButton block = ( blockButton )vs.get ( y );
				String finc ;
                switch ( block.blocktype )
                {
                case MyConstants.simpleType :
                    finc = ExamineInc ( block.command, kk, common, variable );
                    error = executeSimple ( finc, address  );
                    break;
                case MyConstants.interceptType :
                    finc = ExamineInc ( block.command, kk, common, variable );
                    error = executeIntercept ( finc + address  );
                    break;
                case MyConstants.exprType :
                    finc = ExamineInc ( block.command, kk, common, variable );
                    error = executeExpression ( finc );
                    break;
                default:
                    error = errorCode;
                    JOptionPane.showMessageDialog( new Frame(), "More than 2 complex nested functions : " + block.blocktype, "Error", JOptionPane.ERROR_MESSAGE );
                    break;
                }
			}
        } // for
     }
     else error = errorCode;
     return error;
}
/**************************************************************/
// retreive the functions parameters : waiting time, text to check
// and address
/**************************************************************/
private returnParameters translateIf ( String tt )
{
     returnParameters rp = new returnParameters();
     // f = time + tocheck + address
     String f = tt.substring ( 0,tt.indexOf(' ') );
     // function = time
     String function = tt.substring (f.length() + 1, tt.indexOf('"') ).trim();
     // store the actual waiting time
     rp.start = GiveRange ( "if", function ) ;
     // store the text between quoates
     rp.common = tt.substring ( tt.indexOf('"') + 1, tt.lastIndexOf('"') ).trim();
     // store the address after it is substituted
     rp.address = tt.substring( tt.lastIndexOf('"') + 1,tt.length() ).trim();
     rp.address = MyConstants.lookForPair( rp.address );
     rp.error = false;
     debug ( false, "waittime=" + function + " : " + rp.start + " tocheck=" + rp.common + " ifaddress="+rp.address);
     return rp;
}
/**************************************************************/
// retreive the functions parameters : text to check
/**************************************************************/
private returnParameters translateIfs ( String tt )
{
    returnParameters rp = new returnParameters();
    String f = tt.substring ( 0,tt.indexOf(' ') );
    rp.common = tt.substring ( tt.indexOf('"') + 1, tt.lastIndexOf('"')).trim();
    rp.error = false;
    debug ( false, " tocheck=" + rp.common );
    return rp;
}
/**************************************************************/
// retreive the functions parameters : waiting time, text to check
// address is received - used under 'for' command
/**************************************************************/
private returnParameters translateIf ( String tt, String address )
{
     returnParameters rp = new returnParameters();
     // f = time + tocheck + address
     String f = tt.substring ( 0,tt.indexOf(' ') );
     // function = time
     String function = tt.substring (f.length() + 1, tt.indexOf('"') ).trim();
     // store the actual waiting time
     rp.start = GiveRange ( "if", function ) ;
     // store the text between quoates
     rp.common = tt.substring ( tt.indexOf('"') + 1, tt.lastIndexOf('"') ).trim();
     // store the address after it is substituted
     rp.address = MyConstants.lookForPair( address );
     rp.error = false;
     debug ( false, "waittime=" + function + " : " + rp.start + " tocheck=" + rp.common + " ifaddress="+rp.address);
     return rp;
}
/********************************************************************/
//  simple 'if', 'sif' functions
/********************************************************************/
private int executeIf ( int type, String command, Vector<blockButton> blkyes , Vector<blockButton> blkno )
{
    int error = okCode;
    ipdef ipconn ;
    returnParameters rp ;

     debug ( false,"executeIf : " + command );
     switch ( type )
     {
     case MyConstants.ifType:
         rp = translateIf ( command );
         if ( ! rp.error )
         {
            error = executeIfBody ( rp.address, rp.start, rp.common, blkyes, blkno ) ;
         }
         else error = errorCode;
         break;

     case MyConstants.ifsType:
         rp = translateIfs ( command );
         if ( ! rp.error )
         {
            error = executeIfsBody ( rp.common, blkyes, blkno ) ;
         }
         else error = errorCode;
         break;

     default:
         debug ( true, "error execute If with type: " + type );
         error = errorCode;
         break;
     }
    
     return error;
}
/********************************************************************/
//  'if', 'sif' functions under 'for'
// re-build the 'yes' and 'no' functions after the current address  
// is placed into the correct place
// translate the padded command and execute
/********************************************************************/
private int executeIf ( int type, String command, Vector<blockButton> blkyes , Vector<blockButton> blkno, String address )
{
    int error = okCode;
    returnParameters rp;
    Vector<blockButton> vsyes = new Vector<blockButton> (0,1);
    Vector<blockButton> vsno = new Vector<blockButton> (0,1);

     debug ( false,"executeIf ( " + type + " ) with address : " + command + " address=" + address );
     for ( int k = 0; k < blkyes.size (); k ++ )
     {
         blockButton block = ( blockButton )blkyes.get ( k );
         if ( ( block.blocktype == MyConstants.simpleType ) || ( block.blocktype == MyConstants.exprType ) )
         {
             String ffor = block.command;
             if ( ! ffor.trim().startsWith ( "#" ) )
             {
                 if ( ! ffor.trim().startsWith ( "wait " ) && ! ffor.trim().equals ( "break" ) )
                 {
                     ffor = address + " " + ffor;
                 }
             }
             vsyes.add( new blockButton( block.blocktype, ffor ) );
         }
         else
         {
             vsyes.add ( block );
         }
     }
     for ( int k = 0; k < blkno.size (); k ++ )
     {
         blockButton block = ( blockButton )blkno.get ( k );
         if ( ( block.blocktype == MyConstants.simpleType ) || ( block.blocktype == MyConstants.exprType ) )
         {
             String ffor = block.command;
             if ( ! ffor.trim().startsWith ( "#" ) )
             {
                 if ( ! ffor.trim().startsWith ( "wait " ) && ! ffor.trim().equals ( "break" ) )
                 {
                     ffor = address + " " + ffor;
                 }
             }
             vsno.add( new blockButton( block.blocktype, ffor ) );
         }
         else
         {
             vsno.add ( block );
         }
     }
     switch ( type )
     {
     case MyConstants.ifType:
         rp = translateIf ( command, address );
         if ( ! rp.error )
         {
            error = executeIfBody ( rp.address, rp.start, rp.common, vsyes, vsno ) ;
         }
         else error = errorCode;
         break;

     case MyConstants.ifsType:
         rp = translateIfs ( command );
         if ( ! rp.error )
         {
            error = executeIfsBody ( rp.common, vsyes, vsno ) ;
         }
         else error = errorCode;
         break;

     default:
         debug ( true, "error execute If with type: " + type );
         error = errorCode;
         break;
     }

     return error;
}
/********************************************************************/
//  'if', 'sif' functions under 'inc'
// re-build the 'yes' and 'no' functions after the current value of the index  
// is placed into the correct place
// translate the padded command and execute
/********************************************************************/
private int executeIf ( int type, String command, Vector<blockButton> blkyes , Vector<blockButton> blkno, int kk, String common, String variable )
{
    int error = okCode;
    returnParameters rp;
    Vector<blockButton> vsyes = new Vector<blockButton> (0,1);
    Vector<blockButton> vsno = new Vector<blockButton> (0,1);

     debug ( false,"executeIf ( " + type + " ) with padding : " + command + " kk=" + kk + " common=" + common + " variable=" + variable );
     for ( int k = 0; k < blkyes.size (); k ++ )
     {
         blockButton block = ( blockButton )blkyes.get ( k );
         if ( ( block.blocktype == MyConstants.simpleType ) || ( block.blocktype == MyConstants.exprType ) )
         {
             String ffor = block.command;
             ffor = ExamineInc ( ffor, kk, common, variable );
             vsyes.add( new blockButton( block.blocktype, ffor ) );
         }
         else
         {
             vsyes.add ( block );
         }
     }
     for ( int k = 0; k < blkno.size (); k ++ )
     {
         blockButton block = ( blockButton )blkno.get ( k );
         if ( ( block.blocktype == MyConstants.simpleType ) || ( block.blocktype == MyConstants.exprType ) )
         {
             String ffor = block.command;
             ffor = ExamineInc ( ffor, kk, common, variable );
             vsno.add( new blockButton( block.blocktype, ffor ) );
         }
         else
         {
             vsno.add ( block );
         }
     }
     switch ( type )
     {
     case MyConstants.ifType:
         rp = translateIf ( command );
         if ( ! rp.error )
         {
            error = executeIfBody ( rp.address, rp.start, rp.common, vsyes, vsno ) ;
         }
         else error = errorCode;
         break;

     case MyConstants.ifsType:
         rp = translateIfs ( command );
         if ( ! rp.error )
         {
            error = executeIfsBody ( rp.common, vsyes, vsno ) ;
         }
         else error = errorCode;
         break;

     default:
         debug ( true, "error execute If with type: " + type );
         error = errorCode;
         break;
     }
     return error;
}
/***************************************************************/
// transmit the text to be check to user thread corresponding to the address
// say to it if need to be waked up when the text is received or only intercept it
// go sleep for the waiting period of time
// when awaked, check condition  ( ok if the text was received )
// and according to the result of this check execute the blocks of the vector
// of the 'yes' or 'no' block ; 'no' means : during the waiting time
// the requested text was not received
/***************************************************************/
private int executeIfBody ( String address, int waittime, String tocheck, Vector<blockButton> vsyes , Vector<blockButton> vsno )
{
	int error = okCode;
	ipdef ipconn;
	
	try
	{
	 	ipconn = UtilsConnection.add_connection ( address ) ;
	    if ( ipconn != null )
	    {
	    	if ( ipconn.user != null )
	        {
                  if ( waittime > 0 )
                  {
                        ipconn.user.SetToCheck ( this, tocheck, true );
		                // 'if' waits maximum time either it is notified
	                    // that the test to check is captured
                        synchronized ( this )
                        {
                            wait ( waittime );
                        }
                        ipconn.user.ClearToCheck ( );		            		
                   }
                   else
                   {
                        ipconn.user.SetToCheck ( this, tocheck, false );
                   }
	               if ( okCondition )
	               {
	                		// it is notified - execute the yes function
	                	debug ( false, "YES !!! " + vsyes.size() );
	                	error = executeFunction ( vsyes );
	                	// YES functions
	 	        	}
	 	        	else
	 	        	{
		            	debug ( false, "NO !!! " + vsno.size() );
                        ipconn.user.ClearToCheck ( );		            		
				    	execstat.SetNotification ( "TimeOut - failed ","notification" );		            
	                	error = executeFunction ( vsno );
		        	}
		        }
	 	        else
	 	        {
		            debug ( false, "NO !!! " + vsno.size() );
				    execstat.SetNotification ( "TimeOut - failed no connection ","notification" );		            
	                error = executeFunction ( vsno );
		        }
	 	    }
	 	    else
	 	    {
				execstat.SetNotification ( "TimeOut - failed no connection ","notification" );		            
                error = executeFunction ( vsno );
            }
	   } // try
	   catch ( InterruptedException e )
	   {
	        debug ( true, "Interrupted exception from button execution(3)" );
	        error = errorCode;
	   }
   return error;
}
/***************************************************************/
// retreive the operands and the operator of the checking condition
// check condition  
// and according to the result of this check execute the blocks of the vector
// of the 'yes' or 'no' block 
/***************************************************************/
private int executeIfsBody ( String tocheck, Vector<blockButton> blkyes , Vector<blockButton> blkno )
{
	int error = okCode;
	
	okCondition = false;
	tocheck = Utils2.substitute ( tocheck );
	debug(false, "tocheck after substitute=" + tocheck);
    StringTokenizer stc = new StringTokenizer ( tocheck,"=><!", true );
    if ( ( stc.countTokens() < 3 ) || ( stc.countTokens() > 4 ) )
    {
       JOptionPane.showMessageDialog( new Frame(), "Expression " + tocheck + " is not valid ", "Error", JOptionPane.ERROR_MESSAGE );
       error = errorCode;
    }
    else
    {
		// the first part of the condition
		String tocheck1 = stc.nextToken().trim();
		String tocheck2 = "";
		// first character of the operator
	    String	op = stc.nextToken().trim();
		// second character of the operator
		String temp = stc.nextToken().trim();
	    if ( temp.equals("=") ) 
	    {
		     // second character of the operator
		     op = op + temp;
		     // second part of the condition
		     tocheck2 = stc.nextToken().trim();
	    }
	    else
	    {
		     // second part of the condition
		     tocheck2 = temp;
	    }
		debug(false, "tocheck1=" + tocheck1 + " op=" + op + " tocheck2=" + tocheck2);
	    if ( op.equals("==") || op.equals("=") )
	    {
		    if ( tocheck1.equals( tocheck2 ) ) okCondition = true;
	    }	         
	    if ( op.equals("!=") )
	    {
		    if ( ! tocheck1.equals( tocheck2 ) ) okCondition = true;
	    }
	    if ( op.equals(">=") || op.equals(">") || op.equals("<=") || op.equals("<") ) 
	    {
            try
            {
              	int nn = Integer.parseInt ( tocheck1 );
              	int mm = Integer.parseInt ( tocheck2 );
 	         	if ( op.equals(">=") )
 	         	{
	 	         	if ( nn >= mm ) okCondition = true;
 	         	}
 	         	if ( op.equals(">") )
 	         	{
	 	         	if ( nn > mm ) okCondition = true;
 	         	}
 	         	if ( op.equals("<=") )
 	         	{
	 	         	if ( nn <= mm ) okCondition = true;
 	         	}
 	         	if ( op.equals("<") ) 
 	         	{
	 	         	if ( nn < mm ) okCondition = true;
 	         	}
           }
           catch ( NumberFormatException ex )
           {
               JOptionPane.showMessageDialog( new Frame(), "At least one of the condition terms is not number " + tocheck1 + " " + tocheck2, "Error", JOptionPane.ERROR_MESSAGE );
               return ( errorCode ) ;
           }		         
	   }
    }
	if ( okCondition )
	{
		debug ( false, "YES !!! " );
		error = executeFunction ( blkyes );
	}
	else
	{
		debug ( false, "NO !!! " );
		error = executeFunction ( blkno );
	}
	return error;
}
/***************************************************************/
// retreive the parameters : time and text
// of the 'yes' or 'no' block ; 'no' means : during the waiting time
// the requested text was not received
/***************************************************************/
private int executeIntercept ( String function )
{
    int error = okCode ;
    String f,r;
    ipdef ipconn ;

    if ( ! stopButton )
    {
	debug ( false, "executeIntercept " + function );
    // f is the function
    f = function.substring ( 0,function.indexOf(' ') );
    // r is the waiting time
    r = function.substring ( f.length() + 1, function.indexOf('"')).trim();
    int waittime = GiveRange ( "intercept", r ) ;
    // the text to be intercepted
    String tocheck = function.substring (function.indexOf('"')+1, function.lastIndexOf('"')).trim();
    // f is the address
    f = function.substring ( function.lastIndexOf('"') + 1, function.length() ).trim();
    debug ( false, "waittime=" + waittime + " tocheck=" + tocheck + " ifaddress=" + f );
    try
    {
        ipconn = UtilsConnection.add_connection ( f ) ;
        if ( ipconn != null )
        {
             if ( ipconn.user != null )
            {
                if ( waittime > 0 )
                {
                    ipconn.user.SetToCheck ( this, tocheck, true );
	               // 'intercept' waits maximum time either it is notified
	               // that the text to check is captured	                
                    synchronized ( this )
                    {
                        wait ( waittime );
                    }
                    ipconn.user.ClearToCheck ( );		            		
                }
                else
                {
                    ipconn.user.SetToCheck ( this, tocheck, false );
                }
            }
            else
            {
                execstat.SetNotification ( "TimeOut - failed no connection","notification" );
            }
        }
        else
        {
            execstat.SetNotification ( "TimeOut - failed no connection","notification" );
        }
    }
    catch ( InterruptedException e )
    {
        debug ( true, "Interrupted exception from button execution(4)" );
        error = errorCode;
    }
    }
    else
    {
        error = errorCode;
    }
    return error;
}
/********************************************************************/

private int executeSimple ( String function, String address )
{
    String f,r;
    int error = okCode;

    if ( ! stopButton )
    {
    StringTokenizer stt = new StringTokenizer ( function, " ", false ) ;
    if ( stt.countTokens() >= 1 )
    {
        f = stt.nextToken();
        f = f.toLowerCase();
        Integer j = ( Integer )MyConstants.buttonDictionary.get ( f );
        debug ( false, "Execute Simple with address function :" + function + "! f=" + f );
        if ( j != null )
        {
            int ii = function.indexOf ( f );
            // 2 steands for delimiters between function and address
            if ( ii + f.length() + 1 == function.length() )
            {
                r = "";
            }
            else
            {
                r = function.substring ( ii + f.length() + 1, function.length() );
            }
            StringTokenizer stc = new StringTokenizer ( r, " ", false ) ;
            switch ( j.intValue () )
            {
            case MyConstants.openFunction:
            case MyConstants.fileFunction:  // file
                error = executeSimple ( f + " " + address + " " + r ) ;
                break;
                
            case MyConstants.csFunction: // none
            case MyConstants.waitFunction:  // wait
            case MyConstants.breakFunction:
            case MyConstants.buttonFunction:  // button
            case MyConstants.buttonWFunction:  // button
                error = executeSimple ( function + " " ) ;
                break;

            case MyConstants.displayFunction: // display
            case MyConstants.printFunction: // print
            case MyConstants.storeFunction: // store
            case MyConstants.getFunction: // get            
                error = executeSimple ( f + " " + address + " " + r ) ;
                break;

            default:  
                break;
            }
        }
        else
        {
            error = executeSimple ( address + " " + function ) ;
        }
    }
    }
    return error;
}
/********************************************************************/

private int executeExpression ( String function )
{
	String aa = "";
	String bb = "";
    int ii;
    int error = okCode;

    if ( ! stopButton )
    {   
        function = function.substring ( function.indexOf(' ') + 1, function.length());
        debug ( false, "executeExpression " + function );
        StringTokenizer stc = new StringTokenizer ( function, "=", false );
        if ( stc.countTokens() >= 2 ) 
        {   
            aa = stc.nextToken().trim();
            ii = aa.indexOf( '$' );
            if ( ii != -1 )
            {
               bb = Utils2.substitute ( stc.nextToken().trim() ) ;
               debug ( false, "executeExpression aa=" + aa + " bb after substitute=" + bb );
               if ( bb.indexOf('[') != -1 )
               {
                   int tt = GiveRandom ( bb );
                   bb = new Integer (tt).toString();
               }
               else
               {
                   bb = Utils2.calculateAll ( bb );
               }
               debug ( false, "executeExpression final bb=" + bb );
 			   MyConstants.pairs.setProperty( aa, bb ) ;
            }
            else
            {
                JOptionPane.showMessageDialog( new Frame(), "Expression is not valid - variable does not begin with '$'", "Error", JOptionPane.ERROR_MESSAGE );
                error = errorCode;
            }
        }
        else
        {
            JOptionPane.showMessageDialog( new Frame(), "Expression is not valid - '=' missing", "Error", JOptionPane.ERROR_MESSAGE );
            error = errorCode;
        }
    }
    else
    {
        error = errorCode;
    }
    return error;
}
/********************************************************************/

private int executeSimple ( String function )
{
	String aa = "";
	String bb = "";
    String r = "";
    String command = "";
    ipdef ipconn ;
    String f, ff;
    int ii;
    int error = okCode;

    if ( ! stopButton )
    {
    StringTokenizer stt = new StringTokenizer ( function, " ", false ) ;
    debug ( false, "function :" + function + " stt.count=" + stt.countTokens() );
    if ( stt.countTokens() > 0 )
    {
    f = stt.nextToken().trim();
    Integer j = ( Integer )MyConstants.buttonDictionary.get ( f );
    if ( stt.countTokens() >= 1 )
    {
        bb = function.substring( f.length(), function.length() ).trim() ;
        aa = stt.nextToken();
        debug ( false, "function :" + function + "! f=" + f + "! aa=" + aa + "! bb=" + bb );
        if ( j != null )
        {
            ii = function.indexOf ( f );
            // 2 steands for delimiters between function and address
            if ( ii + f.length() + aa.length() + 2 >= function.length() )
            {
                r = "";
            }
            else
            {
                r = function.substring ( ii + f.length() + aa.length() + 2, function.length() );
            }
            StringTokenizer stc = new StringTokenizer ( r, " ", false ) ;
            switch ( j.intValue () )
            {
            case MyConstants.fileFunction:  // file
            {
                if ( aa.equals ( "all" ) )
                {
                try
                {
                    String s;
                    BufferedReader reader = new BufferedReader( new FileReader( r.trim() ) );
                    while( ( s = reader.readLine() ) != null )
                    {
                        s = s.trim();
                        if ( s.length () > 0 )
                        {
                            // add and connect an address from the 'all' file
                            ipconn = UtilsConnection.add_connection ( s );
                            if ( ipconn != null )
                            {
                                if ( ipconn.out != null )
                                {
                                    String s_new = "run ";
                                    s_new = s_new.concat ( reader.readLine() );
                                    debug ( false, "send to " + s + " : " + s_new );
                                    ipconn.out.write( UtilsConnection.send_to_ip ( s_new ) );
                                }
                                else error = errorCode;
                                // o.k. - send the command
                            }
                            else error = errorCode;
                        }
                        else error = errorCode;
                        if ( error == errorCode )
                        {
                            reader.readLine(); // dummy
                        }
                    }
                    reader.close();
                }
                catch ( IOException ex )
                {
                    debug ( true, "Error in file " + r ) ;
                }
                }  // all
                else
                {
                    ipconn = UtilsConnection.add_connection ( aa ) ;
                    command = Utils2.substitute( r ).trim();
                    command = Utils2.calculateAll(command);
                    if ( ipconn != null )
                    {
                        if ( UtilsConnection.connect_and_send ( true, command, aa ) )
                        {
                            error = errorCode ;
                        }
                        else
                        {
                            error = okCode ;
                        }
			    	}
			    	else error = errorCode;
                }
            }
            break;

            case MyConstants.waitFunction:  // wait
            {
	        	int nn = GiveRange ( "wait", bb ) ;
            	try
            	{
            		this.sleep( nn );
            	}
            	catch ( InterruptedException e )
            	{
            		debug ( true, "Interrupted exception from button execution - function wait" );
            	}
            }
            break;

            case MyConstants.buttonWFunction:  // button
            case MyConstants.buttonFunction:  // button
            {
                aa = bb.trim() ;
                boolean exB = false ;
                userButton temp = null;
                int index = -1;
                int indexP = aa.indexOf ( '(' ) ;
                String param = "";
                if ( indexP != -1 )
                {
                    param = aa.substring( indexP + 1, aa.length() - 1 ).trim();
                    aa = aa.substring( 0, indexP - 1 ).trim();
                    if ( ! paramB.equals ("") ) param = paramB ;
                    param = Utils2.substitute ( param );
                }
                int ind = aa.indexOf ( '/' ) ;            
                if ( ind == -1 )
                {
                    index = have_the_name ( aa, adr.buttons, adr.buttons.size() );
                    if ( index != -1 )
                    {
                        temp = ( userButton ) adr.buttons.get ( index );
                        exB = true ;
                    }
                }
                else
                {
                    String bpa = aa.substring ( 0, ind ).trim() ;
                    aa = aa.substring ( ind + 1, aa.length() ).trim() ;
                    DefineButton defb = MyConstants.bp.i_am ( bpa ) ;
                    debug ( false, "bpa=" + bpa + " defb=" + defb + " aa=" + aa ) ;
                    if ( defb != null )
                    {
                        index = have_the_name ( aa, defb.buttons, defb.buttons.size() );
                        if ( index != -1 )
                        {
                            temp = ( userButton ) defb.buttons.get ( index );
                            exB = true ;
                        }
                    }
                }
                if ( exB )
                {   
                    temp.exec.SetParameter ( param ) ;
                    if ( j.intValue () == MyConstants.buttonWFunction )
                    {
	                    // I'm going to execute a 'buttonWait' function
	                    // ==> notify the thread of the button to be executed
	                    // ==> set for it whom is wake it up 
	                    synchronized ( temp.exec )
	                	{
		                	temp.exec.Set ( this );
                    		temp.exec.notify ( );
                		}
                		// go to wait until the called button finish
                        try
                        {
                            synchronized ( this )
                            {
                                wait();
                            }
                        } // try
                        catch ( InterruptedException e )
                        {
                            debug ( true, "Interrupted exception from button execution(5)" );
                        }
                        error = temp.exec.getError ();
                    }
                    else
                    {
	   	                // I'm going to execute a 'button' function
	                    // ==> notify the thread of the button to be executed
	                    // ==> set for it no need to notify me to finish                 
	                	synchronized ( temp.exec )
	                	{
		                	temp.exec.Set ( null );
                    		temp.exec.notify ( );
                		}
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog( new JFrame(), "button " + bb + " is not found", "Error", JOptionPane.ERROR_MESSAGE );
                }
            }
            break;

            case MyConstants.openFunction:
                ipdef t = null;
                int index = UtilsConnection.have_it ( aa );
                if ( index != -1 )
                {
                    t = MyConstants.ipconnections [ index ];
                    if ( t != null )
                        t.OpenConnection();
                    else
                    {
                        t = new ipdef ( aa );
                        if ( t != null )
                            t.OpenConnection();
                    }
                }
                break;
                
            case MyConstants.displayFunction: // display
            case MyConstants.printFunction: // print
            case MyConstants.storeFunction: // store
            case MyConstants.getFunction: // get 

            drawParameters countersParametrs;    
            r = stc.nextToken(); // device
            int indexP = r.indexOf ( '&' ) ;
            int device = 0 ;
            debug ( false,"Command before replacement=" + r );
            if ( indexP != -1 )
            {
               if ( ! paramB.equals ("") )
               {
                   int y = r.indexOf ( ' ', indexP + 1 ) ;
                   if ( y == -1 ) y = r.length();
                   r = r.substring ( 0, indexP ) + paramB + r.substring ( y , r.length() );
               }
               else
               {
                   r = r.substring ( 0, indexP ) + r.substring ( indexP + 1, r.length() );
               }
               r = Utils2.substitute ( r );
               r = Utils2.calculateAll ( r );
            }
            device = Integer.parseInt ( r ) ;
            if ( MyConstantsCounters.dDB[device].countersParametrs != null )
            {
               countersParametrs = MyConstantsCounters.dDB[device].countersParametrs ;
            }
            else
            {
                countersParametrs = new drawParameters ( device );
            }
            countersParametrs.file_name = MyConstants.lookForPair ( aa );
            countersParametrs.max_y = MyConstants.Screen_y;
            switch ( j.intValue () )
            {
            case MyConstants.getFunction:
                countersParametrs.storeForm = false;
                countersParametrs.all_in_one = true;
                countersParametrs.fixForm = false;
                break;
            case MyConstants.displayFunction:
                countersParametrs.storeForm = false;
                countersParametrs.all_in_one = true;
                countersParametrs.fixForm = true;
                break;
            case MyConstants.printFunction:
                countersParametrs.storeForm = false;
                countersParametrs.all_in_one = false;
                countersParametrs.fixForm = false;
                break;
            case MyConstants.storeFunction:
                countersParametrs.all_in_one = false;
                countersParametrs.fixForm = false;
                countersParametrs.storeForm = true;
                break;
            }
            countersParametrs = UtilsCounters.execFilters ( stc, countersParametrs );
            if ( countersParametrs.fixForm )
            {
                Counter c = ( Counter )countersParametrs.graphs.get ( 0 );
                switch ( c.CounterModule() )
                {
                case my_java.TX:
                    countersParametrs.max_x = 960;
                    break;
                case my_java.RX:
                    countersParametrs.max_x = 900;
                    break;
                case my_java.UTOPIA:
                    countersParametrs.max_x = 960;
                    break;
                case my_java.TXFIFO:
                    countersParametrs.max_x = 960;
                    break;
                case my_java.RXFIFO:
                    countersParametrs.max_x = 960;
                    break;
                case my_java.FPGA:
                    countersParametrs.max_x = 450;
                    break;
                case my_java.OLTTX:
                    countersParametrs.max_x = 450;
                    break;
                case my_java.OLTRX:
                    countersParametrs.max_x = 700;
                    break;
                }
            }
            else
            {
                countersParametrs.max_x = 980;
            }
            if ( ! countersParametrs.draw_error )
                MyConstantsCounters.mymain.MyMainRun( countersParametrs );
            countersParametrs.clear();
            break;
            
            default:
                error = okCode;
                break;
            }
        }
        else
        {   // general function
            aa = f;
            int ll = function.length();
            r = function.substring ( aa.length() + 1, ll ).trim();
            if ( ! r.equals ( "" ) && ! aa.trim().startsWith ( "#" ) && ! r.startsWith ( "#" ) )
            {
	            aa = MyConstants.lookForPair ( aa ) ;
                int indexP = r.indexOf ( '&' ) ;
                debug ( false,"Command before replacement=" + r );
                while ( indexP != -1 )
                {
                   if ( ! paramB.equals ("") )
                   {
                       int y = r.indexOf ( ' ', indexP + 1 ) ;
                       if ( y == -1 ) y = r.length();
                       r = r.substring ( 0, indexP ) + paramB + r.substring ( y , r.length() );
                   }
                   else
                   {
                       r = r.substring ( 0, indexP ) + r.substring ( indexP + 1, r.length() );
                   }
                   indexP = r.indexOf ( '&' ) ;
                }
	            r = Utils2.substitute ( r );
	            r = Utils2.calculateAll ( r );
                debug ( false,"Command after replacement(" + paramB + ")=" + r );
                ipconn = UtilsConnection.add_connection ( aa ) ;
                if ( ipconn != null )
                {
                    if ( UtilsConnection.connect_and_send ( false, r, aa ) )
                    {
                        error = errorCode ;
                    }
                    else
                    {
                        error = okCode ;
                    }
                }
                else error = errorCode;
            }
        }
    }
    else
    { // simple function without parameters
        if ( j != null )
        {
            switch ( j.intValue () )
            {
            case 0:  // ?
            case MyConstants.noneFunction:  // none
                 break;
                 
            case MyConstants.csFunction:  //
                 MyConstants.tw.clear_screen(); 
                 break;
                 
            case MyConstants.breakFunction:
                 error = breakCode;
                 break;
                 
            default:
                error = okCode;
                break;
            }
        }
        else
        {
            error = okCode;
        }
    }
    }
    else
    {
        error = errorCode;
    }
    }
    else
    {
        error = errorCode;
    }
    return error;
}
/*****************************************************************/
//
//  for debugging
//
/*****************************************************************/
 private static void debug ( boolean doflag, String str )
 {
     if ( ( doflag ) || ( MyConstants.execdebug_flag ) )
     {
         System.out.println( " execButton : " + str ) ;
     }
 }
 // retreive the random limits and call the library random function
 private int GiveRandom ( String aa )
 {
    int nn = -1;
    StringTokenizer stc = new StringTokenizer ( aa, "-", false ) ; 
    if ( stc.countTokens() == 2)
    {
        int linf ;
        int lsup ;
    
        try
        {
            linf = Integer.parseInt( stc.nextToken().replace('[',' ').trim() );
            lsup = Integer.parseInt( stc.nextToken().replace(']',' ').trim() );
            if ( lsup > linf )
            {
                nn = MyConstants.random.nextInt ( lsup-linf ) + linf;
            }
            else nn = linf;
            debug( false, " linf="+ linf +" lsup="+lsup + " nn=" + nn );
        }
        catch ( NumberFormatException ex )
        {
            JOptionPane.showMessageDialog( new Frame(), "some of 'range' parameters is not number", "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    return nn;
 }
 // retreive the random limits and call the library random function
 // if not a range, return the value itself 
 private int GiveRange( String f, String aa )
 {
       int nn = GiveRandom ( aa );
       if ( nn != -1 )
       {
           debug( false, "f="+f+" random=" + nn );
           execstat.SetWaittime ( f, nn );
       }
       else
       {
           try
           {
               nn = Integer.parseInt ( aa );
           }
           catch ( NumberFormatException ex )
           {
               JOptionPane.showMessageDialog( new Frame(), "wait time is not number", "Error", JOptionPane.ERROR_MESSAGE );
               return ( errorCode ) ;
           }
       }
        
       return nn;
 }
 /*****************************************************/
 // replace the variable with the current value
 // add the padding to the value
 // in case the variable is under parantheses, perform the computation needed
 /*****************************************************/
 private String ExamineInc ( String ff, int inc, String padding, String variable )
 {
  String ffor = ff;
  Integer K = new Integer ( inc );
  String tt = K.toString(); // the string to replace the variable
  String padd = "";

  int index = ffor.indexOf('(');
  int ind = index + 1;
  while ( index != -1 )
  {
      ind = ffor.indexOf(')', ind + 1 );
      String expr = "";
      if ( ind != -1 )
      {
        // take the string between parantheses
        expr = ffor.substring( index + 1, ind ).trim();
      }
      if ( ! expr.equals ("") )
      {
         // substitute parameters and caculate the value
         expr = Utils2.substitute ( inc, expr.trim(), variable ) ;
         if ( ! expr.equals("") )
         {
            padd = expr ;
            for ( int uu = 0; uu < padding.length() - expr.length(); uu ++ )
            {
               padd = "0" + padd ;
            }         
            ffor = ffor.substring( 0, index ) + padd + ffor.substring( ind + 1, ffor.length() );
            index = ffor.indexOf ( '(' );
         }
         else
         {
             if ( ind < ffor.length() - 1 )
             {
                 index = ffor.indexOf ( '(', ind + 1 );
             }
             else
             {
                 index = -1 ;
             }
         }
      }
      else
      {
          index = -1;
          JOptionPane.showMessageDialog( new Frame(), "an expression is open but it is not closed", "Error", JOptionPane.ERROR_MESSAGE );
          return "";
      }
  }
  padd = tt ;
  for ( int uu = 0; uu < padding.length() - tt.length(); uu ++ )
  {
       padd = "0" + padd ;
  }
  debug(false,"ExamineInc before return : " + ffor );

  index = ffor.indexOf( variable );
  while ( index != -1 )
  {
      int jj = Utils2.getStart ( ffor, index );
      int ii = Utils2.getEnd ( ffor, index );
      debug(false,"jj="+jj+" ii="+ii );
      if ( ii - jj == variable.length() )
      {
          tt = ffor.substring( jj, ii );
          if ( tt.equals( variable ) )
          {
              ffor = ffor.substring( 0, jj ) + " " + padd + " " + ffor.substring( ii, ffor.length() );
          }
      }
      index = ffor.indexOf ( variable, index + variable.length() );
  }
  debug(false,"ExamineInc return : " + ffor );
  
  return ffor;
 }
 private int have_the_name ( String name, Vector<userButton> v, int vector_length )
 {
     int ret = -1;

     for ( int t = 0; t < vector_length; t++ )
     {
         userButton temp = ( userButton ) v.get ( t );
         if ( name.equals ( temp.button_name ) )
         {
             ret = t;
             break;
         }
     }

     return ( ret );
 }
 public void SetParameter ( String param )
 {
     paramB = MyConstants.lookForPair(param) ;
 }
}
