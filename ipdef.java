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
import java.net.InetSocketAddress.* ;

/*************************************************************/
//
// class to define a TCP/IP connection
//
/*************************************************************/
class ipdef
{
   public Socket socket ;
   public OutputStream out;
   public InputStream in ;
   public User user;
   public String myaddress;
   public int myIndex;
   public String address;

/*************************************************************/
//
// constructor for an empty definition
//
/*************************************************************/
    public ipdef ( )
    {
	   socket = null;
	   out = null;
	   user = null;
	   in = null;
       myaddress = null;
    }
/*************************************************************/
//
// constructor of a definition, according to the name 
// ( ip address, or mnemonic to be converted )
//
/*************************************************************/
	public ipdef ( String ipaddress )
	{
	   socket = null;
	   out = null;
	   user = null;
	   in = null;
       address = ipaddress;
       if ( MyConstants.alias != null )
       {
           myaddress = ( String ) MyConstants.alias.get ( ipaddress );
           if ( myaddress == null )
           {
               // if there is not a symbolic name - register it as is
               if ( Character.isDigit ( ipaddress.charAt ( 0 ) ) )
               {
                   myaddress = ipaddress;
               } 
               else
               {
                   myaddress = null;
               }
           }
       }
       else
       {
           // no aliasfile defied - register the address as is
           myaddress = ipaddress;
       }
       debug ( false, "Register address " + myaddress + " for " + ipaddress );
   }
/*************************************************************/
//
// open a TCP/IP connection for this address
//
/*************************************************************/
   public void OpenConnection()
   {
	   	boolean success = false;
		try
		{
            if ( myaddress != null )
            {
                if ( socket != null )
                {
			    	if ( socket.isConnected() )
			    	{
                        debug( false,"go to close " + myaddress);
                        Close();
                    }
                    else
                    {
                        debug(false,"socket not NULL, but not connected");
                    }
                }
     		    socket = TimedSocket.getSocket ( myaddress, MyConstants.ipport, MyConstants.ConnectionTO ) ;
			    if ( socket != null )
			    {
			    	if ( socket.isConnected() )
			    	{
        	    		out = socket.getOutputStream () ;
        	    		in = socket.getInputStream ();
        	    		success = true;
			    	}
                    else
                    {
                        debug(true,"socket not NULL, but not connected - after trying to connect");
                    }
			    }
                else
                {
                    debug(true,"socket NULL - after trying to connect");
                }
            }
            if ( ( user == null ) && success )
            {
                GoConnection ();
                // start the listeneing thread
                user.start();
            }
		}
		catch ( SocketTimeoutException toe )
		{
			JOptionPane.showMessageDialog( new JFrame(), toe + " for "  + myaddress + "( " + address + " )", "error", JOptionPane.ERROR_MESSAGE );
		}
		catch ( IOException ioe )
		{
			JOptionPane.showMessageDialog( new JFrame(), ioe + " for "  + myaddress + "( " + address + " )", "error", JOptionPane.ERROR_MESSAGE );
		}
	}
/*************************************************************/
//
// open a TCP/IP connection for this address
//
/*************************************************************/
   private void GoConnection ()
   {
       // create the thread to listen to this connection
       user = new User ( this, address );
       // in case it is the first success connection open the common output/input windows
       if ( ! MyConstants.tw.isVisible () )
       {
           MyConstants.tw.setVisible ( true );
       }
   }
/*************************************************************/
//
// close the connection and remove the address from the global table
//
/*************************************************************/
   private void Close()
   {
       try
       {
           if ( out != null ) out.close();
           if ( in != null ) in.close();
           if ( socket != null ) socket.close();
       }
       catch ( IOException ioe )
       {
       }
       out = null;
       in = null;
       socket = null;
   }
   public void CloseConnection()
   {
        Close();
        int index = UtilsConnection.have_it ( address );
        if ( index != -1 )
        {
            MyConstants.IP_addresses_names [ index ] = "";
            MyConstants.ipconnections [ index ] = null;
        }
    }

/*****************************************************************/
//
//  for debugging
//
/*****************************************************************/
 private void debug ( boolean doflag, String str )
 {
     if ( ( doflag ) || ( MyConstants.ipdefdebug_flag ) )
     {
         System.out.println( "ipdef : " + str ) ;
     }
 }
} // ipdef class
