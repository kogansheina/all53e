package all53e;
import java.awt.* ;
import java.awt.event.* ;
import java.util.* ;
import java.lang.* ;

 class AuditTask extends TimerTask
 {
	public AuditTask ( )
	{
	}
// when press 'start' button into audit frame	
    public void run()
    {
	   for ( int y = 0; y < MyConstants.ips; y ++ )
       {
            if ( MyConstants.connections [ y ] != null )
            {
                auditStatus a = MyConstants.connections [ y ];
     			ipdef t = null;
				t = MyConstants.ipconnections [ a.index ];
				if ( t != null )
				{
					if ( t.socket != null )
					{
						if ( t.socket.isConnected() )
						{
                			if ( ! a.sent )
                			{
                    			a.stringtime = System.currentTimeMillis() ;
                    			a.sent = true;
                    			Integer ii = new Integer ( a.device );
                    			UtilsConnection.connect_and_send ( false, "/application/user/audit " + ii.toString(), t.address );
                			}
                			else
                			{
                    			if ( ( System.currentTimeMillis() - a.stringtime ) >= 1500 )
                    			{
                        			a.auditSetFrame( MyConstants.Not_answer );
                    			}
                    			else
                    			{
                        			Integer ii = new Integer ( a.device );
                        			UtilsConnection.connect_and_send ( false, "/application/user/audit " + ii.toString(), t.address );
                    			}
                			}
						}
            			else
            			{
                            a.auditSetFrame( MyConstants.Not_answer );
            			}
      				 } // socket null                       
                } // t null
            }
    	} // for
	} // run
} // class

