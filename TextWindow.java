package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.* ;
import java.io.*;
import javax.swing.border.Border;
import java.beans.*;
/*****************************************************************/
//
// print the received messages from all the open connections
//
/*****************************************************************/
class TextWindow extends JFrame 
{
	static final long serialVersionUID = 111L;
	private StyleContext sc;
	private DefaultStyledDocument doc;
    private String s1 = "";
    private String s2 = "";
    private Style sb,ss ;
    private FileWriter toFile;
    private int option = 0 ;
    private int fileCounter ;
    private int fileSuffix ;
    private String filename = "" ;
    
    public ArrayList<String> fifo;
/*****************************************************************/
//
// constructor of the window; the parameter 'true' make it visible now
//
/*****************************************************************/
	public TextWindow ( boolean immediate ) 
	{
	  super ( "Output Text Area" );
      toFile = null;
      fileSuffix = 0;
      fifo = new ArrayList<String>(Collections.synchronizedList ( new ArrayList<String>()));
      pack();
      getContentPane().add ( define_window () );
      setBackground ( MyConstants.background ) ;
      setLocation ( MyConstants.output_x, MyConstants.output_y );
      setSize ( 2 * MyConstants.Screen_x / 3, MyConstants.Screen_y - MyConstants.output_y - MyConstants.Screen_y / 20 ) ;
      setVisible ( immediate  );
	}

/************************************************************************/
//
//   define a window to receive text from connection
//   and print it
//
/************************************************************************/
	private JScrollPane define_window ()
	{
		sc = new StyleContext();
        sb = sc.addStyle( null, null );
        ss = sc.addStyle( null, null );
        StyleConstants.setFontFamily( sb, "SansSerif" );
        StyleConstants.setBold( sb, true );
        StyleConstants.setFontSize( sb, 14 );
        StyleConstants.setForeground( sb,Color.black );
        StyleConstants.setFontFamily( ss, "Plain" );
        StyleConstants.setBold( ss, false );
        StyleConstants.setFontSize( ss, 12 );
        StyleConstants.setForeground( ss,Color.black );
		doc = new DefaultStyledDocument( sc );
    	JTextPane p = new JTextPane( doc );
    	p.setEditable ( false );
    	JScrollPane scrollPane = new JScrollPane( p, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS  );
        return scrollPane;
	}
    public void clear_screen()
    {
        try
        {
            doc.remove ( 0, doc.getLength() );
  		} // try
 	 	catch ( BadLocationException e )
  		{
      		System.out.println( "Internal error: " + e );
  		}
    }
/************************************************************************/
//
// beg - is the text to be printed in its associated style ( font, color )
// st is the actual text to be printed
//
/************************************************************************/
    public void printToWindow ( String st, boolean style )
    {
        if ( ( ( toFile != null ) && ( option == 1 ) ) || ( toFile == null ) )
        {
            try
            {
                if ( doc.getLength() >= DefaultStyledDocument.BUFFER_SIZE_DEFAULT * 50 )
                {
                    System.out.println ( "printToWindow remove " ) ; 
                    doc.remove ( 0, DefaultStyledDocument.BUFFER_SIZE_DEFAULT * 30 );
                }
                if ( style )
                    doc.insertString( doc.getLength(), st, sb );
                else doc.insertString( doc.getLength(), st, ss );
  		    } // try
 	 	    catch ( BadLocationException e )
  		    {
      	    	System.out.println( "Internal error: " + e );
  		    }
        }
        try                
		{
			if ( toFile != null )
			{
				toFile.write ( st );
                fileCounter ++ ;
                if ( fileCounter > 10000 )
                {
                    toFile.close() ;
                    toFile = null;
                    setFile ( ) ;
                }
			}
        }
        catch ( IOException eio )
        {
            System.out.println ( "Error writting to text file " + toFile );
        }
    }
/*****************************************************************/
//
// open a log file 
//
/*****************************************************************/
private void setFile ( )
{
    String ff = "";
	try
	{
        Integer I = new Integer ( fileSuffix ) ;
        ff = filename + "." + I.toString();
		toFile = new FileWriter ( ff );
        fileCounter = 0;
        fileSuffix ++ ;
        if ( MyConstants.session != null )
            MyConstants.session.write("Log file = " + ff + "\n" );
	}
	catch ( IOException e )
	{
		toFile = null;
		System.out.println ( "Error opening text file " + ff );
	}
}
public void setFile ( String f, int opt )
{
	if ( f.length() > 0 )
	{
        filename = f;
        option = opt ;
        fileCounter = 0;
        fileSuffix = 0 ;
        setFile();
	}
	else toFile = null;
}
/*****************************************************************/
//
// close the last log open file                                        
//
/*****************************************************************/
	public void CloseFile ()
	{
		if ( toFile != null )
		{
			try
			{
				toFile.close();
			}
			catch ( IOException e )
			{
			}
		}
        toFile = null ;
	}
/************************************************************************/
//
// beginT - is the text to be printed in its associated style ( font, color )
// realT - is the actual text to be printed
//
/************************************************************************/
	public void SetText ( String beginT, String realT )
	{
        try
        {
            if ( beginT == null )
            {
                System.out.println ( "SetText : beginT is null" ) ; 
                beginT = "\n\r";
            }
            if ( realT == null )
            {
                System.out.println ( "SetText : realT is null" ) ; 
                realT = "\n\r";
            }
            fifo.add ( beginT + realT );
            if ( ! MyConstants.twr.isAlive() )
            {
                MyConstants.twr.run();
            }
            else
            {
                debug ( true, "thread is alive : " + fifo.size()) ; 
            }
        }
        catch ( Exception ex )
        {
            System.out.println ( "Exception from SetText " + ex ) ; 
        }
	} // SetText
    private static void debug ( boolean doflag, String str )
    {
        if ( ( doflag ) || ( MyConstants.printdebug_flag ) )
        {
            System.out.println( "TextWindow : " + str ) ;
        }
    }
}
