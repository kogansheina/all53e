package all53e;
import java.awt.* ;
import java.util.* ;
import java.io.* ;
import javax.swing.*;
import java.beans.*;

/************************************************************************/
//
// The application is intended to draw graphically the ONU/OLT counters.
// The input data is received either from a binary file either 'on line'
// through IP connection with any of the involved cards. The number of
// card/files to be represented simultaneously is unlimited ( besides the
// computer power or/and the screen size ). A record into the file or sent
// through connection has the following format ( binary bytes ):
//
// |-----------|--------------------|------------|---------------|-----|------------|---------------|
// | module id | number of counters | counter id | counter value | ... | counter id | counter value |
// |-----------|--------------------|------------|---------------|-----|------------|---------------|
//      1                 4               2             4                    2              4
//
// Counters'id is not limited ; counters' number per module is limited to 50.
// The end of connection ( file too, even if it is not essential ) is a record with
// the id equal to END.
//
// There are a few things connected to the fact that the tool was born for EVBx counters;
// such as the modules number and their names and counters specifications ( in HELP ).
//
// The data received on a connections may be 'saved' in a binary file and analyzed futher.
// The data may be 'translated' into a report ASCII file. The files' name are suffixed by the
// correspondent name; they are the file/address requested and the time ( in miliseconds ) from
// the java birth.
//
// The data may be vizualized as flow's histograme for each module.
//
// The data may be vizualized as a graph of a counter of a module.
//
/************************************************************************/

/////////////////////////////////////////////////////////////////////////
//
//    the main thread waits for a signal from the dialog and
//    creates a new thread for each good set of recived parameters
//
/////////////////////////////////////////////////////////////////////////

class MyMain extends Thread
{
 private Vector<ReadSocket> vs = new Vector<ReadSocket> ( 0, 1 ) ;
 private LinkedList<drawParameters> what = new LinkedList<drawParameters>();

 public void MyMain ( )
 {
 }
 // register a drawing request
 private void SetFrame ( ReadSocket rs, drawParameters parameters_for_thread )
 {
     int dev = parameters_for_thread.device;
     Integer D = new Integer ( dev );
     String ds = D.toString();

     if ( ( parameters_for_thread.max_x == 0 ) || ( parameters_for_thread.max_y == 0 ) )
         return;
     if ( parameters_for_thread.all_in_one )
     {
        if ( ! parameters_for_thread.fixForm )
        {
             if ( MyConstantsCounters.dDB[dev].AllInOne == null )
             {
                 // create the window for 'all in one' option - first request
                  MyConstantsCounters.dDB[dev].AllInOne = new DrawCText ( "All @ " + ds, -1, parameters_for_thread.max_x, parameters_for_thread.max_y, rs, MyConstantsCounters.OneCounterOnWindow );
                  try
                  {
                      this.sleep ( 1000 );
                  }
                  catch ( InterruptedException e )
                  {
                  }
             }
             else
             {
                 // any futher request to 'all in one' - add the IP to its data base
                 MyConstantsCounters.dDB[dev].AllInOne.AddCaller( rs );
             }
        } // all
        else
        {
            // the request to 'get' some counters of an IP into a window
            Counter cc = ( Counter ) parameters_for_thread.graphs.get ( 0 ) ;
            if ( MyConstantsCounters.dDB[dev].FixInOne [ cc.CounterModule() ] == null )
            {
                // first required counter - open the window
                MyConstantsCounters.dDB[dev].FixInOne [ cc.CounterModule() ] = new DrawCText ( "All @ " + ds + " : " + MyConstantsCounters.gs_title [ cc.CounterModule() ], cc.CounterModule(), parameters_for_thread.max_x, parameters_for_thread.max_y, rs, MyConstantsCounters.OneModuleOnWindow );
                try
                {
                    this.sleep ( 1000 );
                }
                catch ( InterruptedException e )
                {
                }
            }
            else
            {
                // any futher request - register it
                MyConstantsCounters.dDB[dev].FixInOne [ cc.CounterModule() ].AddCaller( rs );
            }
        }  // fix
     } // concentrate form
     else
     {
         // request for 'print' all the counters of an IP into a window
         for ( int y = 0; y < MyConstants.ips; y ++ )
         {
             if ( MyConstantsCounters.dDB[dev].EachOne [ y ] == null )
             {
                 rs.Index = y;
                 MyConstantsCounters.dDB[dev].EachOne [ y ] = new DrawCText ( rs.getName() + " @ " + ds, y, parameters_for_thread.max_x, parameters_for_thread.max_y, rs, MyConstantsCounters.OnePackOnWindow );
                 break;
             }
         }
     }
     // nothing to do - thread go sleep
     try
     {
         this.sleep ( 500 );
     }
     catch ( InterruptedException e )
     {
     }
 }
/************************************************************************/
//
//  run the main thread to create/kill ip threads
//
/************************************************************************/
 public void run()
 {
     String file_name = "";
     String original = "";

     // if any request registered
     if ( ! what.isEmpty() )
     {
        // take it
        drawParameters parameters_for_thread = ( drawParameters )what.removeFirst();
        // translate the owner againt the run parameters - for the generic lop cases
        file_name = MyConstants.lookForPair ( parameters_for_thread.file_name );
        original = parameters_for_thread.original;
        debug ( true, "get "+ file_name );
        boolean notFound = true ;
        int ii = -1;
        // check if the IP i already connected for counters
        for ( int i = 0; ( i < vs.size() ) && notFound; i ++ )
        {
            ReadSocket rf = ( ReadSocket ) vs.get ( i ) ;
            if ( original.equals( rf.getName() ) )
            {
                notFound = false ;
                ii = i;
                break;
            }
        }
        if ( notFound )
        {
            // first request of the given IP for counters
            // create its thread
             ReadSocket rs = new ReadSocket( ) ;
             rs.setName( original ) ;
             vs.add ( rs ) ;
             // pass its parameters to the thread ( name, options, counters )
             rs.SetParameters ( parameters_for_thread, this, file_name ) ;
             // open connection
             if ( rs.open_connection () )
             {
                 // if success - open the window and update the drawing data base
                if ( ! parameters_for_thread.storeForm )
                {
                    SetFrame ( rs, parameters_for_thread );
                    rs.SetMoreParameters ( parameters_for_thread ) ;
                }
                // open save and report files - if requested
                rs.open_files ( parameters_for_thread.device ) ;
                // start the receiveing thread
                rs.start() ;
                if ( ( parameters_for_thread.storeForm ) && ( ! parameters_for_thread.stopForm ) )
                {
                    // for 'store' function - no window s built, 
                    // therefore a 'start' command must be sent to target
                    rs.Freeze ( my_java.START, parameters_for_thread.device );
                }
             } // connection done
//             else
//                JOptionPane.showMessageDialog( new JFrame(), "Cannot open a connetion for " + file_name, "Warning", JOptionPane.WARNING_MESSAGE );
        }
        else  // found
        {
            // the IP is already registered for some options
            ReadSocket rs = ( ReadSocket ) vs.get ( ii ) ;
            if ( ( ! parameters_for_thread.storeForm ) && ( parameters_for_thread.stopForm ) )
            {
                // if 'stop' function - then stop the target to send its counters
                rs.Freeze ( my_java.END, parameters_for_thread.device );
            }
            else
            {
                // if not 'store' function - add the new requested options
                if ( ! parameters_for_thread.storeForm )
                {
                    SetFrame ( rs, parameters_for_thread );
                    rs.SetMoreParameters ( parameters_for_thread ) ;
                }
                // open files , if needed and not yet open
                rs.open_files ( parameters_for_thread.device ) ;
                if ( parameters_for_thread.storeForm )
                {
                    // if 'start' , send start to target
                    rs.Freeze ( my_java.START, parameters_for_thread.device );
                }
            }
        } // found
        this.yield();
    }
    this.yield();
 }
 // register any request and serialized them
 public void MyMainRun ( drawParameters parameters )
 {
    drawParameters parameters_to_run = new drawParameters ( parameters );
    String file_name =  MyConstants.lookForPair ( parameters_to_run.file_name );
    parameters_to_run.original = file_name;        
    if ( MyConstants.alias != null )
    {
        file_name = ( String ) MyConstants.alias.get ( parameters_to_run.original );
        parameters_to_run.file_name = file_name;
    }
    if ( file_name == null )
    {
//        JOptionPane.showMessageDialog( new JFrame(), parameters_to_run.original + " is not defined", "Warning", JOptionPane.WARNING_MESSAGE );
        parameters_to_run.file_name = parameters_to_run.original;
    }
    debug ( true, "set " + file_name );
    what.addLast( parameters_to_run );
    SwingUtilities.invokeLater( this );

 } // run
/////////////////////////////////////////////////////////////////////////
//
//   wait to EXIT click and close all the opened files
//
/////////////////////////////////////////////////////////////////////////
 public synchronized void GoExit(  )
 {
    // close all the opened files/sockets - in case the application is closed before they finished
    try
    {
        for ( int i = 0; i < vs.size(); i ++ )
        {
            ReadSocket rf = ( ReadSocket ) vs.get ( i ) ;
            rf.close_files ( ) ;
        }
    }
    catch ( Exception e )
    {
        System.exit ( 0 );
    }
 }
/////////////////////////////////////////////////////////////////////////
//
//   remove a thread from the list, when it closed its window
//
/////////////////////////////////////////////////////////////////////////
 public void kill ( String file_to_kill_parameter )
 {
   String file_to_kill = file_to_kill_parameter ;
   try
   {
       if ( MyConstants.alias != null )
       {
          file_to_kill  = ( String ) MyConstants.reversealias.get ( file_to_kill_parameter );
       }
       if ( ( file_to_kill == null ) || ( file_to_kill.equals("") ) ) file_to_kill = file_to_kill_parameter ;
       int len = vs.size();
       ReadSocket rf = null;
       for ( int i = 0; i < len; i ++ )
       {
           rf = ( ReadSocket ) vs.get ( i ) ;
           if ( file_to_kill.equals( rf.getName() ) )
           {
               break;
           }
       }
       if ( rf != null )
       {
           debug ( true, "kill " + file_to_kill );
           if ( rf.Index != -1 )
           {
              MyConstantsCounters.dDB[0].EachOne [ rf.Index ] = null;
              MyConstantsCounters.dDB[1].EachOne [ rf.Index ] = null;
		  }
          vs.remove( rf );
       }
       else
       {
           debug ( true, "do not find to kill " + file_to_kill );
       }
   }
   catch ( Exception e )
   {
   }
 }
/////////////////////////////////////////////////////////////////////////
//
//   print debug messages
//
/////////////////////////////////////////////////////////////////////////
 private void debug ( boolean doflag, String str )
 {
     if ( doflag )
     {
         System.out.println( "MyMain : " + str ) ;
     }
 }
}// class

