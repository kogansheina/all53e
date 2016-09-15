package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.* ;
import javax.swing.border.Border;
import java.beans.*;

/************************************************************************/
//
//  Define the drawing parameters
//
/************************************************************************/
class DrawCounters extends JPanel
{
	static final long serialVersionUID = 107L;
    private JPanel mine;
    private JFrame frame;
    private tester parent;
	private String currentAddress = "";
	private String currentFile = "";
    private Options checkpanel;
    private drawParameters countersParametrs;
    private MyMain mymain;
    private int device = 0;

    public DrawCounters( JFrame frame, tester all, MyMain me )
    {
       super ();
       this.frame = frame;
       mymain = me;
       parent = all;
       countersParametrs = new drawParameters( 0 );
       mine = createDrawCountersBox();
    }
/************************************************************************/
//
//  Returns the created panel to be connected to frame
//
/************************************************************************/
    public JPanel GetDrawCountersBox ()
    {
        return mine;
    }
/************************************************************************/
//
//  Actually creates the panel with its buttons for classes' functions
//
/************************************************************************/
    private JPanel createDrawCountersBox()
    {
        final int numButtons = 5;
        final String enterAddressCommand = "enterA";
        final String graphsCommand = "graphs";
        final String storeCommand = "store";
        final String optionsCommand = "optionsD";
        final String goCommand = "goDraw";
        JRadioButton[] radioButtons = new JRadioButton[ numButtons ];
        final ButtonGroup group = new ButtonGroup();

        radioButtons[ 0 ] = new JRadioButton( "Enter address" );
        radioButtons[ 0 ].setActionCommand( enterAddressCommand );
        radioButtons[ 0 ].setToolTipText("Choose an IP/saved file to draw its counters");
        radioButtons[ 1 ] = new JRadioButton( "Counters" );
        radioButtons[ 1 ].setActionCommand( graphsCommand );
        radioButtons[ 1 ].setToolTipText("Define counters filters");
        radioButtons[ 2 ] = new JRadioButton( "Options" );
        radioButtons[ 2 ].setActionCommand( optionsCommand );
        radioButtons[ 2 ].setToolTipText("Define counters options ( save file, report file, read/print timers )");
        radioButtons[ 3 ] = new JRadioButton( "GO !!" );
        radioButtons[ 3 ].setActionCommand( goCommand );
        radioButtons[ 3 ].setToolTipText("Activate counters process");
        radioButtons[ 4 ] = new JRadioButton( "Display options" );
        radioButtons[ 4 ].setActionCommand( storeCommand );
        radioButtons[ 4 ].setToolTipText("Display the current set options for counters");

        for ( int i = 0; i < numButtons; i++ )
        {
            group.add( radioButtons[ i ] );
        }
        radioButtons[ 0 ].setSelected( true );

    /*************************************************/
    //        button 0 -  IP/file name               //
    /*************************************************/
    radioButtons[ 0 ].addActionListener( new ActionListener()
    {
	    public void actionPerformed( ActionEvent e )
        {
           String s = dialog ( "Enter the IP:\n", null, "" );
           if ( ( s != null ) && ( s.length() > 0 ) )
           {
                countersParametrs.file_name = s;
	       } // if string
	    } // action
    } ); // addlistener
    /*************************************************/
    //    button 1 -  define graphs to be drawn      //
    /*************************************************/
    radioButtons[ 1 ].addActionListener( new ActionListener()
    {
	    public void actionPerformed( ActionEvent e )
        {
			boolean error = false;
			String aa = dialog ( "Enter device:\n", null, "" );
			if ( ( aa != null ) && ( aa.length() > 0 ) )
			{
					device = Integer.parseInt(aa);
			}
			else
			{
                JOptionPane.showMessageDialog( new JFrame(), "A device is mandatory - default is 0", "Info", JOptionPane.INFORMATION_MESSAGE );
                device = 0;
			}
			if ( device > 1 )
			{
					JOptionPane.showMessageDialog( new JFrame(), "Device may be 0 or 1", "Error", JOptionPane.ERROR_MESSAGE );
					error = true;
			}
			if ( ! error )
			{
				countersParametrs.device = device ;
            	countersParametrs.draw_error = setOtherFilters ( "Counters:\n", countersParametrs.graphs, frame, "" );
			}
	    } // action
    } ); // addlistener
    /*************************************************/
    //    button 3 -  drawing options                //
    /*************************************************/
    radioButtons[ 2 ].addActionListener( new ActionListener()
    {
	    public void actionPerformed( ActionEvent e )
        {
			boolean error = false;
            String aa = dialog ( "Enter device:\n", null, "" );
            if ( ( aa != null ) && ( aa.length() > 0 ) )
            {
                device = Integer.parseInt(aa);
            }
            else
            {
                JOptionPane.showMessageDialog( new JFrame(), "Device is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
                error = true;
            }
            if ( device > 1 )
            {
                JOptionPane.showMessageDialog( new JFrame(), "Device may be 0 or 1", "Error", JOptionPane.ERROR_MESSAGE );
                error = true;
            }
            if ( ! error )
            {
                checkpanel = new Options ( device, 10 /*9 */);
	    		Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     			Double dx = new Double ( pp.getX());
     			Double dy = new Double ( pp.getY());
                checkpanel.makeItVisible ( dx.intValue(), dy.intValue() );
            }
	    } // action
    } ); // addlistener
    /*************************************************/
    //              button 4 - GO !!!!               //
    /*************************************************/
    radioButtons[ 3 ].addActionListener( new ActionListener()
    {
	    public void actionPerformed( ActionEvent e )
        {
			boolean error = false;
			String aa = dialog ( "Enter device:\n", null, "" );
			if ( ( aa != null ) && ( aa.length() > 0 ) )
			{
				device = Integer.parseInt(aa);
			}
			else
			{
                JOptionPane.showMessageDialog( new JFrame(), "A device is mandatory - default is 0", "Info", JOptionPane.INFORMATION_MESSAGE );
                device = 0;
			}
			if ( device > 1 )
			{
					JOptionPane.showMessageDialog( new JFrame(), "Device may be 0 or 1", "Error", JOptionPane.ERROR_MESSAGE );
					error = true;
			}
			if ( ! error )
			{
                
            countersParametrs.device = device ;
            if ( MyConstantsCounters.dDB[device].countersParametrs.defined )
            {
                 countersParametrs.save = MyConstantsCounters.dDB[device].countersParametrs.save;
                 countersParametrs.save_file = MyConstantsCounters.dDB[device].countersParametrs.save_file;
                 countersParametrs.report = MyConstantsCounters.dDB[device].countersParametrs.report;
                 countersParametrs.report_file = MyConstantsCounters.dDB[device].countersParametrs.report_file;
            }
           int go = JOptionPane.showConfirmDialog( null, "GO !!!!", "GO !!!!", JOptionPane.YES_NO_CANCEL_OPTION );
            if ( go == 0 ) // go = 0 ==> yes
            {
                // check an address is provided
				if ( countersParametrs.file_name.equals ("") )
				{
					countersParametrs.draw_error = true ;
					JOptionPane.showMessageDialog( frame, "A file name or an IP address is mandatory", "Error", JOptionPane.ERROR_MESSAGE);
				}
                if ( ! countersParametrs.draw_error && countersParametrs.all_in_one )
                {
                    if ( countersParametrs.graphs.size() == 0 )
                    {
                        countersParametrs.draw_error = true ;
                        JOptionPane.showMessageDialog( frame, "AllInOne option need only counter per module", "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }
                if ( ! countersParametrs.draw_error )
                {
                    // run the thread for frame address
                    mymain.MyMainRun( countersParametrs );
                    // prepare to receive another request
                    countersParametrs.clear();
                    if ( checkpanel != null )
                        checkpanel.clear();
                }
                else
                {
                    // there are some errors
                    go = JOptionPane.showConfirmDialog( null, "Do you want to correct ?", "Correct", JOptionPane.YES_NO_OPTION );
                    if ( go == 1 ) // no => cancel everything
                    {
                       countersParametrs.clear();
                    }
                    else
                    {
                        // yes - try again
                        countersParametrs.draw_error = false;
                    }
                }
            }
            else
            {
				if ( go == 2 ) // cancel
				{
	               countersParametrs.clear();
				}
			}
		}
	    } // action
    } ); // addlistener

    /*************************************************/
    //    button 5 - print options                   //
    /*************************************************/
    radioButtons[ 4 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
			boolean error = false;
			String aa = dialog ( "Enter device:\n", null, "" );
			if ( ( aa != null ) && ( aa.length() > 0 ) )
			{
				device = Integer.parseInt(aa);
			}
			else
			{
                JOptionPane.showMessageDialog( new JFrame(), "A device is mandatory - default is 0", "Info", JOptionPane.INFORMATION_MESSAGE );
                device = 0;
			}
			if ( device > 1 )
			{
				JOptionPane.showMessageDialog( new JFrame(), "Device may be 0 or 1", "Error", JOptionPane.ERROR_MESSAGE );
				error = true;
			}
			if ( ! error )
			{
 	            Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     			Double dx = new Double ( pp.getX());
     			Double dy = new Double ( pp.getY());                                                                  	
            	UtilsCounters.PrintOptions ( "Options " + aa, device, dx.intValue(), dy.intValue() );
			}
        }
    } );
    return Utils.create2LinePane( "Draw Counters:", exit_action(), radioButtons );
   }
/************************************************************************/
//
// receive the filter/graphs definitions
//
// for filters : all - all the modules with their counters ARE NOT drawn as histograms
//               module, all - all the counters of the specified module ARE NOT drawn as histograms
//               module, specify single or range counters NOT to be drawn
// for graphs : there is NO all option; the specified counters are drawn as graphs
//
/************************************************************************/
    private boolean setOtherFilters ( String t, Vector<Counter> filter, JFrame frame, String all )
    {

     boolean error = false;
     int module = -1;
     Counter ff;
     String s;
     String title = "";
     String modulesg [] = { "tx", "rx", "utopia", "txfifo", "rxfifo", "sys_fpga", "clear" };
     String modules [] = { "all", "tx", "rx", "utopia", "txfifo", "rxfifo", "sys_fpga", "clear" };
     if ( all == "" ) s = dialog ( t, modulesg, all ); // options for graphs
                  else s = dialog ( t, modules, all ); // options for filters
     if ( ( s != null ) && ( s.length() > 0 ) )
     {
        if ( s.equals ( "all" ) )  // filter all the modules
        {
            ff = new Counter ( -1, -1 ) ;
            filter.add( ff );
        }
        else
        {
            if ( s.equals ( "clear" ) )
            {
                filter.clear() ;
            }
            else
            {
                // ask for filters for a specified module
                if ( s.equals ( "tx" ) )
                {
                    module = my_java.TX;
                    title = "tx filter:\n";
                }
                if ( s.equals ( "rx" ) )
                {
                    module = my_java.RX;
                    title = "rx filter:\n";
                }
                if ( s.equals ( "utopia" ) )
                {
                    module = my_java.UTOPIA;
                    title = "utopia filter:\n";
                }
                if ( s.equals ( "txfifo" ) )
                {
                    module = my_java.TXFIFO;
                    title = "txfifo filter:\n";
                }
                if ( s.equals ( "rxfifo" ) )
                {
                    module = my_java.RXFIFO;
                    title = "rxfifo filter:\n";
                }
                if ( s.equals ( "sys_fpga" ) )
                {
                    module = my_java.FPGA;
                    title = "sys_fpga filter:\n";
                }
			    CounterOptions co = new CounterOptions ( module, title, filter, all );
		    } // else , not all
        }
	}
    return error;
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
            System.out.println( "DrawCounters : " + str ) ;
        }
    }
}  // class end
