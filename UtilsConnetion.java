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
class UtilsConnection
{
	public static int last_address = 1;
    public static boolean debug_flag = false;
/************************************************************************/
//
// add an address to the table; try to open a connection for it
// returns its connection
//
/************************************************************************/
	public static ipdef add_connection ( String s )
	{
		ipdef temp = null;
		int index;
    
    if ( s != null )
    {
        // check if it is already defined
		index = have_it ( s );
		if ( index == -1 )
		{
			temp = new ipdef ( s );
            if ( temp.myaddress != null )
            {
				temp.myIndex = last_address;
			    MyConstants.IP_addresses_names [ temp.myIndex ] = s;
			    MyConstants.ipconnections [ temp.myIndex ] = temp;
			    last_address ++;
			    temp.OpenConnection ();
                debug ( false, "Add connection for " + s + " at index = " + temp.myIndex );
        		auditStatus a = new auditStatus ( s, temp.myIndex, 0 );
                if ( ! Character.isDigit ( s.charAt ( 0 ) ) )
                {
                    if ( s.startsWith ( "olt" ) )
                    {
                        a = new auditStatus ( s, temp.myIndex, 1 );
                    }
                }            
            }
            else 
                debug ( true, "Cannot add connection for " + s );
		}
		else
		{
			temp = MyConstants.ipconnections [ index ];
			if ( temp == null )
			{
				temp = new ipdef ( s );
                if ( temp.myaddress != null )
                {
				    MyConstants.ipconnections [ index ] = temp;
				    temp.OpenConnection ();
                    debug ( false, "Add connection for " + s + " at index = " + index );
                }
                else 
                    debug ( true, "Cannot add connection for " + s );
			}
		}
     } 
     return temp;
  }
/************************************************************************/
//
// look for an address into their array
// if found, returns its index, otherwise returns -1
//
/************************************************************************/
    public static int have_it ( String s )
    {
       boolean found = false;
       int ii = -1;
       for ( int i = 0; i < last_address; i ++ )
       {
           if ( MyConstants.IP_addresses_names [ i ].equals ( s ))
           {
               ii = i;
           	   found = true;
           	   break;
           }
       }
       return ii;
    }
/*****************************************************************/
//
// convert a string to a byte array and add into the first byte the length of the array
//
/*****************************************************************/
 public static byte [] send_to_ip ( String s )
 {
    String ss = s.trim() + "\r\n" ;
    return ss.getBytes();
 }
/*****************************************************************/
//
// open a connection ( if it is not ) and send a file to be executed
// or a single command
//
/*****************************************************************/
 public static boolean connect_and_send ( boolean command, String cf, String ca )
 {
    boolean error = false;
	ipdef ipconn ;

	int index = have_it ( ca );

	if ( index != -1 )
	{
      // connection already exist
	  ipconn = MyConstants.ipconnections [ index ];
      debug( false, "connect and send " + ca + "#" + cf + "#" );
      if ( ipconn != null )
      {
        if ( ipconn.socket != null )
        {
            if ( ipconn.socket.isConnected() )
            {
    	         if ( command == true ) // file
    	         {
                    // format the command to execute a script on the pack and sent it
               	    String s_new = "run ";
               	    String s = s_new.concat ( cf );
                    try
                    {
                        ipconn.out.write( send_to_ip ( s ) );
                        debug( false, "(1)actually send " + s + "#");
                    }
                    catch ( IOException exw )
                    {
                    }
	             }
                 else
                 {
                    // single commad - may be sent only to a well defined address
	           	    if ( ! ca.equals( "all" ) )
	           	    {
                        try
                        {
                            ipconn.out.write( send_to_ip ( cf ) );
                            debug( false, "(2)actually send " + cf + "#");
                        }
                        catch ( IOException exw )
                        {
                        }
	           	    } // single command with a single address
	           	    else return true;
	             }  // single command
	        } // connected
            else 
            {
               ipconn.OpenConnection();
            }
        } // socket exists
        else return true;
      }
      else return true;
    } // definition exists
    else return true;

    return error;
 }
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
}  // end Utils class

