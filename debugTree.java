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

import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.Dimension;

// build the debug/help tree
 class TreeDebug extends JPanel implements TreeSelectionListener
 {
 	static final long serialVersionUID = 114L;
    private JTree tree;
    JTextArea ed;
    JFrame frame ;

    public TreeDebug( JFrame fr )
    {
        super ();
        frame = fr;
        //Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode( "Debug" );
        createNodes( top );

        //Create a tree that allows one selection at a time.
        tree = new JTree( top );
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

        //Listen for when the selection changes.
        tree.addTreeSelectionListener( this );

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane( tree );

        ed = new JTextArea( 20,20 );//new JEditorPane();
        ed.setFont( new Font( "Dialog", Font.PLAIN, 14 ) );
        ed.setEditable( false );
        JScrollPane edView = new JScrollPane( ed );

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        splitPane.setTopComponent( treeView );
        splitPane.setBottomComponent( edView );

        treeView.setMinimumSize( new Dimension( 100, 400 ) );
        edView.setMinimumSize( new Dimension( 100, 80 ) );
        splitPane.setDividerLocation( 100 );

        splitPane.setPreferredSize( new Dimension( 300, 500 ) );

        //Add the split pane to this panel.
        add( splitPane );
    }

    /** Required by TreeSelectionListener interface. */
    // look what was pressed, make the translation and reacts
    public void valueChanged( TreeSelectionEvent e )
     {
        int x,y,oldv,newv,state;

        DefaultMutableTreeNode node = ( DefaultMutableTreeNode )
                           tree.getLastSelectedPathComponent();

        if ( node == null ) return;

        Object nodeInfo = node.getUserObject();
        if ( node.isLeaf() )
        {
            String s = nodeInfo.toString ();
            int i = s.indexOf ( '(' );
            int l = s.indexOf ( ')' , i + 1 );
            String stc = s.substring ( i + 1, l ) ;
            // prints the alias file - symbolic name ==> IP address
            if ( stc.equals ( "Hashtable" ) )
            {
                for ( i = 0; i < MyConstants.ips; i ++ )
                {
                    String s1 = MyConstants.potential [ i ];
                    if ( s1.length() > 0 )
                    {
                        String s2 = ( String )MyConstants.alias.get ( s1 );
                        ed.append ( s1 + " => " + s2 + "\n\r" );
                    }
                }
            }
            // prints the help for buttons' functions
            if ( stc.equals ( "HelpF" ) )
            {
                PrintWindow helpW = new PrintWindow ( "Help Functions",30,30 );
	            Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     			Double dx = new Double ( pp.getX());
     			Double dy = new Double ( pp.getY());
                helpW.createTipButtonFunctions( dx.intValue(), dy.intValue() );
            }
            // prints the running parameters file and all the variables added during
            // execution of buttons
            if ( stc.equals ( "Parameters" ) )
            {
				Enumeration enk = MyConstants.pairs.keys();
				Enumeration en = MyConstants.pairs.elements() ;			
				while ( enk.hasMoreElements() )
				{
         			ed.append((String)enk.nextElement() + " = " + (String)en.nextElement() + "\n\r");	 			  
            	}
            }
            // prints the help for 'Load Scripts' panel
            if ( stc.equals ( "HelpL" ) )
            {
                PrintWindow helpW = new PrintWindow ( "Help Load",30,30 );
 	            Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     			Double dx = new Double ( pp.getX());
     			Double dy = new Double ( pp.getY());                                      
                helpW.createTipLoad( dx.intValue(), dy.intValue() );
            }
            // prints the help for 'Draw Counters' panel
            if ( stc.equals ( "HelpD" ) )
            {
                PrintWindow helpW = new PrintWindow ( "Help Draw",30,30 );
 	            Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     			Double dx = new Double ( pp.getX());
     			Double dy = new Double ( pp.getY());                                                      
                helpW.createTipDraw( dx.intValue(), dy.intValue() );
            }
            // opens the dialog box to set/clear debug flags
            if ( stc.equals ( "Debug" ) )
            {
                debugOptions dO = new debugOptions();
            }
        }
    }
    // create the main tree
    private void createNodes( DefaultMutableTreeNode top )
    {
        DefaultMutableTreeNode category = null;

        if ( MyConstants.alias != null )
        {
            category = new DefaultMutableTreeNode( "alias(Hashtable)" );
            top.add( category );
        }
        if ( MyConstants.pairs != null )
        {
            category = new DefaultMutableTreeNode( "run parameters(Parameters)" );
            top.add( category );
        }
        category = new DefaultMutableTreeNode( "Help Functions(HelpF)" );
        top.add( category );
        category = new DefaultMutableTreeNode( "Help Load(HelpL)" );
        top.add( category );
        category = new DefaultMutableTreeNode( "Help Draw(HelpD)" );
        top.add( category );
        category = new DefaultMutableTreeNode( "Debug(Debug)" );
        top.add( category );
    }

} // TreeDebug
 public class debugTree
 {
    public debugTree()
    {

        //Create and set up the window.
        JFrame frame = new JFrame("Debug Tree");

        //Create and set up the content pane.
        TreeDebug newContentPane = new TreeDebug(frame);
        frame.setContentPane( newContentPane );

        //Display the window.
        frame.pack();
        frame.setVisible( true );
    }
}
class debugOptions extends JFrame implements ItemListener
{
 	static final long serialVersionUID = 113L;
	private JCheckBox cb [ ];
    static final int optionNumber = 9;

/************************************************************************/
//
// construct check boxes for the option class
//
/************************************************************************/
	public debugOptions ()
	{
		super ( "Options" );
		cb = new JCheckBox [ optionNumber ];
        JPanel pn = new JPanel ( new GridLayout( optionNumber, 1 ), false );
        cb [ 0 ] = createCheckBox( "ipdef", MyConstants.ipdefdebug_flag );
        pn.add( cb [ 0 ] );
        cb [ 1 ] = createCheckBox( "User", MyConstants.Userdebug_flag );
        pn.add( cb [ 1 ] );
        cb [ 2 ] = createCheckBox( "Utils", Utils.debug_flag );
        pn.add( cb [ 2 ] );
        cb [ 3 ] = createCheckBox( "Button", MyConstants.buttondebug_flag );
        pn.add( cb [ 3 ] );
        cb [ 4 ] = createCheckBox( "exec", MyConstants.execdebug_flag );
        pn.add( cb [ 4 ] );
        cb [ 5 ] = createCheckBox( "TextW", MyConstants.printdebug_flag );
        pn.add( cb [ 5 ] );
        cb [ 6 ] = createCheckBox( "Counters", MyConstants.counterdebug_flag );
        pn.add( cb [ 6 ] );
        cb [ 7 ] = createCheckBox( "UtilsC", UtilsConnection.debug_flag );
        pn.add( cb [ 7 ] );
        cb [ optionNumber - 1 ] = createCheckBox( "OK", false );
        pn.add( cb [ optionNumber - 1 ] );
        getContentPane().add ( pn );
	    Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     	Double dx = new Double ( pp.getX());
     	Double dy = new Double ( pp.getY());
     	setLocation(dx.intValue(), dy.intValue());		        
        pack();
        setVisible ( true );
    }
    private JCheckBox createCheckBox( String s, boolean b )
    {
        JCheckBox cb = new JCheckBox( s, b );
        cb.setHorizontalAlignment( JCheckBox.LEFT );
        cb.addItemListener( this );

        return cb;
    }
/************************************************************************/
//
//  listen to chnage in check box and set/clear te correspndent flag
//  on 'ok' close the box
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
                       switch ( k )
                       {
                       case 0:
                           MyConstants.ipdefdebug_flag = true;
                           break;
                       case 1:
                           MyConstants.Userdebug_flag = true;
                           break;
                       case 2:
                           Utils.debug_flag = true;
                           break;
                       case 3:
                           MyConstants.buttondebug_flag = true;
                           break;
                       case 4:
                           MyConstants.execdebug_flag = true;
                           break;
                       case 5:
                           MyConstants.printdebug_flag = true;
                           break;
                       case 6:
                           MyConstants.counterdebug_flag = true;
                           break;
                       case 7:
                           UtilsConnection.debug_flag = true;
                           break;
                       case optionNumber - 1:
                           setVisible ( false );
                           break;
                       }
                   }
                   else
                   {
                       switch ( k )
                       {
                       case 0:
                           MyConstants.ipdefdebug_flag = false;
                           break;
                       case 1:
                           MyConstants.Userdebug_flag = false;
                           break;
                       case 2:
                           Utils.debug_flag = false;
                           break;
                       case 3:
                           MyConstants.buttondebug_flag = false;
                           break;
                       case 4:
                           MyConstants.execdebug_flag = false;
                           break;
                       case 5:
                           MyConstants.printdebug_flag = false;
                           break;
                       case 6:
                           MyConstants.counterdebug_flag = false;
                           break;
                       case 7:
                           UtilsConnection.debug_flag = false;
                           break;
                       }
                   }
               }
            }
        }
    }
}

