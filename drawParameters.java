package all53e;
import java.awt.*;
import java.util.* ;
import java.lang.*;

/************************************************************************/
//
// used as a structure to contain all the drawing parameters for an address/file
//
/************************************************************************/
class drawParameters
{
    public int max_x = MyConstants.Screen_x;
    public int max_y = MyConstants.Screen_y;
    public boolean report = false ;
    public boolean save = false ;
    public boolean type = false;
    public boolean all_in_one = false;// different counters from different packs in an window
    public String file_name = "";
    public String original = "";
    public Vector<Counter> graphs ;
    public boolean draw_error = false;
    public String report_file = "";
    public String save_file = "";
    public int readTime;
    public int printTime;
    public boolean fixForm; // same counter type from all packs in an window
    public boolean storeForm;
    public boolean stopForm;
    public boolean defined;
    public int device;

/************************************************************************/
//
// construct an empty structure
//
/************************************************************************/
    public drawParameters ( int device )
    {
       graphs = new Vector<Counter> ( 0,1 );
       clear();
       this.device = device;
    }
    public drawParameters ( drawParameters base )
    {
       graphs = new Vector<Counter> ( 0,1 );
       clear();
       this.device = base.device;
       this.max_x = base.max_x;
       this.max_y = base.max_y;
       this.report = base.report ;
       this.save = base.save ;
       this.type = base.type;
       this.file_name = base.file_name;
       this.original = base.original;
       for ( int y=0; y < base.graphs.size(); y ++ )
       {
           this.graphs.add( (Counter) base.graphs.get( y ) ) ;
       }
       this.report_file = base.report_file;
       this.save_file = base.save_file;
       this.fixForm = base.fixForm;
       this.storeForm = base.storeForm;
       this.stopForm = base.stopForm;
       this.all_in_one = base.all_in_one;
       this.readTime = base.readTime;
       this.printTime = base.printTime;
       this.defined = base.defined;
    }
/************************************************************************/
//
// clear the structures' fields
//
/************************************************************************/
    public void clear ()
    {
        draw_error = false;
		max_x = MyConstants.Screen_x;
		max_y = MyConstants.Screen_y;
		report = false ;
		save = false ;
		type = false;
		file_name = "";
		original = "";
		graphs.clear() ;
        report_file = "";
        save_file = "";
        fixForm = false;
        storeForm = false;
        stopForm = false;
        all_in_one = false;
        readTime = MyConstantsCounters.readTimeout;
        printTime = MyConstantsCounters.printTimeout;
        defined = false;
	}
}
