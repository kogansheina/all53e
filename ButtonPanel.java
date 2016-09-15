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
//  Define the main object to manage buttons' panels
//  Each button has a name and a set of functions
//  The buttons may be stored in a file, loaded from the file and printed
//  The buttons may be deleted, renamed and modified
//  The functions associated with are of two kinds:
//      - keywords with well defined purpose
//      - any general command which may be understood by a pack
/************************************************************************/

//
//  ButtonPanel
//      --> DefineButton ( buttons vector )
//            -- call addButton ( new userButton ( name, DefineButton parent )
//      --> DefineButton
//      --> DefineButton
//  ..............................
//
class ButtonPanel extends JPanel
{
	static final long serialVersionUID = 101L;
    private JPanel mine;
    private JFrame myframe;
    private tester parent;
    private String path ;
    private String upath ;
    private String kpath ;
   
    public DefineButton db_selftests = null;
    public DefineButton db_swtests = null;
    public DefineButton db_hwtests = null;
    public DefineButton db_datatests = null;
    public DefineButton db_usertests = null;
    public DefineButton db_pkgtests = null;

    public ButtonPanel( JFrame frame, tester all, String buttonspath, String userpath, String pkgpath )
    {
       super ();
       myframe = frame;
       parent = all;   
       path = buttonspath;
       upath = userpath;
       kpath = pkgpath;
       MyConstants.buttonDictionary = createButtonDictionary( MyConstants.bDlength );
       mine = createButtonPanelBox();
    }
    private JPanel createButtonPanelBox ()
    {
       final int numButtons = 8;
       final String selftests = "selftest";
       final String swtests = "swtest";
       final String hwtests = "hwtest";
       final String normal = "normal";
       final String usert = "usert";
       final String pkg = "pkg";
       final String icons = "icons";
       final String addicons = "addicons";
       JRadioButton[] radioButtons = new JRadioButton[ numButtons ];
       final ButtonGroup group = new ButtonGroup();

       radioButtons[ 0 ] = new JRadioButton( "aux tests" );
       radioButtons[ 0 ].setActionCommand( selftests );
       radioButtons[ 0 ].setToolTipText("Auxiliary tests panel( administrator depend )");
       radioButtons[ 1 ] = new JRadioButton( "sw tests" );
       radioButtons[ 1 ].setActionCommand( swtests );
       radioButtons[ 1 ].setToolTipText("SoftWare tests panel( administrator depend )");
       radioButtons[ 2 ] = new JRadioButton( "hw tests" );
       radioButtons[ 2 ].setActionCommand( hwtests );
       radioButtons[ 2 ].setToolTipText("HardWare tests panel( administrator depend )");
       radioButtons[ 3 ] = new JRadioButton( "traffic tests" );
       radioButtons[ 3 ].setActionCommand( normal );
       radioButtons[ 3 ].setToolTipText("Activate traffic tests panel( administrator depend )");
       radioButtons[ 4 ] = new JRadioButton( "user tests" );
       radioButtons[ 4 ].setActionCommand( usert );
       radioButtons[ 4 ].setToolTipText("User tests - anybody read/write");
       radioButtons[ 5 ] = new JRadioButton( "package tests" );
       radioButtons[ 5 ].setActionCommand( pkg );
       radioButtons[ 5 ].setToolTipText("Package approval tests - anybody read/write");
       radioButtons[ 6 ] = new JRadioButton( "define icons" );
       radioButtons[ 6 ].setActionCommand( icons );
       radioButtons[ 6 ].setToolTipText("Define Icons panel ( from a pre-stored file or one-by-one )");
       radioButtons[ 7 ] = new JRadioButton( "add icons" );
       radioButtons[ 7 ].setActionCommand( addicons );
       radioButtons[ 7 ].setToolTipText("Add icons to the existing Icon panel");

       for (int i = 0; i < numButtons; i++ )
       {
           group.add(radioButtons[ i ] );
       }
       // create a DefineButton object for each panel type
       // read the corespondent file, creates the panel
       // the 'user test' panel is created, no matter if it contains something
       // the other panel are created, but they are not made visible
       
       db_usertests = new DefineButton ( upath, "User tests", MyConstants.usertest ) ;
       if ( db_usertests != null ) db_usertests.ReadCreateButtons ( true, MyConstants.setPanel, MyConstants.buttonfile5,  MyConstants.buttonfile5save ) ;
       db_datatests = new DefineButton ( path, "Traffic tests", MyConstants.datatest ) ;
       if ( db_datatests != null ) db_datatests.ReadCreateButtons ( true, MyConstants.not_setPanel, MyConstants.buttonfile4,  MyConstants.buttonfile4save ) ;
       db_selftests = new DefineButton ( path, "Auxilary tests", MyConstants.selftest ) ;
       if ( db_selftests != null ) db_selftests.ReadCreateButtons ( true, MyConstants.not_setPanel, MyConstants.buttonfile1,  MyConstants.buttonfile1save ) ;
       db_swtests = new DefineButton ( path, "SW tests", MyConstants.swtest ) ;
       if ( db_swtests != null ) db_swtests.ReadCreateButtons ( true, MyConstants.not_setPanel, MyConstants.buttonfile2,  MyConstants.buttonfile2save ) ;
       db_hwtests = new DefineButton ( path, "HW tests", MyConstants.hwtest ) ;
       if ( db_hwtests != null ) db_hwtests.ReadCreateButtons ( true, MyConstants.not_setPanel, MyConstants.buttonfile3,  MyConstants.buttonfile3save ) ;
       db_pkgtests = new DefineButton ( kpath, "Package tests", MyConstants.pkgtest ) ;
       if ( db_pkgtests != null ) db_pkgtests.ReadCreateButtons ( true, MyConstants.not_setPanel, MyConstants.buttonfile6,  MyConstants.buttonfile6save ) ;
       // connect a button to every panel type
       // every activation of the panel from the button reads again its file
       // before reading the file, the panel is cleared - vector and buttons
     radioButtons[ 6 ].addActionListener( new ActionListener()
   	 {
       public void actionPerformed( ActionEvent e )
       {
	       if ( MyConstants.BI == null )
	       {
	        	MyConstants.BI = new ButtonIcons ();
           }
           else
           {
	           MyConstants.BI.setVisible();
               MyConstants.BI.askForDefinition(); 
           }  
       }
    } ); // listener add   
     radioButtons[ 7 ].addActionListener( new ActionListener()
   	 {
       public void actionPerformed( ActionEvent e )
       {
	       if ( MyConstants.BI != null )
	       {
	           MyConstants.BI.AddIcons();
           }  
       }
    } ); // listener add   

   /*************************************************/
   //          button 0   - aux tests              //
   /*************************************************/
   radioButtons[ 0 ].addActionListener( new ActionListener()
   {
       public void actionPerformed( ActionEvent e )
       {
           if ( db_selftests != null ) 
           {
           		if ( db_selftests.buttons.size() == 0 ) 
           		{
               		db_selftests.ReadCreateButtons ( false, MyConstants.setPanel, MyConstants.buttonfile1,  MyConstants.buttonfile1save ) ;
           		}
           		else
           		{
	           		db_selftests.buttonPanel.repaint();
	           		db_selftests.buttonPanel.setVisible ( true );
           		}
           }
       } // action
   } ); // listener add
    /*************************************************/
    //          button 1   - sw  tests               //
    /*************************************************/
    radioButtons[ 1 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            if ( db_swtests != null ) 
            {
            	if ( db_swtests.buttons.size() == 0 ) 
            	{
                	db_swtests.ReadCreateButtons ( false, MyConstants.setPanel, MyConstants.buttonfile2,  MyConstants.buttonfile2save ) ;
            	}
            	else
            	{
	            	db_swtests.buttonPanel.repaint();
	            	db_swtests.buttonPanel.setVisible ( true );
            	}
        	}
        } // action
    } ); // listener add
    /*************************************************/
    //          button 2   - hw  tests               //
    /*************************************************/
    radioButtons[ 2 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            if ( db_hwtests != null ) 
            {
	            if ( db_hwtests.buttons.size() == 0 )
            	{
                	db_hwtests.ReadCreateButtons ( false, MyConstants.setPanel, MyConstants.buttonfile3,  MyConstants.buttonfile3save ) ;
            	}
            	else
            	{
	            	db_hwtests.buttonPanel.repaint();
	            	db_hwtests.buttonPanel.setVisible ( true );
            	}
        	}
        } // action
    } ); // listener add
    /*************************************************/
    //          button 3   - traffic  tests           //
    /*************************************************/
    radioButtons[ 3 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            if ( db_datatests != null ) 
            {
	            if ( db_datatests.buttons.size() == 0 )
            	{
                	db_datatests.ReadCreateButtons ( false, MyConstants.setPanel, MyConstants.buttonfile4,  MyConstants.buttonfile4save ) ;
            	}
            	else
            	{
	            	db_datatests.buttonPanel.repaint();
	            	db_datatests.buttonPanel.setVisible ( true );
            	}
        	}
        } // action
    } ); // listener add
    /*************************************************/
    //          button    - user  tests              //
    /*************************************************/

    radioButtons[ 4 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            if ( db_usertests != null ) 
            {
	            if  ( db_usertests.buttons.size() == 0 )
            	{
                	db_usertests.ReadCreateButtons ( false, MyConstants.setPanel, MyConstants.buttonfile5,  MyConstants.buttonfile5save ) ;
            	}
            	else
            	{
	            	db_usertests.buttonPanel.repaint();
	            	db_usertests.buttonPanel.setVisible ( true );
            	}
        	}
		            	
        } // action
    } ); // listener add

    /*************************************************/
    //          button    - package  tests              //
    /*************************************************/

    radioButtons[ 5 ].addActionListener( new ActionListener()
    {
        public void actionPerformed( ActionEvent e )
        {
            if ( db_pkgtests != null ) 
            {
	            if ( db_pkgtests.buttons.size() == 0 )
            	{
                	db_pkgtests.ReadCreateButtons ( false, MyConstants.setPanel, MyConstants.buttonfile6,  MyConstants.buttonfile6save ) ;
            	}
            	else
            	{
	            	db_pkgtests.buttonPanel.repaint();
	            	db_pkgtests.buttonPanel.setVisible ( true );
            	}
        	}
        } // action
    } ); // listener add
    return Utils.create2LinePane( "User's buttons" + ":", exit_action(), radioButtons );
    }

    public JPanel GetDefineButtonBox ()
    {
        return mine;
    }
/************************************************************************/
//
//  Creates the buttons' functions keywords dictionary
//
/************************************************************************/
    private Hashtable<String, Integer> createButtonDictionary ( int length )
    {
        Hashtable<String, Integer> h = new Hashtable<String, Integer>( length );
        for ( int k = 1; k < length - 1; k ++ )
        {
            h.put ( MyConstants.buttonFunctions [ k ], new Integer ( k ) );
        }
        return h;
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
//  Returns the 'DefineButton' object which has the parameter title
//
/************************************************************************/

   public DefineButton i_am ( String name )
   {
      DefineButton rr = null ;

      if ( db_selftests != null )
      {
          if ( db_selftests.i_am ( name ) )
          {
              rr = db_selftests ;
          }
      }
      if ( ( db_swtests != null ) && ( rr == null ) )
      {
          if ( db_swtests.i_am ( name ) )
          {
              rr = db_swtests ;
          }
      }
      if ( ( db_hwtests != null ) && ( rr == null ) )
      {
          if ( db_hwtests.i_am ( name ) )
          {
              rr = db_hwtests ;
          }
      }
      if ( ( db_datatests != null ) && ( rr == null ) )
      {
          if ( db_datatests.i_am ( name ) )
          {
              rr = db_datatests ;
          }
      }
      if ( ( db_usertests != null ) && ( rr == null ) )
      {
          if ( db_usertests.i_am ( name ) )
          {
              rr = db_usertests ;
          }
      }
      if ( ( db_pkgtests != null ) && ( rr == null ) )
      {
          if ( db_pkgtests.i_am ( name ) )
          {
              rr = db_pkgtests ;
          }
      }
      return ( rr ) ;
   }
}  // class end
