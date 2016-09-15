package all53e;
import java.io.* ;
import java.lang.* ;
import java.util.* ;
import java.awt.* ;
import java.awt.FontMetrics.* ;

class Module extends Object
{
  public Counter counters [] ;
  public CounterDisplay countersD [] ;
  private int id ;

  public Module ( int id )
  {
      this.id = id;
      int number = my_java.counters_per_module [ id ];
      counters = new Counter [ number ] ;
      countersD = new CounterDisplay [ number ] ;
      for ( int i = 0; i < number; i ++ )
      {
        counters [ i ] = new Counter ( id, i ) ;
        countersD [ i ] = new CounterDisplay ( i, id ) ;
      }
  }
}
