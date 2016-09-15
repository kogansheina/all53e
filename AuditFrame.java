package all53e;
import java.awt.* ;
import java.awt.event.* ;
import java.util.* ;
import java.lang.* ;

class AuditFrame extends Frame implements  ActionListener
{
	static final long serialVersionUID = 100L;
    static final int countAudit = 20;  
	private Graphics g;
    private int x, y;
    private Font f = new Font( "Serif", Font.ITALIC, 14 );
    private Font f1 = new Font( "Plain", Font.ITALIC | Font.BOLD, 14 );
    private FontMetrics fm;
    private int next_x;
    private Button start_button = new Button ( "start" ) ;
    private Button once_button = new Button ( "once" ) ;
    private Button stop_button = new Button ( "stop" ) ;
    private final int radius = 20;
	private AuditFrame me;
	public java.util.Timer timer;
	public java.util.Timer timerA;
	int maxx, maxy;

	public AuditFrame ( )
	{
		super( "Status" );
		setLayout ( new FlowLayout ( ) ) ;
    	setBackground ( Color.white ) ;
		setResizable( false );
        pack();
		g = getGraphics();
        g.setFont ( f );
        fm = g.getFontMetrics( f );
        next_x = fm.stringWidth ( "  onu_30 : operational" );
        x = countAudit * 2;
        y = countAudit;
        maxx = x + next_x ;
        maxy = countAudit ;
        if ( UtilsConnection.last_address > 34 )
        {
            maxy += countAudit * UtilsConnection.last_address / 2 ;
            maxx += next_x;
        }
        else
        {
            maxy += countAudit * UtilsConnection.last_address ;
        }
        add( once_button ) ;
        once_button.setEnabled ( true ) ;
        once_button.addActionListener ( this ) ;
        once_button.setBackground( Color.green ) ;
        add( start_button ) ;
        start_button.addActionListener ( this ) ;
        start_button.setEnabled ( true ) ;
        start_button.setBackground( Color.green ) ;
        add( stop_button ) ;
        stop_button.setEnabled ( false ) ;
        stop_button.addActionListener ( this ) ;
        stop_button.setBackground( Color.gray ) ;
        me = this;
        timer = new java.util.Timer ();
        timerA = new java.util.Timer ();
	    addWindowListener ( new WindowAdapter ( )
	    {
	       public void windowClosing ( WindowEvent e )
	       {
               setVisible ( false );
               if ( timer != null ) timer.cancel();
               timer = null;
               if ( timerA != null ) timerA.cancel();
               timerA = null;
	       }
	       public void windowDeiconified ( WindowEvent e )
	       {
	         repaint( );
	       }
	       public void windowIconified ( WindowEvent e )
	       {
	       }
	       public void windowDeactivated ( WindowEvent e )
	       {
	       }
	       public void windowActivated ( WindowEvent e )
	       {
	         repaint( );
	       }
	    } ) ;
        for ( int p = 0; p < MyConstants.ips ; p ++ )
        {
            auditStatus a = MyConstants.connections [ p ];
            if ( a != null )
            {
                 if ( y >= maxy )
                 {
                     x += next_x ;
                     y = 2 * countAudit;
                 }
                 else
                 {
                     y += countAudit;
                 }
                 if ( x < maxx )
                 {
                     a.x = x;
                     a.y = y;
                 }
                 else 
                 {
                     break;
                 }
            }
        }
        setSize ( maxx, maxy + 3 * countAudit ) ;
        setLocation ( 10, MyConstants.Screen_y - maxy ) ;	    
        setVisible( true);
	}
	
    public void actionPerformed ( ActionEvent e )
    {
       if ( e.getActionCommand() == "start" )
       {
	       if ( timer == null ) timer = new java.util.Timer();
	       timer.schedule ( new AuditTask ( ), 0, 30000 );
	       if ( timerA == null ) timerA = new java.util.Timer();
	       timerA.schedule ( new AuditTaskAnswers ( me ), 0, 30500 );
           start_button.setEnabled ( false ) ;
           once_button.setEnabled ( false ) ;
           once_button.setBackground ( Color.gray ) ;
           start_button.setBackground ( Color.gray ) ;
           stop_button.setBackground ( Color.green ) ;
           stop_button.setEnabled ( true ) ;
       }
       if ( e.getActionCommand() == "once" )
       {
	       if ( timer == null ) timer = new java.util.Timer();
	       timer.schedule ( new AuditTask ( ), 0 );	       
	       if ( timerA == null ) timerA = new java.util.Timer();
	       timerA.schedule ( new AuditTaskAnswers ( me ), 500 );	       
       }
       if ( e.getActionCommand() == "stop" )
       {
           start_button.setEnabled ( true ) ;
           once_button.setEnabled ( true ) ;
           once_button.setBackground ( Color.green ) ;
           start_button.setBackground ( Color.green ) ;
           stop_button.setBackground ( Color.gray ) ;
           stop_button.setEnabled ( true ) ;
           if ( timer != null ) timer.cancel();
           timer = null;
           if ( timerA != null ) timerA.cancel();
           timerA = null;
       }
    }

	public void repaint ( )
    {
        int [] addrlen = new int [ MyConstants.ips ];
		for ( int p = 0; p < MyConstants.ips ; p ++ )
		{
            addrlen [ p ] = 0;
        }
        for ( int p = 0; p < MyConstants.ips ; p ++ )
        {
            auditStatus a = MyConstants.connections [ p ];
            if ( a != null )
            {
                if ( a.x == 0 )
                {
                    if ( y >= maxy + countAudit )
                    {
                        x += next_x;
                        y = 2 * countAudit;
                    }
                    else
                    {
                        y += countAudit;
                    }
                    if ( x >= maxx )
                    {
                         break;
                    }
                    a.x = x;
                    a.y = y;
                }
                addrlen [ p ] = fm.stringWidth( a.name ) + 5;
            }
        }
        String tt = "";
        g.setFont ( f );
        g.setColor ( Color.white ) ;
        for ( int p = 0; p < MyConstants.ips ; p ++ )
        {
            auditStatus a = MyConstants.connections [ p ];
            if ( a != null )
            {
                if ( a.status != a.previous_status )
                {
                     switch ( a.previous_status )
                     {
                     case MyConstants.Not_answer:
                         tt = "not answer";
                         break;
                     case MyConstants.Inactive:
                     case MyConstants.Error:
                         tt = "inactive";
                         break;
                     case MyConstants.Active:
                         tt = "operational";
                         break;
                     case MyConstants.Standby:
                         tt = "standby";
                         break;
                     }
                     int X = a.x - radius + addrlen [ p ] ;
                     int Y = a.y + radius + 15 ;
                     g.drawString ( tt, X, Y );
                }
            }
        }
        tt = "";
        for ( int p = 0; p < MyConstants.ips ; p ++ )
        {
            auditStatus a = MyConstants.connections [ p ];
            if ( a != null )
            {
                g.setColor ( Color.black ) ;
                int Y = a.y + radius + 15 ;
                int X = a.x - radius ;
                g.setFont ( f1 );
                g.drawString ( a.name, X, Y );
                g.setFont ( f );
                switch ( a.status )
                {
                case MyConstants.Not_answer:
                    g.setColor ( Color.darkGray );
                    tt = "not answer";
                    break;
                case MyConstants.Inactive:
                case MyConstants.Error:
                    g.setColor ( Color.red );
                    tt = "inactive";
                    break;
                case MyConstants.Active:
                    g.setColor ( Color.green.darker() );
                    tt = "operational";
                    break;
                case MyConstants.Standby:
                    g.setColor ( Color.blue );
                    tt = "standby";
                    break;
                }
                g.drawString ( tt, X + addrlen [ p ], Y );
            }
        } // for
    }
}
