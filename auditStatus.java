package all53e;
import java.awt.* ;
import java.awt.event.* ;
import java.util.* ;
import java.io.* ;
import javax.swing.*;
import java.lang.* ;
import java.lang.Thread.* ;

// the basic data base used by audit process
class auditStatus
{
	String name;
	public int device;
	public int status;
	public int previous_status;
	public int index;
    public boolean sent;
    public long stringtime ;
    public int statuspos;
    public String previous;
	public int x;
	public int y;

	public auditStatus ()
	{
		x = 0;
		y = 0;
        statuspos = -1;
        device = 0;
		name = "";
		status = MyConstants.Not_answer;
        previous = "not answer ";
		previous_status = MyConstants.Not_answer;
		index = -1;
        sent = false;
	}
    public void auditSetFrame ( int newStatus )
    {
        previous_status = status;
        status = newStatus;
        sent = false;
    }
	public auditStatus ( String s, int index, int dev )
	{
		x = 0;
		y = 0;
        statuspos = -1;
		status = MyConstants.Not_answer;
		previous_status = MyConstants.Not_answer;
		this.index = index + dev ;
        device = dev;
		MyConstants.connections [ this.index ] = this;
        sent = false;
        previous = "not answer ";
		Integer ii = new Integer ( dev );
		if ( ! Character.isDigit ( s.charAt ( 0 ) ) )
		{
		     if ( s.startsWith ( "olt" ) )
		     {
		     	name = s + " - " + ii.toString ();
		     }
		     else
		     {
		        name = s;
		     }
		 }
		 else
		 {
		    name = s;
		 }
		 name = name + " : ";
	}
}
