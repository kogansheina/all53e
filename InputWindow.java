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
//
// Receive typed string as input for commands
//
class InputWindow extends JFrame implements ActionListener
{
	static final long serialVersionUID = 112L;
    private JTextArea codeText = null;
    private int lastIndex = 0;
    private JButton mb, cb;
    private JScrollPane scrollPane;

    public InputWindow ( boolean immediate )
    {
        super ( "Input Text Area " );
        getContentPane().setLayout( new FlowLayout() );
        JPanel pn = new JPanel();
        codeText = new JTextArea( 20,25 );
        codeText.setFont( new Font( "Dialog", Font.PLAIN, 14 ) );
	    pn.add( codeText );
	    scrollPane = new JScrollPane( codeText,
	    		          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    	getContentPane().add ( "Center", pn );
        pn.add( scrollPane );
        JPanel bp = new JPanel( new GridLayout ( 0, 1 ) );
        mb=new JButton( "Enter" );
        mb.setEnabled ( true );
	    mb.addActionListener( this );
        bp.add( mb );
        cb=new JButton( "Copy" );
        bp.add( cb );
        cb.setEnabled ( true );
	    cb.addActionListener( this );
        getContentPane().add( bp );
        setLocation ( MyConstants.input_x, MyConstants.input_y );
        pack();
        setVisible ( immediate );
	}
	public void actionPerformed( ActionEvent e )
    {
	   JButton b = (JButton )e.getSource();
	   if ( b == cb ) // copy button
	   {
	        String selected = codeText.getSelectedText();
	        if ( selected != null )
	        {
		    	codeText.append ( selected );
		    }
	   }
	   if ( b == mb ) // enter button
	   {
		    String last;
	        String received = codeText.getText();
            int len = received.length();
            if ( received.charAt ( len - 1 ) == '\n' )
            {
                received = received.substring ( 0, len - 1 );
            }
            len = received.length();
	        if ( lastIndex == 0 )
	        {
		    	lastIndex = len;
		    	last = received;
		    }
		    else
		    {
		    	last = received.substring ( lastIndex, len ).trim();
		    	lastIndex = len;
		    }
            // split the content of the window into lines
  			StringTokenizer stc = new StringTokenizer ( last, "\n", false ) ;
  			int number = stc.countTokens();
  			for ( int jj = 0; jj < number; jj ++ )
  			{
				String one = stc.nextToken().trim();
                if ( ! one.equals ( "" ) )
                {
                    // look for the address to sent to 
				    int index = one.indexOf( ' ' );
                    if ( index > 0 )
                    {
                        // pick the address
				        String aa = one.substring( 0,index );
                        // pick the command
				        String command = one.substring ( index + 1, one.length() ).trim();
                        // send it 
				        if ( UtilsConnection.connect_and_send ( false, command, aa ) == true )
				        {
                            // if failed - try to reconnect
				        	ipdef temp = UtilsConnection.add_connection ( aa );
				        	if ( temp != null )
				        	{
				        		if ( temp.out != null )
				        		{
                                    // o.k. - send again
				        			UtilsConnection.connect_and_send ( false, command, aa );
				        		}
				        		else JOptionPane.showMessageDialog( this, "No connection for " + aa,
                                                              "Error", JOptionPane.ERROR_MESSAGE );
				        	}
				        	else JOptionPane.showMessageDialog( this, "No connection for " + aa,
                                                              "Error", JOptionPane.ERROR_MESSAGE );
				        }
                    }
                    else JOptionPane.showMessageDialog( this, "Forgot address ?",
                                                          "Error", JOptionPane.ERROR_MESSAGE );
			    }
            }
		}
	}
}
