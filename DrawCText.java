package all53e;
import java.io.* ;
import java.lang.* ;
import java.lang.Thread.* ;
import java.awt.* ;
import java.awt.event.* ;
import java.util.* ;

class DrawCText extends Frame implements  ActionListener
{
 static final long serialVersionUID = 108L;

 public ReadSocket [] caller;

 private boolean allinone;
 private int allindex = 0;
 private int begin_y = MyConstantsCounters.begin_yA;
 private static int delta_x = MyConstantsCounters.deltax;
 private static int delta_y = MyConstantsCounters.deltay;
 private int stop = 0 ;
 private Button start_button = new Button ( "start" ) ;
 private Button stop_button = new Button ( "suspend" ) ;
 private Button continue_button = new Button ( "resume" ) ;
 private Button once_button = new Button ( "once" ) ;
 private String name ;
 private int max_x ;
 private int max_y ;
 private int active ;
 private int start_x ;
 private int start_y;
 private int type;
 private int fixmod;
 private int prevj,prevjOld,cx;
 private int tcontfactor = my_java.tcontfactor;
 private int begin_s = MyConstantsCounters.begin_s ;
 private int [] x;
 private DrawCText dt = this;
 private int device;
 private String title ;
 private int xx [] = new int [ 8 ];
 private int yy [] = new int [ 8 ];

/////////////////////////////////////////////////////////////////////////
//
//   construct the frame, receive the parameters, add window actions
//
/////////////////////////////////////////////////////////////////////////
 public DrawCText( String titleO, int fixmod, int screen_x, int screen_y, ReadSocket obj, int type )
 {
     super ( titleO ) ;
     this.title = titleO ;
     this.type = type;
     this.fixmod = fixmod;
     StringTokenizer stc = new StringTokenizer ( titleO, "@", false ) ;
     // extract the window name : IP or file name
     String file_separator = MyConstants.SP.getProperty( "file.separator" ) ;
     title = stc.nextToken();
     int ii = title.lastIndexOf ( file_separator ) ;
     if ( ii != -1 )
     {
         title = title.substring( ii + 1, title.length() ); 
     }
     // extract device
     StringTokenizer stt = new StringTokenizer ( stc.nextToken(), " ", false ) ;
	 device = Integer.parseInt( stt.nextToken() ) ;
     setResizable( false );
     max_x = screen_x ;
     max_y = screen_y ;
     allindex = 0;
     allinone = false;
     setLayout ( new FlowLayout ( ) ) ;
     enableEvents ( AWTEvent.WINDOW_EVENT_MASK ) ;
     delta_x += MyConstantsCounters.deltax ;
     delta_y += MyConstantsCounters.deltay ;
     add( start_button ) ;
     start_button.addActionListener ( this ) ;
     start_button.setEnabled ( true ) ;
     start_button.setBackground( Color.green ) ;
     add( stop_button ) ;
     add( continue_button ) ;
     stop_button.addActionListener ( this ) ;
     continue_button.addActionListener ( this ) ;
     stop_button.setEnabled ( true ) ;
     stop_button.setBackground( Color.yellow ) ;
     continue_button.setBackground( Color.gray ) ;
     add( once_button ) ;
     once_button.setEnabled ( true ) ;
     once_button.addActionListener ( this ) ;
     once_button.setBackground( Color.green ) ;
     pack() ;
     setSize ( max_x, max_y ) ;
     setLocation ( delta_x, delta_y ) ;
     setVisible ( true ) ;
     setBackground ( MyConstants.background ) ;

     addWindowListener ( DrawWindowA ( ) ) ;

     // set parent type and pointer
     // set beginning coordinates
     switch ( type )
     {
     case MyConstantsCounters.OnePackOnWindow: 
         // only one IP in window
         caller = new ReadSocket [ 1 ];
         break;
     case MyConstantsCounters.OneCounterOnWindow:
         // any combination of counters
         // create thread for all all possible connections
         allinone = true;
         start_x = 0;
         start_y = begin_y;
         caller = new ReadSocket [ MyConstants.ips ];
         break;
     case MyConstantsCounters.OneModuleOnWindow:
         // all packs display the all the counters of the same kind of module
         // into the same window
         // set beginning coordinates
         start_x = MyConstantsCounters.begin_xA;
         start_y = begin_y + 30;
         // create thread for all all possible connections
         caller = new ReadSocket [ MyConstants.ips ];
         // create an array of x coordinate for all possible lines
         x = new int [ 32 ];
         Graphics g = getGraphics();
         Font f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s + 4 ) ;
         g.setFont ( f );
         FontMetrics fm = g.getFontMetrics( f );
         g.setColor ( Color.black ) ;
         String tt = "onu_32 :       ";
         x[ 0 ] = MyConstantsCounters.begin_xA + fm.stringWidth( tt );
         int l, factor = 1;
         // factor = the number of columns ( counters )
         switch ( fixmod )
         {
         case my_java.FPGA:
             factor = 3;
             break;
         case my_java.TX:
         case my_java.RXFIFO:
         case my_java.TXFIFO:
             factor = 16;
             break;
         case my_java.UTOPIA:
             if ( title.startsWith("olt") ) factor = 8;
               else factor = 16;
             break;
         case my_java.RX:
             factor = 6;
             break;
         case my_java.OLTTX:
             factor = 2;
             break;
         case my_java.OLTRX:
             // factor is number of super columns
             factor = 4;
             tt = "grant255 : ";
             x[ 0 ] = MyConstantsCounters.begin_xA + fm.stringWidth( tt );
             l = max_x/4 ;
             // each super column has 3 columns
             // write the title of all columns
             for ( int z = 0; z < 3; z ++ )
             {
                 x [ z + 1 ] = x[ z ] + l;
                 g.drawString ( "Valid / Average ", x[ z ] - fm.stringWidth( tt ), MyConstantsCounters.begin_yA - 10 );
             }
             g.drawString ( "Valid / Average ", x[ 3 ] - fm.stringWidth( tt ) , MyConstantsCounters.begin_yA - 10 );
             break;
         }
         // for the other cases, write the titles
         if ( fixmod != my_java.OLTRX )
         {
            l = ( max_x - fm.stringWidth( tt ) ) / factor;
            g.drawString ( MyConstantsCounters.fix_titles [ fixmod ] [ 0 ].trim(), x[ 0 ] , MyConstantsCounters.begin_yA - 10 );
            for ( int z = 1; z < factor; z ++ )
            {
                x[ z ] = x[ z - 1 ] + l;
                g.drawString ( MyConstantsCounters.fix_titles [ fixmod ] [ z ].trim(), x[z] , MyConstantsCounters.begin_yA - 10 );
            }
         }
         break;
     }
     AddCaller ( obj );
 }
 // add the thread ( IP ) to use this frame ( window )
 public void AddCaller ( ReadSocket obj )
 {
     caller [ allindex ++ ] = obj;
     // if 'start' was pressed - send start command to this new user
     if ( ! start_button.isEnabled () )
     {
         obj.Freeze ( my_java.START, device );
     }
     // if 'stop' was pressed - send stop command to this new user
     if ( ! stop_button.isEnabled () )
     {
         obj.Freeze ( my_java.SUSPEND, device );
     }
 }
 //
 // set the coordinates for a new counters
 //
 public xypoint SetDraw ( boolean fix, int module, int type )
 {
     if ( ! fix )
     {
        // all in one - next free place
        // increment x - to be onthe same line
        // until the possible end of the line and
        // then put x at the beginning and increment y
        if ( module == my_java.FPGA )
        {
           // special case : - 2 counters of 40 bits 
           // counter 0 and 1 ==> one displayed counter 
           // counter 2 and 3 ==> one displayed counter
           switch ( type )
           {
           case 0:
           case 2:
           case 4:
               if ( start_x == 0 )
               {
                   start_x = MyConstantsCounters.begin_xA;
               }
               else
               {
                  start_x = start_x + MyConstantsCounters.next_x;
                  if ( start_x > max_x - MyConstantsCounters.next_x )
                  {
                      start_x = MyConstantsCounters.begin_xA;
                      start_y = start_y + MyConstantsCounters.next_y;
                      if ( start_y > max_y - MyConstantsCounters.next_y ) start_y = -1;
                  }
               }
               break;
           case 1:
           case 3:
               break;
           }
        }
        else
        {
            if ( start_x == 0 ) start_x = MyConstantsCounters.begin_xA;
            else
            {
                start_x = start_x + MyConstantsCounters.next_x ;
                if ( start_x > max_x - MyConstantsCounters.next_x )
                {
                    start_x = MyConstantsCounters.begin_xA;
                    start_y = start_y + MyConstantsCounters.next_y;
                    if ( start_y > max_y - MyConstantsCounters.next_y ) start_y = -1;
                }
            }
        }
     }
     else
     {
         // for fix form, only y will change 
         // x is fix and was previously calculated
         start_y = start_y + MyConstantsCounters.next_y;
         if ( start_y > max_y - MyConstantsCounters.next_y ) start_y = -1;
     }

     xypoint point = new xypoint ( start_x, start_y );

     return point;
 }
 //
 // set the coordinates for a new module
 // split the frame according to the pack type !!!
 //
 public xypoint SetDraw ( int module )
 {
    int module_id = module;
    start_y = MyConstantsCounters.begin_y ;
    start_x = 70;
    if ( module > my_java.END ) module_id = module - my_java.END - 1;
    if ( title.startsWith("olt") )
    {
        start_x = start_x + ( int )( MyConstantsCounters.x_countolt [ module_id ] * ( double )max_x );
        start_y = start_y + ( int )( MyConstantsCounters.y_countolt [ module_id ] * ( double )max_y );
    }
    else
    {
        start_y = start_y + ( int )( MyConstantsCounters.y_count [ module_id ] * ( double )max_y );
        start_x = start_x + ( int )( MyConstantsCounters.x_count [ module_id ] * ( double )max_x );
    }
    xypoint point = new xypoint ( start_x, start_y );

    return point;
}

 private WindowAdapter DrawWindowA ( )
 {
    WindowAdapter WA = new WindowAdapter ( )
    {
 /////////////////////////////////////////////////////////////////////////
 //
 //   when a frame is closed - must close all the open filed of this thread
 //   and have to inform the main thread to remove it from its list
 //   this will permit to open it again in other codintions
 //
 /////////////////////////////////////////////////////////////////////////
        public void windowClosing ( WindowEvent e )
        {
            ReadSocket rf = null;

            switch ( type )
            {
             case MyConstantsCounters.OnePackOnWindow:
                 rf = caller[ 0 ] ;
                 if ( rf != null )
                 {
                     rf.Index = -1;
                     rf.notify_main( dt ) ;
                 }
                 MyConstantsCounters.dDB[device].EachOne [ fixmod ] = null;
                break;
             case MyConstantsCounters.OneCounterOnWindow:
                for ( int j = 0; j < allindex ; j ++ )
                {
                    rf = caller[ j ] ;
                    if ( rf != null )
                        rf.notify_main( dt );
                }
                allindex = 0;
                MyConstantsCounters.dDB[device].allIn = false;
                MyConstantsCounters.dDB[device].AllInOne = null;
                break;
             case MyConstantsCounters.OneModuleOnWindow:
                 for ( int j = 0; j < allindex ; j ++ )
                 {
                     rf = caller[ j ] ;
                     if ( rf != null )
                        rf.notify_main( dt );
                 }
                 allindex = 0;
                 MyConstantsCounters.dDB[device].FixInOne [ fixmod ] = null;
                break;
            }
            setVisible ( false ) ;
        }
        public void windowDeiconified ( WindowEvent e )
        {
          setVisible ( true ) ;
          repaint( getGraphics() );
        }
        public void windowIconified ( WindowEvent e )
        {
        }
        public void windowDeactivated ( WindowEvent e )
        {
        }
        public void windowActivated ( WindowEvent e )
        {
          setVisible ( true ) ;
          repaint( getGraphics() );
        }
     };
     return WA;
}
 //
 // main drawing procedure
 //
 public void repaint ( Graphics g )
 {
     // paint the frame according to the frame type
     switch ( type )
     {
      case MyConstantsCounters.OnePackOnWindow:
          if ( caller [ 0 ] != null )
          {
             for ( int jj = 0; jj < my_java.OLTRX + 1; jj ++ )
             {
                if ( jj == my_java.END ) continue;
                if ( caller [ 0 ].modules[ device ][ jj ] != null )
                {
                    int number = my_java.counters_per_module [ jj ];
                    for ( int k = 0; k < number; k ++ )
                    {
                        Counter cc = caller [ 0 ].modules[ device ][ jj ].counters[ k ];
                        if ( cc != null )
                        {
                            if ( cc.CounterGet() == -1 )
                            {
                                 CounterDisplay cd = caller [ 0 ].modules[ device ][ jj ].countersD[ k ];
                                 if ( cd.GetFrame ( MyConstantsCounters.OnePackOnWindow ) == this )
                                    DrawCount ( g, cd, caller [ 0 ].getName (), false );
                            }
                        }
                    }
                }
             }
          }
          break;
     case MyConstantsCounters.OneCounterOnWindow:
          for ( int ii = 0; ii < MyConstants.ips; ii ++ )
          {
              if (caller [ ii ] != null )
              {
                  for ( int jj = 0; jj < my_java.OLTRX + 1; jj ++ )
                  {
                     if ( jj == my_java.END ) continue;
                     if ( caller [ ii ].modules[ device ][ jj ] != null )
                     {
                         int number = my_java.counters_per_module [ jj ];
                         for ( int k = 0; k < number; k ++ )
                         {
                             Counter cc = caller [ ii ].modules[ device ][ jj ].counters[ k ];
                             if ( cc != null )
                             {
                                 if ( cc.CounterGet() == -1 )
                                 {
                                      CounterDisplay cd = caller [ ii ].modules[ device ][ jj ].countersD[ k ];
                                      if ( cd.GetFrame ( MyConstantsCounters.OneCounterOnWindow ) == this )
                                        DrawAll ( g, cd, caller [ ii ].getName (), false );
                                 }
                             }
                         }
                     }
                  }
              }
          }
          break;
      case MyConstantsCounters.OneModuleOnWindow:
          Font f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s + 4 ) ;
          g.setFont ( f );
          FontMetrics fm = g.getFontMetrics( f );
          g.setColor ( Color.black ) ;
          int factor = 1;
          switch ( fixmod )
          {
          case my_java.FPGA:
              factor = 3;
              break;
          case my_java.TX:
          case my_java.RXFIFO:
          case my_java.TXFIFO:
              factor = 16;
              break;
          case my_java.UTOPIA:
              if ( title.startsWith("olt") ) factor = 8;
                else factor = 16;
              break;
          case my_java.RX:
              factor = 6;
              break;
          case my_java.OLTTX:
              factor = 2;
              break;
          case my_java.OLTRX:
              factor = 4;
              for ( int z = 0; z < factor; z ++ )
              {
                  g.drawString ( "Valid / Average ", x[ z ] - fm.stringWidth( "grant255 : " ), MyConstantsCounters.begin_yA - 10 );
              }
              break;
          }
          if ( fixmod != my_java.OLTRX )
          {
              for ( int z = 0; z < factor; z ++ )
              {
                  g.drawString ( MyConstantsCounters.fix_titles [ fixmod ] [ z ].trim(), x[z] , MyConstantsCounters.begin_yA - 10 );
              }
          }
          for ( int ii = 0; ii < MyConstants.ips; ii ++ )
          {
              if ( caller [ ii ] != null )
              {
                  if ( caller [ ii ].modules[ device ][ fixmod ] != null )
                  {
                      int number = my_java.counters_per_module [ fixmod ];
                      for ( int k = 0; k < number; k ++ )
                      {
                          Counter cc = caller [ ii ].modules[ device ][ fixmod ].counters[ k ];
                          if ( cc != null )
                          {
                              if ( cc.CounterGet() == -1 )
                              {
                                   CounterDisplay cd = caller [ ii ].modules[ device ][ fixmod ].countersD[ k ];
                                   if ( cd.GetFrame ( MyConstantsCounters.OneModuleOnWindow ) == this )
                                       DrawFix ( g, cd, caller [ ii ].getName (), false );
                              }
                          }
                      }
                  }
              }
          }
          break;
     }
 }
 public void paint ( CounterDisplay cd, String name )
 {
    Graphics g = getGraphics();
    switch ( type )
    {
    case MyConstantsCounters.OnePackOnWindow:
         DrawCount ( g, cd, name, true );
         break;
     case MyConstantsCounters.OneCounterOnWindow:
         DrawAll ( g, cd, name, true );
         break;
     case MyConstantsCounters.OneModuleOnWindow:
         DrawFix ( g, cd, name, true );
         break;
    }
 }
 // 'print' function - all pack's counters in a window
 private void DrawCount ( Graphics g, CounterDisplay cd, String name, boolean r )
 {
     Integer j ;
     Font f;
     int oltlimit = 0;
     int module = cd.GetModule();
     int id = cd.GetId();
     int y_s = 0;
     int xO = cd.GetCoordinateX ( type ); // beginning of a module
     int yO = cd.GetCoordinateY ( type );
     int x,y = 0;
     int value = cd.GetNewValue();
     int old = cd.GetOldValue();
     boolean delete = false;

     if ( cd.GetState() < 0 ) return;
     f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s ) ;
     g.setFont ( f );
     FontMetrics fm = g.getFontMetrics( f );
     g.setColor ( Color.black ) ;
     String tt = MyConstantsCounters.gs_title [ module ].trim() ;
     j = new Integer ( id );
     x = xO - begin_s / 2 + fm.stringWidth ( tt );
     if ( r )
     {
        if ( cd.GetState() > 0 ) delete = true;
     }
     debug( false, "DrawCount " + module );
     try
     {
     switch ( module )
     {
     case my_java.OLTRX:
         g.drawString ( tt, xO - begin_s, yO );
         y_s = MyConstantsCounters.y_s2;
         oltlimit = my_java.MaxTconts * tcontfactor; // 64 * 2 + 256 * 4
         int factor = 4;
         tt = MyConstantsCounters.rx_titles[ 0 ] + MyConstantsCounters.rx_titles[ 1 ] + MyConstantsCounters.rx_titles[ 2 ] + MyConstantsCounters.rx_titles[ 3 ];
         g.drawString ( tt, x - 30 , yO );
         fm = g.getFontMetrics( f );
         if ( id < oltlimit )
         {
              if ( ( id % ( factor * tcontfactor ) ) == 0 )
              {
                  y = yO + y_s + ( ( id ) / ( factor * tcontfactor ) ) * y_s;
                  j = new Integer ( ( id ) / tcontfactor );
                  tt = MyConstantsCounters.rx_titles[ 4 ] + j.toString();
                  g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
              }
              else
              {
                     y = yO + y_s + ( ( id ) / ( factor * tcontfactor ) ) * y_s;
              }
              if ( delete )
              {
                     j = new Integer ( old );
                     g.setColor ( MyConstants.background ) ;
                     g.drawString ( j.toString(), xO + ( id % ( factor * tcontfactor ) ) * ( MyConstantsCounters.countSpace - 5), y );
              }
              j = new Integer ( value );
              if ( id % tcontfactor == 3 ) 
                     g.setColor ( Color.blue);
              else
                     g.setColor ( Color.red );
              g.drawString ( j.toString(), xO + ( id % ( factor * tcontfactor ) ) * ( MyConstantsCounters.countSpace - 5), y );
         }
         else
         {
             y = yO + y_s + ( my_java.MaxTconts / 4 )* y_s;
             tt = MyConstantsCounters.rx_titles[ 5 ] ;
             g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
             if ( delete )
             {
                    j = new Integer ( old );
                    g.setColor ( MyConstants.background ) ;
                    g.drawString ( j.toString(), xO , y );
             }
             j = new Integer ( value );
             g.setColor ( Color.red );
             g.drawString ( j.toString(), xO , y );
          }
         break;
     case my_java.RXFIFO:
     case my_java.TXFIFO:
         g.drawString ( tt, xO - begin_s, yO );
         y_s = MyConstantsCounters.y_s1;
         y = yO + ( id + 2 ) * ( y_s ) ;
         tt = MyConstantsCounters.titles[ module ][ id ] ;
         g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
         if ( delete )
         {
             j = new Integer ( old );
             g.setColor ( MyConstants.background ) ;
             g.drawString ( j.toString(), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 25 , y );
         }
         j = new Integer ( value );
         g.setColor ( Color.red );
         g.drawString ( j.toString(), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 25 , y );
         break;
     case my_java.OLTTX:
         g.drawString ( tt, xO - begin_s, yO );
         y_s = MyConstantsCounters.y_s;
         y = yO + ( id + 2 ) * ( y_s ) ;
         tt = MyConstantsCounters.tx_titles[ id ] ;
         g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
         if ( delete )
         {
             j = new Integer ( old );
             g.setColor ( MyConstants.background ) ;
             g.drawString ( j.toString(), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] ) - 25, y );
         }
         j = new Integer ( value );
         g.setColor ( Color.red );
         g.drawString ( j.toString(), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] ) - 25, y );
         break;
     case my_java.FPGA:
         g.drawString ( tt, xO - begin_s, yO );
         y_s = MyConstantsCounters.y_s;
         y = yO + ( id / 2 + 2 )* ( y_s );
         g.setColor ( Color.black );
         switch ( id )
         {
         case 0:
         case 2:
             prevj = value;
             prevjOld = old;
             cx = xO;
             break;
         case 1:
         case 3:
             tt = MyConstantsCounters.titles[ module ][ id / 2 ] ;
             g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, prevjOld, 6 ), cx + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 25 , y );
             }
             g.setColor ( Color.red );
             g.drawString ( getHex ( value, prevj, 6 ), cx + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 25 , y );
             break;
         case 4:
             tt = MyConstantsCounters.titles[ module ][ id / 2 ] ;
             g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, 0, 4 ), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 25 , y );
             }
             g.setColor ( Color.red );
             g.drawString ( getHex ( value, 0, 4 ), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 25 , y );
             break;
         }
         break;

       default:
         g.drawString ( tt, xO - begin_s, yO );
         y_s = MyConstantsCounters.y_s;
         y = yO + ( id + 2 ) * ( y_s ) ;
         tt = MyConstantsCounters.titles[ module ][ id ] ;
         g.drawString ( tt , xO - MyConstantsCounters.countSpace , y );
         if ( delete )
         {
             j = new Integer ( old );
             g.setColor ( MyConstants.background ) ;
             g.drawString ( j.toString(), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 40 , y );
         }
         j = new Integer ( value );
         g.setColor ( Color.red );
         g.drawString ( j.toString(), xO + fm.stringWidth( MyConstantsCounters.titles[ 0 ][ 0 ] )- 40 , y );
         break;
     }
     }
     catch ( ArrayIndexOutOfBoundsException ex )
     {
//       debug ( true, "Out Of Bounds : module = " + module + " id = " + id );
     }
}
 // 'get' function - different counters, drom different pack into the same window
 private void DrawAll ( Graphics g, CounterDisplay cd, String name, boolean r )
 {
     Integer j ;
     Font f;
     FontMetrics fm ;
     int oltlimit = 0;
     int module = cd.GetModule();
     int id = cd.GetId();
     int y_s = MyConstantsCounters.y_s;
     int xO = cd.GetCoordinateX ( type ); // beginning of a module
     int yO = cd.GetCoordinateY ( type );
     int value = cd.GetNewValue();
     int old = cd.GetOldValue();
     boolean delete = false;

     if ( cd.GetState() < 0 ) return;
     f = new Font( "Serif", Font.PLAIN, y_s ) ;
     g.setFont ( f );
     fm = g.getFontMetrics( f );
     g.setColor ( Color.black ) ;
     String tt = name + " : " + MyConstantsCounters.gs_title [ module ] ;
     if ( r )
     {
        if ( cd.GetState() > 0 ) delete = true;
     }
     debug( false, "DrawAll " + module );
     switch ( module )
     {
     case my_java.OLTRX:
         g.drawString ( tt, xO, yO + y_s + 2 );
         f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s1 ) ;
         g.setFont ( f );
         fm = g.getFontMetrics( f );
         oltlimit = my_java.MaxTconts * tcontfactor; // 64 * 2 + 128 * 4
         {
             if ( id < oltlimit )
             {
                 j = new Integer ( id );
                 tt = MyConstantsCounters.rx_titles[ 4 ] + j.toString();
             }
             else
             {
                  tt = MyConstantsCounters.rx_titles[ 5 ] ;
             }
         }
         g.drawString ( tt , xO , yO );
         if ( delete )
         {
             j = new Integer ( old );
             g.setColor ( MyConstants.background ) ;
             g.drawString ( j.toString(), xO + fm.stringWidth( tt ) + 5, yO );
         }
         g.setColor ( Color.red );
         j = new Integer ( value );
         g.drawString ( j.toString(), xO + fm.stringWidth ( tt ) + 5, yO );
         break;
     case my_java.OLTTX:
         g.drawString ( tt, xO, yO + y_s + 2 );
         f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s1 ) ;
         g.setFont ( f );
         fm = g.getFontMetrics( f );
         tt = MyConstantsCounters.tx_titles[ id ] ;
         g.drawString ( tt , xO , yO );
         if ( delete )
         {
             j = new Integer ( old );
             g.setColor ( MyConstants.background ) ;
             g.drawString ( j.toString(), xO + fm.stringWidth( tt ) + 5, yO );
         }
         g.setColor ( Color.red );
         j = new Integer ( value );
         g.drawString ( j.toString(), xO + fm.stringWidth ( tt ) + 5, yO );
         break;
     case my_java.FPGA:
         switch ( id )
         {
         case 0:
         case 2:
             prevj = value;
             prevjOld = old;
             cx = xO;
             break;
         case 1:
         case 3:
             g.drawString ( tt, cx , yO + y_s + 2 );
             f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s1 ) ;
             g.setFont ( f );
             fm = g.getFontMetrics( f );
             tt = MyConstantsCounters.titles[ module ][ id / 2 ] ;
             g.drawString ( tt , cx , yO );
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, prevjOld, 6 ) , cx + fm.stringWidth ( tt ) + 5, yO );
             }
             g.setColor ( Color.red );
             g.drawString ( getHex ( value, prevj, 6 ) , cx + fm.stringWidth ( tt ) + 5, yO );
             break;
         case 4:
             g.drawString ( tt, xO , yO + y_s + 2 );
             f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s1 ) ;
             g.setFont ( f );
             fm = g.getFontMetrics( f );
             tt = MyConstantsCounters.titles[ module ][ id / 2 ] ;
             g.drawString ( tt , xO , yO );
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, prevjOld, 4 ), xO + fm.stringWidth ( tt ) + 5, yO );
             }
             g.setColor ( Color.red );
             g.drawString ( getHex ( value, prevj, 4 ), xO + fm.stringWidth ( tt ) + 5, yO );
             break;
         }
         break;

     default:
         g.drawString ( tt, xO, yO + y_s + 2 );
         f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s1 ) ;
         g.setFont ( f );
         fm = g.getFontMetrics( f );
         tt = MyConstantsCounters.titles[ module ][ id ] ;
         g.drawString ( tt, xO , yO );
         if ( delete )
         {
             j = new Integer ( old );
             g.setColor ( MyConstants.background ) ;
             g.drawString ( j.toString(), xO + fm.stringWidth( tt ) + 5, yO );
         }
         g.setColor ( Color.red );
         j = new Integer ( value );
         g.drawString ( j.toString(), xO + fm.stringWidth ( tt ) + 5, yO );
         break;
     }
 }
 // 'display' function - same counter type from all requested packs in the same window
 private void DrawFix ( Graphics g, CounterDisplay cd, String name, boolean r )
 {
     Integer j ;
     Font f;
     FontMetrics fm ;
     int l,factor = 1;
     int oltlimit = 0;
     int module = cd.GetModule();
     int id = cd.GetId();
     int xO = cd.GetCoordinateX ( type ); // beginning of a module
     int yO = cd.GetCoordinateY ( type );
     int value = cd.GetNewValue();
     int old = cd.GetOldValue();
     boolean delete = false;

     if ( cd.GetState() < 0 ) return;
     f = new Font( "Serif", Font.PLAIN, MyConstantsCounters.y_s + 4 ) ;
     g.setFont ( f );
     fm = g.getFontMetrics( f );
     g.setColor ( Color.black ) ;
     String tt = "onu_32 :       ";
     if ( r )
     {
        if ( cd.GetState() > 0 ) delete = true;
     }
     debug( false, "DrawFix " + module );
     switch ( module )
     {
     case my_java.FPGA:
         tt = name + " : ";
         if ( id == 0 )
         {
               cx = yO;
               g.setColor ( Color.black ) ;
               j = new Integer ( id );
               g.drawString ( tt, MyConstantsCounters.begin_xA, cx );
         }
         switch ( id )
         {
         case 0:
         case 2:
             prevj = value;
             prevjOld = old;
             break;
         case 1:
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, prevjOld, 6 ) , x[ 0 ], cx );
             }
             g.setColor ( Color.blue );
             g.drawString ( getHex ( value, prevj, 6 ), x[ 0 ], cx );
             break;
         case 3:
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, prevjOld, 6 ) , x[ 1 ], cx );
             }
             g.setColor ( Color.blue );
             g.drawString ( getHex ( value, prevj, 6 ), x[ 1 ], cx );
             break;
         case 4:
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 g.drawString ( getHex ( old, prevjOld, 4 ) , x[ 2 ], cx );
             }
             g.setColor ( Color.blue );
             g.drawString ( getHex ( value, prevj, 4 ), x[ 2 ], cx );
             break;
         }
         break;

     case my_java.TX:
         factor = 16;
         tt = name + " : ";
         if ( id == 0 )
         {
               cx = yO;
               g.setColor ( Color.black ) ;
               j = new Integer ( id );
               g.drawString ( tt, MyConstantsCounters.begin_xA, cx );
         }
         if ( id < 16 )
         {
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 j = new Integer ( old );
                 g.drawString ( j.toString(), x [ id ] , cx );
             }
             g.setColor ( Color.blue );
             j = new Integer ( value );
             g.drawString ( j.toString(), x [ id ], cx );
         }
         break;

     case my_java.RXFIFO:
     case my_java.TXFIFO:
         factor = 16;
         l = ( max_x - fm.stringWidth( tt ) ) / factor;
         g.drawString ( MyConstantsCounters.fix_titles [ module ] [ 0 ].trim(), x[ 0 ] , MyConstantsCounters.begin_yA - 10 );
         for ( int z = 1; z < factor; z ++ )
         {
             x[ z ] = x[ z - 1 ] + l;
             g.drawString ( MyConstantsCounters.fix_titles [ module ] [ z ].trim(), x[z] , MyConstantsCounters.begin_yA - 10 );
         }
         tt = name + " : ";
         if ( id == 0 )
         {
               cx = yO;
               g.setColor ( Color.black ) ;
               j = new Integer ( id );
               g.drawString ( tt, MyConstantsCounters.begin_xA, cx );
         }
         if ( id % 2 == 0 )
         {
             if ( delete )
             {
                 g.setColor ( MyConstants.background ) ;
                 j = new Integer ( old );
                 g.drawString ( j.toString(), x [ id / 2 ] , cx );
             }
             g.setColor ( Color.blue );
             j = new Integer ( value );
             g.drawString ( j.toString(), x [ id / 2 ] , cx );
         }
         break;

     case my_java.UTOPIA:
         if ( name.startsWith("olt") ) factor = 8;
           else factor = 16;
         tt = name + " : ";
         if ( id == 0 )
         {
               cx = yO;
               g.setColor ( Color.black ) ;
               j = new Integer ( id );
               g.drawString ( tt, MyConstantsCounters.begin_xA, cx );
         }
         if ( delete )
         {
             g.setColor ( MyConstants.background ) ;
             j = new Integer ( old );
             g.drawString ( j.toString(), x [ id ] , cx );
         }
         g.setColor ( Color.blue );
         j = new Integer ( value );
         g.drawString ( j.toString(), x [ id ], cx );
         break;

     case my_java.RX:
         factor = 6;
         tt = name + " : ";
         if ( id == 0 )
         {
               cx = yO;
               g.setColor ( Color.black ) ;
               j = new Integer ( id );
               g.drawString ( tt, MyConstantsCounters.begin_xA, cx );
         }
         if ( delete )
         {
             g.setColor ( MyConstants.background ) ;
             j = new Integer ( old );
             g.drawString ( j.toString(), x [ id ] , cx );
         }
         g.setColor ( Color.blue );
         j = new Integer ( value );
         g.drawString ( j.toString(), x [ id ], cx );
         break;

     case my_java.OLTTX:
         factor = 2;
         tt = name + " : ";
         if ( id == 0 )
         {
               cx = yO;
               g.setColor ( Color.black ) ;
               j = new Integer ( id );
               g.drawString ( tt, MyConstantsCounters.begin_xA, cx );
         }
         if ( delete )
         {
             g.setColor ( MyConstants.background ) ;
             j = new Integer ( old );
             g.drawString ( j.toString(), x [ id ] , cx );
         }
         g.setColor ( Color.blue );
         j = new Integer ( value );
         g.drawString ( j.toString(), x [ id ], cx );
         break;

     case my_java.OLTRX:
         factor = 4;
         oltlimit = my_java.MaxTconts * my_java.tcontfactor; // 64 * 2 + 256 * 4
         if ( id == 0 )
         {
             cx = yO;
         }
         if ( id < oltlimit ) 
         {
             int jj = id;
             if ( ( jj % ( factor * 4 ) ) == 0 )
             {
                 cx += MyConstantsCounters.next_y - 7;
                 g.setColor ( Color.black ) ;
                 Integer ii;
                 for ( int z = 0; z < 4; z ++ )
                 {
                     ii = new Integer ( jj / factor + z );
                     tt = "grant" + ii.toString() + " : ";
                     g.drawString ( tt, x[ z ] - fm.stringWidth( "grant255 : " ) , cx );
                 }
             }
             switch ( active )
             {
             case 0: // once
                 if ( ( jj % 4 ) == 0 )
                 {
                     if ( delete )
                     {
                         g.setColor ( MyConstants.background ) ;
                         j = new Integer ( old );
                         g.drawString ( j.toString(), x [ ( jj/4 ) % 4 ] , cx );
                     }
                     g.setColor ( Color.blue );
                     j = new Integer ( value );
                     g.drawString ( j.toString(), x [ ( jj/4 ) % 4 ] , cx );
                 }
                 break;
             case 1 : // start
                 if ( ( jj % 4 ) == 3 )
                 {
                     if ( delete )
                     {
                         g.setColor ( MyConstants.background ) ;
                         j = new Integer ( old );
                         g.drawString ( j.toString(), x [ ( jj/4 ) % 4 ] , cx );
                     }
                     g.setColor ( Color.blue );
                     j = new Integer ( value );
                     g.drawString ( j.toString(), x [ ( jj/4 ) % 4 ] , cx );
                 }
                 break;
             }
         }
         break;
     }
 }
 public void actionPerformed ( ActionEvent e )
 {
    if ( e.getActionCommand() == "suspend")
    {
        stop = my_java.SUSPEND ;
        stop_button.setBackground( Color.gray ) ;
        continue_button.setBackground ( Color.yellow ) ;
        stop_button.setEnabled ( false ) ;
        once_button.setEnabled ( true ) ;
        once_button.setBackground ( Color.green ) ;
        continue_button.setEnabled ( true ) ;
    }

    if ( e.getActionCommand() == "resume")
    {
        stop = my_java.RESUME ;
        active = 1;
        stop_button.setBackground( Color.yellow ) ;
        continue_button.setBackground ( Color.gray ) ;
        stop_button.setEnabled ( true ) ;
        once_button.setEnabled ( false ) ;
        once_button.setBackground ( Color.gray ) ;
        continue_button.setEnabled ( false ) ;
    }
    if ( e.getActionCommand() == "start")
    {
        active = 1;
        stop = my_java.START ;
        start_button.setEnabled ( false ) ;
        once_button.setEnabled ( false ) ;
        once_button.setBackground ( Color.gray ) ;
        start_button.setBackground ( Color.gray ) ;
    }
    if ( e.getActionCommand() == "once")
    {
        active = 0;
        stop = my_java.READONCE ;
    }
    ReadSocket rf = null;
    switch ( type )
    {
     case MyConstantsCounters.OnePackOnWindow:
         rf = caller[ 0 ] ;
         if ( rf != null )
             rf.Freeze ( stop, device );
         break;
     case MyConstantsCounters.OneCounterOnWindow:
     case MyConstantsCounters.OneModuleOnWindow:
        for ( int j = 0; j < allindex ; j ++ )
        {
            rf = caller[ j ] ;
            if ( rf != null )
                rf.Freeze ( stop, device );
        }
        break;
    }
 }

 private String getHex ( int value, int val, int len )
 {
     String toReturn = "0x";

     if ( len == 6 )
     {
         toReturn = toReturn + getHexNibble ( ( val & 0x000000F0 ) >> 4 );
         toReturn = toReturn + getHexNibble ( val & 0x0000000F );
     }
     toReturn = toReturn + getHexNibble ( ( value & 0xF0000000 ) >> 28 );
     toReturn = toReturn + getHexNibble ( ( value & 0x0F000000 ) >> 24 );
     toReturn = toReturn + getHexNibble ( ( value & 0x00F00000 ) >> 20 );
     toReturn = toReturn + getHexNibble ( ( value & 0x000F0000 ) >> 16 );
     toReturn = toReturn + getHexNibble ( ( value & 0x0000F000 ) >> 12 );
     toReturn = toReturn + getHexNibble ( ( value & 0x00000F00 ) >> 8 );
     toReturn = toReturn + getHexNibble ( ( value & 0x000000F0 ) >> 4 );
     toReturn = toReturn + getHexNibble ( value & 0x0000000F );

    return toReturn;
 }
 private String getHexNibble ( int value )
 {
     String toReturn = "";

     switch ( value )
     {
     case 0:
         toReturn = toReturn + "0";
         break;
     case 1:
         toReturn = toReturn + "1";
         break;
     case 2:
         toReturn = toReturn + "2";
         break;
     case 3:
         toReturn = toReturn + "3";
         break;
     case 4:
         toReturn = toReturn + "4";
         break;
     case 5:
         toReturn = toReturn + "5";
         break;
     case 6:
         toReturn = toReturn + "6";
         break;
     case 7:
         toReturn = toReturn + "7";
         break;
     case 8:
         toReturn = toReturn + "8";
         break;
     case 9:
         toReturn = toReturn + "9";
         break;
     case 10:
         toReturn = toReturn + "A";
         break;
     case 11:
         toReturn = toReturn + "B";
         break;
     case 12:
         toReturn = toReturn + "C";
         break;
     case 13:
         toReturn = toReturn + "D";
         break;
     case 14:
         toReturn = toReturn + "E";
         break;
     case 15:
         toReturn = toReturn + "F";
         break;
     }
    return toReturn;
 }
 private void debug ( boolean doflag, String str )
 {
     if ( doflag  ) //|| MyConstantsCounters.counterdebug_flag )
     {
         System.out.println( "DrawCText : " + str ) ;
     }
 }
}
