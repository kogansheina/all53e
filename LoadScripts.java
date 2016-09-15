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
import javax.swing.border.Border;
import java.beans.*;

/************************************************************************/
//
//  Load script files to a chosen address
//  Send a single command to a chosen address
//  Loads special files - 'all' kind :
//      ip addresss
//      file to be sent
//
//      ...............
//
//      ip addresss
//      file to be sent
/************************************************************************/
class LoadScripts extends JPanel
{
 	static final long serialVersionUID = 106L;
    private JPanel mine;
    private JFrame frame;
	private String currentAddress = "";
	private String currentFile = "";
    private FileDialog selectfile;
    private tester parent;

    public LoadScripts ( JFrame frame, tester all )
    {
       super ();
       this.frame = frame;
       parent = all;
       selectfile = new FileDialog ( frame );
       mine = createLoadScriptBox();
    }
/************************************************************************/
//
//  Returns the created panel to be connected to frame
//
/************************************************************************/
    public JPanel GetLoadScriptsBox ()
    {
        return mine;
    }
/************************************************************************/
//
//  Actually creates the panel with its buttons for classes' functions
//
/************************************************************************/
    private JPanel createLoadScriptBox()
    {
        final int numButtons = 6;
        final String pickOneAddressCommand = "pickoneA";
        final String enterOneAddressCommand = "enteroneA";
        final String pickOneFileCommand = "pickoneF";
        final String enterACommand = "enterAC";
        final String openCommand = "open";
        final String closeCommand = "close";
        JRadioButton[] radioButtons = new JRadioButton[ numButtons ];
        final ButtonGroup group = new ButtonGroup();

        radioButtons[ 0 ] = new JRadioButton( "Pick an address" );
        radioButtons[ 0 ].setToolTipText("Choose an IP already defined");
        radioButtons[ 0 ].setActionCommand( pickOneAddressCommand );
        radioButtons[ 1 ] = new JRadioButton( "Enter an address" );
        radioButtons[ 1 ].setActionCommand( enterOneAddressCommand );
        radioButtons[ 1 ].setToolTipText("Define/choose an IP");
        radioButtons[ 2 ] = new JRadioButton( "Pick a file" );
        radioButtons[ 2 ].setActionCommand( pickOneFileCommand );
        radioButtons[ 2 ].setToolTipText("Choose a file to be loaded");
        radioButtons[ 3 ] = new JRadioButton( "Command window" );
        radioButtons[ 3 ].setActionCommand( enterACommand );
        radioButtons[ 3 ].setToolTipText("Open the Input window");
        radioButtons[ 4 ] = new JRadioButton( "Log options" );
        radioButtons[ 4 ].setActionCommand( openCommand );
        radioButtons[ 4 ].setToolTipText("Log file options");
        radioButtons[ 5 ] = new JRadioButton( "Audit options" );
        radioButtons[ 5 ].setActionCommand( closeCommand );
        radioButtons[ 5 ].setToolTipText("Activate the audit process");

        for (int i = 0; i < numButtons; i++ )
        {
            group.add(radioButtons[ i ] );
        }
        radioButtons[ 0 ].setSelected( true );
    /*************************************************/
    //          button 0 - choose IP address         //
    /*************************************************/
    radioButtons[ 0 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            String s = dialog ( "Choose an IP address or 'all':\n", MyConstants.IP_addresses_names, "all" );
            if ( ( s != null ) && ( s.length() > 0 ) )
            {
                currentAddress = s;
                if ( ! s.equals( "all" ) )
                {
                    if ( UtilsConnection.add_connection ( s ) == null )
                    {
				        JOptionPane.showMessageDialog( frame, "No connection for " + s, "Error", JOptionPane.ERROR_MESSAGE);
					}
                }
            } // if string
        } // action
    } ); // listener add
    /*************************************************/
    //          button 1 - enter an IP address       //
    /*************************************************/
    radioButtons[ 1 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            String s = dialog ( "Enter an IP address or 'all':\n", null, "all" );
            if ( ( s != null ) && ( s.length() > 0 ) )
            {
               currentAddress = s;
               if ( ! s.equals( "all" ) )
               {
                    if ( UtilsConnection.add_connection ( s ) == null )
                    {
				        JOptionPane.showMessageDialog( frame, "No connection for " + s, "Error", JOptionPane.ERROR_MESSAGE);
					}
               }
            } // if string
        } // action
    } ); // listener add
    /*************************************************/
    //       button 2 - add script file address      //
    /*************************************************/
    radioButtons[ 2 ].addActionListener( new ActionListener()
    {
 	    public void actionPerformed( ActionEvent e )
        {
    	    currentFile = choose_file ( );
    	    if ( ! currentFile.endsWith( "null" ) )// 'cancel' on choose file
    	    {
            	if ( currentAddress.equals ( "all" ) ) // execute an 'all' file type
            	{
		    		try
		    		{
		    		    String s;
		    		    BufferedReader reader = new BufferedReader( new FileReader( currentFile ) );
		    		    while( ( s = reader.readLine() ) != null )
		    		    {
                            boolean error = false;
		    		    	s = s.trim();
                            if ( stringOK ( s ) )
		    		    	{
                                // add and connect an address from the 'all' file
		    		    		ipdef ipconn = UtilsConnection.add_connection ( s );
		    		        	if ( ipconn != null )
		    		        	{
                                    if ( ipconn.out != null )
                                    {
                                        String s_new = "run ";
                                        s = reader.readLine();
                                        if ( stringOK ( s ) )
                                        {
                                            s_new = s_new.concat ( s );
                                            debug ( true, "send to " + s + " : " + s_new );
                                            ipconn.out.write( UtilsConnection.send_to_ip ( s_new ) );
                                        }
                                    }
                                    else error = true;
                                    // o.k. - send the command
		    		    		}
                                else error = true;
		    		    	}
                            else error = true;
                            if ( error )
                            {
                                reader.readLine(); // dummy
                            }
                        }
                        reader.close();
		    		}
		    		catch ( IOException ex )
		    		{
                        debug ( true, "Error in file " + currentFile ) ;
		    		}
		    	}
		    	else
		    	{	
                    boolean error = false;
                    if ( ( currentAddress != null ) && ( ! currentAddress.equals("") ) )
                    {
                        if ( UtilsConnection.add_connection ( currentAddress ) != null )
                        {
                        	// a 'single' file to be sent
		    		    	error = UtilsConnection.connect_and_send ( true, currentFile, currentAddress );
					    }
					    else error = true;
                    }
                    else 
                    { // to load a run file
	                    parent.createRunParameters (currentFile);
                    }
                    if ( error )
                    {
                        JOptionPane.showMessageDialog( frame, "No connection or file error", "Error", JOptionPane.ERROR_MESSAGE);
                    }
		    	}
		    }
        } // action
    } ); // listener add
    /*************************************************/
    //       button 3 -  enter a single command      //
    /*************************************************/
    radioButtons[ 3 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            if ( MyConstants.iw != null )
            {
                if ( ! MyConstants.iw.isVisible () )
                {
                    MyConstants.iw.setVisible ( true );
                }
            }
            else
            {
                MyConstants.iw = new InputWindow ( true );
            }
    	} // action
    } ); // listener add

    /*************************************************/
    //       button 4 -  open a log file      //
    /*************************************************/
    radioButtons[ 4 ].addActionListener( new ActionListener()
    {
         public void actionPerformed( ActionEvent e )
         {
             String log_action [] = { "open log file only", "open log file", "close log file" };
             int store = 0;
             String s = dialog ( "Log options\n", log_action, log_action[ 0 ] );
             if ( ( s != null ) && ( s.length() > 0 ) )
             {
                 if ( s.equals ( "close log file" ) )
                 {
                    store = 2;
                 }
                 if ( s.equals ( "open log file" ) )
                 {
                    store = 1;
                 }
                 switch ( store )
                 {
                 case 0:
                   selectfile = new FileDialog ( new JFrame() );
                   selectfile.setVisible(true);
                   s = selectfile.getDirectory() + selectfile.getFile();
                   if ( ( s != null ) && ( s.length() > 0 ) )
                   {
                       if ( ! s.endsWith( "null" ) )
                       {
                            MyConstants.tw.setFile ( s, 0 );
                       }
                   }
                   break;
                 case 1:
                     selectfile = new FileDialog ( new JFrame() );
                     selectfile.setVisible(true);
                     s = selectfile.getDirectory() + selectfile.getFile();
                     if ( ( s != null ) && ( s.length() > 0 ) )
                     {
                         if ( ! s.endsWith( "null" ) )
                         {
                              MyConstants.tw.setFile ( s, 1 );
                         }
                     }
                     break;
                 case 2:
                     MyConstants.tw.CloseFile ();
                   break;
                 }
             }
    	 } // action
    } ); // listener add
    
    /*************************************************/
    //       button 5 -  audit      //
    /*************************************************/
    radioButtons[ 5 ].addActionListener( new ActionListener()
    {
         public void actionPerformed( ActionEvent e )
         {
             if ( MyConstants.potential != null )
             {
                for ( int i = 0; i < MyConstants.ips; i ++ )
                {
                    String s1 = MyConstants.potential [ i ];
                    if ( s1.length() > 0 )
                    {
                       if ( UtilsConnection.add_connection ( s1 ) == null )
                       {
                           JOptionPane.showMessageDialog( frame, "No connection for " + s1, "Error", JOptionPane.ERROR_MESSAGE);
                       }
                       try
                       {
                           Thread.currentThread().sleep( 100 );
                       }
                       catch ( InterruptedException exc )
                       {
                           debug ( true, "Interrupted exception from button execution - function audit" );
                       }
                    }
                }
                Frame aa = new AuditFrame();
             }
             else debug( true,"potential is null" );
    	 } // action
    } ); // listener add
    // create the radio buttons and the 'exit' button in the bottom of the panel
    return Utils.create2LinePane( "Load Scripts" + ":", exit_action(), radioButtons );

    } // createLoadScriptBox

/************************************************************************/
//
//  check the read line is a valid one 
//
/************************************************************************/
    private boolean stringOK ( String s )
    {
        if ( s == null ) return false;
        if ( s.length() <= 0 ) return false;
        if ( s.charAt ( 0 ) == '#' ) return false;
        return true;
    }
/************************************************************************/
//
//  Use the dialog to choose or enter something
//
/************************************************************************/
    private String dialog ( String s, String [] options , String defaultoption )
    {
        String sret = (String)JOptionPane.showInputDialog( frame, s, s,
                        JOptionPane.PLAIN_MESSAGE, null, options, defaultoption );
        return sret;
    }
/************************************************************************/
//
//  Choose a file for a purpose
//
/************************************************************************/
    private String choose_file( )
    {
    	selectfile.setVisible(true);
        return ( selectfile.getDirectory() + selectfile.getFile() );

    } // choose file
/************************************************************************/
//
//  Creates an Action Listener for the 'exit' button
//
/************************************************************************/
	private ActionListener exit_action ()
	{
		ActionListener al = new ActionListener()
        {
           public void actionPerformed( ActionEvent event )
           {
			   parent.actionExit ();
           }
        };

       return al;
   }
/************************************************************************/
//
//  debug messages
//
/************************************************************************/
    private void debug ( boolean doflag, String str )
    {
        if ( doflag )
        {
            System.out.println( "LoadScripts : " + str ) ;
        }
    }
}  // class end
