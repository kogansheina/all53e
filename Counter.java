package all53e;
import java.lang.* ;
/////////////////////////////////////////////////////////////////////////
//
//   Counter - implements a counter
//
/////////////////////////////////////////////////////////////////////////
class Counter
{
    private int id ;
    private int module ;
    private int value ;

    public Counter ()
    {
    }
    public Counter ( int module_id, int type )
    {
		module = module_id ;
        id = type ;
        value = type ;
    }
    public void CounterSet ( )
    {
        value = -1 ;
    }
    public int CounterGet ( )
    {
        return value ;
    }
    public int CounterId ( )
    {
        return id ;
    }
    public int CounterModule ( )
    {
        return module ;
    }
}
