package all53e;
import java.awt.*;
import java.lang.*;
import java.util.* ;
import javax.swing.*;
import java.io.*;

class resultType
{
   public String type;
   public String note;
   public int inttype;

   public void resultType()
   {
       type = "";
       note = "";
       inttype = -1 ;
   }
}
/************************************************************************/
//
// implemets users' button definition and execution statistic
//
/************************************************************************/
class execResult
{
	private Vector<resultType> results;
    private resultType result = null;
	
	public execResult()
	{
        results = new Vector<resultType> (0,1) ;
	}
	
	public void SetNotification ( String note, String text )
	{
        result = new resultType () ;
        result.note = note;
        result.type = text;
        results.add(result);
	}
	public void SetWaittime ( String f, int note )
	{
        result = new resultType () ;
        result.inttype = note;
        result.type = f.trim();
        results.add(result);
	}
	
	public void SetIndex ( int note, String f )
	{
        SetWaittime( f,note );
	}
	
	public void SetAddress ( String note )
	{
        SetNotification ( note, "for" ) ;
	}
	
	public boolean IsValid ()
	{
        if ( results.size() > 0 ) return true;
		return false;
	}
	// displays the aleready accumulated statistics for a spcific button, current execution
    // into a window
	public void PrintBlock ( JTextArea content, String name, int number, int full )
	{
        content.append("Button : " + name + ", TEST " + number + "\n");
        if ( full == 0 ) // yes
        {
            for ( int k = 0; k < results.size() - 1; k ++ )
            {
                result = ( resultType )results.get ( k ) ;
                String tempS = result.type ;
                content.append("Function '" + tempS + "'" );
            
                if ( tempS.equals( "notification" ) )
                {
                    content.append(" is '" + result.note + "'\n" );
                    if ( result.inttype > -1 )
                    {
                        content.append(" waiting " + result.inttype + " msec" ) ;
                    }
                }
                if ( tempS.equals( "inc" ) || tempS.equals( "loop" ) )
                {
                    content.append(" index is " + result.inttype ) ;
                }
                if ( tempS.equals( "for" ) )
                {
                    content.append(" address " + result.note ) ;
                }
                if ( tempS.equals( "wait" ) )
                {
                    content.append(" " + result.inttype + " msec") ;
                }          
                if ( tempS.equals( "parameter" ) )
                {
                    content.append("=" + result.note ) ;
                }          
                content.append ("\n");
            }
        }
        result = ( resultType )results.get ( results.size() - 1 ) ;
        if ( result.type.equals("result") )
            content.append(" Result : " + result.note  + "\n" );
        else System.out.println("result vector has not conclude : " + result.type ) ;
	}
    // check if the test failed or not
    public void conclude ()
    {
        int k;
        boolean pass = true;

        for ( k = 0; k < results.size(); k ++ )
        {
            result = ( resultType )results.get ( k ) ;
            String tempS = result.note ;
            if ( tempS != null ) 
            {
                if ( ( tempS.compareToIgnoreCase( "test failed" ) == 0 ) ||
                     ( tempS.compareToIgnoreCase( "failure" ) == 0 ) )
                {
                    SetNotification ( "TEST FAILED", "result" );
                    pass = false;
                    break;
                }
            }
        }
        if ( pass )
        {
            SetNotification ( "TEST PASSED", "result" );
        }
    }
	// displays the aleready accumulated statistics for a spcific button, current execution
    // into statistic file
	public void PrintBlock( String s, int number )
	{
        if ( MyConstants.statFile != null )
        {
	        try
	        {
	            MyConstants.statFile.write ( "Button : " + s + ", TEST " + number  + "\n" ) ;
        	    for ( int k = 0; k < results.size() - 1; k ++ )
        	    {
                    result = ( resultType )results.get ( k ) ;
                    String tempS = result.type ;
                    MyConstants.statFile.write ("Function '" + tempS + "'" );
                    if ( tempS.equals( "notification" ) )
                    {
                        MyConstants.statFile.write(" is '" + result.note + "'\n" );
                        if ( result.inttype >  -1 )
                        {
                            MyConstants.statFile.write(" waiting " + result.inttype + " msec" ) ;
                        }
                    }
                    if ( tempS.equals( "inc" ) || tempS.equals( "loop" ) )
                    {
                        MyConstants.statFile.write(" index is " + result.inttype ) ;
                    }
                    if ( tempS.equals( "for" ) )
                    {
                        MyConstants.statFile.write(" address " + result.note ) ;
                    }
                    if ( tempS.equals( "wait" ) )
                    {
                        MyConstants.statFile.write( " " + result.inttype + " msec") ;
                    }          
                    if ( tempS.equals( "parameter" ) )
                    {
                        MyConstants.statFile.write("=" + result.note ) ;
                    }          
                    MyConstants.statFile.write("\n");
        	    } // for
                result = ( resultType )results.get ( results.size() - 1 ) ;
                if ( result.type.equals("result") )
                    MyConstants.statFile.write(" Result : " + result.note  + "\n" );
                else System.out.println("result vector has not conclude : " + result.type ) ;
    		} // try
        	catch ( IOException e )
    		{
	    		System.out.println(" Error in writting statistics file" );
    		}
		}
	}
}
