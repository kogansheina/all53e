package all53e;
import java.awt.*;
import java.util.* ;
import java.lang.*;

class deviceDataBase
{
	public drawParameters countersParametrs ;
	public DrawCText [] FixInOne;
	public DrawCText [] EachOne;
	public DrawCText AllInOne;
	public boolean allIn ;

    // build the data base - per device used by drawing option
	public deviceDataBase ( int device )
	{
		countersParametrs = new drawParameters ( device );
		FixInOne = new DrawCText [ my_java.OLTRX + 1 ];
		for ( int y = 0; y < my_java.OLTRX + 1; y ++ )
		{
		     FixInOne [ y ] = null;
		}
		EachOne = new DrawCText [ MyConstants.ips ];
		for ( int y = 0; y < MyConstants.ips; y ++ )
		{
		    EachOne [ y ] = null;
		}
		AllInOne = null;
		allIn = false;
	}
}
