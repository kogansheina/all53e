package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.* ;
import java.beans.*; 
/************************************************************************/
// User class
//
// Many utilities, used by the program - defined as static ( such as interface !!! )
//
/************************************************************************/
class Utils2
{
    public static boolean debug_flag = false;
/*****************************************************************/
//
//  for debugging
//
/*****************************************************************/
 private static void debug ( boolean doflag, String str )
 {
     if ( ( doflag ) || ( debug_flag ) )
     {
         System.out.println( "Utils2 : " + str ) ;
     }
 }
/*****************************************************************/
//
// evaluate an expression
//
/*****************************************************************/
 private static String calculate ( String expr )
 {
     String temp = "";
     int result = 0;
     LinkedList<Integer> nr = new LinkedList<Integer> ();
     LinkedList<String> op = new LinkedList<String> ();
     int len = 0;
     String padd = "" ;

     debug ( false, "to calculate=" + expr ) ;
     if ( expr.indexOf ('^') == -1 )
     {
         // split the string into perators and operands
     	 StringTokenizer stc = new StringTokenizer ( expr,"*/%+-!", true );
     	 while ( stc.hasMoreTokens())
     	 {
             // add the operators to 'operators list'
         	temp = stc.nextToken().trim();
         	if ( temp.equals("*") ||
          		 temp.equals("/") ||
          		 temp.equals("%") ||
          		 temp.equals("+") ||
          		 temp.equals("-") ||
                 temp.equals("!") )
         	{
             	op.add(temp) ;
         	}
         	else
         	{   // add operands to list - as numbers !!!
             	if ( len < temp.length() )
             	{
                 	len = temp.length();
             	}
             	try
             	{
                    int itemp = Integer.parseInt ( temp, 10 ) ;
                 	nr.add( new Integer ( itemp ) );
             	}
             	catch ( NumberFormatException e )
             	{     // not a number - nothing to calculae, return the input
                    System.out.println("Utils : Number exception " + temp + " : " + expr );
                	return ( expr );
             	}
         	}
     	 }
         // the order of evaluation is important
         // begin to evaluate with the not logical operator
         // uses only one operand - its LSB bit
     	 int ind = op.indexOf ("!");
     	 while ( ind != -1 )
     	 {
	        Integer op1 = ( Integer ) nr.remove(ind);
	        result =  ( ~ ( op1.intValue() & 1 ) & 1 );
	        op.remove(ind);
	        nr.add(ind, new Integer (result));
	        ind = op.indexOf ("!");
     	 }
         // permform all the first degree operations : multiply, divide and modulo
         // multiply has to be the first, in case a divide is next - otherwise
         // may receive 0 from the division ad as final result also 0
     	 ind = op.indexOf ("*");
     	 while ( ind != -1 )
     	 {
	        Integer op1 = ( Integer ) nr.remove(ind);
	        Integer op2 = ( Integer ) nr.remove(ind);
	        result = op1.intValue() * op2.intValue();
	        op.remove(ind);
	        nr.add(ind, new Integer (result));
	        ind = op.indexOf ("*");
     	 }
	     ind = op.indexOf ("/");
	     while ( ind != -1 )
	     {
	        Integer op1 = ( Integer ) nr.remove(ind);
	        Integer op2 = ( Integer ) nr.remove(ind);
	        result = op1.intValue() / op2.intValue();
	        op.remove(ind);
	        nr.add( ind, new Integer (result));
	        ind = op.indexOf ("/");
	     }
	     ind = op.indexOf ("%");
	     while ( ind != -1 )
	     {
	        Integer op1 = ( Integer ) nr.remove(ind);
	        Integer op2 = ( Integer ) nr.remove(ind);
	        result = op1.intValue() % op2.intValue();
	        op.remove(ind);
	        nr.add(ind, new Integer (result));
	        ind = op.indexOf ("%");
	     }
         // substruct must before add, because we deal only with positive
         // numbers - each pair from the list
	     ind = op.indexOf ("-");
	     while ( ind != -1 )
	     {
	        Integer op1 = ( Integer ) nr.remove(ind);
	        Integer op2 = ( Integer ) nr.remove(ind);
	        result = op1.intValue() - op2.intValue();
	        op.remove(ind);
	        nr.add(ind, new Integer (result) );
	        ind = op.indexOf ("-");
	     }
	     Integer op1 = ( Integer ) nr.removeFirst();
	     result = op1.intValue();
	     ind = op.indexOf ("+");
	     if ( ind != -1 )
	     {
	        while ( ind != -1 )
	        {
	           Integer op2 = ( Integer ) nr.removeFirst();
	           result += op2.intValue();
	           op.removeFirst();
	           ind = op.indexOf ("+");
	        }
	     }
         // padd with zero ( there are comands wich need a full string )
         Integer K = new Integer ( result );
         if ( result >= 0 )
         {
	        for ( int uu = 0; uu < len - K.toString().length(); uu ++ )
	        {
	           padd = "0" + padd ;
	        }
	        padd = padd + K.toString();
         }
         else
         {
             // in case the number is negative - no padding is needed
             padd = K.toString();
         }
 	 }
 	 else
 	 {
         // concatenate strings
     	StringTokenizer stc = new StringTokenizer ( expr,"^", false );
     	while ( stc.hasMoreTokens())
     	{
         	padd = padd + stc.nextToken() ;	 	 
 	 	}
	 }
     debug( false, "calculated=" + padd ) ;
     return ( padd );
 }
 
 /*****************************************************/
 /* used by 'inc' function - substitute the variable with the current value
   and performs the calculation inside the expression */
 // a string which begins as the variable ( at index ) may be
 // it if it ends with one of the characters from the list
 /*****************************************************/
 public static int getEnd ( String ss, int index )
 {
	 String subs = ss.substring( index, ss.length() );
	 StringTokenizer stt = new StringTokenizer ( subs," /*+-%^)",true );
     debug ( false, "subs="+subs);
	 String s = stt.nextToken();
	 return ( index + s.length() );
 }
 // check if the string at index has before it :
 // an alphanumeric character ==> it is not the incremenal variable
 // $ ==> it is a run parameter , & ==> is a button parameter
 public static int getStart ( String ss, int index )
 {
     int i = index ;
     if ( index > 0 )
     {
        i --;
        while ( Character.isLetterOrDigit( ss.charAt( i ) ) && ( i >= 0 ) )
        {
            i--;
            if ( i < 0 ) break;
        }
        if ( i < 0 ) i = 0;
          else 
          {
               if ( ( ss.charAt(i) != '$') && ( ss.charAt(i) != '&') )
               {
                   i++ ;
               }
          }
	    
     }
	 return ( i );
 }
 // used by 'inc' function
 // inc the actual value of the incrementa variable
 // vari is te variable to be found in ss and substituted by inc
 public static String substitute ( int inc, String ss, String vari )
 {
     String ret = "";
     String variable = vari.trim();

     debug ( false, "inc="+inc+" variable="+variable+" ss="+ss);
     int index = ss.indexOf( variable );
     if ( index == -1 ) return ( "" );

     int first = 0;
     while ( index != -1 )
     {
         debug ( false, "index="+index);
         int jj = getStart ( ss, index );
         int ii = getEnd ( ss, index );
         String tt ;
         if ( ii != -1 )
         {
             // according to its end, it may be THE variable
             // pick it between start and end
             tt = ss.substring( jj, ii );
             debug ( false, "inc="+tt+"#jj="+jj+" ii="+ii);
             if ( tt.equals( variable ) )
             {
                 // it is; copy the previous part of the string,
                 // replace it with the value of inc
                 ret = ret + ss.substring ( first, jj ) + String.valueOf ( inc ) + " ";
             }
             else
             {
                 // it is not
                 // copy and replace with the equivalent from pairs
                 tt = MyConstants.lookForPair ( tt.trim() );
                 ret = ret + ss.substring ( first, jj ) + tt;
             }
             // check futher
             index = ss.indexOf ( variable, ii + 1 );
             first = ii ;			
         }
         else
         {
             // it cannot be
             // take up to the end and finish the loop
             tt = ss.substring ( index, ss.length() );
             tt = MyConstants.lookForPair ( tt );
             ret = ret + ss.substring ( first, index ) + tt;
             index = -1;
             first = ss.length();			
         }
     }
     // dd the rest - if any
     if ( first < ss.length() )
     {
         ret = ret + ss.substring( first, ss.length() );
     }

     debug( false,"return ret=" + ret );
     ret = calculate ( ret ) ;
     debug(false,"return after evaluation=" + ret ) ;

     return ( ret );
  }
 /*****************************************************/
 /* performs the calculation inside the expression(s) 
 /*****************************************************/
 public static String calculateAll( String ss )
 {
     String ret = ss;

     int index = ret.indexOf('(');
     while ( index != -1 )
     {
       int ind = ret.indexOf(')');
       String expr = "";
       if ( ind != -1 )
       {
         expr = ret.substring( index + 1, ind ).trim();
       }
       if ( ! expr.equals ("") )
       {
          expr = calculate ( expr.trim()  ) ;
       }
       ret = ret.substring( 0, index ) + expr + ret.substring( ind + 1, ret.length() );
       index = ret.indexOf ( '(' );
     }
     debug(false,"return after evaluation=" + ret ) ;
     return ret;
 }
 //
 // substitute all the run parameters with their equivalent values
 //
 public static String substitute ( String ss )
 {
	String ret = "";
	
	debug(false,"string to be substituted :" + ss ) ;
    // look for a string beginnin with $
	int index = ss.indexOf( '$' );
    int format = 0 ;
	if ( index != -1 )
    {
	    int first = 0;
        // look for all variables in the string
	    while ( index != -1 )
	    {
            format = 0 ;
            // look for the end of the stringrepresenting he variable
            int ii = getEnd ( ss, index + 1 );
	    	String tt;
	    	if ( ii != -1 )
	    	{
                // found - pick it
                // replace it
	    		tt = ss.substring ( index, ii );
                if ( ( tt.charAt(1) == 'x' ) || ( tt.charAt(1) == 'X' ) )
                {
                    format = 1 ;
                    tt = "$" + tt.substring( 2 ,tt.length() );
                }
	    		tt = MyConstants.lookForPair ( tt );
                // form the string from what was before and the new value
                String KtoString = new String ( tt );
                if ( format == 1 )
                {
                    Integer K = new Integer ( tt );
                    KtoString = Integer.toString( K.intValue(),16 ) ;
                }
	    		ret = ret + ss.substring ( first, index	) + KtoString ;
                // look futher
	    		index = ss.indexOf ( '$', ii + 1 );
	    		first = ii ;			
	    	}
	    	else
	    	{
                // not found - copy the string until its end
                // begin with $ and not found, means it is the last in the string
	    		tt = ss.substring ( index, ss.length() );
                if ( ( tt.charAt(1) == 'x' ) || ( tt.charAt(1) == 'X' ) )
                {
                    format = 1 ;
                    tt = "$" + tt.substring( 2 ,tt.length() );
                }
                // replace
	    		tt = MyConstants.lookForPair ( tt );
                // form the string from what was before and the new value
                String KtoString = new String ( tt );
                if ( format == 1 )
                {
                    Integer K = new Integer ( tt );
                    KtoString = Integer.toString( K.intValue(),16 ) ;
                }
	    		ret = ret + ss.substring ( first, index	) + KtoString;
                // finish the loop
	    		index = -1;
	    		first = ss.length();			
	    	}
	    }
        // copy the rest - if any
	    if ( first < ss.length() )
	    {
	    	ret = ret + ss.substring( first, ss.length() );
	    }
    }
	else
    {  // there is no variable - return the original
        ret = ss ;
    }
	debug(false,"return ret=" + ret );
    
	return ( ret );
 }
}  // end Utils class

