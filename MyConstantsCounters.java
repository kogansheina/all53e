package all53e;
import java.awt.* ;
import java.lang.* ;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.io.* ;

/////////////////////////////////////////////////////////////////////////
//
//               %%%% MyConstants %%%%
//
/////////////////////////////////////////////////////////////////////////
class MyConstantsCounters
{   
/* for print counters */
    public static MyMain mymain;
	public static final int Tcont = 2;
    public static int readTimeout = 10;
    public static int printTimeout = 10;
    public static int y_s = 11 ;   // the height of the string letters     
    public static int y_s1 = y_s ;        // the height of the string letters
    public static int y_s2 = y_s - 1 ;    // the height of the string letters
    public static int next_x = 13 * y_s1 ;
    public static int next_y = 18 ;
    public static final int begin_y = 90 ; // begin the graph
    public static final int begin_s = 65 ; // begin the string
    public static final int begin_x = 70 ; // begin the graph
    public static final int begin_xA = 10 ;
    public static final int begin_yA = 90 ;
    public static final int count = 2 ;
    public static final int countSpace = 60;
    public static String screen_default = "850,990" ;
    public static final int deltax = 30 ;
    public static final int deltay = 5 ;
	public static final int OnePackOnWindow = 0;   // all the counters of a pack into an window
	public static final int OneCounterOnWindow = 1;// classical 'all-in-one' = different counters of differrent modules of different packs
	public static final int OneModuleOnWindow = 2; // classical 'fix' = counters of the same module from all packs

    // global data for device
    public static deviceDataBase [] dDB;
    // constants related to packs
    public static int MaxCountersPerType = my_java.MaxTconts * my_java.tcontfactor + 3;
    public static int max_buffer = MaxCountersPerType * 6 ;


    // strings
    public static final String [] rx_titles =
    {
	        "  Valid         ",
	        "Urcv          ",
		    "Disd           ",
		    "Avg           ",
		    "Grant   ",
		    "Invalid  "
    };
    public static final String [] tx_titles =
    {
		    "Non matched vpi ",
		    "Grant underrun  "
	};

    public static final String [][] titles =
    {
        {
            "Grant     0     ",
            "Grant     1     ",
            "Grant     2     ",
            "Grant     3     ",
            "Grant     4     ",
            "Grant     5     ",
            "Grant     6     ",
            "Grant     7     ",
            "Grant     8     ",
            "Grant     9     ",
            "Grant     10    ",
            "Grant     11    ",
            "Grant     12    ",
            "Grant     13    ",
            "Grant     14    ",
            "Grant     15    ",
            "Idle cells      ",
            "Total cells     "
        },
        {
           "Hec discarded    ",
           "Hec corrected    ",
           "Msg CRC errors   ",
           "BIP errors       ",
           "Correct cells    ",
           "Grant CRC error  "
        },
        {
           "Phy   0          ",
           "Phy   1          ",
           "Phy   2          ",
           "Phy   3          ",
           "Phy   4          ",
           "Phy   5          ",
           "Phy   6          ",
           "Phy   7          ",
           "Phy   8          ",
           "Phy   9          ",
           "Phy   10         ",
           "Phy   11         ",
           "Phy   12         ",
           "Phy   13         ",
           "Phy   14         ",
           "Phy   15         "
        },
        {
           "Que 0  valid   ",
           "Que 0  discard ",
           "Que 1  valid   ",
           "Que 1  discard ",
           "Que 2  valid   ",
           "Que 2  discard ",
           "Que 3  valid   ",
           "Que 3  discard ",
           "Que 4  valid   ",
           "Que 4  discard ",
           "Que 5  valid   ",
           "Que 5  discard ",
           "Que 6  valid   ",
           "Que 6  discard ",
           "Que 7  valid   ",
           "Que 7  discard ",
           "Que 8  valid   ",
           "Que 8  discard ",
           "Que 9  valid   ",
           "Que 9  discard ",
           "Que 10 valid   ",
           "Que 10 discard ",
           "Que 11 valid   ",
           "Que 11 discard ",
           "Que 12 valid   ",
           "Que 12 discard ",
           "Que 13 valid   ",
           "Que 13 discard ",
           "Que 14 valid   ",
           "Que 14 discard ",
           "Que 15 valid   ",
           "Que 15 discard ",
           "Que 16 valid   ",
           "Que 16 discard ",
        },
        {
           "Que 0  valid   ",
           "Que 0  discard ",
           "Que 1  valid   ",
           "Que 1  discard ",
           "Que 2  valid   ",
           "Que 2  discard ",
           "Que 3  valid   ",
           "Que 3  discard ",
           "Que 4  valid   ",
           "Que 4  discard ",
           "Que 5  valid   ",
           "Que 5  discard ",
           "Que 6  valid   ",
           "Que 6  discard ",
           "Que 7  valid   ",
           "Que 7  discard ",
           "Que 8  valid   ",
           "Que 8  discard ",
           "Que 9  valid   ",
           "Que 9  discard ",
           "Que 10 valid   ",
           "Que 10 discard ",
           "Que 11 valid   ",
           "Que 11 discard ",
           "Que 12 valid   ",
           "Que 12 discard ",
           "Que 13 valid   ",
           "Que 13 discard ",
           "Que 14 valid   ",
           "Que 14 discard ",
           "Que 15 valid   ",
           "Que 15 discard ",
           "Que 16 valid   ",
           "Que 16 discard ",
        },

        {
           "Injected cells   ",
           "Good cells       ",
           "Bad  cells       "
        }
    };
    public static final String [][] fix_titles =
    {
        {
            "Grt 0  ",
            "Grt 1  ",
            "Grt 2  ",
            "Grt 3  ",
            "Grt 4  ",
            "Grt 5  ",
            "Grt 6  ",
            "Grt 7  ",
            "Grt 8  ",
            "Grt 9  ",
            "Grt 10 ",
            "Grt 11 ",
            "Grt 12 ",
            "Grt 13 ",
            "Grt 14 ",
            "Grt 15 "
        }, // onu tx
        {
           "Hec discarded    ",
           "Hec corrected    ",
           "Msg CRC    ",
           "BIP errors       ",
           "Correct cells    ",
           "Grant CRC   "
        },  // on rx
        {
           "Phy 0   ",
           "Phy 1   ",
           "Phy 2   ",
           "Phy 3   ",
           "Phy 4   ",
           "Phy 5   ",
           "Phy 6   ",
           "Phy 7   ",
           "Phy 8   ",
           "Phy 9   ",
           "Phy 10  ",
           "Phy 11  ",
           "Phy 12  ",
           "Phy 13  ",
           "Phy 14  ",
           "Phy 15  "
        },  // utopia
        {
            "Que 0  ",
            "Que 1  ",
            "Que 2  ",
            "Que 3  ",
            "Que 4  ",
            "Que 5  ",
            "Que 6  ",
            "Que 7  ",
            "Que 8  ",
            "Que 9  ",
            "Que 10 ",
            "Que 11 ",
            "Que 12 ",
            "Que 13 ",
            "Que 14 ",
            "Que 15 ",
        }, // txfifo
        {
            "Que 0  ",
            "Que 1  ",
            "Que 2  ",
            "Que 3  ",
            "Que 4  ",
            "Que 5  ",
            "Que 6  ",
            "Que 7  ",
            "Que 8  ",
            "Que 9  ",
            "Que 10 ",
            "Que 11 ",
            "Que 12 ",
            "Que 13 ",
            "Que 14 ",
            "Que 15 ",
        }, // rxfifo
        {
           "Injected cells   ",
           "Good cells       ",
           "Bad  cells       "
        }, // fpga
        {
            // dummy
        },
        {
            "Non match vpi",
            "Grant underrun"
        }, // olt tx
        {
            "Valid",
            "Unreceived",
            "Discarded",
            "Average "
        } // olt rx
    };
    public static final String[] gs_title =
    {
        "Txpon", // 0
        "Rxpon", // 1
        "Utopia", // 2
        "TxFifo", // 3
        "RxFifo", // 4
        "Fpga", // 5
        "",
        "Txpon Olt",
        "Rxpon Olt"
    } ;
    public static final double [] x_count =
    {
		0,
		0,
		0.3,
		0.3,
		0.6,
		0.6
	};
	public static final double [] y_count =
	{
		0,
		0.3,
		0,
		0.3,
		0.3,
		0
	};
	public static final double [] y_countolt =
	{
		0,
		0.15,
		0,
		0,
		0,
		0
	};
	public static final double [] x_countolt =
	{
		0,
		0,
		0.2,
		0.4,
		0.6,
        0.8
	};
    public static final String [] onuolt =
    {
        "onu",
        "olt"
    };
    public static final String [] sys_fpga_option =
    {
        "all",
        "Injected",
        "Good",
        "Bad"
    };
    public static final String [] onu_rx =
    {
       "HecDiscarded",
       "HecCorrected",
       "MsgCRC",
       "BIP",
       "Correct",
       "GrantCRC"
    };
    public static final String [] olt_rx =
    {
        "Grant",
        "Invalid"
    };
    public static final String [] onu_tx =
    {
        "Grant",
        "Idle",
        "Total"
    };
    public static final String [] olt_tx =
    {
        "Vpi",
        "Grant"
    };
}
/* end print counters */
