package all53e;
import java.io.* ;
import java.lang.* ;
import java.lang.Thread.* ;
import java.awt.* ;
import java.awt.event.* ;
import java.util.* ;

class CounterDisplay
{
    private int id ;
    private int module ;
	private DrawCText PackOnFrame = null;
	private DrawCText CounterOnFrame = null;
	private DrawCText AllModulesOnFrame = null;
	private int xPackOnFrame,yPackOnFrame;
	private int xCounterOnFrame,yCounterOnFrame;
	private int xAllModulesOnFrame,yAllModulesOnFrame;
	private int oldValue;
    private int newValue;
    private int first = -1;

    public CounterDisplay ( )
    {
    }
    public CounterDisplay ( int id, int module )
    {
        this.id = id;
        this.module = module;
	    oldValue = -1;
	    newValue = -1;
        first = -1;
    }
    public void SetFrame ( int type, DrawCText frame)
    {
	    switch ( type )
	    {
	   	 case MyConstantsCounters.OnePackOnWindow:
	   	 	PackOnFrame = frame;
	   	 	break;
	   	 case MyConstantsCounters.OneCounterOnWindow:
	   	    CounterOnFrame = frame;
	   	    break;
	   	 case MyConstantsCounters.OneModuleOnWindow:
	   	    AllModulesOnFrame = frame;
	   	    break;
	    }
	}
	public void SetValue ( int value )
	{
	    oldValue = newValue;
	    newValue = value;
        switch ( first )
        {
        case -1:
        case 0:
            first ++;
            break;
        }
	}
	public void SetCoordinateX ( int type, int x )
	{
	    switch ( type )
	    {
	    	case MyConstantsCounters.OnePackOnWindow:
	    		xPackOnFrame = x;
	    		break;
	    	case MyConstantsCounters.OneCounterOnWindow:
	    		xCounterOnFrame = x;
	    		break;
	    	case MyConstantsCounters.OneModuleOnWindow:
	    		xAllModulesOnFrame = x;
	    		break;
	    }
	}
	public void SetCoordinateY ( int type, int y )
	{
		switch ( type )
		{
			 case MyConstantsCounters.OnePackOnWindow:
			 	yPackOnFrame = y;
			 	break;
			 case MyConstantsCounters.OneCounterOnWindow:
			 	yCounterOnFrame = y;
			 	break;
			 case MyConstantsCounters.OneModuleOnWindow:
			 	yAllModulesOnFrame = y;
			 	break;
		}
	}
	public int GetCoordinateX ( int type )
	{
	    int retValue = 0;
		switch ( type )
		{
			 case MyConstantsCounters.OnePackOnWindow:
			 	retValue = xPackOnFrame;
			 	break;
			 case MyConstantsCounters.OneCounterOnWindow:
			 	retValue = xCounterOnFrame;
			 	break;
			 case MyConstantsCounters.OneModuleOnWindow:
			 	retValue = xAllModulesOnFrame;
			 	break;
		}
		return retValue;
	}
	public int GetCoordinateY ( int type )
	{
	     int retValue = 0;
		 switch ( type )
		 {
		 	case MyConstantsCounters.OnePackOnWindow:
		 		 retValue = yPackOnFrame;
		 		 break;
		 	case MyConstantsCounters.OneCounterOnWindow:
		 		 retValue = yCounterOnFrame;
		 		 break;
		 	case MyConstantsCounters.OneModuleOnWindow:
		 		 retValue = yAllModulesOnFrame;
		 		 break;
		 }
		 return retValue;
	}
	public DrawCText GetFrame ( int type )
	{
		DrawCText retValue = null;
		switch ( type )
		{
			 case MyConstantsCounters.OnePackOnWindow:
			 	 retValue = PackOnFrame;
			 	 break;
			 case MyConstantsCounters.OneCounterOnWindow:
			 	 retValue = CounterOnFrame;
			 	 break;
			 case MyConstantsCounters.OneModuleOnWindow:
			 	 retValue = AllModulesOnFrame;
			 	 break;
		}

		return retValue;
	}
    public int GetOldValue()
    {
        return oldValue;
    }
    public int GetNewValue()
    {
        return newValue;
    }
    public int GetModule()
    {
        return module;
    }
    public int GetId()
    {
        return id;
    }
    public int GetState()
    {
        return first;
    }
}
