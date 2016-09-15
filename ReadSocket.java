package all53e;
import java.io.* ;
import java.lang.* ;
import java.net.*;
import javax.swing.* ;
import java.util.* ;

/////////////////////////////////////////////////////////////////////////
//
//   implements the thread to read a binay record received on a socket and draw it
//
/////////////////////////////////////////////////////////////////////////
class ReadSocket extends Thread
{
    public int Index = -1;
    public Module [][] modules;

    private final int counterCounter = 2;
    private final int counterValue = 4;
    private final int counterLength = counterCounter + counterValue;

	private drawParameters [] dP;
    private InputStream in ;
    private OutputStream outin ;
    private int end ;
    private boolean stop_file ;
    private FileWriter [] out ;
    private FileOutputStream[] obin ;
    private Socket socket ;
    private MyMain mm ;
    private String Name;
    private String NameStr = "";
    private byte [] NameB ;
    private int mydev;
	private final int readFile = 0;
	private final int endOfImage = 1;
	private final int endOfFile = 2;
	private long readtime = 0;
	private int readtimeI [] = new int [ 2 ];

/************************************************************************/
//
//  creates the thread for an address
//
/************************************************************************/
    public ReadSocket ( )
    {
		dP = new drawParameters [ 2 ];
        out = new FileWriter [ 2 ];
        obin = new FileOutputStream [ 2 ] ;
        end = readFile ;
        socket = null ;
        stop_file = false ;
        for ( int i = 0; i < 2; i ++ )
        {
        	out[i] = null ;
        	obin[i] = null ;
        	dP[i] = null;
		}
		outin = null ;
        in = null ;
        NameB = new byte [ 4 ];
        for ( int i = 0; i < 4; i ++ )
        {
            NameB [ i ] = 0;
        }
    }
/************************************************************************/
//
//  receive all the needed parameters
//
/************************************************************************/
    public void SetParameters ( drawParameters dPi, MyMain caller, String name )
    {
		mydev = dPi.device;
        Name = name;
        mm = caller;
        // build all the possible modules
        modules = new Module [2][ my_java.OLTRX + 1 ];
        for ( int y = 0; y < my_java.OLTRX + 1; y ++ )
        {
            if ( y == my_java.END ) continue;
            modules [mydev][ y ] = new Module ( y );
        }
        dP [mydev] = dPi;
        if ( Character.isDigit ( Name.charAt ( 0 ) ) )
        {
            // build byte array from the IP name
            StringTokenizer sname = new StringTokenizer ( Name,".",false );
            int i = 0;
            while ( sname.hasMoreTokens() && ( i < 5 ) )
            {
                int I = Integer.parseInt ( sname.nextToken() );
                NameB [ i++ ] = ( byte ) I ;
            }
        }
    }
/************************************************************************/
//
//  add this new thread to the common frame ( in case of 'all in one' )
//
/************************************************************************/
    public void SetMoreParameters ( drawParameters dP )
    {
		mydev = dP.device;
        Counter ff;
        xypoint point;
        int module;
        int prev_module = -2;
        int id;
        if ( dP.graphs.size() == 0 )
        {
           ff = new Counter ( -1, -1 );
           dP.graphs.add ( ff );
        }
        this.dP [mydev] = dP;
        for ( int i = 0; i < dP.graphs.size() ; i ++ )
        {
            ff = ( Counter ) dP.graphs.get ( i ) ;
            module = ff.CounterModule();
            if ( module != -1 )
            {
               id = ff.CounterId();
               if ( id != -1 )
               {
                   modules [mydev][ module ].counters [ id ].CounterSet();
                   if ( dP.all_in_one )
                   {
                       if ( dP.fixForm )
                       {
                           modules [mydev][ module ].countersD [ id ].SetFrame( MyConstantsCounters.OneModuleOnWindow, MyConstantsCounters.dDB[mydev].FixInOne [ module ] );
                           if ( prev_module != module )
                           {
                               point = MyConstantsCounters.dDB[mydev].FixInOne [ module ].SetDraw ( dP.fixForm, module, id );
                               modules [mydev][ module ].countersD [ id ].SetCoordinateY ( MyConstantsCounters.OneModuleOnWindow, point.Y );
                               modules [mydev][ module ].countersD [ id ].SetCoordinateX ( MyConstantsCounters.OneModuleOnWindow, point.X );
                               prev_module = module;
                           }
                       }
                       else
                       {
                           modules [mydev][ module ].countersD [ id ].SetFrame( MyConstantsCounters.OneCounterOnWindow, MyConstantsCounters.dDB[mydev].AllInOne );
                           point = MyConstantsCounters.dDB[mydev].AllInOne.SetDraw ( dP.fixForm, module, id );
                           modules [mydev][ module ].countersD [ id ].SetCoordinateY ( MyConstantsCounters.OneCounterOnWindow, point.Y );
                           modules [mydev][ module ].countersD [ id ].SetCoordinateX ( MyConstantsCounters.OneCounterOnWindow, point.X );
                       }
                   }
                   else
                   {
                       modules [mydev][ module ].countersD [ id ].SetFrame( MyConstantsCounters.OnePackOnWindow, MyConstantsCounters.dDB[mydev].EachOne [ Index ] );
                       point = MyConstantsCounters.dDB[mydev].EachOne [ Index ].SetDraw ( module );
                       modules [mydev][ module ].countersD [ id ].SetCoordinateY ( MyConstantsCounters.OnePackOnWindow, point.Y );
                       modules [mydev][ module ].countersD [ id ].SetCoordinateX ( MyConstantsCounters.OnePackOnWindow, point.X );
                   }
               }
               else
               {
                   int number = my_java.counters_per_module [ module ];
                   for ( int y = 0; y < number; y ++ )
                   {
                       modules [mydev][ module ].counters [ y ].CounterSet();
                       if ( dP.all_in_one )
                       {
                           if ( dP.fixForm )
                           {
                               modules [mydev][ module ].countersD [ y ].SetFrame( MyConstantsCounters.OneModuleOnWindow, MyConstantsCounters.dDB[mydev].FixInOne [ module ] );
                               if ( prev_module != module )
                               {
                                  point = MyConstantsCounters.dDB[mydev].FixInOne [ module ].SetDraw ( dP.fixForm, module, y );
                                  modules [mydev][ module ].countersD [ y ].SetCoordinateY ( MyConstantsCounters.OneModuleOnWindow, point.Y );
                                  modules [mydev][ module ].countersD [ y ].SetCoordinateX ( MyConstantsCounters.OneModuleOnWindow, point.X );
                                  prev_module = module;
                               }
                           }
                           else
                           {
                               modules [mydev][ module ].countersD [ y ].SetFrame( MyConstantsCounters.OneCounterOnWindow, MyConstantsCounters.dDB[mydev].AllInOne );
                               point = MyConstantsCounters.dDB[mydev].AllInOne.SetDraw ( dP.fixForm, module, y );
                               modules [mydev][ module ].countersD [ y ].SetCoordinateY ( MyConstantsCounters.OneCounterOnWindow, point.Y );
                               modules [mydev][ module ].countersD [ y ].SetCoordinateX ( MyConstantsCounters.OneCounterOnWindow, point.X );
                           }
                       }
                       else
                       {
                           modules [mydev][ module ].countersD [ y ].SetFrame( MyConstantsCounters.OnePackOnWindow, MyConstantsCounters.dDB[mydev].EachOne [ Index ] );
                           point = MyConstantsCounters.dDB[mydev].EachOne [ Index ].SetDraw ( module );
                           modules [mydev][ module ].countersD [ y ].SetCoordinateY ( MyConstantsCounters.OnePackOnWindow, point.Y );
                           modules [mydev][ module ].countersD [ y ].SetCoordinateX ( MyConstantsCounters.OnePackOnWindow, point.X );
                       }
                   }
               }
            }
            else
            {
                // 'all' option for module
                for ( i = 0; i < my_java.OLTRX + 1; i ++ )
                {
                    if ( i == my_java.END ) continue;
                    int number = my_java.counters_per_module [ i ];
                    for ( int y = 0; y < number; y ++ )
                    {
                        modules [mydev][ i ].counters [ y ].CounterSet();
                        if ( dP.all_in_one )
                        {
                            if ( dP.fixForm )
                            {
                                modules [mydev][ i ].countersD [ y ].SetFrame( MyConstantsCounters.OneModuleOnWindow, MyConstantsCounters.dDB[mydev].FixInOne [ i ] );
                                if ( prev_module != i )
                                {
                                    point = MyConstantsCounters.dDB[mydev].FixInOne [ i ].SetDraw ( dP.fixForm, i, y );
                                    modules [mydev][ i ].countersD [ y ].SetCoordinateY ( MyConstantsCounters.OneModuleOnWindow, point.Y );
                                    modules [mydev][ i ].countersD [ y ].SetCoordinateX ( MyConstantsCounters.OneModuleOnWindow, point.X );
                                    prev_module = i;
                                }
                            }
                            else
                            {
                                modules [mydev][ i ].countersD [ y ].SetFrame( MyConstantsCounters.OneCounterOnWindow, MyConstantsCounters.dDB[mydev].AllInOne );
                                point = MyConstantsCounters.dDB[mydev].AllInOne.SetDraw ( dP.fixForm, i, y );
                                modules [mydev][ i ].countersD [ y ].SetCoordinateY ( MyConstantsCounters.OneCounterOnWindow, point.Y );
                                modules [mydev][ i ].countersD [ y ].SetCoordinateX ( MyConstantsCounters.OneCounterOnWindow, point.X );
                            }
                        }
                        else
                        {
                            modules [mydev][ i ].countersD [ y ].SetFrame( MyConstantsCounters.OnePackOnWindow, MyConstantsCounters.dDB[mydev].EachOne [ Index ] );
                            point = MyConstantsCounters.dDB[mydev].EachOne [ Index ].SetDraw ( i );
                            modules [mydev][ i ].countersD [ y ].SetCoordinateY ( MyConstantsCounters.OnePackOnWindow, point.Y );
                            modules [mydev][ i ].countersD [ y ].SetCoordinateX ( MyConstantsCounters.OnePackOnWindow, point.X );
                        }
                    }
                }
            }
        }
    }
/************************************************************************/
//
//  write to the report file
//
/************************************************************************/
    private void give_report( byte buffer [], int module, int length, int dev )
    {
        byte temp [] = new byte [ counterValue ];
        byte temp2 [] = new byte [ counterCounter ];
        int type ;
        int module_id;

        if ( out[dev] != null )
        {
        module_id = module;
        try
        {
          if ( module_id != my_java.END_IMAGE )
          {
            if ( module_id > my_java.END ) module_id = module_id - my_java.END - 1;
            // write the title into the report file
            // 6 = dev , '.', ' : ' and CR
            out[dev].write ( MyConstantsCounters.gs_title [ module_id ] + "\n", 0, MyConstantsCounters.gs_title [ module_id ].length() + 1 ) ;
            // calculate all counters for the module
            for ( int i = 0; i < length; i ++ )
            {
                for ( int j = 0; j < counterCounter; j ++ )
                {
                    temp2 [ j ] = buffer [ counterLength * i + j ] ;
                }
                type = byte_to_int ( temp2 );
                for ( int j = 0; j < counterValue; j ++ )
                {
                    temp [ j ] = buffer [ counterCounter + counterLength * i + j ] ;
                }
                // write the counter's value into the report file
                out[dev].write ( type + " : " + byte_to_int ( temp ) + "\n" ) ;
            }
          }
          else
          {
            String ttimes = "END IMAGE\n";
            out[dev].write ( ttimes, 0, ttimes.length() ) ;
          }
        }
        catch ( IOException e )
        {
           debug ( true, "Error in " + Name ) ;
        }
    }
    }
    private void give_report( int dev, int ttime )
    {

        if ( out[dev] != null )
        {
            try
            {
                String ttimes = "time : " + ttime + "\n";
                out[dev].write ( ttimes, 0, ttimes.length());
            }
            catch ( IOException e )
            {
               debug ( true, "Error in " + Name ) ;
            }
        }
    }
    private int byte_to_int ( byte temp [] )
    {
       java.math.BigInteger bi = new java.math.BigInteger ( temp ) ;
       bi = bi.abs() ;
       // write the counter's value into the report file
       return bi.intValue();
    }
    // open counter connection
    public boolean open_connection ( )
    {
       try
       {
          if ( Character.isDigit ( Name.charAt ( 0 ) ) )
          {
              socket = TimedSocket.getSocket ( Name, my_java.CS_CONNECTION_PORT, MyConstants.ConnectionTO ) ;
              if ( socket != null )
              {
                  socket.setReceiveBufferSize ( MyConstantsCounters.max_buffer );
                  in = socket.getInputStream () ;
                  outin = socket.getOutputStream () ;
              }
              else debug ( true, "cannot open connection to " + Name );
          }
          else
          {
              in = new FileInputStream ( Name ) ;
          }
       }
       catch ( IOException e )
       {
          debug ( true, "Error in open " + Name ) ;
       }
       if ( in == null ) return false;
       return true;
   }
   // open save and report files
   public void open_files ( int dev )
   {
       String filename = "";
       String path = "";
       Calendar calendar = Calendar.getInstance();
       int m = calendar.get( Calendar.MONTH ) + 1;
       int d = calendar.get( Calendar.DAY_OF_MONTH );
       int h = calendar.get( Calendar.HOUR_OF_DAY );
       int mm = calendar.get( Calendar.MINUTE );
       String file_separator = MyConstants.SP.getProperty( "file.separator" ) ;
       try
       {
           // append the date to the file name
    	   if ( ( dP[dev].report ) && ( out[dev] == null ) )
    	   {
               if ( Character.isDigit ( Name.charAt ( 0 ) ) )
               {
                 int ii = dP[dev].report_file.lastIndexOf ( file_separator ) ;
                 if ( ii == -1 )
                 {
                      filename = dP[dev].report_file ;
                 }
                 else
                 {
                     path = dP[dev].report_file.substring( 0, ii + 1 );
                     filename = dP[dev].report_file.substring( ii + 1, dP[dev].report_file.length() ); 
                 }
    	  	     out[dev] = 
                      new FileWriter ( path + getName() +  "." + filename + "." + dev + "." + d + "." + m + "." + h + "." + mm ) ;
               }
               else
               {
                   out[dev] = 
                        new FileWriter ( dP[dev].report_file + "." + dev + "." + d + "." + m + "." + h + "." + mm ) ;
               }
               out[dev].write ( Name + "\n", 0, Name.length() + 1  ) ;               
    	   }
    	   if ( ( dP[dev].save ) && ( obin[dev] == null ) )
    	   {
               int ii = dP[dev].save_file.lastIndexOf ( file_separator ) ;
               if ( ii == -1 )
               {
                    path = "";
                    filename = dP[dev].save_file ;
               }
               else
               {
                   path = dP[dev].save_file.substring( 0, ii + 1 );
                   filename = dP[dev].save_file.substring( ii + 1, dP[dev].save_file.length() ); 
               }
    	  	    obin[dev] 
                    = new FileOutputStream ( path + getName() +  "." + filename + "." + dev + "." + d + "." + m + "." + h + "." + mm ) ;
                obin[dev].write( NameB, 0, 4 );
    	   }
    	}
       	catch ( IOException e )
       	{
	       	
 		  if ( dP[dev].report )
            debug ( true, "Error in open report file " + path + getName() +  "." + filename + "." + dev + "." + d + "." + m + "." + h + "." + mm ) ;
 		  if ( dP[dev].save )
            debug ( true, "Error in open save file " + path + getName() +  "." + filename + "." + dev + "." + d + "." + m + "." + h + "." + mm ) ;
       	}
    }
   //
   // read one window of counters
   //
    private int readImage ( )
    {
	    int stop = readFile;
	    int module_id;
	    int l = -1;
	    int length;
	    byte buffer [] = new byte [ MyConstantsCounters.max_buffer ];
   		byte tt [ ] = new byte [ counterCounter ];
		byte tb [] = new byte [ 4 ];
		 
   		try
		{
            // read first 4 bytes = time 
		    l = in.read( tb, 0, 4 );
		    if ( l == -1 ) 
		    {
			    stop = endOfFile;
		    }
            else
            {
                if ( dP[mydev].report )
                {
                    give_report ( mydev, byte_to_int ( tb ) );
                }
            }
	    }
	    catch ( IOException ex )
	    {
		    stop = endOfFile;
	    }
        // read until end of image
	    while ( stop == readFile ) 
	    {
		    try
		    {
                // read module ( byte )
		    	module_id = in.read();
                if ( module_id == my_java.END_IMAGE )
                {
                    // end of image
	                stop = endOfImage;
                    if ( dP[mydev].report )
                    {
                        give_report ( null, module_id, 0, mydev ) ;
                    }
                    // go to display the last portion of the received image
                    if ( ! dP[mydev].storeForm )
                    {
	                   drawlast ( mydev );
                    }
                }
                else
                {
                    // read the number of counters to read for the module
                	l = in.read ( tt , 0, counterCounter );
                	length = byte_to_int ( tt );
                    // read all the counters of this module
                    // each counter has id ( 2 bytes ) + value ( 4 bytes )
                	l = in.read ( buffer , 0, counterLength * length ) ;
                    if ( dP[mydev].report )
                    {
                        give_report ( buffer, module_id, length, mydev ) ;
                    }
                    // go to display the received image
                    if ( ! dP[mydev].storeForm )
                       Paint ( module_id, length, buffer, mydev ) ;
            	}
	    	}
	    	catch ( IOException ex )
	    	{
		    	stop = endOfFile;
	    	}
	    }
	    return stop;
    }
/************************************************************************/
//
//  overwrite the run mrthod of the thread
//
/************************************************************************/
 public void run()
 {
   int length ;
   byte buffer [] ;
   byte bufferS [] ;
   int module_id, device = 0;
   byte temp [] = new byte [ counterValue ];
   byte tt [ ] = new byte [ counterCounter ];

   tt [ 0 ] = 0;
   buffer = new byte [ MyConstantsCounters.max_buffer ] ;
   bufferS = new byte [ MyConstantsCounters.max_buffer ] ;

   // if we want to display the counters 'offline' from a previously saved file 
   if ( ! Character.isDigit ( Name.charAt ( 0 ) ) )
   {
       try
       {
           // read the IP address and format the correspondent string
           in.read ( NameB , 0, 4 );
           for ( int i = 0; i < 4; i ++ )
           {
	           tt [ 1 ] = NameB [ i ];
         	   java.math.BigInteger bi = new java.math.BigInteger( tt ) ;
               NameStr = NameStr + bi.abs().toString();
               if ( i < 3 )
               {
                   NameStr = NameStr + ".";
               }
           }
       	   if ( out[mydev] != null )
           {
           		out[mydev].write ( NameStr + "\n", 0, NameStr.length() + 1  ) ;               	       
           }           
       }
       catch ( IOException e )
       {
           NameStr = "";
       }
       while ( stop_file == false )
       {
	       int endI = readImage( );
	       if ( endI == endOfFile )
	       {
		        stop_file = true;
	       }
	       else
	       {
           		try
           		{
              		// 'stop'
              		this.sleep( 2000 ) ;
           		}
           		catch ( InterruptedException e )
           		{
           		}
           }
      }
      closemyfile( mydev );	
      System.out.println ( Name + " finished." ) ;
   }
   else
   {   // display 'on line' - the name is what we asked
       NameStr = Name ;
   }
// while until this connection finish
   while ( end == readFile )
   {
      int len ;

      if ( stop_file == false )
      {
         try
         {
           // each image begins with the byte of device
           device = in.read () ;
           if ( ( device == -1 )   || ( device == my_java.END ))
           {
	           end = endOfFile;
	           break;
           }
           if ( readtime == 0 )
           {
               readtime = System.currentTimeMillis();
               readtimeI[ device ] = 0;
           }
           else
           {
               readtimeI[ device ] += ( int ) (( System.currentTimeMillis() - readtime ) / 1000L );
               readtime = System.currentTimeMillis();
           }
           if ( dP[device].save && ( obin[device] != null ) )
           {
               byte tb [] = new byte [ 4 ];
               tb [ 0 ] = ( byte ) ( readtimeI[device] & 0xFF );
               tb [ 1 ] = ( byte ) ( ( readtimeI[device] & 0xFF00 ) >> 8);
               tb [ 2 ] = ( byte ) ( ( readtimeI[device] & 0xFF0000 ) >> 16);
               tb [ 3 ] = ( byte ) ( ( readtimeI[device] & 0xFF000000 ) >> 24);
               obin[device].write( tb, 0, 4 ) ;
           }
           if ( dP[device].report )
           {
               give_report ( device, readtimeI[device] ) ;
           }
           // read the file until its end or 'stop'
           while ( end == readFile )
           {
           	   module_id = in.read () ;
           	   if ( ( module_id == -1 )  || ( module_id == my_java.END ) )
           	   {
	           		end = endOfFile;
	           		break;
               }
               if ( module_id == my_java.END_IMAGE )
               {
                   // last portion of the image
                     if ( obin[device] != null )
                     {
                          obin[device].write( module_id ) ;
                     }
                     if ( dP[device].report )
                     {
                          give_report ( null, module_id, 0, device ) ;
                     }
                     if ( ! dP[device].storeForm )
                     {
	                     drawlast ( device );
                     }
                     break;
               }  // end image
               else
               {
                   // read a counter : type + value
               		len = in.read ( tt , 0, counterCounter );
           	   		if ( len == -1 ) 
           	   		{
	           			end = endOfFile;
	           			break;
               		}
               		if ( dP[device].save && ( obin[device] != null ) )
               		{
                   		obin[device].write( module_id ) ;
                   		obin[device].write( tt, 0, counterCounter ) ;
               		}
               		length =  byte_to_int ( tt );
               		debug ( false, "module = " + module_id + " length = " + length ) ;
                    int l;
                    len = 0;
                    // read all the counters of this module
                    l = in.read ( bufferS , 0, counterLength * length ) ;
                    if ( l == -1 )
                    {
	                    end = endOfFile;
	                    break;
                    }
                    else
                    {
                        // save the read buffer
                       for ( int t = 0; t < l; t ++ )
                       {
                           buffer [ t ] = bufferS [ t ];
                       }
                       len += l;
                       // in case not all the expected bytes arrived in this IP packet
                       while ( ( len < counterLength * length ) && ( end == readFile ) )
                       {
                           // read until all the expected length arrived
                           l = in.read ( bufferS , 0, counterLength * length - len ) ;
                           if ( l == -1 )
                           {
	                           end = endOfFile;
	                           break;
                           }
                           // concatenate the last buffer to the first one
                           for ( int t = 0; t < l; t ++ )
                           {
                               buffer [ t + len ] = bufferS [ t ];
                           }
                           len += l;
                       }
                       if ( end == readFile )
                       {
                           // go with the read image to draw it according 
                           // to the defined options and counters
                       		if ( ! dP[device].storeForm ) 
                            	Paint ( module_id, length, buffer, device ) ;
                       		if ( ( dP[device].save ) && ( obin[ device ] != null ) )
                       		{
                           		obin[device].write( buffer, 0, len ) ;
                       		}
                       		if ( dP[device].report )
                       		{
                           		give_report ( buffer, module_id, length, device ) ;
                       		}
                       		// give to other threads a chance
                       		this.yield() ;
                   	   }
                    }
               }  // not end of image
           }  // while - read an image
         } // try
         catch ( IOException e )
         {
             debug ( true, "Error in address " + Name ) ;
             notify_main (device);
         }
         if ( end == endOfFile )
         {
	         closemyfile ( device );
             System.out.println ( Name + " finished." ) ;
         }
      } // if stop
      else
      {
          try
          {
              // 'stop'
              this.sleep( 2000 ) ;
          }
          catch ( InterruptedException e )
          {
          }
      }
  } // while end
 } // run
 private void closemyfile ( int device )
 {
     end = endOfFile ;
     try
     {
     	if ( socket != null )
        {
        	socket.close ();
            socket = null ;
            outin.close();
        }
        if ( in != null )
        {
           in.close();
           in = null;
        }
        if ( out[device] != null )
        {
            out[device].close();
            out[device] = null;
        }
        if ( obin[device] != null )
        {
            obin[device].close() ;
            obin[device] = null;
        }
        readtime = 0 ;
    }
    catch ( IOException e )
    {
          debug ( true, "Error in closing address' files " + Name ) ;
    }
 }
 // last portion of the image
 private void drawlast ( int device )
 {
    DrawCText dt = null;
    for ( int m = 0; m < my_java.OLTRX + 1; m ++ )
     {
        if ( m == my_java.END ) continue;
        int number = my_java.counters_per_module [ m ];
        for ( int c = 0 ; c < number; c ++ )
        {
             dt = modules [device][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OnePackOnWindow );
             if ( dt != null )
             {
             	if ( modules [device][ m ].counters [ c ].CounterGet() == -1 )
                     dt.paint ( modules [device][ m ].countersD[ c ], getName() );
             }
             dt = modules [device][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OneCounterOnWindow );
             if ( dt != null )
             {
                if ( modules [device][ m ].counters [ c ].CounterGet() == -1 )
                     dt.paint ( modules [device][ m ].countersD[ c ], getName() );
             }
             dt = modules [device][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OneModuleOnWindow );
             if ( dt != null )
             {
                if ( modules [device][ m ].counters [ c ].CounterGet() == -1 )
                     dt.paint ( modules [device][ m ].countersD[ c ], getName() );
             }
        }
    }
 }
/************************************************************************/
//
//  receive the 'stop' / 'continue' signals from the drawing frame
//  send the command to the target : action, read period, draw period and device
/************************************************************************/
 public synchronized void Freeze ( int sf, int dev )
 {
     switch ( sf )
     {
     case my_java.READONCE:
         stop_file = false;
         break;
      case my_java.END:
         end = endOfFile;
         stop_file = true;
         break;
      case my_java.SUSPEND:
         stop_file = true;
         break;
      case my_java.RESUME:
      case my_java.START:
         stop_file = false;
         break;
    }
    if ( dP[dev] != null )
    {
     int max = dP[dev].printTime;
     if ( dP[dev].readTime > max ) max = dP[dev].readTime;
     if ( socket != null )
     {
        byte [] wr ;
        wr = new byte [ 4 ] ;
        wr [ 0 ] = ( byte )sf ;
        wr [ 1 ] = ( byte )dP[dev].readTime;
        wr [ 2 ] = ( byte )max; ;
        wr [ 3 ] = ( byte )dev ;
        try
        {
            outin.write( wr, 0 ,4 ) ;
            this.sleep ( 100 );
        }
        catch ( InterruptedException e )
        {
        }
        catch ( IOException e )
        {
             debug ( true, "Error in sending " + sf + " to address " + Name ) ;
        }
     }
    }
 }
/************************************************************************/
/////////////////////////////////////////////////////////////////////////
//
//  called when the applications is going to be closed - press EXIT
//
/************************************************************************/
/////////////////////////////////////////////////////////////////////////
 public void close_files( )
 {
     for ( int i = 0; i < 2; i ++ )
     {
		Freeze( my_java.END, i );
		closemyfile(i);
	 }
     System.out.println ( Name + " finished." ) ;
 }
/************************************************************************/
//
//  called when the frame of the thread is closed
//
/************************************************************************/
 public void notify_main ( DrawCText killed )
 {
 DrawCText dt = null;
 for ( int m = 0; m < my_java.OLTRX + 1; m ++ )
 {
   if ( m == my_java.END ) continue;
   int number = my_java.counters_per_module [ m ];
   for ( int dev = 0; dev < 2; dev ++ )
   {
      for ( int c = 0 ; c < number; c ++ )
      {
		if ( modules [dev][ m ] != null )
		{
             dt = modules [dev][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OnePackOnWindow );
             if ( dt == killed )
             {
                 modules [dev][ m ].countersD [ c ].SetFrame ( MyConstantsCounters.OnePackOnWindow, null );
             }
             else
             {
                 dt = modules [dev][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OneCounterOnWindow );
                 if ( dt == killed )
                 {
                    modules [dev][ m ].countersD [ c ].SetFrame ( MyConstantsCounters.OneCounterOnWindow, null );
                 }
                 else
                 {
                     dt = modules [dev][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OneModuleOnWindow );
                     if ( dt == killed )
                     {
                         modules[dev] [ m ].countersD [ c ].SetFrame ( MyConstantsCounters.OneModuleOnWindow, null );
                     }
                 }
             }
		 }
		}
     }
    }
     boolean exist = false;
     for ( int m = 0; ( ( m < my_java.OLTRX + 1 ) && ! exist ) ; m ++ )
     {
         if ( m == my_java.END ) continue;
         int number = my_java.counters_per_module [ m ];
         for ( int dev = 0; dev < 2; dev ++ )
		 {
         for ( int c = 0 ; c < number; c ++ )
         {
			 if ( modules [dev][ m ] != null )
			 {
             dt = modules [dev][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OnePackOnWindow );
             if ( dt != null )
             {
                 exist = true;
                 break;
             }
             dt = modules [dev][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OneCounterOnWindow );
             if ( dt != null )
             {
                 exist = true;
                 break;
             }
             dt = modules [dev][ m ].countersD [ c ].GetFrame ( MyConstantsCounters.OneModuleOnWindow );
             if ( dt != null )
             {
                 exist = true;
                 break;
             }
		 }
	 }
    }
   }
     if ( ! exist )
     {
        close_files ( ) ;
        mm.kill ( Name ) ;
     }
 }
/************************************************************************/
//
// end the thread and close frame etc. when the target closes or fails
//
/************************************************************************/
 public void notify_main ( int dev )
 {
     for ( int m = 0; m < my_java.OLTRX + 1; m ++ )
     {
         if ( m == my_java.END ) continue;
         int number = my_java.counters_per_module [ m ];
         for ( int c = 0 ; c < number; c ++ )
         {
             modules [dev][ m ].countersD [ c ].SetFrame ( MyConstantsCounters.OnePackOnWindow, null );
             modules [dev][ m ].countersD [ c ].SetFrame ( MyConstantsCounters.OneCounterOnWindow, null );
             modules [dev][ m ].countersD [ c ].SetFrame ( MyConstantsCounters.OneModuleOnWindow, null );
         }
     }
     close_files ( ) ;
     mm.kill ( Name ) ;
     if ( Index != -1 )
       MyConstantsCounters.dDB[dev].EachOne [ Index ] = null ;
 }
 /******************************************************************/
 //  receives a buffer with all the counters of a module
 //  receives the number of the counters
 /******************************************************************/
 private void Paint ( int module_id, int length, byte [] buffer, int dev )
 {

     int type ;				// counter type
     int height ;		    // counter value
     byte temp [] = new byte [ counterValue ];
     byte temp2 [] = new byte [ counterCounter ];

     // for every counter
     for ( int i = 0; i < length; i ++ )
     {
         // take its type
         for ( int j = 0; j < counterCounter ; j ++ )
         {
             temp2 [ j ] = buffer [ i * counterLength + j ];
         }
         type = byte_to_int ( temp2 );
         // take its value
         for ( int j = 0; j < counterValue; j ++ )
         {
             temp [ j ] = buffer [ counterCounter + counterLength * i + j ] ;
         }
         height = byte_to_int ( temp );
         debug ( false, "Counter index = " + type + " counter_value = "  + height ) ;
         try
         {
             // update the drawing data base 
             // it will be used by all frames displaying this pack/module/counter
            if ( modules [dev][ module_id ].counters [ type ].CounterGet() == -1 )
            {
                modules [dev][ module_id ].countersD [ type ].SetValue ( height );
            }
         }
         catch ( NullPointerException npe )
         {
             if ( modules [dev][ module_id ] == null )
                 debug ( true, "module=" + module_id );
             else
             {
                 if ( modules [dev][ module_id ].counters [ type ] == null )
                    debug ( true, "Counter module=" + module_id + " type=" + type );
                 if ( modules [dev][ module_id ].countersD [ type ] == null )
                     debug ( true, "CounterD module=" + module_id + " type=" + type );
             }
         }
     }
 }
/************************************************************************/
//
//   print debug messages
//
/************************************************************************/
 private void debug ( boolean doflag, String str )
 {
     if ( doflag ) //|| MyConstants.counterdebug_flag )
     {
         System.out.println( "ReadSocket : " + str ) ;
     }
 }
 } //ReadSocket class
