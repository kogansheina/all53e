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

class ButtonIcons extends JFrame implements ActionListener
{
	private static final int buflen = 80;
	private char buf [] = new char [ buflen ];	
	private int lastIcon = 0; 
	private JPanel frame;
    private JButton bb [] ;
	private ImageIcon icon [];
	public String filenames [] = new String [ MyConstants.iconsN ];	
	public String names [] = new String [ MyConstants.iconsN ];
	public DefineButton defb = null;
	public ModifyButton modb = null;
	
    public ButtonIcons( )
    {   
        super("Button's Icons");
        icon = new ImageIcon[ MyConstants.iconsN ] ;  
        bb = new JButton [ MyConstants.iconsN ];	    
	    for ( int i =0; i < MyConstants.iconsN; i ++ )
		{
			names [ i ] = "icon" + new Integer ( i ).toString() ;
			icon [ i ] = null; 
		} 
        frame = new JPanel(new GridLayout( 0,5 ));
        frame.setOpaque(true);
        JScrollPane scrb = new JScrollPane(frame, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrb);
        setVisible(false);
	}
	public void askForDefinition()
	{
        int go = JOptionPane.showConfirmDialog( null, "From file", "From file", JOptionPane.YES_NO_OPTION );
		if ( go == 0 )
		{
			FileDialog selectfile = new FileDialog ( new JFrame() );       
        	String s ;
	        selectfile.setVisible(true);
	        s = selectfile.getDirectory() + selectfile.getFile();
        	if ( ( s != null ) && ( s.length() > 0 ) )
        	{
	        	FileReader fr = null;

            	if ( ! s.endsWith( "null" ) )
            	{
            		try
            		{
                	fr = new FileReader ( s ) ;
            		}
            		catch ( IOException e )
            		{
                	System.out.println( "Error in open icon file " +  s ) ;
            		}
		    		if ( fr != null )
		    		{
		    			try
		    			{
			    		int rd = fr.read( buf );
                        String rr = null;
	        			while ( rd != -1 )
		    			{	
                            String ss = null;
			    			String sbuf = new String ( buf );
                            sbuf = sbuf.substring( 0, rd );
                            StringTokenizer stc = new StringTokenizer ( sbuf,"\n",false);
                            int count = stc.countTokens() ;
                            if ( rr != null )
                            {
                                ss = rr + stc.nextToken();
                                rr = null;
                            }
                            if ( sbuf.lastIndexOf('\n') < rd - 1 )
                            {
                                count --;
                                rr = sbuf.substring( sbuf.lastIndexOf('\n') + 1, rd );
                            }
                            for ( int k =0; k < count; k ++ )
                            {
                                if ( ss == null )
                                    ss = stc.nextToken();

                                if ( ! IconAlreadyDefined(ss) )
                                {
                                    filenames [ lastIcon ] = ss;
                                    icon [ lastIcon ] = new ImageIcon( filenames [ lastIcon ] );
                                    bb [ lastIcon ] = new JButton ( icon [ lastIcon ] ) ;
                                    bb [ lastIcon ].addActionListener(this);
                                    bb [ lastIcon ].setActionCommand ( names [ lastIcon ] );
                                    bb [ lastIcon ].setToolTipText( "file : " + filenames [ lastIcon ] );
                                    frame.add ( bb [ lastIcon ] );
                                    lastIcon ++;
                                    if ( lastIcon >= MyConstants.iconsN )
                                    {
                                        JOptionPane.showMessageDialog( new JFrame(), "No rooms for more icons", "Error", JOptionPane.ERROR_MESSAGE );
                                        rd = -1;
                                    }
                                }
                                ss = null;
                            }
                            if ( rd != -1 )
							    rd = fr.read( buf );
	    				}
	    				fr.close();
	    				setVisible();
		    			}
		    			catch ( IOException e )
    	    			{
    	    			}
		    		}			
			  	}
		  	}
	  	}
		else
		{	
			AddIcons();
		}       	         
    }
    private boolean IconAlreadyDefined ( String ss )
    {
        boolean found = false;

        for ( int k = 0; k < lastIcon; k ++ )
        {
            if ( filenames [ k ].equals ( ss ))
            {
                found = true;
                break;
            }
        }
        return ( found );
    }
    public void addIconsFromDefinition( String s, ImageIcon img )
    {
        int i = lastIcon; 
        if ( ! IconAlreadyDefined(s) )
        {
	        filenames [ i ] = s;
            icon [ i ] = img;
 	        bb [ i ] = new JButton ( icon [ i ] ) ;
	        bb [ i ].addActionListener(this);
	        bb [ i ].setActionCommand ( names [ i ] );
            bb [ lastIcon ].setToolTipText( "file : " + s );
	        frame.add ( bb [ i ] );       
            lastIcon ++ ;
            if ( lastIcon >= MyConstants.iconsN )
            {
                JOptionPane.showMessageDialog( new JFrame(), "No rooms for more icons", "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
	}
   
	public void AddIcons()
	{
		FileDialog selectfile = new FileDialog ( new JFrame() );       
        String s ;
        int i = lastIcon; 
        for ( ; i < MyConstants.iconsN; i ++ )
        {
	        selectfile.setVisible(true);
	        s = selectfile.getDirectory() + selectfile.getFile();
        	if ( ( s != null ) && ( s.length() > 0 ) )
        	{
            	if ( ! s.endsWith( "null" ) )
            	{
                    if ( ! IconAlreadyDefined(s) )
                    {
	            	    filenames [ i ] = s;
                	    icon [ i ] = new ImageIcon( s );
	        		    bb [ i ] = new JButton ( icon [ i ] ) ;
	        		    bb [ i ].addActionListener(this);
	        		    bb [ i ].setActionCommand ( names [ i ] );
                        bb [ lastIcon ].setToolTipText( "file : " + s );
	        		    frame.add ( bb [ i ] );
                    }
            	}
            	else break;     
        	}
        	else break;     
		}
		lastIcon = i;
		setVisible();
        int go = JOptionPane.showConfirmDialog( null, "Store in file", "Store in file", JOptionPane.YES_NO_OPTION );
		if ( go == 0 )
		{
			selectfile.setVisible(true);
	        s = selectfile.getDirectory() + selectfile.getFile();
        	if ( ( s != null ) && ( s.length() > 0 ) )
        	{
	        	FileWriter fr = null;
            	if ( ! s.endsWith( "null" ) )
            	{
            	try
            	{
                	fr = new FileWriter ( s ) ;
            	}
            	catch ( IOException e )
            	{
                	System.out.println( "Error in open icon file " +  s ) ;
            	}
		    	if ( fr != null )
		    	{
		    		try
		    		{
	        			for ( i = 0; i < lastIcon; i ++ )
		    			{	
			    			String temp = filenames[i]+"\n";
			    			temp.getChars(0,temp.length(),buf,0 );		    		
			    			fr.write(buf,0,temp.length());
	    				}
	    				fr.close();
		    		}
		    		catch ( IOException e )
    	    		{
    	    		}
		    	}
        	} /* not null */
	            	
          }/* file name */
		} /*yes */
	}
    public void actionPerformed(ActionEvent e)
    {
	    for ( int i =0; i < MyConstants.iconsN; i ++ )
	    {
		    if ( e.getActionCommand() == names [ i ] )
		    {
			    if ( ( defb != null ) && ( modb == null ) )
			    {
        			String sf = (String)JOptionPane.showInputDialog( new Frame(), "Enter button name:\n", "Enter button name:\n",
                        JOptionPane.PLAIN_MESSAGE, null, null, "" );			    
       				if ( ( sf == null ) || ( sf.length() == 0 ) )
       				{
            			JOptionPane.showMessageDialog( new Frame(), "Button is supposed to have a name", "Warning", JOptionPane.WARNING_MESSAGE );
       				}
       				else
       				{
	        			// check there is not another button into the same panel with the same name
            			if ( have_the_name ( sf.trim(), defb.buttons, defb.buttons.size() ) != -1 )
            			{
                  			// give warning window !!!!
                  			JOptionPane.showMessageDialog( new Frame(), "There is already a button with this name", "Warning", JOptionPane.WARNING_MESSAGE );
            			}
            			else
            			{			    
	            			defb.addButton ( new userButton ( sf.trim(), defb, icon [ i ], filenames [ i ] ) );
	            			defb.must_write_file = true;			    
			    			break;
		    			}
        			}
    			}
    			else
    			{
	    			if ( ( defb == null ) && ( modb != null ) )
	    			{
		    			modb.do_storeCommand( icon [ i ], filenames [ i ] );
	    			}
    			}
    			defb = null;
		    	modb = null;
    		}
		}
	}

	public void setVisible ( boolean f, DefineButton db )
	{
		defb = db ;
		if ( f )
		{
			setVisible ( );
		}
		else
		{
			setVisible(false);
		}
	}
	public void setVisible ( )
	{
        pack();
//        setSize(100,200);
        if ( ! isVisible() )
        {
            setVisible(true);
        }
        else 
        {
            repaint();
        }
	}

	public void setVisible ( boolean f, ModifyButton db )
	{
		modb = db ;
		if ( f )
		{
			setVisible ( );
		}
		else
		{
			setVisible(false);
		}
	}
	
/************************************************************************/
//
// look into the buttons' data base for the given name
// if found , returns the index into buttons' vector
// otherwise return -1
//
/************************************************************************/
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
}


