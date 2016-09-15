package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.* ;
import javax.swing.border.Border;
import java.beans.*;
class Remake
{
    public String ff;
    public Vector <Integer> endfunction;
    public int type;
    public int level;

    public Remake ()
    {
        clear();
    }
    public void clear()
    {
        type = MyConstants.noneType;
        ff = "";
        level = 0;
        endfunction = new Vector<Integer> (0,1);
        endfunction.add( new Integer ( 1 ) );
    }
}
class ModifyButton extends JFrame 
{
	static final long serialVersionUID = 105L;
    static final int Width = 16;
    static final int Heigth = 38;

	private JButton msb = new JButton( "Save & Exit" );
	private JButton mb = new JButton( "Save Button" );
	private JButton db = new JButton( "Delete" );
	private JButton eb = new JButton( "Cancel" );
	private JButton cb = new JButton( "Copy from" );
	private JButton sb = new JButton( "Statistics" );
	private JButton brk = new JButton( "Break" );
    private ActionListener ALforinsertButtons = null;
    private ActionListener ALcancelButtons = null;
    private ActionListener ALcopyButtons = null;
    private ActionListener ALbreakButtons = null;
    private ActionListener ALstatisticsButtons = null;
    private ActionListener ALdeleteButtons = null;
    private WindowAdapter WAformodifyButtons = null;
    private ActionListener colorButtons = null;
    private ActionListener iconsButtons = null;
    private Color last;       
 	private int modifyIndex = -1;
 	private DefineButton caller;
    private Remake rm;
    private Remake tr;
    private String ret;
    private ModifyButton me ;
    public userButton copy ;
    public userButton UB ;
    public MouseListener MLfornewButtons = null;
    public JTextArea content;
    private JRadioButtonMenuItem bc [] ;

 public ModifyButton ( DefineButton df, userButton uB )
 {
	 super ();
	 caller = df;
     UB = uB ;
     me = this ;
	 copy = new userButton ( uB );
     rm = null;
     ret = "";
     bc = new JRadioButtonMenuItem [MyConstants.palete] ;
     modifyIndex = have_the_name ( copy.button_name, df.buttons, df.buttons.size() );
                  
     iconsButtons = new ActionListener()      
     {	       
	     	public void actionPerformed ( ActionEvent e )
    		{
	    		if ( MyConstants.BI != null )
                	MyConstants.BI.setVisible(true, me);               	
                else JOptionPane.showMessageDialog( new JFrame(), "No icons defined", "Error", JOptionPane.ERROR_MESSAGE );
    		}             	    		
	 };		
     colorButtons = new ActionListener()
     {
	    public void actionPerformed ( ActionEvent e )
    	{
	    	if ( ! UB.exec.deleted )
            {
                if ( ! UB.exec.is_executeing )
                {
             		if ( e.getActionCommand() == "default" )
             		{
	             		last = Color.white;
    					do_storeCommand(null,null);	             	
             		}  
             		if ( e.getActionCommand() == "color" )
             		{	    		
	    				for ( int i = 0; i < MyConstants.palete; i ++ )
	    				{
	    					if ( e.getSource() == bc [ i ] )
	    					{		    	
		    					last = MyConstants.bcl [ i ];
		    					do_storeCommand(null,null);    				
		    					break;
	    					} 
    					}
    				} /* color */
				}
    			else
    			{
	                 JOptionPane.showMessageDialog( new JFrame(), "Button is in execution", "Error", JOptionPane.ERROR_MESSAGE );     				
    			}
			}
		   else
		   {
            	JOptionPane.showMessageDialog( new Frame(), "The button was deleted", "Warning", JOptionPane.WARNING_MESSAGE );			
		   }
		}
	};
    ALdeleteButtons = new ActionListener()
    {
	    public void actionPerformed ( ActionEvent e )
    	{
	    	if ( ! UB.exec.deleted )
            {
                if ( ! UB.exec.is_executeing )
                {	
            		int index = have_the_name ( copy.button_name, caller.buttons, caller.buttons.size() ) ;
            		userButton temp = ( userButton ) caller.buttons.remove ( index );
            		temp.ub.setVisible ( false );
            		temp.exec.deleted = true;
            		caller.buttonPanel.getContentPane().remove ( temp.ub );
            		caller.must_write_file = true ;
            		setVisible ( false );
				}
    			else
    			{
	                 JOptionPane.showMessageDialog( new JFrame(), "Button is in execution", "Error", JOptionPane.ERROR_MESSAGE );     				
    			}
			}
			else
			{
            	JOptionPane.showMessageDialog( new Frame(), "The button was deleted", "Warning", JOptionPane.WARNING_MESSAGE );			
			}
		}
	};
   
     MLfornewButtons = new MouseListener()
     {
         public void mouseClicked( MouseEvent event )
         {
             if  ( SwingUtilities.isRightMouseButton ( event ) )            
             { 
                String s = null;
                s = dialog ( "Enter a function:\n", MyConstants.buttonFunctions, "?" );
                if ( ( s != null ) && ( s.length() > 0 ) )
                {
                   if ( s.startsWith ( "?" ) )
                   {
                       PrintWindow helpW = new PrintWindow ( "Help", 10, 50 );
 	            	   Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     				   Double dx = new Double ( pp.getX());
     				   Double dy = new Double ( pp.getY());                      
                       helpW.createTipButtonFunctions( dx.intValue(), dy.intValue()/*MyConstants.button_wx, MyConstants.button_wy*/ );
                   }
                   else
                   {
                       int endOffset = 0;
                       int startOffset= 0;
                       try
                       {
                          int ii = content.getCaretPosition();
                          int start = content.getLineOfOffset( ii );
                          // start is the line number
                          endOffset = content.getLineEndOffset ( start );
                          startOffset = content.getLineStartOffset ( start );
                          String temp = caller.MakeButtonFunction ( s ) ;
                          userButton utemp = new userButton();
                          utemp.addFunction ( temp );
     					  ret = stringButton ( utemp, content );
                          content.replaceRange ( ret + "\n", startOffset, endOffset );
                        }
                        catch ( BadLocationException ble )
                        {
                             System.out.println( " Bad Position exception " + startOffset + " " + endOffset );
                        }
                   } // a new function added
                } // a new string is entered
             } // right mouse
         } //actionPerformed
         public void mouseReleased( MouseEvent event ){ }
         public void mousePressed( MouseEvent event ){ }
         public void mouseEntered( MouseEvent event ){ }
         public void mouseExited( MouseEvent event ){ }
     };

	 ALcancelButtons = new ActionListener()
	 {
	     public void actionPerformed( ActionEvent e )
	     {
             String s = ""; 
	         JButton b = ( JButton )e.getSource();
	         if ( ( b == eb ) && eb.isEnabled() )
	         {
                 setVisible ( false );
             }
	     }
	 };

	 ALbreakButtons = new ActionListener()
	 {
	     public void actionPerformed( ActionEvent e )
	     {
	         JButton b = ( JButton )e.getSource();
             if ( ! UB.exec.deleted )
             {
	            if ( ( b == brk ) && brk.isEnabled() )
	            {
                    UB.exec.stopButton = true;
                }
             }
             else
             {
                JOptionPane.showMessageDialog( new Frame(), "The button was deleted", "Warning", JOptionPane.WARNING_MESSAGE );
             }
	     }
	 };
	 ALcopyButtons = new ActionListener()
	 {
	     public void actionPerformed( ActionEvent e )
	     {
             String s = ""; 
	         JButton b = ( JButton )e.getSource();
             if ( ! UB.exec.deleted )
             {
	         if ( ( b == cb ) && cb.isEnabled() ) // copy
	         {
                if ( ! UB.exec.is_executeing )
                {
             	s = dialog ( "From button :\n", null, null );
                if ( ( s != null ) && ( s.length() > 0 ) )
                {
                    int index = -1;
                    int ind = s.indexOf ( '/' ) ;            

                    if ( ind == -1 )
                    {
                        index = have_the_name ( s.trim(), caller.buttons, caller.buttons.size() );
                        if ( index != -1 )
                        {
                            userButton tempButton = ( userButton ) caller.buttons.get ( index );
                            displayButton ( tempButton, content );
                        }
                        else
                        {
                            JOptionPane.showMessageDialog( new JFrame(), "Button " + s + " does not exist", "Error", JOptionPane.ERROR_MESSAGE );
                        }
                    }
	                else
                    {
                        String bpa = s.substring ( 0, ind ).trim() ;
                        s = s.substring ( ind + 1, s.length() ).trim() ;
                        DefineButton defb = MyConstants.bp.i_am ( bpa ) ;
                        if ( defb != null )
                        {
                            index = have_the_name ( s, defb.buttons, defb.buttons.size() );
                            if ( index != -1 )
                            {
                                userButton tempButton = ( userButton ) defb.buttons.get ( index );
                                displayButton ( tempButton, content );
                            }
                            else
                            {
                                JOptionPane.showMessageDialog( new JFrame(), "Button " + s + " does not exist", "Error", JOptionPane.ERROR_MESSAGE );
                            }
                        }
                        else
                        {
	                        JOptionPane.showMessageDialog( new JFrame(), "Button Panel " + bpa + " does not exist", "Error", JOptionPane.ERROR_MESSAGE ); 
                        }
                    }
				}
                }
                else
                {
                    JOptionPane.showMessageDialog( new JFrame(), "Button " + s + " is in execution", "Error", JOptionPane.ERROR_MESSAGE ); 
                }
			 }
             }
             else
             {
                JOptionPane.showMessageDialog( new Frame(), "The button was deleted", "Warning", JOptionPane.WARNING_MESSAGE );
             }
	     }
	 };
	 ALstatisticsButtons = new ActionListener()
	 {
	     public void actionPerformed( ActionEvent e )
	     {
             String s = ""; 
	         JButton b = ( JButton )e.getSource();
             if ( ! UB.exec.deleted )
             {
	         if ( ( b == sb ) && sb.isEnabled() && ( UB.results != null ))
	         {
                int go = JOptionPane.showConfirmDialog( null, "Full ?", "Full", JOptionPane.YES_NO_OPTION );
		        JFrame fr = new JFrame ( "Button statistics " ) ;
		        JTextArea content = new JTextArea ( Width, Heigth );
                content.setFont( new Font( "S", Font.PLAIN, 14 ) );
                content.setForeground( Color.black );
                content.setBackground( Color.lightGray );
                JPanel pn = new JPanel ( );
                JScrollPane textScroller = new JScrollPane( content,
		                                       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                                       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		        pn.setOpaque(true);
                content.setEditable( false );
                textScroller.getViewport().add( content );
		        pn.setLayout( new BorderLayout() );
		        pn.add( textScroller, BorderLayout.CENTER );
                fr.getContentPane().add ( pn );
		        for ( int k = 0; k < UB.results.size(); k ++ )
		        {
			        execResult eR = ( execResult ) UB.results.get ( k );
			        eR.PrintBlock ( content, UB.button_name, k + 1, go ) ;
		        }
	            Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     			Double dx = new Double ( pp.getX());
     			Double dy = new Double ( pp.getY());
     			fr.setLocation(dx.intValue(), dy.intValue());		        
                fr.pack();
                fr.setVisible ( true );
			 }	         
             }
             else
             {
                JOptionPane.showMessageDialog( new Frame(), "The button was deleted", "Warning", JOptionPane.WARNING_MESSAGE );
             }
	     }
	 };
	 ALforinsertButtons = new ActionListener()
	 {
	     public void actionPerformed( ActionEvent e )
	     {
             boolean gerror = false;
             String s = "";
             if ( ! UB.exec.deleted )
             {
	         JButton b = ( JButton )e.getSource();
	         if ( ( ( b == mb ) && mb.isEnabled() ) ||
                  ( ( b == msb ) && msb.isEnabled() ) )// save button
	         {
	 		      b.setEnabled ( false );
	              String received = content.getText();
                  rm = new Remake();
                  copy.vs.removeAllElements();
	              StringTokenizer stc = new StringTokenizer ( received, "\n", false ) ;
                  if ( stc.hasMoreTokens() == false ) System.out.println ("content empty");
	              while ( stc.hasMoreTokens() )
	              {
                       s = stc.nextToken().trim();
                       rm.type = MyConstants.ComplexType ( s ) ;
                       String temp = remakeFunction ( s, rm ) ;
                       if ( ! temp.equals ( rm.ff ) )
                       {
                           if ( rm.endfunction.size() == 1 )
                           {
                               Integer I = ( Integer )rm.endfunction.get(0) ;
                               if ( I.intValue() == 1 )
                               {
                                   copy.addFunction ( temp );
                                   rm.clear();
                               }
                               else
                               {
                                  rm.ff = rm.ff + temp;
                               }
                           }
                           else
                           {
                              rm.ff = rm.ff + temp;
                           }
                       }
                       else 
                       {
                           gerror = true;
                           JOptionPane.showMessageDialog( new Frame(), "Something wrong in the last changes", "Warning", JOptionPane.WARNING_MESSAGE );
                       }
	              }
	              if ( ( modifyIndex != -1 ) && ! gerror )
	              {
	 				  caller.buttons.remove ( modifyIndex );
                  	  caller.must_write_file =  true ;
                      UB.vs.removeAllElements();
                      for ( int k = 0; k < copy.vs.size() ; k ++ )
                      {
                          UB.vs.add( ( blockButton )copy.vs.get( k ) );
                      }
                  	  caller.buttons.add( modifyIndex, UB );
              	  }
                  if ( b == msb ) 
                  {
                      content.selectAll();
                      content.replaceSelection ( "" );
                      if ( rm != null ) rm.clear();
                      setVisible ( false );
                  }
                  else
                  {
                      b.setEnabled ( true );
                  }
		 	 }
             }
             else
             {
                JOptionPane.showMessageDialog( new Frame(), "The button was deleted", "Warning", JOptionPane.WARNING_MESSAGE );
             }
	     }
	 };

     ///////
     //  main body of the modify class
     //////

        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu( "Rename" );
        JMenuItem mi = new JMenuItem("only name");
    	mi.setActionCommand( "default" );
    	mi.addActionListener( colorButtons ) ;	         
        menu.add(mi);
        mi = new JMenuItem("icon");
    	mi.addActionListener( iconsButtons ) ;        
        menu.add(mi);
        JMenu submenu = new JMenu ( "color");    				
     	for ( int i = 0; i < MyConstants.palete; i ++ )
     	{
        	  bc [ i ] = new JRadioButtonMenuItem( MyConstants.bclName [ i ] );
              bc [ i ].setEnabled ( true ) ;
        	  bc [ i ].setBackground ( MyConstants.bcl [ i ] ) ;
              bc [ i ].addActionListener ( colorButtons ) ;
              bc [ i ].setActionCommand( "color" );                    
        	  submenu.add ( bc [ i ] );
     	}
		menu.add(submenu);
        menubar.add(menu);
    				
	 content = new JTextArea( Width, Heigth );
	 content.setFont( new Font( "Dialog", Font.PLAIN, 14 ) );
	 content.setForeground( Color.black );
	 JScrollPane textScroller = new JScrollPane( content,
	                                   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	                                   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
	 content.setEditable( true );
	 getContentPane().setLayout( new BorderLayout() );
	 JPanel bp = new JPanel ( new BorderLayout() );
     msb.setToolTipText("Save button content and characteristics and close the window");
     mb.setToolTipText("Save button content and characteristics");
     eb.setToolTipText("Close the window without saveing any change");
     db.setToolTipText("Delete the button from the panel");
     brk.setToolTipText("Break the button execution\n at the end of the current function");
     cb.setToolTipText("Copy the content of a chosen button\n to this button");
     sb.setToolTipText("Display the execution statistics\n of the current button");
     menu.setToolTipText("Change button characteristics : name/color/icon");
	 JToolBar panel = new JToolBar();
	 panel.add( msb );
	 panel.add( mb );
	 panel.add( eb );	 
	 panel.add( db );
	 panel.add( menubar);
	 panel.add( brk );
	 panel.add( cb );
	 panel.add( sb );
	 bp.add(panel, BorderLayout.PAGE_START);
	 bp.add ( textScroller);
     getContentPane().add ( bp );
     
     addWindowListener ( new WindowAdapter ( )
	 {
	      public void windowClosing ( WindowEvent e )
	      {
              content.selectAll();
              content.replaceSelection ( "" );
	      }
	      public void windowDeiconified ( WindowEvent e ){}
	      public void windowIconified ( WindowEvent e ){}
	      public void windowDeactivated ( WindowEvent e ){}
	      public void windowActivated ( WindowEvent e ){}
	  } );
	  
	 setTitle ( "Button : " + copy.button_name );
	 msb.setEnabled ( true );
	 mb.setEnabled ( true );
	 eb.setEnabled ( true );
	 cb.setEnabled ( true );
	 sb.setEnabled ( true );
	 db.setEnabled ( true );
	 brk.setEnabled ( true );
     content.setBackground( Color.white );
//     Utils.printList ( new blockButton ( copy.vs ) );
     displayButton ( copy, content );
     msb.addActionListener( ALforinsertButtons );
     mb.addActionListener( ALforinsertButtons );
     eb.addActionListener( ALcancelButtons );
     cb.addActionListener( ALcopyButtons );
     sb.addActionListener( ALstatisticsButtons );
     db.addActionListener( ALdeleteButtons );
     brk.addActionListener( ALbreakButtons );

     content.addMouseListener( MLfornewButtons );
 	 pack();
     Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     Double dx = new Double ( pp.getX());
     Double dy = new Double ( pp.getY());
     int xLocation = dx.intValue() - Width/2;
     if ( xLocation <= 0 )
     {
        xLocation = xLocation + MyConstants.button_wx;
     }
     if ( xLocation >= MyConstants.Screen_x )
     {
        xLocation = xLocation - MyConstants.button_wx;
     }
     int yLocation = dy.intValue() - Heigth/2;
     if ( yLocation <= 0 )
     {
        yLocation = yLocation + MyConstants.button_wy;
     }
     if ( yLocation >= MyConstants.Screen_y )
     {
        yLocation = yLocation - MyConstants.button_wy;
     }
 	 setLocation ( xLocation, yLocation );
// 	 setLocation ( MyConstants.button_wx + MyConstants.Screen_x / 9 , MyConstants.button_wy );
	 setVisible ( true );
	  
 }
 
 	 public void do_storeCommand( ImageIcon f, String fname )
	 {
        String s = dialog ( "Enter new name :\n", null, null );
        int index = have_the_name ( copy.button_name, caller.buttons, caller.buttons.size() ) ;
        if ( ( s != null ) && ( s.length() > 0 ) )
        {
            if ( have_the_name ( s.trim(), caller.buttons, caller.buttons.size() ) != -1 )
            {
                  // give warning window !!!!
                  JOptionPane.showMessageDialog( new Frame(), "There is already a button with this name", "Warning", JOptionPane.WARNING_MESSAGE );
            }
            else
            {
                  userButton temp = ( userButton ) caller.buttons.remove ( index );
                  temp.button_name = s.trim();
                  caller.buttons.insertElementAt ( temp,index );
                  temp.ub.setText ( temp.button_name ) ;
                  caller.must_write_file = true ;
                  setTitle ( "Button : " + temp.button_name );
                  if ( f == null )
                  {
                  	if ( last != Color.white )
                  	{
	                  temp.setColor ( last ) ;
                  	}
                  	else
                  	{
	                  temp.retColor ( );
	                  temp.retIcon ( );
                  	}
              	  }
              	  else
              	  {
	              	  temp.setIcon ( f, fname ) ;
              	  }
             }
         }
         else
         {
             userButton temp = ( userButton ) caller.buttons.get ( index );
             if ( f == null )
             {
               if ( last != Color.white )
               {
                 temp.setColor ( last ) ;
                 caller.must_write_file = true ;
               }
               else
               {
                   temp.retColor ( );
                   temp.retIcon ( );
               }
             }
             else
             {
                 temp.setIcon ( f, fname ) ;
                 caller.must_write_file = true ;
             }
         }
	}

 // format a function string from the the content of the window - in case of 
 // typeing free a function
 public String remakeFunction ( String s, Remake rm )
 {
    String f = rm.ff;
    debug( false, "remake: " + s + "#f="+f+"#type="+rm.type+"#end="+rm.endfunction.size());
    switch ( rm.type )
    {
    case MyConstants.endType:
        Integer End = ( Integer ) rm.endfunction.remove ( rm.level ) ; 
        switch ( End.intValue() )
        {
        case 0:
        case 3:
            rm.level --;
            if ( rm.level == 0 )
            {
                f = f + "@ ;";
			}
            else
            {
                f= "@ ;";
            }
            break;
            
        case 2:
            f = "?;";
            rm.endfunction.add ( new Integer ( 3 ) );
            break;
        }
        break;
        
    case MyConstants.simpleType:
    case MyConstants.interceptType:
    case MyConstants.exprType:
        f = s + " ;";
        break;
        
    case MyConstants.loopType:
    case MyConstants.incType:
    case MyConstants.forType:
		rm.level ++;
        rm.endfunction.add ( new Integer ( 3 ) );
        f = s  + " ";
        break;
        
    case MyConstants.ifsType:        
    case MyConstants.ifType:
		rm.level ++;
        f = s + ";";
        rm.endfunction.add ( new Integer ( 2 ) );
        break;
    }
    return f;
 }
/************************************************************************/
//
//  Use the dialog to choose or enter something
//
/************************************************************************/
    private String dialog ( String s, String [] options , String defaultoption )
    {
        String sret = (String)JOptionPane.showInputDialog( this, s, s,
                        JOptionPane.PLAIN_MESSAGE, null, options, defaultoption );
        return sret;
    }
/************************************************************************/
//
// displays the button definition
//
/************************************************************************/
private static void displayButton ( userButton temp, JTextArea content )
{
    try
    {
	Vector<blockButton> vs = temp.vs;
    for ( int k = 0; k < vs.size(); k ++ )
    {
        blockButton Itype = ( blockButton )vs.get ( k );
        int type = Itype.blocktype;
        switch ( type )
        {
        case MyConstants.simpleType :
        case MyConstants.interceptType :
        case MyConstants.exprType :
             String ss = Itype.command;
             if ( ! ss.equals("") )
             {
                print_and_debug ( content, type, ss + "\n" );
             }
             break;
             
        case MyConstants.loopType :
        case MyConstants.forType :
        case MyConstants.incType :
        case MyConstants.ifType :
        case MyConstants.ifsType :
        	 print_and_debug ( content, type, Itype.command + "\n");
             printAblock ( Itype.vs, content );
             print_and_debug ( content, type, "END\n" );
             break;

      case MyConstants.ifTypeNo :
             printAblock ( Itype.vs, content );
             print_and_debug ( content, type, "END\n" );
             break;
             
        case MyConstants.noneType :
             print_and_debug ( content, type, "END\n" );
             break;
        }
	}
    }
    catch ( ClassCastException cce )
    {
        debug ( true, "ClassCastException : " + cce.getMessage() );
        cce.printStackTrace();
    }
}
// displays the button definition
// form the string of the functions typed into the window
// the string must obey the internal standard
private static String stringButton ( userButton temp, JTextArea content )
{
	String ret = "";
	Vector<blockButton> vs = temp.vs;
    try
    {
    for ( int k = 0; k < vs.size(); k ++ )
    {
        blockButton Itype = ( blockButton )vs.get ( k );
        int type = Itype.blocktype;
        switch ( type )
        {
        case MyConstants.simpleType :
        case MyConstants.interceptType :
        case MyConstants.exprType :
             String ss = Itype.command;
             if ( ! ss.equals("") )
             {
                ret = ret + ss + "\n" ;
             }
             break;
             
        case MyConstants.loopType :
        case MyConstants.forType :
        case MyConstants.incType :
        case MyConstants.ifType :            
        case MyConstants.ifsType :        
        	 ret = ret + Itype.command + "\n";    
             ret = printAblock ( Itype.vs, content, ret ) + "END\n";
             break;
             
        case MyConstants.ifTypeNo :   
             ret = printAblock ( Itype.vs, content, ret ) + "END\n";
             break;
             
        case MyConstants.noneType :
             ret = ret + "END\n";
             break;
        }
	}
    }
    catch ( ClassCastException cce )
    {
        debug ( true, "ClassCastException : " + cce.getMessage() );
        cce.printStackTrace();
    }
	return ret;
}
private static void print_and_debug ( JTextArea content, int type, String str )
{
	boolean doflag = false;
	if ( doflag )
	{
		System.out.print ( "type=" + type + " str=" + str );
	}
	content.append ( str );
}
// displays an inner block of the primary function's blocks
 private static void printAblock ( Vector<blockButton> vs, JTextArea content )
 {

     for ( int t = 0; t < vs.size(); t ++ )
     {
	     blockButton blk = ( blockButton ) vs.get(t);
         try
         {
         int type = blk.blocktype;
         switch ( type )
         {
         case MyConstants.simpleType:
         case MyConstants.interceptType :
         case MyConstants.exprType :
             print_and_debug ( content, type, blk.command + "\n" );
             break;
             
         case MyConstants.loopType:
         case MyConstants.forType:
         case MyConstants.incType:
         case MyConstants.ifType:
         case MyConstants.ifsType :
             print_and_debug ( content, type, blk.command + "\n" );
             printAblock ( blk.vs, content ) ;
             print_and_debug ( content, type, "END\n" );
             break;
         case MyConstants.ifTypeNo:
             printAblock ( blk.vs, content ) ;
             print_and_debug ( content, type, "END\n" );
             break;
         }
       }
       catch ( ClassCastException cce )
       {
           debug ( true, "ClassCastException : " + cce.getMessage() );
           cce.printStackTrace();
       }
     }
 }
  private static String printAblock ( Vector<blockButton> vs, JTextArea content, String Sret )
  {
	  String ret = Sret;
      for ( int t = 0; t < vs.size(); t ++ )
      {
	      blockButton blk = ( blockButton ) vs.get(t);
          try
          {
          int type = blk.blocktype;
          switch ( type )
          {
          case MyConstants.simpleType:
          case MyConstants.interceptType :
          case MyConstants.exprType :
              ret = ret + blk.command + "\n" ;
              break;
              
          case MyConstants.loopType:
          case MyConstants.forType:
          case MyConstants.incType:
          case MyConstants.ifType:
          case MyConstants.ifsType :
              ret = ret + blk.command + "\n" ;
              ret = printAblock ( blk.vs, content, ret ) ;
              ret = ret + "END\n";
              break;
          case MyConstants.ifTypeNo:
              ret = printAblock ( blk.vs, content, ret ) ;    
              ret = ret + "END\n";
              break;
          }
      }
      catch ( ClassCastException cce )
      {
          debug ( true, "ClassCastException : " + cce.getMessage() );
          cce.printStackTrace();
      }
      }
      return ret;
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
          System.out.println( "ModifyButton : " + str ) ;
      }
  }
}
