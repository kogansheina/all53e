package all53e;
import java.awt.* ;
import java.lang.* ;
import java.util.* ;

 class AuditTaskAnswers extends TimerTask
 {
	private AuditFrame frame ; 

	public AuditTaskAnswers ( AuditFrame f )
	{
		frame = f;
	}
// when press 'start' button into audit frame	
    public void run()
    {
        frame.repaint();
	} // run
} // class

