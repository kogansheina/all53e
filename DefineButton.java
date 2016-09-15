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
import javax.swing.ImageIcon;

/************************************************************************/
//
//  Define user's buttons
//  Each button has a name and a set of functions
//  The buttons may be stored to a file, loaded from the file and printed
//  The buttons may be deleted
//  The functions associated with are of two kinds:
//      - keywords with well defined purpose
//      - any general command which may be understood by a pack
/*   each functions terminates with ';'
     loop/for/inc functions -after the last function ( terminated, of course with ; )
     has '@' to indicate the end of the block
     'if' function has before the TRUE block ( after address ) ';'
     the TRUE block terminates with '?' after its last function ( which obviously termiates with ';'
     the FALSE block terminates with '@' after its last function ( which obviously termiates with ';'
*/

//      --> userButton ( name, DefineButton parent )
//   on create function AND on read panel's buttons
//      --> Modify ( parent ( DefineButton ), userButton ) 

// to execute a button - its thread is notified ( it is most of time in wait )   

/************************************************************************/
class DefineButton 
{
    private FileDialog selectfile;
    private FileInputStream inButton ;
    private FileOutputStream outButton ;
    private FileInputStream inButtonDef ;
    private FileOutputStream outButtonDef ;
    private DefineButton me;
    private String buttontitle;
    private String mypath = "";
    private String filename = "";
    private String filenamesave = "";

    public JFrame buttonPanel = null;
    public int mytype ;
    public Vector<userButton> buttons = new Vector<userButton> (0,1);
    public MouseListener MLfornewButtons = null;
    public boolean must_write_file = false;
    private ActionListener actionButtons = null;
    private ActionListener colorButtons = null;
    private ActionListener iconsButtons = null;    
    private String deleteCommand = "delete";
    private String storeCommand = "create";
    private String renameCommand = "rename";        
    private String saveCommand = "save";
    private String defaultCommand ="default";
    private String colorsCommand = "colors";
	private JRadioButtonMenuItem bc [];
	private Color last;
        
    public DefineButton( String path, String tt, int type )
    {
        super ();
        buttontitle = tt;
	    mypath = path ;
        me = this;
        mytype = type ;
        last = Color.white;
        bc = new JRadioButtonMenuItem [ MyConstants.palete ]; 
        
       MLfornewButtons = defineMouse ();
       actionButtons = new ActionListener()
       {
           public void actionPerformed( ActionEvent e )
           {
   // 'delete' option into button panel                 
             if ( e.getActionCommand() == deleteCommand )
             {
				do_deleteCommand();
			 } 
  // 'rename' option from the button panel			 
	         if ( e.getActionCommand() == renameCommand )
             {
				do_renameCommand();
			 } 
  // 'save' option from the button panel			 
	         if ( e.getActionCommand() == saveCommand )
             {
				do_saveCommand();
			 } 
		 }	       
       };
       colorButtons = new ActionListener()
       {
	     	public void actionPerformed ( ActionEvent e )
    		{
             	if ( e.getActionCommand() == defaultCommand )
             	{
	             	last = Color.white;
    				do_storeCommand();	             	
             	} 
             	if ( e.getActionCommand() == colorsCommand )
             	{	    		
	    			for ( int i = 0; i < MyConstants.palete; i ++ )
	    			{
	    				if ( e.getSource() == bc [ i ] )
	    				{		    	
		    				last = MyConstants.bcl [ i ];
		    				do_storeCommand();    				
		    				break;
	    				} 
    				}
    			}
			}
		};
       iconsButtons = new ActionListener()
       {	       
	     	public void actionPerformed ( ActionEvent e )
    		{
	    		if ( MyConstants.BI != null )
                	MyConstants.BI.setVisible(true, me);
                else JOptionPane.showMessageDialog( new JFrame(), "No icons defined", "Error", JOptionPane.ERROR_MESSAGE );	                	
    		}             	    		
		};
        for ( int i = 0; i < MyConstants.palete; i ++ )
        {
        	bc [ i ] = new JRadioButtonMenuItem( MyConstants.bclName [ i ] );
            bc [ i ].setEnabled ( true ) ;
        	bc [ i ].setBackground ( MyConstants.bcl [ i ] ) ;
            bc [ i ].addActionListener ( colorButtons ) ;
            bc [ i ].setActionCommand( colorsCommand );                    
        }				
  }  
   private void do_deleteCommand()
   {       
	    int index = -1;
        String s = dialog ( "Enter button name:\n", null, "" );
        if ( ( s == null ) || ( s.length() == 0 ) )
        {
            JOptionPane.showMessageDialog( new Frame(), "Button is supposed to have a name", "Warning", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
	        // check the requested button exists
            index = have_the_name ( s.trim(), buttons, buttons.size() ) ;
            if ( index == -1 )
            {
                // give warning window !!!!
                JOptionPane.showMessageDialog( new Frame(), "There is no button with this name", "Warning", JOptionPane.WARNING_MESSAGE );
            }
            else
            {
	            // confirm the deleteing
                int go = JOptionPane.showConfirmDialog( null, "Delete it", "Delete it", JOptionPane.YES_NO_CANCEL_OPTION );
                if ( go == 0 ) // yes
                {
                    userButton temp = ( userButton ) buttons.get ( index );
                    if ( ! temp.exec.is_executeing )
                    {
                        temp = ( userButton ) buttons.remove ( index );
                        temp.ub.setVisible ( false );
                        temp.exec.deleted = true;
                        buttonPanel.getContentPane().remove ( temp.ub );
                        must_write_file = true ;
                    }
                    else
                    {
                        JOptionPane.showMessageDialog( new JFrame(), "Button " + s + " is in execution", "Error", JOptionPane.ERROR_MESSAGE ); 
                    }
                }
             }
         }
   } // delete
   
   private void do_renameCommand()
   {
        String s = dialog ( "Enter button name:\n", null, "" );
        if ( ( s == null ) || ( s.length() == 0 ) )
        {                    
            JOptionPane.showMessageDialog( new Frame(), "Button is supposed to have a name", "Warning", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
            int index = have_the_name ( s.trim(), buttons, buttons.size() ) ;
            if ( index == -1 )
            {
               // give warning window !!!!
               JOptionPane.showMessageDialog( new Frame(), "There is no button with this name", "Warning", JOptionPane.WARNING_MESSAGE );
            }
	        else
	        { 
               String ss = dialog ( "Enter new button name:\n", null, "" );
               if ( ( ss == null ) || ( ss.length() == 0 ) )
               {
                  JOptionPane.showMessageDialog( new Frame(), "Button is supposed to have a name", "Warning", JOptionPane.WARNING_MESSAGE );
               }
               else
               {
                   if ( have_the_name ( ss.trim(), buttons, buttons.size() ) != -1 )
                  {
                        // give warning window !!!!
                        JOptionPane.showMessageDialog( new Frame(), "There is already a button with this name", "Warning", JOptionPane.WARNING_MESSAGE );
                  }
                  else
                  {
                       userButton temp = ( userButton ) buttons.remove ( index );
                       temp.button_name = ss.trim();
                       buttons.insertElementAt ( temp,index );
                       temp.ub.setText ( temp.button_name ) ;
                       must_write_file = true ;
                  }
              } // rename action
          }
      }
   }
 
   private void do_storeCommand()
   {      
       String sf = dialog ( "Enter button name:\n", null, "" );
       if ( ( sf == null ) || ( sf.length() == 0 ) )
       {
            JOptionPane.showMessageDialog( new Frame(), "Button is supposed to have a name", "Warning", JOptionPane.WARNING_MESSAGE );
       }
       else
       {
	        // check there is not another button into the same panel with the same name
            if ( have_the_name ( sf.trim(), buttons, buttons.size() ) != -1 )
            {
                  // give warning window !!!!
                  JOptionPane.showMessageDialog( new Frame(), "There is already a button with this name", "Warning", JOptionPane.WARNING_MESSAGE );
            }
            else
            {
	            if ( last == Color.white ) 
	            {
                	addButton ( new userButton ( sf.trim(), me ) );
            	}
            	else
            	{   
	            	addButton ( new userButton ( sf.trim(), me, last ) );
            	}
                must_write_file = true ;
            }
        }
	}  
	
   private void do_saveCommand()
   {
       WriteButtons( );
   }
  // mouse listener : left = execution; right = display / modify 
  public MouseListener defineMouse ()
  {
	  MouseListener ml = new MouseListener()
      {
           public void mouseClicked( MouseEvent event )
           {
               userButton temp = null;
               // search for the pressed button definition and execute it
               JButton source = ( JButton )event.getSource ();
               int k = 0;
               for ( ; k < buttons.size(); k++ )
               {
                   temp = ( userButton ) buttons.get ( k );
                   if ( temp.ub == source )
                   {
                       break;
                   }
               }
               // left click ==> execute the button
               if ( SwingUtilities.isLeftMouseButton ( event ) )
               {
                   // notify the button thread - it will wake it up
	                synchronized ( temp.exec )
	                {
		                temp.exec.Set ( null );
                    	temp.exec.notify ( );
                	}
               }
               // right click ==> modify the button
               if ( SwingUtilities.isRightMouseButton ( event ) )              
               {
                   ModifyButton modify = new ModifyButton ( me, temp );
               }
           } // mouse clicked
           public void mouseReleased( MouseEvent event ){ }
           public void mousePressed( MouseEvent event ){ }
           public void mouseEntered( MouseEvent event ){ }
           public void mouseExited( MouseEvent event ){ }
     }; // definition
       
       return ml;
   }

/************************************************************************/
//
//  write the buttons definition to the external file(s)
//          in case of add/delete/rename button
//  the permission is checked for non user buttons - if administrator
//  may write all files; otherwise only in case the path is not the standard one
//
/************************************************************************/
    public void WriteButtons( )
    {
        outButton = null;
        outButtonDef = null;
        int k, t = 0;

        if ( must_write_file )
        {
    	    try
    	    {
    	        outButton = new FileOutputStream ( mypath + filename ) ;
    	        outButtonDef = new FileOutputStream ( mypath + filename + ".def" ) ;
    	    }
    	    catch ( IOException ex )
    	    {
    	      System.out.println ( "error in open writting button file " + mypath + filename  );
    	    }
    	    if ( outButton != null )
    	    {
                boolean addtof = false;
                String f = "";
                // loop on all buttons of the panel
    	    	for ( k =0; k < buttons.size(); k ++ )
    	    	{
    	    		userButton temp = ( userButton )buttons.get ( k ) ;
    	    		try
    	    		{
	    	    		// write the name length and name
                        outButton.write( ( byte )temp.button_name.length() );                      
    	    			outButton.write( temp.button_name.getBytes() );
    	    			outButtonDef.write( ( byte )temp.button_name.length() );
    	    			outButtonDef.write( temp.button_name.getBytes() );
    	    			byte tempint [] = new byte [ 4 ];
    	    			tempint [ 0 ] = ( byte )( temp.buttonColor & 0xFF ) ;
    	    			tempint [ 1 ] = ( byte )( ( temp.buttonColor >> 8 ) & 0xFF ) ;
    	    			tempint [ 2 ] = ( byte )( ( temp.buttonColor >> 16 ) & 0xFF ) ;
    	    			tempint [ 3 ] = ( byte )( ( temp.buttonColor >> 24 ) & 0xFF ) ;
    	    			outButtonDef.write( tempint );
    	    			if ( temp.buttonIcon > -1 )
    	    			{
    	    				outButtonDef.write( ( byte )MyConstants.BI.filenames [ temp.buttonIcon ].length() );
    	    				outButtonDef.write( MyConstants.BI.filenames [ temp.buttonIcon ].getBytes() );
	    			    }
	    			    else
	    			    {
		    			    outButtonDef.write( 0 );
	    			    }
    	    			Vector<blockButton> vs = temp.vs;
    	    			// looop on all the button's functions
                        for ( t = 0; t < vs.size(); t ++ )
						{
                            blockButton blk = ( blockButton )vs.get ( t );                          
                            int type = blk.blocktype;
                            switch ( type )
                            {
                            case MyConstants.interceptType:
                            case MyConstants.simpleType:
                            case MyConstants.exprType:
                                f = blk.command + ";";
                                break;
                                
                            case MyConstants.loopType:
                            case MyConstants.incType:
                            case MyConstants.forType:
                                f = blk.command ;
                                String tempf = makeFunctionFromBlock ( blk.vs );
                                if ( ! tempf.endsWith(";") ) tempf = tempf + ";";
                                f = f + tempf + "@;";
                                break;
                                
                            case MyConstants.ifsType:                                
                            case MyConstants.ifType:
                                f = blk.command + "; ";
                                f =f + makeFunctionFromBlock ( blk.vs );
                                // the 'if', 'sif' functions contains
                                // the 'no' block also
                                addtof = true;
                                break;
                                
                            case MyConstants.ifTypeNo:
                                f = f + "?;" + makeFunctionFromBlock ( blk.vs );
                                if ( ! f.endsWith(";") ) f = f + ";";
                                f = f + "@;";
                                addtof = false;
                                break;
                            }
                            if ( ! addtof )
                            {
	                            // write the function length
                                outButton.write( ( byte )f.length() );
                                outButton.write( ( byte )(f.length() >> 8 ));
                                outButton.write( f.getBytes() );
                                f = "";
                            }
						}
						// mark end of the file
						outButton.write( 0xF0 );
						outButton.write( 0xF0 );
    	    	    }
    	    	    catch ( IOException ex )
    	    		{
                       System.out.println ( "exception:" + ex );
    	    		   System.out.println ( "error in writting button file " + mypath + filename + " k=" + k + " t=" + t );
    	    		}
    	    	} // for
    	    	try
    	    	{
    	    		outButton.close ();
    	    		outButtonDef.close ();
    	    	}
    	    	catch ( IOException ex )
    	    	{
    	    	}
    	    } // if
         }
    }
/************************************************************************/
//
//  Actually creates the panel with its menu
//
/************************************************************************/
    private JPanel createDefineButtonBox()
    {
        JPanel panel = new JPanel();
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu( "Operations" );
        JMenu submenuC = new JMenu("Create");
        JMenuItem mi = new JMenuItem("default");
        mi.setActionCommand( defaultCommand );
        mi.addActionListener( colorButtons ) ;
        submenuC.add(mi);
        JMenu mm = new JMenu("color");      
        for ( int i = 0; i < MyConstants.palete; i ++ )
        {
	        mm.add( bc [ i ] ) ;
        }
        submenuC.add(mm);
        mi = new JMenuItem("icon");
        mi.addActionListener( iconsButtons ) ;        
        submenuC.add(mi);        
        menu.add ( submenuC ) ;
        mi = new JMenuItem ( "Delete") ;
        mi.setActionCommand( deleteCommand );
        mi.addActionListener( actionButtons ) ;
        menu.add ( mi ) ;
        mi = new JMenuItem ( "Rename") ;
        mi.setActionCommand( renameCommand );
        mi.addActionListener( actionButtons ) ;
        menu.add ( mi ) ;
        mi = new JMenuItem ( "Save Panel") ;
        mi.setActionCommand( saveCommand );
        mi.addActionListener( actionButtons ) ;
        menu.add ( mi ) ;
        menubar.add( menu );
        panel.add ( menubar ) ;

     return ( panel );

    } // createDefineButtonBox

/************************************************************************/
//
//  Add the buttons' panel - only if it is neccesary
//
/************************************************************************/
    private void addButtonsPanel ()
    {        
        buttonPanel = new JFrame ( buttontitle + " ( " + mypath + filename + " )" );
        buttonPanel.getContentPane().setLayout ( new GridLayout ( 0,4 )  ) ;
        switch ( mytype )
        {
        case MyConstants.selftest:
            buttonPanel.setLocation ( MyConstants.button_x, MyConstants.button_y + 3 * MyConstants.Screen_y / 5 );
            break ;
            
        case MyConstants.swtest:
            buttonPanel.setLocation ( MyConstants.button_x, MyConstants.button_y + MyConstants.Screen_y / 4 );
            break ;
            
        case MyConstants.hwtest:
            buttonPanel.setLocation ( MyConstants.button_x + 30, MyConstants.button_y + 3 * MyConstants.Screen_y / 5 );
            break ;
            
        case MyConstants.datatest:
            buttonPanel.setLocation ( MyConstants.button_x, MyConstants.button_y + MyConstants.Screen_y / 2 );
            break ;
            
        case MyConstants.usertest:
            buttonPanel.setLocation ( MyConstants.button_x, MyConstants.button_y );
            break ;

        case MyConstants.pkgtest:
            buttonPanel.setLocation ( MyConstants.button_x + 40, MyConstants.button_y );
            break;
        }
        buttonPanel.getContentPane().add ( createDefineButtonBox() ) ; 
        
        // take care of the last modifications made to the buttons/panel
        // when the panel window is forced to close
        buttonPanel.addWindowListener ( new WindowAdapter ( )
        {
        public void windowClosing ( WindowEvent e )
        {
            WriteButtons( );
        }
        public void windowDeiconified ( WindowEvent e ) {}
        public void windowIconified ( WindowEvent e ) {}
        public void windowDeactivated ( WindowEvent e ) {}
        public void windowActivated ( WindowEvent e ) {}
     });
    }
/************************************************************************/
//
//  Use the dialog to choose or enter something
//
/************************************************************************/
    private String dialog ( String s, String [] options , String defaultoption )
    {
        String sret = (String)JOptionPane.showInputDialog( new Frame(), s, s,
                        JOptionPane.PLAIN_MESSAGE, null, options, defaultoption );
        return sret;
    }
/************************************************************************/
//
//  Choose a file for a purpose
//
/************************************************************************/
    private String choose_file( String title )
    {
        selectfile = new FileDialog ( new Frame(), title );
    	selectfile.setVisible(true);
        String str = selectfile.getDirectory() + selectfile.getFile();
        if ( str.endsWith( "null" ) ) return "";
        return ( str );

    } // choose file
/************************************************************************/
//
//  Check a range is given correctly : [ xx - yy ]
//
/************************************************************************/
    
    private String CheckRange ( String t )
    {
        int index = -1;
        String sfunction = "" ;
         
        if ( ! t.startsWith ("[") )
        {
			try
			{
				Integer.parseInt ( t );
			}
			catch ( NumberFormatException e )
			{

               JOptionPane.showMessageDialog( new Frame(), "Wrong parameter - must be integer or range", "Error", JOptionPane.ERROR_MESSAGE );
			}
			sfunction = t;
        }
        else
        {
	        if ( ! t.endsWith ("]" ) )
	        {              
		        JOptionPane.showMessageDialog( new Frame(), "Range must ends with ']'", "Error", JOptionPane.ERROR_MESSAGE );
	        }
	        else
	        {
		        StringTokenizer stc = new StringTokenizer ( t.trim(),"-",false);
		        if ( stc.countTokens() != 2 )
		        {
			       JOptionPane.showMessageDialog( new Frame(), "Range must separate the values with '-'", "Error", JOptionPane.ERROR_MESSAGE );
		        }
		        else
		        {
                    // extract and check the range limits
                     String h1 = stc.nextToken().trim();
                     h1 = h1.substring( 1, h1.length()).trim();
                     String h2 = stc.nextToken().trim();
                     h2 = h2.substring( 0, h2.length() - 1 ).trim();
					 try
					 {
						  Integer.parseInt ( h1 );
						  Integer.parseInt ( h2 );
					 }
					 catch ( NumberFormatException e )
					 {
                          JOptionPane.showMessageDialog( new Frame(), "Wrong range limits - must be numeric " + h1 + " " + h2, "Error", JOptionPane.ERROR_MESSAGE );
					 }
                     sfunction = "[" + h1 + "-" + h2 + "]";
                 }
             }
         }
         return sfunction ;
     }
/************************************************************************/
private String defineGeneral ( String s, boolean forF )
{
   String sfunction = s;
   String aa;

   String str = dialog ( "Enter command:\n", null, "" );
   if ( ( str != null ) && ( str.length() > 0 ) )
   {
       sfunction = str;
       if ( ! str.trim().startsWith( "#" ) )
       {
           if ( ! forF )
           {
               aa = dialog ( "Enter an address:\n", null, "" );
               if ( ( aa == null ) || ( aa.length() == 0 ) )
               {
                    JOptionPane.showMessageDialog( new Frame(), "An IP address is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
                    sfunction = "";
               }
               else
               {
                   sfunction = aa + " " + sfunction ;
               }
           }
       }
   }
   else
   {
       sfunction = "";
   }
   if ( ! sfunction.equals ( "" ) ) sfunction = sfunction + ";" ;
   return sfunction ;
 }
 /************************************************************************/
 private String defineButton ( String s )
 {
    String sfunction = s;

    String aa = dialog ( "Enter a button name:\n", null, "" );
    if ( ( aa == null ) || ( aa.length() == 0 ) )
    {
       JOptionPane.showMessageDialog( new Frame(), "Button name is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
       sfunction = "";
    }
    else
    {
        sfunction = s + " " + aa ;
    }
    if ( ! sfunction.equals ( "" ) ) sfunction = sfunction + ";" ;
    return sfunction ;
 }
 /************************************************************************/
 private String defineFile ( String s, boolean forF )
 {
    String sfunction = s;
    String str;
    String aa;

    str = choose_file( "Choose file" );
    if ( str.equals( "" ) )
    {
        str = " ";
    }
    if ( ! forF )
    {
        aa = dialog ( "Enter an address:\n", null, "" );
        if ( ( aa != null ) && ( aa.length() > 0 ) )
        {
            sfunction = s + " " + aa + " " + str;
        }
        else
        {
            JOptionPane.showMessageDialog( new Frame(), "An IP address is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
            sfunction = "";
        }
    }
    else
    {
        sfunction = s + " " + str;
    }
    if ( ! sfunction.equals ( "" ) ) sfunction = sfunction + ";" ;
    return sfunction ;
 }
 /************************************************************************/
 private String defineExpression( String s )
 {
    String sfunction = s;
    String t  = dialog ( "Enter variable:\n", null, "" );
    if ( t != null )
    {
        if ( t.startsWith ( "$" ) )
        {
            sfunction = s + " " + t + " = ";
            t  = dialog ( "Enter expression:\n", null, "" );
            if ( t != null )
            {
                sfunction = sfunction + t ;
            }
            else
            {
                JOptionPane.showMessageDialog( new JFrame(), "Expression is null" , "Error", JOptionPane.ERROR_MESSAGE ); 
                sfunction = "";
            }
        }
        else
        {
            JOptionPane.showMessageDialog( new JFrame(), "Variable must begin with '$' " , "Error", JOptionPane.ERROR_MESSAGE ); 
            sfunction = "";
        }
    }
    else
    {
        sfunction = "";
    }
    if ( ! sfunction.equals ( "" ) ) sfunction = sfunction + ";" ;
    return sfunction ;
 }
 /************************************************************************/
 private String defineWait( String s )
 {
    String sfunction = s;

    String t  = dialog ( "Enter time:\n", null, "" );
    if ( t != null )
    {
        t = CheckRange ( t.trim() ) ;
        if ( ! t.equals("") )
        {
            sfunction = s + " " + t;
        }
        else
        {
            sfunction = "" ;
        }
    }
    else
    {
        sfunction = "";
    }
    if ( ! sfunction.equals ( "" ) ) sfunction = sfunction + ";" ;
    return sfunction ;
 }
 /************************************************************************/
 private String defineIntercept( String s, boolean forF )
 {
    String sfunction = s;
    String aa;

    String test = dialog ( "Enter TimeOut and text to catch\n", null, "" );
    if ( ( test == null ) || ( test.length() == 0 ) )
    {
        JOptionPane.showMessageDialog( new Frame(), "A text is mandatory and it has to be quoated", "Error", JOptionPane.ERROR_MESSAGE );
        sfunction = "";
    }
    else
    {
        int l1 = test.indexOf ( '"' ) ;
        int l2 = -1;
        if ( l1 != -1 )
        {
            l2 = test.indexOf ( '"', l1 + 1 ) ;
        }
        if ( ( l1 < 0 ) || ( l2 < 0 ) )
        {
            JOptionPane.showMessageDialog( new Frame(), "A text is mandatory and it has to be quoated", "Error", JOptionPane.ERROR_MESSAGE );
            sfunction = "";
        }
        String t = test.substring( 0, l1 ) ;
        t = CheckRange ( t.trim() );
        if ( t.equals ( "" ) )
        {
            sfunction = "" ;
        }
        if ( ! sfunction.equals("") )
        {                        
            test = t + " " + test.substring ( l1, l2 + 1 ) + " " ;
            if ( ! forF )
            {
                aa = dialog ( "Enter an address:\n", null, "" );
                if ( ( aa == null ) || ( aa.length() == 0 ) )
                {
                    JOptionPane.showMessageDialog( new Frame(), "An IP address is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
                    sfunction = "";
                }
                else
                {
                    sfunction = sfunction + " " + test + " " + aa ;
                }
            }
            else
            {
                sfunction = sfunction + " " + test ;
            }
        }
    }
    if ( ! sfunction.equals ( "" ) ) sfunction = sfunction + ";" ;
    return sfunction ;
}
/************************************************************************/

private String defineIf( String s, boolean forF )
{
    String sfunction = s;
    String aa = "";
    String test;

    if ( ! s.startsWith("sif") )
    {
    	test = dialog ( "Enter TimeOut and text to check\n", null, "" );
    	if ( ( test == null ) || ( test.length() == 0 ) )
    	{
        	JOptionPane.showMessageDialog( new Frame(), "A check text is mandatory and it has to be quoated", "Error", JOptionPane.ERROR_MESSAGE );
        	sfunction = "";
    	}
	}
	else
	{
    	test = dialog ( "Enter condition\n", null, "" );
    	if ( ( test == null ) || ( test.length() == 0 ) )
    	{
        	JOptionPane.showMessageDialog( new Frame(), "A condition is mandatory and it has to be quoated", "Error", JOptionPane.ERROR_MESSAGE );
        	sfunction = "";
    	}		
	}
	if ( ! sfunction.equals("") )
    {
        int l1 = test.indexOf ( '"' ) ;
        int l2 = -1;
        if ( l1 != -1 )
        {
            l2 = test.indexOf ( '"', l1 + 1 ) ;
        }
        if ( ( l1 < 0 ) || ( l2 < 0 ) )
        {
            JOptionPane.showMessageDialog( new Frame(), "A check text is mandatory and it has to be quoated", "Error", JOptionPane.ERROR_MESSAGE );
            sfunction = "";
        }
        else
        {
        String t = "";
        if ( ! s.startsWith ("sif") )
        {
        	t = test.substring( 0, l1 ) ;
        	t = CheckRange ( t.trim() ) + " ";
        	if ( t.equals ( "" ) )
        	{
            	sfunction = "" ;
        	}
    	}
        if ( ! sfunction.equals("") )
        {                        
            test = t + test.substring ( l1, l2 + 1 ) + " " ;
            if ( ! forF && ! s.startsWith("sif" ) )
            {   
                aa = dialog ( "Enter an address:\n", null, "" );
                if ( ( aa == null ) || ( aa.length() == 0 ) )
                {
                    JOptionPane.showMessageDialog( new Frame(), "An IP address is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
                    sfunction = "";
                }
            }
        }
        if ( ! sfunction.equals("") )
        {                        
            String ok = "";
            String notok = "";
            String ss = "";
            while ( ss != MyConstants.terminate )
            {
                ss = dialog ( "Enter TRUE function:\n", MyConstants.buttonFunctions, "" );
                if ( ( ss != null ) && ( ss.length() > 0 ) )
                {
                    if ( ss != MyConstants.terminate )
                    {
                        if ( forF )
                        {
                            ok = ok + MakeForFunction ( ss );
                        }
                        else
                        {
                            ok = ok + MakeButtonFunction ( ss );
                        }
                    }
                }
                else
                {
                    ok = ok + ";";
                }
            }
            ss = "";
            while ( ss != MyConstants.terminate )
            {
                ss = dialog ( "Enter FALSE function:\n", MyConstants.buttonFunctions, "" );
                if ( ( ss != null ) && ( ss.length() > 0 ) )
                {
                    if ( ss != MyConstants.terminate )
                    {
                        if ( forF )
                        {
                            notok = notok + MakeForFunction ( ss );
                        }
                        else
                        {
                            notok = notok + MakeButtonFunction ( ss );
                        }
                    }
                }
                else
                {
                    notok = notok + ";";
                }
            }
            if ( forF || s.startsWith("sif" ) )
            {
                sfunction = sfunction + " " + test + ";" + ok + "?;" + notok + "@;";
            }
            else
            {
                sfunction = sfunction + " " + test + aa + ";" + ok + "?;" + notok + "@;";
            }
        }
        }
    }
    return sfunction ;
}
/************************************************************************/

private String defineLoop( String s, boolean forF )
{
    String sfunction = s;
    String aa;

    String howmany = dialog ( "Enter how many times:\n", null, "" );
    if ( ( howmany != null ) && ( howmany.length() > 0 ) )
    {
        howmany = howmany.trim() ;
        String ok = "";
        String ss = "";
        while ( ss != MyConstants.terminate )
        {
            ss = dialog ( "Enter function:\n", MyConstants.buttonFunctions, "" );
            if ( ( ss != null ) && ( ss.length() > 0 ) )
            {
                if ( ss != MyConstants.terminate )
                {
                    if ( forF )
                    {
                        ok = ok + MakeForFunction ( ss );
                    }
                    else
                    {
                        ok = ok + MakeButtonFunction ( ss );
                    }
                }
                else
                {
                    sfunction = sfunction + " " + howmany + " " + ok;
                }
            }
            else
            {
                sfunction = sfunction + " " + howmany + " " + ok;
            }
        }
        sfunction = sfunction + "@;";
    }
    else
    {
        sfunction = "";
    }
    return sfunction ;
}
/************************************************************************/

private String defineFor( String s )
{
    String sfunction = s;
    String aa;

    String range = dialog ( "Enter addresses range:\n", null, "" );
    if ( ( range != null ) && ( range.length() > 0 ) )
    {
        StringTokenizer stc = new StringTokenizer ( range, "-", false ) ;
        if ( stc.countTokens() != 2 )
        {
            JOptionPane.showMessageDialog( new Frame(), "The addresses must be separated by '-'", "Error", JOptionPane.ERROR_MESSAGE );
            sfunction = "";
        }
        else
        {
            String st = stc.nextToken().trim();
            String et = stc.nextToken().trim();
            range = st + "-" + et;
            String ok = "";
            String ss = "";
            while ( ss != MyConstants.terminate )
            {
                ss = dialog ( "Enter function:\n", MyConstants.buttonFunctions, "" );
                if ( ( ss != null ) && ( ss.length() > 0 ) )
                {
                    if ( ss != MyConstants.terminate )
                    {
                        if ( ss == "for" )
                        {
                            sfunction = "";
                            JOptionPane.showMessageDialog( new Frame(), "It cannot be used under 'for' function", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                        else
                        {
                            ok = ok + MakeForFunction ( ss );
                        }
                    }
                    else
                    {
                        sfunction = sfunction + " " + range + " " + ok;
                    }
                }
                else
                {
                    sfunction = sfunction + " " + range + " " + ok;
                }
            }
            sfunction = sfunction + "@;";
        }
    }
    else
    {
        sfunction = "";
    }
    return sfunction ;
}
/************************************************************************/

private String defineInc( String s, boolean forF )
{
    String sfunction = s;
    String aa;

    String varr = dialog ( "Enter variable:\n", null, "" );
    if ( !sfunction.equals("") )
    {
    String range = dialog ( "Enter increment limits and step:\n", null, "" );
    if ( ( range != null ) && ( range.length() > 0 ) )
    {
        StringTokenizer stc = new StringTokenizer ( range, "-", false ) ;
        if ( stc.countTokens() != 3 )
        {
            JOptionPane.showMessageDialog( new Frame(), "The parameters must be separated by '-'", "Error", JOptionPane.ERROR_MESSAGE );
            sfunction = "";
        }
        else
        {
            String st = stc.nextToken().trim();
            String et = stc.nextToken().trim();
            String it = stc.nextToken().trim();
            range = st + "-" + et + "-" + it;
            String ok = "";
            String ss = "";
            while ( ss != MyConstants.terminate )
            {
                ss = dialog ( "Enter function:\n", MyConstants.buttonFunctions, "" );
                if ( ( ss != null ) && ( ss.length() > 0 ) )
                {
                    if ( ss != MyConstants.terminate )
                    {
                        if ( forF )
                        {
                            ok = ok + MakeForFunction ( ss );
                        }
                        else
                        {
                            ok = ok + MakeButtonFunction ( ss );
                        }
                    }
                    else
                    {
                        if ( ( varr != null ) && ( varr.length() > 0 ) )
                        {
                            sfunction = sfunction + " " + varr.trim() + "=" + range + " " + ok ;
                        }
                        else
                        {
                            sfunction = sfunction + " " + range + " " + ok;
                        }
                    }
                }
                else
                {
                    if ( ( varr != null ) && ( varr.length() > 0 ) )
                    {
                        sfunction = sfunction + " " + varr.trim() + "=" + range + " " + ok ;
                    }
                    else
                    {
                        sfunction = sfunction + " " + range + " " + ok;
                    }
                }
            }
            sfunction = sfunction + "@;";
        }
    }
    else
    {
        sfunction = "";
    }
    }
    return sfunction ;
}
/************************************************************************/
//
//  Receive button's function - make a specific dialog for each of them
//
/************************************************************************/
public String MakeButtonFunction ( String s )
{
	String sfunction = s.trim();

	Integer j = ( Integer )MyConstants.buttonDictionary.get ( sfunction );
    if ( j != null )
    {
    	int ii = j.intValue ();
        switch ( ii )
        {
        case MyConstants.fileFunction: // file
                sfunction = defineFile ( sfunction, false ) ;
				break;

		case MyConstants.waitFunction: // wait
                sfunction = defineWait ( sfunction ) ;
				break;

        case MyConstants.interceptFunction: // intercept
                sfunction = defineIntercept ( sfunction, false ) ;
                break;
                
		case MyConstants.ifsFunction: // sif
		case MyConstants.ifFunction: // if
                sfunction = defineIf ( sfunction, false ) ;
				break;

        case MyConstants.exprFunction: // expr
                sfunction = defineExpression ( sfunction ) ;
                break;

		case MyConstants.loopFunction: // loop
                sfunction = defineLoop ( sfunction, false ) ;
			    break;

		case MyConstants.forFunction: // for
                sfunction = defineFor ( sfunction ) ;
			    break;

        case MyConstants.incFunction: // inc
                sfunction = defineInc ( sfunction, false ) ;
                break;

        case MyConstants.buttonWFunction: // execute button
        case MyConstants.buttonFunction: // execute button
                sfunction = defineButton ( sfunction ) ;
                break;

        case MyConstants.openFunction: // open
            String aa = dialog ( "Enter an address:\n", null, "" );
            if ( ( aa == null ) || ( aa.length() == 0 ) )
            {
                 JOptionPane.showMessageDialog( new Frame(), "Address is mandatory", "Error", JOptionPane.ERROR_MESSAGE );
                 sfunction = "";
            }
            else
            {
                sfunction = sfunction + " " + aa ;
            }
            break;

        case MyConstants.csFunction: // cs
        case MyConstants.breakFunction: // break        
            sfunction = sfunction + " ;" ;
	    	break;

        default:
            break;
		}
	}
    else
    {   // general
        sfunction = defineGeneral ( sfunction, false ) ;
    }
    
    debug (false, "def="+sfunction);
	return sfunction;
}
/************************************************************************/
//
//  Receive button's function - make a specific dialog for each of them
//
/************************************************************************/
	public String MakeForFunction ( String s )
	{
		String sfunction = s.trim();

		Integer j = ( Integer )MyConstants.buttonDictionary.get ( s );
        if ( j != null )
        {
        	int ii = j.intValue ();
            switch ( ii )
            {
            case MyConstants.fileFunction: // file
                sfunction = defineFile ( sfunction, true ) ;
				 break;

            case MyConstants.waitFunction: // wait
                sfunction = defineWait ( sfunction ) ;
                break;

            case MyConstants.buttonWFunction: // execute button
            case MyConstants.buttonFunction: // execute button
                 sfunction = defineButton ( sfunction ) ;
                 break;

            case MyConstants.incFunction: // inc
                sfunction = defineInc ( sfunction, true ) ;
                break;

            case MyConstants.loopFunction: // loop
                sfunction = defineLoop ( sfunction, true ) ;
		   	    break;

            case MyConstants.exprFunction: // expr
                 sfunction = defineExpression ( sfunction ) ;
                 break;

            case MyConstants.interceptFunction: // intercept
                sfunction = defineIntercept ( sfunction, true ) ;
                break;

			case MyConstants.ifFunction: // if
 			case MyConstants.ifsFunction: // if
               sfunction = defineIf ( sfunction, true ) ;
				break;

            case MyConstants.forFunction: // for

                sfunction = "";
                JOptionPane.showMessageDialog( new Frame(), "It cannot be used under 'for' function", "Error", JOptionPane.ERROR_MESSAGE );
				break;

            case MyConstants.csFunction: // cs
        	case MyConstants.breakFunction: // break            
            case MyConstants.openFunction: // open
            	sfunction = sfunction + " ;";
            	break;
            	
            default: 
				break;
			}
		}
        else
        {   // general
            sfunction = defineGeneral ( sfunction, true ) ;
        }
        
        debug (false, "fordef="+sfunction);
		return sfunction;
	}
/************************************************************************/
//
//  Read the button dfinition from buttons' file and re-create their data base
//
/************************************************************************/
    public void ReadCreateButtons ( boolean firstRead, boolean first, String buttonfile, String buttonfilesave )
    {
       filename = buttonfile ;
       filenamesave = buttonfilesave ;
       FileOutputStream outsave = null;
       // read the button file - a record has a fixed structure
       try
       {
           inButton = new FileInputStream ( mypath + filename ) ;
           
           if ( inButton != null )
           {
               File temp = new File ( mypath + filenamesave ) ;
               File temp1 = new File ( mypath + filename ) ;
               if ( temp.exists() )
               {
                    if ( temp1.lastModified() > temp.lastModified() )
                    {
                        outsave = new FileOutputStream ( mypath + filenamesave ) ;
                    }
               }
               else
               {
                   outsave = new FileOutputStream ( mypath + filenamesave ) ;
               }
           }
       }
       catch ( FileNotFoundException e )
       {
           System.out.println("file " + mypath + filename + " not found" );
           inButton = null;
       }
       if ( inButton != null )
       {
           try
           {
         	  int len = 0;
         	  while ( len != -1 )
         	  {
         		int llen = inButton.read ( );
                if ( outsave != null )
                {
                    outsave.write( ( byte ) llen );
                }
         		len = llen;
         		if ( ( len > 0 ) && ( len != 0xF0F0 ) )
         		{
         			byte [] str = new byte [ len ];
         			len = inButton.read ( str );
                    if ( outsave != null )
                    {
                        outsave.write( str );
                    }
         			userButton toadd = new userButton ( new String ( str ), this );
                   // read buttons' functions
         	  		while ( len != -1 )
         	  		{
         				llen = inButton.read ( );
                        len = ( inButton.read ( ) << 8 ) | llen;
                        if ( outsave != null )
                        {
                            outsave.write( ( byte )llen );
                            outsave.write( ( byte )( len >> 8 ));
                        }
         				if ( ( len > 0 ) && ( len != 0xF0F0 ) )
         				{
         					str = new byte [ len ];
         					len = inButton.read ( str );
                            if ( outsave != null )
                            {
                                outsave.write( str );
                            }
                            String ss = new String ( str );
         					toadd.addFunction ( ss.substring ( 0, ss.length() - 1 ) );
       					}
       					else break;
					}
         		    addButton ( toadd );
				}
				else 
                {
                   break;
                }
              }
         	  inButton.close();
              if ( outsave != null )
              {
                  outsave.write( 0xF0 );
                  outsave.write( 0xF0 );
                  outsave.close();
              }
           }
           catch ( IOException e )
           {
        	    System.out.println ( "error in reading button file " + mypath + filename + " or writting save file " + mypath + filenamesave );
           }
       }
       if ( firstRead )
       {
       try
       {
           inButtonDef = new FileInputStream ( mypath + filename + ".def" ) ;
           if ( inButtonDef != null )
           {
               for ( int k = 0; k < buttons.size(); k ++ )
               {
                   userButton toAdd = ( userButton ) buttons.get ( k ) ;
                   int len = inButtonDef.read();
                   if ( len != -1 )
                   {
                        byte str [] = new byte [ len ];
                        len = inButtonDef.read( str );			       			
                        if ( len != -1 )
                        {
                            String strname = new String ( str );
                            if ( strname.equals((Object)toAdd.button_name ))
                            {
                                str = new byte [ 4 ];
                                len = inButtonDef.read ( str );
                                if ( len != -1 )
                                {
                                    int y = ( str [ 3 ] << 24 ) | ( str [ 2 ] << 16 ) | ( str [ 1 ] << 8 ) | ( str [ 0 ] ) ;
                                    toAdd.setColor ( MyConstants.bcl [ y ] );
                                    len = inButtonDef.read();
                                    if ( ( len != -1 ) & ( len != 0 ) )
                                    {
                                        str = new byte [ len ];
                                        len = inButtonDef.read( str );
                                        if ( len != -1 )
                                        {
                                            String fi = new String ( str );
                                            toAdd.setIcon ( new ImageIcon ( fi ), fi );
                                        }
                                    }
                                    else
                                    {
                                        if ( len == 0 )
                                        {
                                            toAdd.retIcon();
                                        }
                                    }
                                }
                            }
                        }
                    }
               }
           }
           inButtonDef.close(); 
           if ( MyConstants.BI != null )
              MyConstants.BI.setVisible();
       }
       catch ( IOException e )
       {
            System.out.println ( "error in reading button file " + mypath + filename + " or writting save file " + mypath + filenamesave );
       }    

       }
       if ( first == MyConstants.setPanel )
       {    
           if ( buttonPanel == null ) 
           {
                addButtonsPanel();
                buttonPanel.pack();
           }
           buttonPanel.setVisible ( true );
       }
    }
/************************************************************************/
   
    private String makeFunctionFromBlock ( Vector<blockButton> vs )
    {
	    String f = "";
        for ( int t = 0; t < vs.size(); t ++ )
        {
	        blockButton blk = ( blockButton) vs.get(t);
	        if ( MyConstants.buttondebug_flag )
	        {
	        	Utils.printList(blk);
			}
	        int type = blk.blocktype;
            switch ( type )
            {
            case MyConstants.interceptType:
            case MyConstants.simpleType:
            case MyConstants.exprType:
                f = f + blk.command + ";";
                break;
                
            case MyConstants.loopType:
            case MyConstants.forType:
            case MyConstants.incType:
            	f = f + blk.command + ";";
                String tempf = makeFunctionFromBlock ( blk.vs );
                if ( ! tempf.endsWith(";") ) tempf = tempf + ";";
                f = f + tempf + "@ ;";
                break;
                
            case MyConstants.ifType:
            case MyConstants.ifsType:
            	f = f + blk.command + ";";
                f = f + makeFunctionFromBlock ( blk.vs );
                break;
                
            case MyConstants.ifTypeNo:
                f = f + "?;" + makeFunctionFromBlock ( blk.vs ) + "@ ;";
                break;

            default:
                break;
            
            }
        }
        debug (false," return f = " + f );
        return f;
    }
/************************************************************************/
//
//   add a button to the internal data base
//   connect the new button to the listener
//   repaint the panel and the new Frame()
//   mark that will be an action on the buttons' file on 'exit'
//
/************************************************************************/
public userButton addButton ( userButton currentButton )
{
      if ( buttonPanel == null ) 
      {
          addButtonsPanel();
          buttonPanel.pack();
      }
	  buttons.add( currentButton );
	  currentButton.ub.addMouseListener( MLfornewButtons );
	  if ( currentButton.getColor() == MyConstants.background )
	  {
		  setColor ( currentButton );
      }
      setDefaultColor ( currentButton );
     	 
	     currentButton.ub.setVisible ( true );
         if ( buttonPanel != null )
         {
             buttonPanel.getContentPane().add ( currentButton.ub );
             buttonPanel.pack();
             buttonPanel.repaint ( );
         }
            
	     return currentButton;
}
private void setColor ( userButton currentButton )
{

      switch ( mytype )
      {
          case MyConstants.selftest:
                currentButton.setColor ( MyConstants.cyan ) ;
                break;
                
          case MyConstants.swtest:
                currentButton.setColor ( MyConstants.green ) ;
                break;
                
          case MyConstants.hwtest:
                currentButton.setColor ( MyConstants.magenta ) ;
                break;
                
          case MyConstants.datatest:
                currentButton.setColor ( MyConstants.orange ) ;
                break;
                
          case MyConstants.usertest:
                currentButton.setColor ( MyConstants.pink ) ;
                break;

          case MyConstants.pkgtest:
                currentButton.setColor ( MyConstants.blue ) ;
                break;
         }
}
private void setDefaultColor ( userButton currentButton )
{

      switch ( mytype )
      {
          case MyConstants.selftest:
                currentButton.setDefaultColor ( MyConstants.cyan ) ;
                break;
                
          case MyConstants.swtest:
                currentButton.setDefaultColor ( MyConstants.green ) ;
                break;
                
          case MyConstants.hwtest:
                currentButton.setDefaultColor ( MyConstants.magenta ) ;
                break;
                
          case MyConstants.datatest:
                currentButton.setDefaultColor ( MyConstants.orange ) ;
                break;
                
          case MyConstants.usertest:
                currentButton.setDefaultColor ( MyConstants.pink ) ;
                break;

          case MyConstants.pkgtest:
                currentButton.setDefaultColor ( MyConstants.blue ) ;
                break;
         }
}

    public String i_am ( )
    {
        return ( buttontitle ) ;
    }

    public boolean i_am ( String name )
    {
        return ( name.equals ( buttontitle ) ) ;
    }
    
    public void  clearButtons ()
    {
        userButton temp ;

        for ( int y = 0; y < buttons.size(); y ++ )
        {
            temp = ( userButton ) buttons.get ( y );
            temp.ub.setVisible ( false );
            buttonPanel.getContentPane().remove ( temp.ub );
        }
        buttons.clear() ;
        if ( buttonPanel != null )
        {
            buttonPanel.pack();
            buttonPanel.repaint ( );
        }
    }
    
    private int have_the_name ( String name, Vector<userButton> v, int vector_length )
    {
        int ret = -1;

        for ( int t = 0; t < vector_length; t++ )
        {
            userButton temp = ( userButton ) v.get ( t );
            if ( name.equals ( temp.button_name ) )
            {
                ret = t;
                break;
            }
        }

        return ( ret );
    }
    private static void debug ( boolean doflag, String str )
    {
        if ( ( doflag ) || ( MyConstants.buttondebug_flag ) )
        {
            System.out.println( "DefineButton : " + str ) ;
        }
    }
}  // class end
