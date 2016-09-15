package all53e;
import java.awt.* ;
import java.lang.* ;
// common file with similar h files on targets - for counters send sake
class my_java
{
    public static final int TX = 0;
    public static final int RX = 1;
    public static final int UTOPIA = 2;
    public static final int TXFIFO = 3;
    public static final int RXFIFO = 4;
    public static final int FPGA = 5;
    public static final byte END = 6 ;
    public static final int END_IMAGE = 254 ;
    public static final byte SUSPEND = 1 ;
    public static final byte RESUME = 0 ;
    public static final byte START = 2 ;
    public static final byte STOP = 3 ;
    public static final byte READONCE = 4 ;
    public static final byte OLTTX = END + TX + 1 ;
    public static final byte OLTRX = OLTTX + 1 ;
    public static final int CS_CONNECTION_PORT = 30000 ;
    public static final int MaxTconts = 256;
    public static int onufactor = 2;
    public static int tcontfactor = 4;
    public static final int [] counters_per_module = 
    {
        16+3, // onu tx
        6, // onu rx
        16*2,// utopia
        16*2,// txfifo
        16*2,// rxfifo
        5,// fpga
        0, // end
        2, // olt tx
        MaxTconts * tcontfactor + 1 // olt rx
    };
}
