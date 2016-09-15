package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.* ;
import java.beans.*; 
/************************************************************************/
// User class
//
// Many utilities, used by the program - defined as static ( such as interface !!! )
//
/************************************************************************/
class Utils
{
    public static boolean debug_flag = false;
/*****************************************************************/
//
//  for debugging
//
/*****************************************************************/
 private static void debug ( boolean doflag, String str )
 {
     if ( ( doflag ) || ( debug_flag ) )
     {
         System.out.println( "Utils : " + str ) ;
     }
 }
/*****************************************************************/
//
// create the radio buttons for tabbed panels
// connect the 'exit' button to a listener
//
/*****************************************************************/
 public static JPanel create2LinePane( String description, ActionListener al, JRadioButton[] radioButtons )
 {
       JLabel label = new JLabel( description );
       int numPerColumn = radioButtons.length / 2;
       if ( ( radioButtons.length % 2 ) != 0 ) numPerColumn ++;
       JPanel grid = new JPanel( new GridLayout( 0, numPerColumn ) );
       for ( int j = 0; j < radioButtons.length; j += numPerColumn )
       {
           for ( int i = 0; i < numPerColumn; i++ )
           {
               if ( i + j < radioButtons.length )
               {
                   grid.add( radioButtons[ i + j ] );
               }
               else break;
           }
       }
       JPanel box = new JPanel();
       box.setLayout( new BoxLayout( box, BoxLayout.Y_AXIS ) ); // unix
       box.add( label );
       grid.setAlignmentX( 0.0f );
       box.add( grid );
       JPanel pane = new JPanel( new BorderLayout() );
       pane.add( box, BorderLayout.BEFORE_FIRST_LINE );
       JButton exitButton = new JButton( "EXIT" );
       exitButton.addActionListener( al );

       pane.add( exitButton, BorderLayout.AFTER_LAST_LINE );

       JButton debugButton = new JButton( "Debug & Help" );
       debugButton.addActionListener( new ActionListener()
       {
            public void actionPerformed( ActionEvent e )
            {
                debugTree dt = new debugTree ();
            } // action
       } ); // addlistener

       pane.add( debugButton, BorderLayout.AFTER_LINE_ENDS );

       return pane;
  }
/*****************************************************************/
//
// convert an integer to 4 bytes array
//
/*****************************************************************/
    public static int putInt ( int value, byte array [], int index )
    {
        int j = index;
        array [ j ++ ] = ( byte )( ( value & 0xFF000000 ) >> 24 ) ;
        array [ j ++ ] = ( byte )( ( value & 0xFF0000 ) >> 16 ) ;
        array [ j ++ ] = ( byte )( ( value & 0xFF00 ) >> 8 ) ;
        array [ j ++ ] = ( byte )( value & 0xFF );

        return j;
    }
/*****************************************************************/
//
// convert 4 bytes to an integer
//
/*****************************************************************/
    public static int getInt ( byte array [], int index )
    {
        int j = index;
        int value;
        byte temp [] = new byte [ 4 ] ;

        temp [ 0 ] = array [ j++ ] ;
        temp [ 1 ] = array [ j++ ]  ;
        temp [ 2 ] = array [ j++ ]  ;
        temp [ 3 ] = array [ j++ ]  ;
        java.math.BigInteger bi = new java.math.BigInteger( temp ) ;
        value = bi.intValue() ;

        return value;
    }

 /*****************************************************/
 /* for debugging purposes
 /*****************************************************/
  public static void printList( blockButton parent )
 {
    int tt = parent.vs.size();

    System.out.println("blockButton: size:" + tt + " type:" + parent.blocktype + " command:" + parent.command + "#");
    for ( int k = 0; k < tt; k ++ )
    {
       printList ( ( blockButton ) parent.vs.get ( k ) );
    }
 }
}  // end Utils class

