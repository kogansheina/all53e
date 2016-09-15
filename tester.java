package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.* ;
import javax.swing.border.Border;
import java.beans.*;

//
// Main class of tester
//

class tester extends JPanel
{
	static final long serialVersionUID = 102L;
	private JFrame frame;
	private static String path  = "";
	private static String upath  = "";
	private static String kpath  = "";
	private static String form ;
	private static int border_limit = 40;
    private static String aliasFile = "";
    private static String runFile = "";
    private static boolean immediate = false;
    
public tester ( JFrame frame )
{
	super( new BorderLayout() );
	this.frame = frame ;
	MyConstants.currentTime = System.currentTimeMillis();
	// IP_addresses_name array contains all the IPs used by the current run
	MyConstants.IP_addresses_names = new String [ MyConstants.ips ];
	// ipconnections array contains the connection definition for each IP
	MyConstants.ipconnections = new ipdef [ MyConstants.ips ];
	// connections array contains the audit instance for each connection
	MyConstants.connections = new auditStatus [ MyConstants.ips ];	
    // initialize the IP addresses array
	for ( int i = 1; i < MyConstants.ips; i ++ )
	{
	    MyConstants.IP_addresses_names [ i ] = "";
	}
	MyConstants.IP_addresses_names [ 0 ] = "all";
    MyConstants.random = new Random ( MyConstants.seed );
    // create the conversion array : between names and IP addresses
    createAliasDictionary ( aliasFile );
    MyConstants.pairs = new Properties();   
    // create the running parameters
    createRunParameters ( runFile ) ;
    MyConstantsCounters.mymain = new MyMain();
    // add the tabs to the panel
    JTabbedPane tabbedPane = new JTabbedPane();
    //Lay them out.
    Border padding = BorderFactory.createEmptyBorder( border_limit, border_limit, 5, border_limit );
    // Creates ButtonPanel object - it is the father of all ( 5 ) buttons' panels
    // parameters : path = for standard panel, upath for userPanel ( if any )
    MyConstants.bp = new ButtonPanel ( frame, this, path, upath, kpath ) ;
    // returns the JPanel of the Buttons'
    JPanel defineButtonPanel = MyConstants.bp.GetDefineButtonBox( );
    defineButtonPanel.setBorder( padding );
    tabbedPane.addTab( "User's Buttons", null, defineButtonPanel, "Define Button" );
     // creates the LoadScripts object
    LoadScripts lsc = new LoadScripts ( frame, this );
    // returns the JPanel of the Load'
    JPanel loadScriptPanel = lsc.GetLoadScriptsBox();   
    loadScriptPanel.setBorder( padding );    
    tabbedPane.addTab( "Load Script to ...", null, loadScriptPanel, "Load Scripts" );   
    // creates the DrawCounters object
    DrawCounters dc = new DrawCounters( frame, this, MyConstantsCounters.mymain );
    // returns the JPanel of DrawCounters
    JPanel drawCountersPanel = dc.GetDrawCountersBox();
    drawCountersPanel.setBorder( padding );    
    tabbedPane.addTab( "Draw Counters from ...", null, drawCountersPanel, "Draw Counters" );
    add( tabbedPane, BorderLayout.CENTER );
	// dDB has an entry per device
	MyConstantsCounters.dDB = new deviceDataBase [ 2 ] ;
	MyConstantsCounters.dDB [ 0 ] = new deviceDataBase ( 0 );
	MyConstantsCounters.dDB [ 1 ] = new deviceDataBase ( 1 );
	// creates the output window frame
	MyConstants.tw = new TextWindow ( immediate );
	// creates the output window thread and start it
	MyConstants.twr = new TextWindowRun ( );
    MyConstants.twr.start();
    // open all the registered connections
	OpenAll oa = new OpenAll ();
	oa.start();	
}  // class tester

/************************************************************************/
//
//    create the action for the 'exit' button
//
//    - write the buttons to their files
//    - send 'end' command to all open connections, and close them
//    - close all the files/connections used by drawing function
//
/************************************************************************/
	public void actionExit ()
	{
        int go = JOptionPane.showConfirmDialog( null, "Save statistics", "Save statistics", JOptionPane.YES_NO_CANCEL_OPTION );
        if ( go != 2 ) // yes
        {
        if ( MyConstants.bp != null )
        {
            if ( MyConstants.bp.db_selftests != null )
    	        MyConstants.bp.db_selftests.WriteButtons( );
            if ( MyConstants.bp.db_swtests != null )
    	        MyConstants.bp.db_swtests.WriteButtons( );
            if ( MyConstants.bp.db_hwtests != null )
    	        MyConstants.bp.db_hwtests.WriteButtons( );
            if ( MyConstants.bp.db_datatests != null )
    	        MyConstants.bp.db_datatests.WriteButtons( );
            if ( MyConstants.bp.db_usertests != null )
    	        MyConstants.bp.db_usertests.WriteButtons( );
            if ( MyConstants.bp.db_pkgtests != null )
    	        MyConstants.bp.db_pkgtests.WriteButtons( );
        }
        else
        {
            System.out.println( "error - bp is null" ) ;
        }
        // close the log file - if any
		MyConstants.tw.CloseFile ();
        // send quit message to all opend connections
        for ( int y = 0; y < MyConstants.ips; y ++ )
        {
            if ( ! MyConstants.IP_addresses_names[ y ].equals ( "" ) )
            {
                UtilsConnection.connect_and_send ( false, "quit", MyConstants.IP_addresses_names[ y ] ) ;
            }
        }
        // save statistics fle - if the user wants
        if ( go == 0 ) // yes
        {
            FileDialog selectfile = new FileDialog ( new Frame () );
            selectfile.setVisible( true );
            String fs = selectfile.getDirectory() + selectfile.getFile() ;
            try
            {
                MyConstants.statFile = new FileWriter ( fs ) ;
            }
            catch ( IOException e )
            {
                System.out.println( "Error in open statistics file " +  fs ) ;
            }
		    if ( MyConstants.statFile != null )
		    {
		    	try
		    	{
                    writeStat ( MyConstants.bp.db_selftests, "Auxilary tests / " ) ;
                    writeStat ( MyConstants.bp.db_swtests, "SW tests / " ) ;
                    writeStat ( MyConstants.bp.db_hwtests, "HW tests / " ) ;
                    writeStat ( MyConstants.bp.db_datatests, "Traffic tests / " ) ;
                    writeStat ( MyConstants.bp.db_usertests, "User tests / " ) ;
                    writeStat ( MyConstants.bp.db_pkgtests, "Package tests / " ) ;
		    		MyConstants.statFile.close();
                    if ( MyConstants.session != null )
					    MyConstants.session.write("Statistics file = " + fs + "\n");
		    	}
		    	catch ( IOException e )
    	    	{
    	    	}
		    }
        }
        try
        {
            // close the histry file - if it was open
            if ( MyConstants.session != null )
			    MyConstants.session.close();
		}
		catch ( IOException e )
    	{
    	}
        // close all the files and connetions of any drawing session
        MyConstantsCounters.mymain.GoExit();
        System.out.println("Going to exit ..." ) ;
        System.exit ( 0 );
        }
	}
    // run along the statistics of a button and write to statistics file
    private void writeStat ( DefineButton temp, String tt )
    {
        if ( temp != null )
        {
            for ( int jj = 0; jj < temp.buttons.size(); jj ++ )
            {
                userButton ub = ( userButton ) temp.buttons.get ( jj );
                for ( int k = 0; k < ub.results.size(); k ++ )
                {
                    execResult eR = ( execResult ) ub.results.get ( k );
                    eR.PrintBlock ( tt + ub.button_name, k + 1 ) ;
                }
            }
        }
    }
/************************************************************************/
//
//  Pass to other classes the main frame pointer
//
/************************************************************************/
    public JFrame GetFrame ()
    {
        return frame;
    }
/************************************************************************/
//
//  Creates the alias dictionary
//
// read "ip_addr_conv" file and build 2 hash tables to translate in both diresctions
// a line must have the following form : name ipaddress
// the name may be any string but the following conventions are used futher:
// for the addresses to be used in 'for' function, the name MUST end with 2 digits
// for some drawing functions I use the fact that the is 'olt'
//
/************************************************************************/
    private void createAliasDictionary ( String aliasfile )
    {
        Hashtable<String, String> h = null;
        Hashtable<String, String> hr = null;
        String s;
        int index = 0;

        if ( aliasfile.length() > 0 )
        {
            try
            {
                FileReader fr = new FileReader ( aliasfile );
                if ( fr != null )
                {
                    h = new Hashtable<String, String>( MyConstants.ips );
                    hr = new Hashtable<String, String>( MyConstants.ips );
                    MyConstants.potential = new String [ MyConstants.ips ];
                    for ( int y = 0; y < MyConstants.ips; y ++ )
                    {
                        MyConstants.potential [ y ] = "";
                    }
                    BufferedReader reader = new BufferedReader( fr );
                    while( ( ( s = reader.readLine() ) != null ) && ( index < MyConstants.ips ) )
                    {
                        s = s.trim();
                        if ( s.length () > 0 )
                        {
                            StringTokenizer stc = new StringTokenizer ( s, " ", false );
                            int number = stc.countTokens();
                            if ( number >= 2 )
                            {
                                String s1 = stc.nextToken().trim();
                                String s2 = stc.nextToken().trim();
                                // if the line begins with '#'
                                // the address is skipped
                                if ( s1.charAt ( 0 ) != '#' )
                                {
                                    h.put ( s1, s2 );
                                    hr.put ( s2, s1 );
                                    MyConstants.potential [ index ++ ] = s1;
                                }
                            }
                        }
                    }
                    reader.close();
                    fr.close();
                }
            }
            catch ( IOException ex )
            {
                System.out.println("file " + aliasfile + " not found" );
            }
        }
        MyConstants.alias = h;
        MyConstants.reversealias = hr;
    }
 //********************************************************************
 //
 // read the 'runFile' and set the running variable
 //
 //*******************************************************************   
   public void createRunParameters ( String runFile )
   {
    
    if ( ! runFile.equals("") )
    {
     try
     {
		FileInputStream fr = new FileInputStream( runFile );
		if ( fr != null )
		{
	         MyConstants.pairs.load( fr );
        }
     }
     catch ( IOException ex )
     {
         System.out.println("file " + runFile + " not found" );
     }
    }
    }
/************************************************************************/
//                                                                      //
//////                            MAIN                             ///////
//                                                                      //
/************************************************************************/
    public static void main( String[] args )
    {
        final String version = "5.3e";
		final tester mytester;
        int device = 0; // UNIX
        boolean error = false;
        boolean b_option = false;
        boolean d_option = false;
        boolean doHistory = false;

        String [] help =
        {
            "Parameters may be : ",
            "   -a < file name > : alias for IP addresses",
            "   -k < file for package approval buttons file >",
            "   -p < path > : path for all used files ( buttons, alias, etc )",
            "   -b < file for user buttons file >",
            "   -r < file for run parameter >",
            "   -h ( do history )",
            "   -s < number > : seed",
            "   -t < number > : connection timeout ( msec )",
            "   -d < pc | unix | linux > : device type ( default is UNIX )",
        };

        System.out.println ( "\nVERSION : " + version + "\n" );
        MyConstants.SP = System.getProperties();
//        MyConstants.SP.list(System.out);
        String file_separator = MyConstants.SP.getProperty( "file.separator" ) ;
        String file_path = MyConstants.SP.getProperty( "user.home" ) ;
       if ( args.length == 0 )
        {
            for ( int k = 0; k < help.length; k ++ )
            {
                System.out.println ( help [ k ] );
            }
        }
        else
        {
            for ( int k = 0; k < args.length; k ++ )
            {
                int who = 0;
                String st = args [ k ];
                if ( st.equals ( "-a" ) )
                {
                    who = 1;
                }
                if ( st.equals ( "-p" ) )
                {
                    who = 2;
                }
                if ( st.equals ( "-b" ) )
                {
                    who = 3;
                }
                if ( st.equals ( "-d" ) )
                {
                    who = 4;
                }
                if ( st.equals ( "-k" ) )
                {
                    who = 5;
                }
                if ( st.equals ( "-s" ) )
                {
                    who = 6;
                }
                if ( st.equals ( "-r" ) )
                {
                    who = 8;
                }
                if ( st.equals ( "-h" ) )
                {
                    who = 9;
                }
                if ( st.equals ( "-t" ) )
                {
                    who = 10;
                }
                switch ( who )
                {
                case 1:
                    if ( args.length > k )
                    {
                        aliasFile = args [ ++k ];
                    }
                    else
                    {
                        System.out.println ( "wrong parameter for '-a' option" );
                        error = true;
                    }
                    break;

                case 2:
                    if ( args.length > k )
                    {
                        MyConstants.ip_file_path = args [ ++k ]+ file_separator ;
                        d_option = true;
                    }
                    else
                    {
                        System.out.println ( "wrong parameter for '-p' option" );
                        error = true;
                    }
                    break;

                case 3:
                    if ( args.length > k )
                    {
                        upath = args [ ++k ];
                    }
                    else
                    {
                        System.out.println ( "wrong parameter for '-b' option" );
                        error = true;
                    }
                    break;

                case 4:
                    if ( args.length > k )
                    {
                        String arg = args [ ++k ];
                        if ( arg.equals ( "pc" ) )
                        {
                            device = 1;
                            MyConstants.Screen_x = 666;
                            MyConstants.Screen_y = 850;
                        }
                        else
                        {
                            if ( arg.equals ( "linux" ) )
                            {
                                device = 2;
                            }
                            else
                            {
                                if ( ! arg.equals ( "unix" ) )
                                {
                                    System.out.println ( "wrong parameter for '-d' option" );
                                    error = true;
                                }
                            }
                        }
                    }
                    break;

                case 5:
                    if ( args.length > k )
                    {
                        kpath = args [ ++k ];
                    }
                    else
                    {
                        System.out.println ( "wrong parameter for '-k' option" );
                        error = true;
                    }
                    break;

                case 6:
                    MyConstants.seed = Integer.parseInt ( args [ ++k ] );
                    break;
                    
                case 8:
                    if ( args.length > k )
                    {
                        runFile = args [ ++k ];
                    }
                    else
                    {
                        System.out.println ( "wrong parameter for '-r' option" );
                        error = true;
                    }
                    break;

                case 9:
                    doHistory = true;
                    break;

                case 10:
                    Integer T = new Integer ( args [ ++k ] ) ;
                    MyConstants.ConnectionTO = T.intValue() ;
                    break;

                default:
                    System.out.println ( "wrong parameter " + st );
                    error = true;
                    break;
                }
            }
        }
        if ( error ) System.exit ( 0 );
        if ( MyConstants.ip_file_path.equals("") )
        {
            MyConstants.ip_file_path = file_path + file_separator ;
        }
        if ( path.equals("") )  // no path for buttons
        {
            path = MyConstants.ip_file_path ;
        }
        if ( upath.equals("") )  // no path for buttons
        {
            upath = MyConstants.ip_file_path ;
        }
        else
        {
            int index = upath.lastIndexOf(file_separator);
            String filename = "";
            if ( index != -1 )
            {
               filename = upath.substring( index + 1,upath.length() );
               upath = upath.substring( 0, index + 1 ) ;
            }
            else
            {
                filename = upath ;
            }
            MyConstants.buttonfile5 = filename ;
            MyConstants.buttonfile5save = MyConstants.buttonfile5 + ".save" ;
        }
        if ( kpath.equals("") )  // no path for buttons
        {
            kpath = MyConstants.ip_file_path ;
        }
        else
        {
            int index = kpath.lastIndexOf(file_separator) ;
            String filename = "";
            if ( index != -1 )
            {
               filename = kpath.substring( index + 1,kpath.length() );
               kpath = kpath.substring( 0, index + 1 ) ;
            }
            else
            {
                filename = kpath ;
            }
            MyConstants.buttonfile6 = filename ;
            MyConstants.buttonfile6save = MyConstants.buttonfile6 + ".save" ;
        }
        if ( aliasFile.equals ("") ) // no ip_addr_conv is given
        {
            aliasFile = MyConstants.ip_file_path + MyConstants.addressfile ;
        }
        if ( doHistory )
        {
            // if histry required - open the file and write to it the current parameters
            Calendar calendar = Calendar.getInstance();
            int m = calendar.get( Calendar.MONTH ) + 1;
            int d = calendar.get( Calendar.DAY_OF_MONTH );
            int h = calendar.get( Calendar.HOUR_OF_DAY );
            int mm = calendar.get( Calendar.MINUTE );
            try
            {
            	MyConstants.session = new FileWriter ( file_path + file_separator + "testerSession" + "." + d + "." + m + "." + h + "." + mm ) ;
            	MyConstants.session.write( "Version = " + version + "\n" );
            	MyConstants.session.write( "Buttons' path = " + MyConstants.ip_file_path + "\n" );
            	MyConstants.session.write( "Addresses = " + aliasFile + "\n" );
            	MyConstants.session.write( "User uttons' path = " + upath + "\n" );
            	MyConstants.session.write( "Package buttons' path = " + kpath + "\n" );
            	MyConstants.session.write( "Parameters file = " + runFile + "\n" );
            	MyConstants.session.write( "Seed = " + MyConstants.seed + "\n" );
    	    }
		    catch ( IOException e )
    	    {
	        	System.out.println("Session file cannot be open");
    	    }
        }
        else MyConstants.session = null;
        MyConstants.output_x = MyConstants.Screen_x / 30;
        MyConstants.output_y = MyConstants.Screen_y / 4;
        MyConstants.input_x = MyConstants.Screen_x / 2;
        MyConstants.input_y = MyConstants.Screen_y / 4;
        MyConstants.button_x = ( MyConstants.Screen_x / 4 ) * 3;    
        MyConstants.button_wx = MyConstants.Screen_x / 3 - 20;
        MyConstants.button_wy = MyConstants.Screen_y / 3 - 20;       
        JFrame frame = new JFrame( "Tester - " + version );
        Container contentPane = frame.getContentPane();
        contentPane.setLayout( new GridLayout( 1,1 ) );
        mytester = new tester( frame );
		contentPane.add( mytester );
		frame.addWindowListener ( new WindowAdapter ( )
		{
			  public void windowClosing ( WindowEvent e )
			  {
			     mytester.actionExit();
			  }
			  public void windowDeiconified ( WindowEvent e ){}
			  public void windowIconified ( WindowEvent e ){}
			  public void windowDeactivated ( WindowEvent e ){}
			  public void windowActivated ( WindowEvent e ){}
		} ) ;
        frame.setLocation( 50, 0 );
        frame.pack();
    	frame.setVisible( true );
    } // main
}

