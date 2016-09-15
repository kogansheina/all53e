package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.* ;
import java.io.*;
import javax.swing.border.Border;
import java.beans.*;
/*****************************************************************/
//
// print the received messages from all the open connections
//
/*****************************************************************/
class TextWindowRun extends Thread 
{

/*****************************************************************/
//
// constructor of the window; the parameter 'true' make it visible now
//
/*****************************************************************/
 public TextWindowRun ( ) 
 {
 }
 public void run()
 {
     while ( MyConstants.tw.fifo.size () > 0 )
     {
         try
         {
             String ss = ( String )MyConstants.tw.fifo.remove( 0 ) ;
             if ( ss != null )
             {
                MyConstants.tw.printToWindow ( ss, false );
//                this.sleep ( 20 ) ;
             }
         }
         catch ( NoSuchElementException nse )
         {
             System.out.println ("No element in LinkedList " );
         }
         catch ( Exception ex )
         {
           System.out.println ( "Exception from run TextWindowRun " + ex + " size=" + MyConstants.tw.fifo.size() ) ; 
         }
     } // while
//     DEBUG ( false, "Fifo size = " + MyConstants.tw.fifo.size() );

 } // run
 private static void debug ( boolean doflag, String str )
 {
     if ( ( doflag ) || ( MyConstants.printdebug_flag ) )
     {
         System.out.println( "TextWindow : " + str ) ;
     }
 }
} // class
