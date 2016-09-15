package all53e;
import java.awt.*;
import java.lang.*;
import javax.swing.*;
import java.util.* ;
import java.io.*;

class OpenAll extends Thread
{
	public void OpenAll()
	{
	}
	
	public void run()
	{
		// connect all the already defined IP addresses
    	if ( MyConstants.potential != null )
    	{
        	for ( int y = 0; y < MyConstants.ips; y ++ )
        	{
            	if ( MyConstants.potential [ y ] != "" )
            	{
                	UtilsConnection.add_connection ( MyConstants.potential [ y ] ) ;
            	}
        	}
    	}
	}
}
