package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.* ;
import javax.swing.border.Border;
import java.beans.*;

/************************************************************************/
//
// class for several printings - helps/button content etc.
//
/************************************************************************/
class PrintWindow extends JFrame 
{
	static final long serialVersionUID = 110L;
    private int device = 0;
    private StyleContext sc;
    private Style sb,ss ;
    private DefaultStyledDocument doc;

/************************************************************************/
//
// construct the main frame and give it the required title
//
/************************************************************************/
    public PrintWindow ( String title, int x, int y ) 
    {
        super ( title );
        setBackground ( Color.lightGray ) ;
        setSize ( x, y ) ;
        JPanel pn = new JPanel ( );
		sc = new StyleContext();
        sb = sc.addStyle( null, null );
        ss = sc.addStyle( null, null );
        StyleConstants.setFontFamily( sb, "SansSerif" );
        StyleConstants.setBold( sb, true );
        StyleConstants.setFontSize( sb, 14 );
        StyleConstants.setForeground( sb,Color.black );
        StyleConstants.setFontFamily( ss, "Plain" );
        StyleConstants.setBold( ss, false );
        StyleConstants.setFontSize( ss, 14 );
		doc = new DefaultStyledDocument( sc );
    	JTextPane p = new JTextPane( doc );
    	p.setEditable ( false );
    	JScrollPane scrollPane = new JScrollPane( p, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED  );
        getContentPane().add ( scrollPane );
        setVisible ( false);
    }

/************************************************************************/
//
// Print the help for button's function definition
//
/************************************************************************/
    public void createTipButtonFunctions ( int x, int y )
    {
        String [] helpText =
        {
           "\n",
           "< script file > < IP address >\n",
           "< time | [l1 - l2] >\n",
           "< time | [l1 - l2] < text to be intercepted >>\n",
           "< button name >\n",
           "< button name >\n",
           "< time | [l1 - l2] > < checking test > < address >\n    waiting time : integer or [ l1 - l2 ]\n    text to be received and checked ( under quotes)\n    f1 : block of functions for TRUE \n    f2 : block of functions for FALSE \n    f1, f2 may be any functions besides 'if', 'sif'\n",
           "< number > f1 ...\n    f1 : block of any functions\n",
           "< start address - end address > f1...\n    addresses MUST have at least 2 digits \n    the last 2 digits of the address are incremented\n    f1 : block of any functions besides 'for' \n",
           "[ variable= ]< start address - end address - step > f1...\n    f1 : block of any functions besides 'if'\n    f1 : the parameter to be incremented will be variable or 'i'\n",
           "< IP address >\n    any function recognized by the shell; with its entire path\n",
           "nothing, close blocks of 'for', 'inc', 'loop' and 'if'\n",
           "< address > - open connection for address\n",
           "< checking test >\n    text to be checked (under quotes)\n    f1 : block of functions for TRUE \n    f2 : block of functions for FALSE \n    f1, f2 may be any functions besides 'if', 'sif'\n",
           "finish a 'loop', 'for' and 'inc' loop\n",
           "clear output screen\n",
           "assign a value to a variable\n    the content of the variable is obtained from the evaluation\n    of an expression; the operators are :\n      *, /,+,-,%, ! for 'not' logical and ^ for string concatenation\n    - the expression must be between parantheses\n",
           "displays the same kind of counter from many packs( module per window)\n",
           "displays all the counters of a pack(device per window)\n",
           "displays a combination of any counters from any packs\n", 
           "store all received counters - binary or ASCII\n"
        };
        String [] noteText =
        {
           " NOTE:\n",
           "  In case the functions are introduced in a free way, they\n",
           "     must obey the same rules, besides the 'general' which is\n",
           "     introduced as in the Input Text Window : <address> <command>\n",
           "     ",
           "     Nesting is permited only for one complex function inner to \n",
           "     another complex function - as mentioned for each function. \n",
           "     For expressions under any functions or 'expr' function must \n",
           "     be between parantheses.\n"
        };
        String [] helpWText =
        {
           "Keywords:",
           "  file ",
           "  wait ",
           "  intercept ",
           "  button ",
           "  buttonwait ",
           "  if ",
           "  loop ",
           "  for ",
           "  inc ",
           "  general ",
           "  end ",
           "  open ",
           "  sif ",
           "  break ",
           "  cs ",
           "  expr ",
           "  display ",
           "  print ",
           "  get ",
           "  store "
        };
        try
        {
            doc.remove( 0, doc.getLength() );
            for ( int k = 0; k < helpText.length; k ++ )
            {
                doc.insertString( doc.getLength(), helpWText [ k ], sb );
                doc.insertString( doc.getLength(), helpText [ k ], ss );
            }
            doc.insertString( doc.getLength(), noteText [ 0 ], sb );
            for ( int k = 1; k < noteText.length; k ++ )
            {
                doc.insertString( doc.getLength(), noteText [ k ], ss );
            }
        } // try
        catch ( BadLocationException e )
        {
            System.out.println( "Internal error: " + e );
        }
        setLocation ( x, y );
        pack();
        setVisible ( true );
    }
/************************************************************************/
//
// Print the help for 'Load Scripts to ...' panel
//
/************************************************************************/
    public void createTipLoad ( int x, int y )
    {
        String [] helpText =
        {
           "\n",
           "Choose a pack ( from the list )\n   to send the script file\n",
           "Add an IP address to list\n   the list may be used in buttons\n",
           "Choose the script file to be sent\n   if no address is selected\n   the file is loaded as run parameters\n",
           "Opens a window to send command to any pack in the list\n   the syntax is <addess> < comand>\n   the commands are sent after the 'enter' button is pressed\n",
           "Permits to open/close a log file\n   it wll contain everything is displayd on the 'output window'\n",
           "Permits to audit the packs\n   it gives the status as the packs theyselves see it\n"
        };
        String [] helpWText =
        {
           "Buttons:",
           "  Pick an address ",
           "  Enter an address ",
           "  Pick a file ",
           "  Command window ",
           "  Log options ",
           "  Audit options "
        };
        try
        {
            doc.remove( 0, doc.getLength() );
            for ( int k = 0; k < helpText.length; k ++ )
            {
                doc.insertString( doc.getLength(), helpWText [ k ], sb );
                doc.insertString( doc.getLength(), helpText [ k ], ss );
            }
        } // try
        catch ( BadLocationException e )
        {
            System.out.println( "Internal error: " + e );
        }
        setLocation ( x, y );
        pack();
        setVisible ( true );
    }
/************************************************************************/
//
// Print the help for 'Draw Counters from ...' panel
//
/************************************************************************/
    public void createTipDraw ( int x, int y )
    {
        String [] helpText =
        {
           "\n",
           "Choose a pack ( symbolic or by address )\n   to receive its counters\n",
           "Execute function 'print' for the selected IP\n   according to set Options\n",
           "Define counters to be printed - no definition\n   will print all the counters of the pack\n",
           "Displays the current options of the device\n   for displaying counters\n",
           "Permits to change the options for the device\n   it is vailable for display functions from buttons\n"
        };
        String [] helpWText =
        {
           "Buttons:",
           "  Enter IP address ",
           "  GO !! ",
           "  Counters ",
           "  Print request ",
           "  Options "
        };
        try
        {
            doc.remove( 0, doc.getLength() );
            for ( int k = 0; k < helpText.length; k ++ )
            {
                doc.insertString( doc.getLength(), helpWText [ k ], sb );
                doc.insertString( doc.getLength(), helpText [ k ], ss );
            }
        } // try
        catch ( BadLocationException e )
        {
            System.out.println( "Internal error: " + e );
        }
        setLocation ( x, y );
        pack();
        setVisible ( true );
    }
}
