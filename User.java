package all53e;
import java.awt.*;
import java.awt.event.*;
import java.awt.FileDialog.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.* ;
import java.beans.*; 


/************************************************************************/
//
// thread to listen and print what is received on its IP connection
//
/************************************************************************/
class User extends Thread
{
    public boolean debug_flag = false;
	private String myconnection;
	private ipdef t;
    private execButton ubt;
    private String [] toCheck;
    private boolean [] toWake;
    private String last = "";
    private String current;
    private int last_intercept = 0;

/************************************************************************/
//
// constructor for the user's button thread - the null one         
//
/************************************************************************/
	public User ()
	{
		myconnection = "";
		t = null;
        toCheck = new String [ MyConstants.ToIntercept ] ;
        toWake = new boolean [ MyConstants.ToIntercept ] ;
        ClearToCheck ();
	}
/************************************************************************/
//
// constructor for the user's button thread - set the address and its connection         
//
/************************************************************************/
	public User ( ipdef myipdef, String ipaddress )
	{
		t = myipdef;
		myconnection = ipaddress;
        toCheck = new String [ MyConstants.ToIntercept ] ;
        toWake = new boolean [ MyConstants.ToIntercept ] ;
        ClearToCheck ();
	}
/************************************************************************/
//
// set the button to wait to be awaked and its text to check
//
/************************************************************************/
    public void SetToCheck ( execButton ub, String strToCheck, boolean wakeup )
    {
       ubt = ub;
       debug ( false, "ToCheck : " + last_intercept + "=" + strToCheck ) ;
       if ( last_intercept < MyConstants.ToIntercept )
       {
           toCheck [ last_intercept ] = strToCheck;
           toWake [ last_intercept ++ ] = wakeup;
       }
    }
 // clear any checking/intercept text of the button
    public void ClearToCheck ( )
    {
       for ( int y = 0; y < MyConstants.ToIntercept; y ++ )
       {
           toCheck [ y ] = "";
           toWake [ y ] = false;
       }
       last_intercept = 0;
    }
/************************************************************************/
//
// listen to connection
// read - the first byte gives the length of the following record
// read the record
// write the test to the TextWindow, common to all the defined connections
// interrupts for a while to give chance to others
//
/************************************************************************/
public void run ()
{
    // the maximum uffer to be received at once
	byte [] buffer = new byte [ 300 ];
    String st;
    int do_finish = -1;
    int len = 0;

    for ( ;; )
	{
        if ( t.socket != null )
        {
		    if ( t.socket.isConnected() )
		    {
                try
                {
                    // read the stream
		    	    len = t.in.read ( buffer );
		    	    if ( ( len != 0 ) && ( len != -1 ) )
		    	    {
                        // if anything received - format the identification string
                        String beg = myconnection + " > ";
		    		    st = new String ( buffer, 0, len );
                        // look into the received string for end of the line
  		    		    StringTokenizer stc = new StringTokenizer ( st, "\n\r", true ) ;
  		    		    int number = stc.countTokens();
                        // analize the received string
                        // split it according to end of line characters
                        // if no ending character was received, keep the buffer and wait
  		    		    for ( int jj = 0; jj < number; jj ++ )
  		    		    {
		    		    	current = stc.nextToken();
                            if ( current.length() == 1 )
                            {
                                if ( current.charAt ( 0 ) == '\n' )
                                {
                                    do_finish = 1;
                                }
                                else
                                {
                                    // into BL application, the lnes are ended by '\r'
                                    if ( current.charAt ( 0 ) == '\r' )
                                    {
                                        do_finish = 0;
                                    }
                                    else
                                    {
                                        do_finish = 2;
                                    }
                                }
                            }
                            else
                            {
                                do_finish = 2;
                            }
                            switch ( do_finish )
                            {
                            case 1:  // for '\n' - do nothing
                                break;
            
                            case 0: // for '\r' - do newline
                                // check for the 'if' function the 'yes' condition
                                if ( ( ubt != null ) && ( last_intercept > 0 ) )
                                {
                                    // Do I need to wake somebody ?
                                    // Did I receive what was expected ?
                                    int wakeup = Is ( last );
                                    // yes
                                    if ( wakeup != -1 )
                                    {
                                        // notify the waiting thread with the received string
                                        // the 'if' function, either 'intercept'
                                         synchronized ( ubt )
                                         {
	                                         ubt.notify( last, toWake [ wakeup ]);
                                         }
                                         if ( toWake [ wakeup ] )
                                         {
                                             // mark as received the last message
                                             Reorganize ( wakeup ) ;
                                         }
                                    }
                                }
                                // check for a possible answer o audit prcess
                                if ( last.trim().startsWith( MyConstants.auditAnswer ) )
                                {
                                   if ( MyConstants.connections [ t.myIndex ] != null )
                                   {
                                      int dev = 0;
                                      if ( last.trim().length() >= MyConstants.auditAnswer.length() + 4 )
                                      {
                                          dev = Integer.parseInt( last.substring( MyConstants.auditAnswer.length() + 2, MyConstants.auditAnswer.length() + 4 ).trim() );
                                      }
                                      // yes - send the answer to the its status
                                      MyConstants.connections [ t.myIndex + dev ].auditSetFrame( Integer.parseInt( last.substring( MyConstants.auditAnswer.length(), MyConstants.auditAnswer.length() + 2 ).trim() ) );
                                      debug ( false, "last to audit:" + last );
                                   }
                                   else debug ( true,"audit answer received, but connection is null " + t.myIndex );
                                }
		    					else
		    					{
                                    // check it it is not the 'echo' of the audit command
                                    if ( ! last.trim().startsWith( MyConstants.auditCommand ) )
                                    {
                                        debug ( false, "last to output:" + last );
                                        MyConstants.tw.SetText ( beg, last + "\n" );
                                    }
                                }                               
                                last = "";
                                break;
            
                            case 2:  // anything else - add to the line
                                last = last + current;
                                break;
            
                            default:
                                debug ( true, " switch error " + do_finish );
                                break;
                            }
  		    		    } // for
                        this.yield() ;
		    	    }  // if len
                    else
                    {
                        // give others the chance
                        this.sleep ( 1000 );
                    }
		        }
                catch ( IOException ex )
                {
                        debug ( false, " I/O error in user's thread read " + myconnection );
                }
	            catch ( InterruptedException ie )
	            {
	            		debug ( true, " Interrupt error in user's thread " + myconnection );
                        t.CloseConnection();
	            }
            } // if connected or not reset
            else
            {
                // the connection is lost and the pack is not more in reset
                debug ( true, myconnection + " lost connection" );
            }
        }
        else
        {
            try
            {
                this.sleep( 1000 ) ;
            }
            catch ( InterruptedException ie )
            {
            }
//            debug ( true, myconnection + " connection closed" );
        }
    }  // listen to connection forever
}
/*****************************************************************/
// check the received sting against the intercept table
// if found return the index into the array - used futher to
// to check if to wake up the caller thred or not
/*****************************************************************/
private int Is ( String last )
{
    int index = -1;
    int y = 0;
    while ( ( index == -1 ) && ( y < last_intercept ) )
    {
        if ( ! toCheck [ y ].equals(""))
        {
            index = last.indexOf ( toCheck [ y ] ) ;
        }
        y++ ;
    }
    debug ( false, "Is : " + last + " index=" + index ) ;
    if ( index == -1 ) return -1 ;
    return ( y - 1 );
}
/*****************************************************************/
// after a requested tring was received, delete its entry from the
// intercept array and move all the next entries, in order to have a
// continue table                                 
/*****************************************************************/
 private void Reorganize ( int index ) 
 {
     toCheck [ index ] = "";
     if ( last_intercept > 0 )
     {
         last_intercept -- ;
         for ( int y = index; y < last_intercept ; y ++ )
         {
             toCheck [ y ] = toCheck [ y + 1 ];
             toWake [ y ] = toWake [ y + 1 ];
         }
     }
 }
/*****************************************************************/
//
//  for debugging
//
/*****************************************************************/
 private void debug ( boolean doflag, String str )
 {
     if ( ( doflag ) || ( MyConstants.Userdebug_flag ) )
     {
         System.out.println( System.currentTimeMillis() + " User : " + str ) ;
     }
 }
}
