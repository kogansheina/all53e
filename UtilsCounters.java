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
import java.beans.*; 
/************************************************************************/
// User class
//
// Many utilities, used by the program - defined as static ( such as interface !!! )
//
/************************************************************************/
class UtilsCounters
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
         System.out.println( "UtilsCounters : " + str ) ;
     }
 }
/************************************************************************/
//
// Print the set of drawing options for the current drawing session - per device
//
/************************************************************************/
  public static void PrintOptions ( String title, int device, int x, int y ) 
  {
      StyleContext sc;
      Style sb,ss ;
      DefaultStyledDocument doc;

      JFrame frame = new JFrame ( title );
      frame.setBackground ( Color.lightGray ) ;
      frame.setSize ( 10, 50 ) ;
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
      frame.getContentPane().add ( scrollPane );
      frame.setVisible ( false);
      try
      {
        doc.remove( 0, doc.getLength() );
        drawParameters countersParametrs =  MyConstantsCounters.dDB[device].countersParametrs ;
        if ( countersParametrs != null )
        {
            doc.insertString( doc.getLength(), " Screen : " + countersParametrs.max_x + " " + countersParametrs.max_y + "\n", ss );
            doc.insertString ( doc.getLength()," Options : \n", ss );
            doc.insertString ( doc.getLength()," report : " + countersParametrs.report + "\n", ss );
            doc.insertString ( doc.getLength()," save : " + countersParametrs.save + "\n", ss );
            doc.insertString ( doc.getLength()," all in one : " + countersParametrs.all_in_one + "\n", ss );
            doc.insertString ( doc.getLength()," fix form : " + countersParametrs.fixForm + "\n", ss );
            doc.insertString ( doc.getLength()," store form : " + countersParametrs.storeForm + "\n", ss );
            doc.insertString ( doc.getLength()," stop form : " + countersParametrs.stopForm + "\n", ss );
            doc.insertString ( doc.getLength()," report file : " + countersParametrs.report_file + "\n", ss );
            doc.insertString ( doc.getLength()," save file : " + countersParametrs.save_file + "\n", ss );
            doc.insertString ( doc.getLength()," read time : " + countersParametrs.readTime + " seconds\n", ss );
            doc.insertString ( doc.getLength()," print time : " + countersParametrs.printTime + " seconds\n", ss );
            doc.insertString ( doc.getLength()," Counters : \n", ss );
            for ( int k =0; k < countersParametrs.graphs.size(); k ++ )
            {
                Counter temp = ( Counter ) countersParametrs.graphs.get ( k );
                doc.insertString ( doc.getLength(), MyConstantsCounters.gs_title [ temp.CounterModule() ] + " " + MyConstantsCounters.titles [ temp.CounterModule() ][ temp.CounterId()].trim() + "\n", ss );
            }
        }
        else doc.insertString ( doc.getLength()," No options defined", ss );
      } // try
      catch ( BadLocationException e )
      {
          System.out.println( "Internal error: " + e );
      }
      frame.setLocation ( x, y );
      frame.pack();
      frame.setVisible ( true );
  }

/////////////////////////////////////////////////////////////////////////
//
// return a Filter class after looking for a convient stream token
// into the given text ( option )
//
/////////////////////////////////////////////////////////////////////////
  public static boolean getOptionsPerModule( int index, String input, Vector<Counter> v, int onu )
  {
      String str ;
      boolean error = false ;

      str = input ;
      str += "," ;
      // look for all tokens separated by comma
      StringTokenizer stc = new StringTokenizer ( str, ",", false ) ;
      while ( stc.hasMoreTokens() && ! error )
      {
          error =  stringCounters ( stc.nextToken(), index, v, onu ) ;
      }

      return error ;
 }
/////////////////////////////////////////////////////////////////////////
//
//   check and convert the counter number in filters
//
/////////////////////////////////////////////////////////////////////////
 private static boolean  stringCounters ( String st, int index, Vector<Counter> v, int onu )
 {
     boolean error = false ;
     Counter ff = null ;
     String strc;
     int tcontfactor = my_java.tcontfactor;
    
    strc = st.trim();
    if ( strc != "" )
    {
        if ( strc.indexOf( '-') == -1 )
        {
            switch ( onu )
            {
            case -1: // default
                int val = Integer.parseInt ( strc );
                if ( ( index == my_java.RXFIFO ) || ( index == my_java.TXFIFO ) )
                {
                    ff = new Counter ( index, val * 2 ) ;
                    ff.CounterSet();
                    v.add( ff );
                    ff = new Counter ( index, val * 2 + 1 ) ;
                    ff.CounterSet();
                    v.add( ff );
                }
                else
                {
                    ff = new Counter ( index, val ) ;
                    ff.CounterSet();
                    v.add( ff );
                    debug( false,  "add filter ( not range )" + index + " rest : " +  val ) ;
                }
                break;

            case 0: // olt tx
                val = Integer.parseInt ( strc );
                ff = new Counter ( my_java.OLTTX, val ) ;
                ff.CounterSet();
                v.add( ff );
                debug( false,  "add filter " + index + " rest : " +  val ) ;
                break;

            case MyConstantsCounters.Tcont: // olt rx

				val = Integer.parseInt ( strc );
                for ( int r = 0; r < tcontfactor; r ++ )
                {
                    int q = val * tcontfactor + r;
                    ff = new Counter ( my_java.OLTRX, q ) ;
                    ff.CounterSet();
                    v.add( ff );
                    debug( false, "add filter ( not range )" + index + " rest : " + q ) ;
                }
                break;
            }
        }
        else
        {
            StringTokenizer stc = new StringTokenizer ( strc, "-", false ) ;
            while ( stc.hasMoreTokens() && ! error )
            {
                String ss = stc.nextToken().trim() ;
                if ( ss != "" )
                {
                    if ( stringIsNumeric ( ss ) )
                    {
                        int l2;
                        int l1 = Integer.parseInt ( ss );
                        ss = stc.nextToken().trim() ;
                        if ( ss != "" )
                        {
                            if ( stringIsNumeric ( ss ) )
                            {
                                l2 = Integer.parseInt ( ss );
                                for ( int k = l1; k <= l2; k++ )
                                {
                                    switch ( onu )
                                    {
                                    case -1:
										int val = k;
                                        if ( ( index == my_java.RXFIFO ) || ( index == my_java.TXFIFO ) )
                                        {
                                            ff = new Counter ( index, val * 2 ) ;
                                            ff.CounterSet();
                                            v.add( ff );
                                            ff = new Counter ( index, val * 2 + 1 ) ;
                                            ff.CounterSet();
                                            v.add( ff );
                                        }
                                        else
                                        {
                                            ff = new Counter ( index, val ) ;
                                            ff.CounterSet();
                                            v.add( ff );
                                            debug( false, "add filter " + index + " rest : " +  val ) ;
                                        }
                                        break;

                                    case 0:
										val = k;
                                        ff = new Counter ( my_java.OLTTX, val ) ;
                                        ff.CounterSet();
                                        v.add( ff );
                                        debug( false, "add filter " + index + " rest : " +  val ) ;
                                        break;

                                    case MyConstantsCounters.Tcont:
										val = k * tcontfactor ;
                                        for ( int r = 0; r < tcontfactor; r ++ )
                                        {
                                            int q = val + r;
                                            ff = new Counter ( my_java.OLTRX, q ) ;
                                            ff.CounterSet();
                                            v.add( ff );
                                            debug( false, "add filter " + index + " rest : " +  val ) ;
                                        }
                                        break;
									}
                                }
                            }
                            else
                            {
                                error = true ;
                                debug ( true, "Error in counter number " + ss ) ;
                            }
                        }
                        else
                        {
                            error = true ;
                            debug ( true, "Error in counter number " + ss ) ;
                        }
                    }
                    else
                    {
                        error = true ;
                        debug ( true, "Error in counter number " + ss ) ;
                    }
                }
                else
                {
                    error = true ;
                    debug ( true,"Error in counter number " + strc ) ;
                }
            }
        }
    }

    return error ;
 }
 public static String setCOptions ( String dd )
 {
     String ret = "";
     int device = Integer.parseInt ( dd );
     MyConstantsCounters.dDB[device].countersParametrs.clear();
     String s = choose_file( "Choose report file" );
     if ( ! s.equals ("") )
     {
         MyConstantsCounters.dDB[device].countersParametrs.defined = true ;
         MyConstantsCounters.dDB[device].countersParametrs.report = true ;
         MyConstantsCounters.dDB[device].countersParametrs.report_file = s;
     }
     s = choose_file( "Choose save file" );
     if ( ! s.equals ("") )
     {
         MyConstantsCounters.dDB[device].countersParametrs.defined = true ;
         MyConstantsCounters.dDB[device].countersParametrs.save = true ;
         MyConstantsCounters.dDB[device].countersParametrs.save_file = s;
     }
     if ( MyConstantsCounters.dDB[device].countersParametrs.defined )
     {
        ret = " options ";
        if ( MyConstantsCounters.dDB[device].countersParametrs.report )
        {
            ret = ret + "report " + MyConstantsCounters.dDB[device].countersParametrs.report_file + " ";
        }
        if ( MyConstantsCounters.dDB[device].countersParametrs.save )
        {
            ret = ret + "save " + MyConstantsCounters.dDB[device].countersParametrs.save_file + " " ;
        }
     }
     else
     {
         ret = ret + "stop" ;
     }
     return ret;
 }
 public static String setFilters ( String t, int function )
 {
     String s1 = "";
     String s = "";
     String modulesg [] = { "tx", "rx", "utopia", "txfifo", "rxfifo", "sys_fpga" };

     String toreturn = "";
     s = dialog ( t, modulesg, "" ); // options for graphs
     if ( ( s != null ) && ( s.length() > 0 ) )
     {
         toreturn = toreturn + s;
         // ask for filters for a specified module
         if ( s.equals ( "tx" ) )
         {
             s1 = dialog ( s, MyConstantsCounters.onuolt, "" );
             if ( ( s1 != null ) && ( s1.length() > 0 ) )
             {
                 toreturn = toreturn + " " + s1;
                 if ( s1.equals ( "onu" ) )
                 {
                     if ( function == MyConstants.getFunction )
                     {
                         s = dialog ( "onu counters:", MyConstantsCounters.onu_tx, "all" );
                         if ( ( s != null ) && ( s.length() > 0 ) )
                         {
                             if ( s.equals ( "Grant" ) )
                             {
                                 s1 = dialog ( "grant number", null, "" );
                                 if ( ( s1 != null ) && ( s1.length() > 0 ) )
                                 {
                                     toreturn = toreturn + " " + s + " " + s1;
                                 }
                             }
                             else
                             {
                                 toreturn = toreturn + " " + s;
                             }
                         }
                     }
                     else
                     {
                         toreturn = toreturn + " " + "all";
                     }
                 }
                 else
                 {
                     if ( function == MyConstants.getFunction )
                     {
                         s = dialog ( "olt counters:", MyConstantsCounters.olt_tx, "all" );
                         if ( ( s != null ) && ( s.length() > 0 ) )
                         {
                             toreturn = toreturn + " " + s;
                         }
                     }
                     else
                     {
                         toreturn = toreturn + " " + "all";
                     }
                 }
             }
         }
         if ( s.equals ( "rx" ) )
         {
             s1 = dialog ( s, MyConstantsCounters.onuolt, "" );
             if ( ( s1 != null ) && ( s1.length() > 0 ) )
             {
                 toreturn = toreturn + " " + s1;
                 if ( s1.equals ( "onu" ) )
                 {
                     if ( function == MyConstants.getFunction )
                     {
                         s = dialog ( "onu counters:", MyConstantsCounters.onu_rx, "all" );
                         if ( ( s != null ) && ( s.length() > 0 ) )
                         {
                             toreturn = toreturn + " " + s;
                         }
                     }
                     else
                     {
                         toreturn = toreturn + " " + "all";
                     }
                 }
                 else
                 {
                     if ( function == MyConstants.getFunction )
                     {
                         s = dialog ( "olt counters:", MyConstantsCounters.olt_rx, "all" );
                         if ( ( s != null ) && ( s.length() > 0 ) )
                         {
                             if ( s.equals ( "Grant" ) )
                             {
                                 s1 = dialog ( "grant number", null, "" );
                                 if ( ( s1 != null ) && ( s1.length() > 0 ) )
                                 {
                                     toreturn = toreturn + " " + s + " " + s1;
                                 }
                             }
                             else
                             {
                                 if ( s.equals ( "Onu" ) )
                                 {
                                     s1 = dialog ( "onu number", null, "" );
                                     if ( ( s1 != null ) && ( s1.length() > 0 ) )
                                     {
                                         toreturn = toreturn + " " + s + " " + s1;
                                     }
                                 }
                                 else
                                 {
                                     toreturn = toreturn + " " + s;
                                 }
                             }
                         }
                     }
                     else
                     {
                         toreturn = toreturn + " " + "all";
                     }
                 }
             }
         }
         if ( s.equals ( "utopia" ) )
         {
             if ( function == MyConstants.getFunction )
             {
                 s = dialog ( "phy number", null, "" );
                 if ( ( s != null ) && ( s.length() > 0 ) )
                 {
                     toreturn = toreturn + " " + s;
                 }
             }
             else
             {
                 toreturn = toreturn + " " + "all";
             }
         }
         if ( s.equals ( "txfifo" ) || s.equals ( "rxfifo" ) )
         {
             if ( function == MyConstants.getFunction )
             {
                 s = dialog ( "queue number", null, "" );
                 if ( ( s != null ) && ( s.length() > 0 ) )
                 {
                     toreturn = toreturn + " " + s;
                 }
             }
             else
             {
                 toreturn = toreturn + " " + "all";
             }
         }
         if ( s.equals ( "sys_fpga" ) )
         {
             if ( function == MyConstants.getFunction )
             {
                 s = dialog ( s, MyConstantsCounters.sys_fpga_option, "all" );
                 if ( ( s != null ) && ( s.length() > 0 ) )
                 {
                     toreturn = toreturn + " " + s;
                 }
             }
             else
             {
                 toreturn = toreturn + " " + "all";
             }
         }
     }
     return toreturn;
}

 private static String dialog ( String s, String [] options , String defaultoption )
 {
     String sret = (String)JOptionPane.showInputDialog( new JFrame(), s, s,
                     JOptionPane.PLAIN_MESSAGE, null, options, defaultoption );
     return sret;
 }
/*****************************************************************/
//
// check if the string represents a number
//
/*****************************************************************/
 public static boolean stringIsNumeric ( String str )
 {
     boolean isNumeric = true ;
     for ( int k = 0; ( k < str.length() ) && isNumeric; k ++ )
     {
         if ( ! Character.isDigit ( str.charAt ( k ) ) ) isNumeric = false ;
     }
     return isNumeric ;
 }
/************************************************************************/
//
// choose a file for an purpose
//
/************************************************************************/
    private static String choose_file( String title )
    {
        FileDialog selectfile = new FileDialog ( new Frame(), title );
    	selectfile.setVisible(true);
        String str = selectfile.getDirectory() + selectfile.getFile();
        if ( str.endsWith( "null" ) ) return "";
        return ( str );

    } // choose file
/************************************************************************/
//
//  execute the the counters definition for "display" function
//
/************************************************************************/
 public static drawParameters execFilters ( StringTokenizer stc, drawParameters InputCountersParametrs )
 {
 drawParameters countersParametrs = new drawParameters ( InputCountersParametrs ) ;
 int nc = stc.countTokens();
 int module = -1;
 String m = "";
 if ( nc > 0 ) 
 {
     if ( ! countersParametrs.storeForm )
     {
     m = stc.nextToken(); // module
     if ( m.equals( "tx" ) )
     {
         module = my_java.TX;
     }
     if ( m.equals( "rx" ) )
     {
         module = my_java.RX;
     }
     if ( m.equals( "rxfifo" ) )
     {
         module = my_java.RXFIFO;
     }
     if ( m.equals( "txfifo" ) )
     {
         module = my_java.TXFIFO;
     }
     if ( m.equals( "utopia" ) )
     {
         module = my_java.UTOPIA;
     }
     if ( m.equals( "sys_fpga" ) )
     {
         module = my_java.FPGA;
     }
     String filterest = "";
     switch ( module )
     {
     case my_java.TX:
         if ( nc > 2 )
         {
             String u = stc.nextToken();
             if ( ( countersParametrs.fixForm ) || ( ! countersParametrs.fixForm && ! countersParametrs.all_in_one ) )
             {
                 if ( u.equals( "onu" ) )
                 {
                     Counter ff = new Counter ( module, -1 ) ;
                     ff.CounterSet();
                     countersParametrs.graphs.add( ff );
                 }
                 else
                 {
                     Counter ff = new Counter ( my_java.OLTTX, -1 ) ;
                     countersParametrs.graphs.add( ff );
                 }
                 stc.nextToken();  // stays for 'all'
             }
             else
             {
                if ( u.equals( "onu" ) )
                {
                    if ( stc.nextToken().equals( MyConstantsCounters.onu_tx [ 0 ] ) )
                    { // grant
                        while ( stc.hasMoreTokens() )
                        {
                            filterest = filterest + stc.nextToken() ;
                        }
                        countersParametrs.draw_error =
                        UtilsCounters.getOptionsPerModule( my_java.TX, filterest, countersParametrs.graphs, -1 ) ;
                    }
                    else
                    {
                       int val = -1;
                       int y = 0;
                       String mine = stc.nextToken();
                       for ( y = 1; y < MyConstantsCounters.onu_tx.length; y ++ )
                       {
                          if ( mine.equals ( MyConstantsCounters.onu_tx [ y ] ) )
                          {
                             val = 15 + y;
                             break;
                          }
                       }
                       if ( y == MyConstantsCounters.onu_tx.length )
                       {
                           countersParametrs.draw_error = true;
                       }
                       else
                       {
                           Counter ff = new Counter ( module, val ) ;
                           ff.CounterSet();
                           countersParametrs.graphs.add( ff );
                       }
                    }
                }  // onu
                else
                {
                    if ( u.equals( "olt" ) )
                    {
                        int val = -1;
                        int y = 0;
                        String mine = stc.nextToken();
                        for ( y = 1; y < MyConstantsCounters.olt_tx.length; y ++ )
                        {
                           if ( mine.equals ( MyConstantsCounters.olt_tx [ y ] ) )
                           {
                              val = y;
                              break;
                           }
                        }
                        if ( y == MyConstantsCounters.olt_tx.length )
                        {
                            countersParametrs.draw_error = true;
                        }
                        else
                        {
                            Counter ff = new Counter ( my_java.OLTTX, val ) ;
                            ff.CounterSet();
                            countersParametrs.graphs.add( ff );
                        }
                    } // olt
                    else
                    {
                        countersParametrs.draw_error = true;
                    }
                }
             } // all in one
         }
         else
         {
             countersParametrs.draw_error = true;
         }
         break;

     case my_java.RX:
         if ( nc > 2 )
         {
             String u = stc.nextToken();
             if ( ( countersParametrs.fixForm ) || ( ! countersParametrs.fixForm && ! countersParametrs.all_in_one ) )
             {
                 if ( u.equals( "onu" ) )
                 {
                     Counter ff = new Counter ( module, -1 ) ;
                     countersParametrs.graphs.add( ff );
                 }
                 else
                 {
                     Counter ff = new Counter ( my_java.OLTRX, -1 ) ;
                     countersParametrs.graphs.add( ff );
                 }
                 stc.nextToken();  // stays for 'all'
             }
             else
             {   // get function
                if ( u.equals( "onu" ) )
                {
                    int val = -1;
                    int y = 0;
                    String mine = stc.nextToken();
                    for ( y = 0; y < MyConstantsCounters.onu_rx.length; y ++ )
                    {
                       if ( mine.equals ( MyConstantsCounters.onu_rx [ y ] ) )
                       {
                          val = y;
                          break;
                       }
                    }
                    if ( y == MyConstantsCounters.onu_rx.length )
                    {
                        countersParametrs.draw_error = true;
                    }
                    else
                    {
                        Counter ff = new Counter ( module, val ) ;
                        ff.CounterSet();
                        countersParametrs.graphs.add( ff );
                    }
                }  // onu
                else
                {
                    if ( u.equals( "olt" ) )
                    {
                        String mc = stc.nextToken();
                        if ( mc.equals( MyConstantsCounters.olt_rx [ 0 ] ) )
                        { // Tcont
                            while ( stc.hasMoreTokens() )
                            {
                                filterest = filterest + stc.nextToken() ;
                            }
                            countersParametrs.draw_error =
                            UtilsCounters.getOptionsPerModule( my_java.OLTRX, filterest,
                              countersParametrs.graphs, MyConstantsCounters.Tcont ) ;
                        }
                        else
                        {
                            if ( mc.equals( MyConstantsCounters.olt_rx [ 1 ] ) )
                            { // Invalid
                                Counter ff = new Counter ( my_java.OLTRX, my_java.MaxTconts * 3 ) ;
                                ff.CounterSet();
                                countersParametrs.graphs.add( ff );
                            }
                            else
                            {
                                if ( mc.equals( MyConstantsCounters.olt_rx [ 2 ] ) )
                                { // ranging
                                    Counter ff = new Counter ( my_java.OLTRX, my_java.MaxTconts * 3 + 1 ) ;
                                    ff.CounterSet();
                                    countersParametrs.graphs.add( ff );
                                    ff = new Counter ( my_java.OLTRX, my_java.MaxTconts * 3 + 2 ) ;
                                    ff.CounterSet();
                                    countersParametrs.graphs.add( ff );
                                }
                                else
                                {
                                    countersParametrs.draw_error = true;
                                }
                            }
                        }
                    }  // olt
                    else
                    {
                        countersParametrs.draw_error = true;
                    }
             }
             }  // get function
         }  // nc > 2
         else
         {
             countersParametrs.draw_error = true;
         }
         break;

     case my_java.UTOPIA:
     case my_java.RXFIFO:
     case my_java.TXFIFO:
         if ( ( countersParametrs.fixForm ) || ( ! countersParametrs.fixForm && ! countersParametrs.all_in_one ) )
         {
            Counter ff = new Counter ( module, -1 ) ;
            countersParametrs.graphs.add( ff );
            stc.nextToken();  // stays for 'all'
         }
         else
         {
             countersParametrs.draw_error =
                 UtilsCounters.getOptionsPerModule( module, stc.nextToken(), countersParametrs.graphs, -1 ) ;
         }
         break;

     case my_java.FPGA:
         if ( ( countersParametrs.fixForm ) || ( ! countersParametrs.fixForm && ! countersParametrs.all_in_one ) )
         {
            for ( int y = 0; y < 3; y ++ )
            {
                Counter ff = new Counter ( module, 2 * y ) ;
                ff.CounterSet();
                countersParametrs.graphs.add( ff );
                if ( y != 2 )
                {
                    ff = new Counter ( module, 2 * y + 1 ) ;
                    ff.CounterSet();
                    countersParametrs.graphs.add( ff );
                }
            }
            stc.nextToken();  // stays for 'all'
         }
         else
         {
            int val = -1;
            int y = 0;
            String mine = stc.nextToken();
            if ( mine.equals ( "all" ) )
            {
                for ( y = 0; y < 3; y ++ )
                {
                    Counter ff = new Counter ( module, 2 * y ) ;
                    ff.CounterSet();
                    countersParametrs.graphs.add( ff );
                    if ( y != 2 )
                    {
                        ff = new Counter ( module, 2 * y + 1 ) ;
                        ff.CounterSet();
                        countersParametrs.graphs.add( ff );
                    }
                }
            }
            else
            {
               for ( y = 1; y < MyConstantsCounters.sys_fpga_option.length; y ++ )
               {
                   if ( mine.equals ( MyConstantsCounters.sys_fpga_option [ y ] ) )
                   {
                      val = y - 1;
                      break;
                   }
               }
               if ( y == MyConstantsCounters.sys_fpga_option.length )
               {
                   countersParametrs.draw_error = true;
               }
               else
               {
                   Counter ff = new Counter ( module, val ) ;
                   ff.CounterSet();
                   countersParametrs.graphs.add( ff );
                   if ( val < 2 )
                   {
                       ff = new Counter ( module, val + 1 ) ;
                       ff.CounterSet();
                       countersParametrs.graphs.add( ff );
                   }
               }
            }
         }
         break;

     default:
//         countersParametrs.draw_error = true;
         break;
    }
    } // not store
    if ( stc.hasMoreTokens() )
    {
        String oo;
        if ( ( ! countersParametrs.storeForm ) && ( ! countersParametrs.all_in_one ) && ( ! countersParametrs.fixForm ) )
        {
            oo = m;
        }
        else
        {
            oo = stc.nextToken();
        }
        if ( oo.equals("options") )
        {
           while ( stc.hasMoreTokens() )
           {
                String opt = stc.nextToken();
                if ( opt.equals("report") )
                {
                   countersParametrs.report = true ; 
                   String temp = setOpt ( stc );
                   if ( temp.equals("") )
                   {
                        countersParametrs.draw_error = true;
                   }
                   else
                   {
                       countersParametrs.report_file = Utils2.substitute ( temp );
                       countersParametrs.report_file = Utils2.calculateAll ( countersParametrs.report_file );
                   }
                }
                if ( opt.equals("save") )
                {
                   countersParametrs.save = true ; 
                   String temp = setOpt ( stc );
                   if ( temp.equals("") )
                   {
                        countersParametrs.draw_error = true;
                   }
                   else
                   {
                       countersParametrs.save_file = Utils2.substitute ( temp );
                       countersParametrs.save_file = Utils2.calculateAll ( countersParametrs.save_file );
                   }
                }
            }
        }
        else
        {
            if ( oo.equals("stop") )
            {
                countersParametrs.stopForm = true;
            }
            else
            {
                countersParametrs.draw_error = true;
            }
        }
    }
 }
 return countersParametrs;
 }
 // look for drawing functions options : save/report/stop
 private static String setOpt ( StringTokenizer stc )
 {
    String strOpt = "";
    
    if ( stc.hasMoreTokens() )
    {
        strOpt = stc.nextToken().trim();
        debug(false,"option="+strOpt );
        if ( strOpt.equals( "(" ) )
        {
            if ( stc.hasMoreTokens() )
            {
                strOpt = strOpt + stc.nextToken().trim();
               if ( ! strOpt.endsWith( ")" ) )
                {
                    if ( stc.hasMoreTokens() )
                    {
                        strOpt = strOpt + stc.nextToken().trim();
                        if ( ! strOpt.endsWith( ")" ) )
                        {
                           strOpt = ""; 
                        }
                    }
                    else
                    {
	                    strOpt = "";
					}
                }
            }
            else 
            {
                strOpt = ""; 
            }
        }
        else
        {
             if ( ! strOpt.endsWith( ")" ) )
             {
                 if ( stc.hasMoreTokens() )
                 {
                     strOpt = strOpt + stc.nextToken().trim();
                 }
                 debug(false,"option="+strOpt );
             }
        }
    }
    return ( strOpt );
}
}

