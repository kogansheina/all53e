package all53e;
import java.awt.*;
import java.awt.event.*;
import java.awt.FileDialog.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.* ;
import javax.swing.border.Border;
import java.beans.*;
/************************************************************************/
//
// class to define the options for a counter session
//
/************************************************************************/
class Options extends JFrame implements ItemListener
{
	static final long serialVersionUID = 109L;
	private JCheckBox cb [ ];
    private FileDialog selectfile;
    private int optionNumber ;
    private int device = 0;

/************************************************************************/
//
// construct check boxes for the option class
//
/************************************************************************/
	public Options ( int device, int options )
	{
		super ( "Options" );
		boolean error = false;
        optionNumber = options;
        this.device = device;
		if ( MyConstantsCounters.dDB[device].countersParametrs == null )
		{
            MyConstantsCounters.dDB[device].countersParametrs = new drawParameters ( device ) ;
		}
		cb = new JCheckBox [ optionNumber ];
		selectfile = new FileDialog ( this );
        JPanel pn = new JPanel ( new GridLayout( optionNumber, 1 ), false );
        cb [ 0 ] = createCheckBox( "report", false );
        cb [ 0 ].setToolTipText("A report file will be generated");
        pn.add( cb [ 0 ] );
        cb [ 1 ] = createCheckBox( "save", false );
        cb [ 1 ].setToolTipText("A binary file will be generated; it may be loaded later");
        pn.add( cb [ 1 ] );
        if ( optionNumber > 3 )
        {
            cb [ 2 ] = createCheckBox( "screen size", false );
            pn.add( cb [ 2 ] );
            cb [ 3 ] = createCheckBox( "all in one", false );
            cb [ 3 ].setToolTipText("All chosen counters are displayed\n into the same window");
            pn.add( cb [ 3 ] );
            cb [ 4 ] = createCheckBox( "read time", false );
            cb [ 4 ].setToolTipText("Set read counters frequency - in seconds");
            pn.add( cb [ 4 ] );
            cb [ 5 ] = createCheckBox( "print time", false );
            cb [ 5 ].setToolTipText("Set display counters frequency - in seconds");
            pn.add( cb [ 5 ] );
            cb [ 6 ] = createCheckBox( "fix form", false );
            cb [ 6 ].setToolTipText("All the counters of the chosen device\n are displayed into the same window");
            pn.add( cb [ 6 ] );
            cb [ 7 ] = createCheckBox( "store form", false );
            cb [ 7 ].setToolTipText("All the counters of the chosen device\n are stored ( and not displayed ) into the save file, from this moment");
            pn.add( cb [ 7 ] );
            cb [ 8 ] = createCheckBox( "stop form", false );
            cb [ 8 ].setToolTipText("The store option is disabled from this moment");
            pn.add( cb [ 8 ] );
        }
        cb [ optionNumber - 1 ] = createCheckBox( "OK", false );
        cb [ optionNumber - 1 ].setToolTipText("Take all the options");
        pn.add( cb [ optionNumber - 1 ] );
        getContentPane().add ( pn );
        setSize ( 200,240 );
        setVisible ( false );
	}
	private String dialog ( String s, String [] options , String defaultoption )
	{
	    String sret = (String)JOptionPane.showInputDialog( new JFrame(), s, s,
	                        JOptionPane.PLAIN_MESSAGE, null, options, defaultoption );
	    return sret;
	}

/************************************************************************/
//
// choose a file for an purpose
//
/************************************************************************/
    private String choose_file( )
    {
    	selectfile.setVisible(true);
        return ( selectfile.getDirectory() + selectfile.getFile() );

    } // choose file
/************************************************************************/
//
//  makes the dialog to change the screen size
//
/************************************************************************/
    private void update_screen ()
    {
		String s1 = ( String )JOptionPane.showInputDialog( this, "Screen size:\n", "Screen size:\n",
                        JOptionPane.PLAIN_MESSAGE, null, null, MyConstantsCounters.screen_default );
        if ( ( s1 != null ) && ( s1.length() > 0 ) )
        {
            StringTokenizer st = new StringTokenizer ( s1, ",", false ) ;
            s1 = st.nextToken().trim() ;
            if ( UtilsCounters.stringIsNumeric ( s1 ) )
            {
               int screen_x = Integer.parseInt( s1 ) ;
               if ( screen_x == 0 ) screen_x = MyConstants.Screen_x ;
               s1 = st.nextToken().trim() ;
               int screen_y = Integer.parseInt( s1 ) ;
               if ( screen_y == 0 ) screen_y = MyConstants.Screen_y ;
               MyConstantsCounters.dDB[device].countersParametrs.max_x = screen_x;
               MyConstantsCounters.dDB[device].countersParametrs.max_y = screen_y;
            }
        } // if string
        else
        {
            MyConstantsCounters.dDB[device].countersParametrs.draw_error = true;
            // give warning window !!!!
            JOptionPane.showMessageDialog( this, "Screen size must be numeric", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
/************************************************************************/
//
//  makes the dialog to change the read/print timeout
//
/************************************************************************/
    private int updateTime ()
    {
        int tt = 0;
        Integer j = new Integer ( MyConstantsCounters.readTimeout );
		String s1 = ( String )JOptionPane.showInputDialog( this, "Timeout:\n", "Timeout:\n",
                        JOptionPane.PLAIN_MESSAGE, null, null, j.toString() );
        if ( ( s1 != null ) && ( s1.length() > 0 ) )
        {
            if ( UtilsCounters.stringIsNumeric ( s1 ) )
            {
               tt = Integer.parseInt( s1 ) ;
            }
        } // if string
        else
        {
            tt = MyConstantsCounters.readTimeout;
        }
        return tt;
    }
/************************************************************************/
//
//  creates the option check box
//
/************************************************************************/
    private JCheckBox createCheckBox( String s, boolean b )
    {
        JCheckBox cb = new JCheckBox( s, b );
        cb.setHorizontalAlignment( JCheckBox.LEFT );
        cb.addItemListener( this );

        return cb;
    }
/************************************************************************/
//
//  listen to chnage in check box
//
/************************************************************************/
public void itemStateChanged( ItemEvent e )
{
  JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
  if ( obj != null )
  {
      for ( int k = 0; k < cb.length; k ++ )
      {
         if ( obj.equals ( cb [ k ] ) )
         {
             if ( e.getStateChange() == e.SELECTED )
             {
                 if ( k == optionNumber - 1)
                 {
                     if ( MyConstantsCounters.dDB[device].allIn )
                     {
                         MyConstantsCounters.dDB[device].countersParametrs.all_in_one = true;
                     }
                     MyConstantsCounters.dDB[device].countersParametrs.defined = true;
                     setVisible ( false );
                 }
                 else
                 {
                    switch ( k )
                    {
                    case 0:
                    
                        MyConstantsCounters.dDB[device].countersParametrs.report = true;
                        String str = choose_file();
                        if ( str.endsWith( "null" ) )
                        {
                            MyConstantsCounters.dDB[device].countersParametrs.report_file = "";
                        }
                        else
                        {
                            MyConstantsCounters.dDB[device].countersParametrs.report_file = str;
                        }
                        break;
                    
                    case 1:
                    
                        MyConstantsCounters.dDB[device].countersParametrs.save = true;
                        str = choose_file();
                        if ( str.endsWith( "null" ) )
                        {
                            MyConstantsCounters.dDB[device].countersParametrs.save_file = "";
                        }
                        else
                        {
                            MyConstantsCounters.dDB[device].countersParametrs.save_file = str;
                        }
                        break;
                    
                    case 2:
                    
                        update_screen ();
                        break;
                    
                    case 3:
                    
                         MyConstantsCounters.dDB[device].countersParametrs.all_in_one = true;
                         break;
                    
                    case 4:
                    
                         MyConstantsCounters.dDB[device].countersParametrs.readTime = updateTime();
                         break;
                    
                    case 5:
                    
                         MyConstantsCounters.dDB[device].countersParametrs.printTime = updateTime();
                         break;
                    
                    case 6:
                    
                         MyConstantsCounters.dDB[device].countersParametrs.all_in_one = true;
                         MyConstantsCounters.dDB[device].countersParametrs.fixForm = true;
                         cb [ 3 ].setSelected( true );
                         break;
                    
                    case 7:
                    
                         MyConstantsCounters.dDB[device].countersParametrs.storeForm = true;
                         break;

                    case 8:
                         MyConstantsCounters.dDB[device].countersParametrs.stopForm = true;
                         break;
                    }
                 }
             }
             else
             {
                 switch ( k )
                 {
                 case 0:

                     MyConstantsCounters.dDB[device].countersParametrs.report = false;
                     MyConstantsCounters.dDB[device].countersParametrs.report_file = "";
                     break;

                 case 1:

                     MyConstantsCounters.dDB[device].countersParametrs.save = false;
                     MyConstantsCounters.dDB[device].countersParametrs.save_file = "";
                     break;

                 case 2:

                     MyConstantsCounters.dDB[device].countersParametrs.max_x = MyConstants.Screen_x;
                     MyConstantsCounters.dDB[device].countersParametrs.max_y = MyConstants.Screen_y;
                     break;

                 case 3:

                      MyConstantsCounters.dDB[device].countersParametrs.all_in_one = false;
                      MyConstantsCounters.dDB[device].countersParametrs.fixForm = false;
                      cb [ 6 ].setSelected( false );
                      break;

                 case 4:

                      MyConstantsCounters.dDB[device].countersParametrs.readTime = MyConstantsCounters.readTimeout;
                      break;

                 case 5:

                      MyConstantsCounters.dDB[device].countersParametrs.printTime = MyConstantsCounters.printTimeout;
                      break;

                 case 6:

                      MyConstantsCounters.dDB[device].countersParametrs.fixForm = false;
                      break;

                 case 7:
                      MyConstantsCounters.dDB[device].countersParametrs.storeForm = false;
                      break;

                 }
             }
         }
      }
  }
}
/************************************************************************/
//
//  make the option frame visible
//
/************************************************************************/
   public void makeItVisible ( int x, int y )
   {
       if ( MyConstantsCounters.dDB[device].countersParametrs == null )
       {
           MyConstantsCounters.dDB[device].countersParametrs = new drawParameters ( device ) ;
       }
	   setLocation ( x, y );
	   setVisible ( true );
   }
/************************************************************************/
//
//  re-set the check box optionse/unvisible
//
/************************************************************************/
   public void clear ( )
   {
       for ( int k = 0; k < cb.length; k ++ )
       {
           if ( cb [ k ] != null )
            cb [ k ].setSelected( false );
       }
   }
}