package all53e;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.* ;
import javax.swing.border.Border;
import java.beans.*;

/************************************************************************/
//
// class to define the counters to be drawn/displayed   etc.
//
/************************************************************************/
class CounterOptions extends JFrame implements ItemListener
{
	static final long serialVersionUID = 103L;
	private JCheckBox cb [ ];
	private drawParameters countersParametrs;
	private String [] onuolt =
	{
		"onu",
		"olt"
	};
	private Counter ff = null;
	private int optionNumber;
	private Vector<Counter> v = null;
    private String all;
    private int module;
    private int tcontfactor = my_java.tcontfactor;

/************************************************************************/
//
// constructs the class, for a module and a type of filter
//
/************************************************************************/
	public CounterOptions ( int module, String moduleName, Vector<Counter> vec, String all )
	{
		super ( "Counter for " + moduleName );
		String s1 = "";
		String s = "";
		int queue = 0;
		optionNumber = 0;
		v = vec;
        this.all = all;
        this.module = module;
		switch ( module )
		{
            // tx
			case my_java.TX:

        		s1 = dialog ( moduleName, onuolt, all );
                // ask onu or olt
        		if ( ( s1 != null ) && ( s1.length() > 0 ) )
        		{
            		if ( s1.equals ( "onu" ) )
            		{
						optionNumber = 3;
					}
					else
					{
                        if ( s1.equals ( "all" ) )
                        {
                            ff = new Counter ( module, -1 ) ;
                            v.add( ff );
                        }
                        else
                        {
                            optionNumber = 2;
                        }
					}
				}
				break;

			case my_java.RX:

        		s1 = dialog ( moduleName, onuolt, "" );
                // ask onu or olt
        		if ( ( s1 != null ) && ( s1.length() > 0 ) )
        		{
            		if ( s1.equals ( "onu" ) )
            		{
						optionNumber = 6;
					}
					else
					{
                        // if not all, then there are 3 classes of counters in olt rx
                        if ( s.equals ( "all" ) )
                        {
                            ff = new Counter ( module, -1 ) ;
                            v.add( ff );
                        }
                        else optionNumber = 3;
					}
				}
				else s1 = "onu";
				break;

			case my_java.TXFIFO:
            case my_java.RXFIFO:

        		s = dialog ( "queue number", null, all );
        		if ( ( s != null ) && ( s.length() > 0 ) )
        		{
                        UtilsCounters.getOptionsPerModule( module, s, v, -1 );
                }
				break;

			case my_java.UTOPIA:

        		s = dialog ( "phy number", null, all );
        		if ( ( s != null ) && ( s.length() > 0 ) )
        		{
                    if ( s.equals ( "all" ) )
                    {
                        ff = new Counter ( module, -1 ) ;
                        v.add( ff );
                    }
                    else
                    {
                       UtilsCounters.getOptionsPerModule( module, s, v, -1 );
                    }
				}
				break;

			case my_java.FPGA:

				optionNumber = 3;
				break;

		}
		if ( optionNumber > 0 )
		{
			cb = new JCheckBox [ optionNumber + 1 ];
	    	JPanel pn = new JPanel ( new GridLayout( optionNumber, 1 ), false );
	    	switch ( module )
			{
				case my_java.TX:

			    	if ( s1.equals ( "onu" ) )
			        {
						cb [ 0 ] = new JCheckBox( "grant", false );
	    				cb [ 0 ].addItemListener( ILonutx () );
	    				for ( int k = 1; k < optionNumber; k ++ )
	    				{
							cb [ k ] = new JCheckBox( MyConstantsCounters.titles [ module ][ 15 + k ], false );
		        			cb [ k ].addItemListener( ILonutx () );
						}
					}
					else
					{
						cb [ 0 ] = new JCheckBox( MyConstantsCounters.tx_titles [ 0 ], false );
				        cb [ 0 ].addItemListener( ILolttx () );
						cb [ 1 ] = new JCheckBox( MyConstantsCounters.tx_titles [ 1 ], false );
				        cb [ 1 ].addItemListener( ILolttx () );
					}
					break;

				case my_java.RX:
			        if ( s1.equals ( "onu" ) )
			        {
						for ( int k = 0; k < optionNumber; k ++ )
	    				{
							cb [ k ] = new JCheckBox( MyConstantsCounters.titles [ module ][ k ], false );
		        			cb [ k ].addItemListener( ILonurx () );
						}
					}
					else
					{
						cb [ 0 ] = new JCheckBox( "grant", false );
		        		cb [ 0 ].addItemListener( ILoltrx () );
						cb [ 1 ] = new JCheckBox( "Invalid grants", false );
		        		cb [ 1 ].addItemListener( ILoltrx () );
                        cb [ 2 ] = new JCheckBox( "Ranging", false );
                        cb [ 2 ].addItemListener( ILoltrx () );
					}
					break;

				case my_java.TXFIFO:
				case my_java.UTOPIA:
				case my_java.RXFIFO:

					break;

				case my_java.FPGA:

					cb [ 0 ] = new JCheckBox( "Injected", false );
		        	cb [ 0 ].addItemListener( ILfpga () );
					cb [ 1 ] = new JCheckBox( "Good received", false );
		        	cb [ 1 ].addItemListener( ILfpga () );
					cb [ 2 ] = new JCheckBox( "Bad received", false );
		        	cb [ 2 ].addItemListener( ILfpga () );
					break;
			}
	    	cb [ optionNumber ] = new JCheckBox( "OK", false );
	    	cb [ optionNumber ].addItemListener( this );
			for ( int j = 0; j <= optionNumber; j ++ )
			{
				cb [ j ].setHorizontalAlignment( JCheckBox.LEFT );
				pn.add( cb [ j ] );
			}
	    	getContentPane().add ( pn );
	        Point pp = MouseInfo.getPointerInfo(). getLocation();     				
     		Double dx = new Double ( pp.getX());
     		Double dy = new Double ( pp.getY());
     		setLocation(dx.intValue(), dy.intValue());		        
            setSize ( 150,300 );
            setVisible ( true );
	    }
	}
/************************************************************************/
//
// listen to any check box change
//
/************************************************************************/
   public void itemStateChanged( ItemEvent e )
   {
       JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
       if ( obj != null )
       {
              if ( obj.equals ( cb [ optionNumber ] ) )
              {
                  if ( e.getStateChange() == e.SELECTED )
                  {
                      setVisible ( false );
                  }
              }
       }
   }
/************************************************************************/
//
// ask something from the user
//
/************************************************************************/
private String dialog ( String s, String [] options , String defaultoption )
{
    String sret = (String)JOptionPane.showInputDialog( this, s, s,
                    JOptionPane.PLAIN_MESSAGE, null, options, defaultoption );
    return sret;
}
/************************************************************************/
//
// listen to the check box for onu tx module's counters
//
/************************************************************************/
private ItemListener ILonutx ()
{
ItemListener IL = new ItemListener ()
{
	public void itemStateChanged( ItemEvent e )
	{
		int grant = 0;
		Counter ff = null;
	    JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
	    if ( obj != null )
	    {
	        for ( int k = 0; k < cb.length - 1; k ++ )
	        {
	            if ( obj.equals ( cb [ k ] ) && ( e.getStateChange() == e.SELECTED ) )
	            {
	                switch ( k )
	                {
					case 0:
						String ss = dialog ( "grant number", null, all );
            			if ( ( ss != null ) && ( ss.length() > 0 ) )
            			{
                            if ( ss.equals ( "all " ) )
                            {
                                for ( int j = 0; j < 16; j ++ )
                                {
                                    ff = new Counter ( my_java.TX, j ) ;
                                    ff.CounterSet() ;
                                    v.add( ff );
                                }
                            }
                            else
                            {
                                UtilsCounters.getOptionsPerModule( module, ss, v, -1 );
                            }
							// onu, tx, grant
						}
						break;

					default:
                        ff = new Counter ( my_java.TX, 15 + k ) ;
                        ff.CounterSet();
                        v.add( ff );
						break;
					} // switch
			    } // equal
		    } // for
	    } // null
	}	// item changed
}; // new listener
return IL;
}
/************************************************************************/
//
// listen to the check box for onu rx module's counters
//
/************************************************************************/
private ItemListener ILonurx ()
{
ItemListener IL = new ItemListener ()
{
	public void itemStateChanged( ItemEvent e )
	{
		Counter ff = null;
	    JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
	    if ( obj != null )
	    {
	        for ( int k = 0; k < cb.length - 1; k ++ )
	        {
	            if ( obj.equals ( cb [ k ] ) && ( e.getStateChange() == e.SELECTED ) )
	            {
                   ff = new Counter ( my_java.RX, k ) ;
                   ff.CounterSet();
                   v.add( ff );
			    } // switch
		    } // for
	    } // equal
	}	// item changed
}; // new listener
return IL;
}
/************************************************************************/
//
// listen to the check box for fpga module's counters
//
/************************************************************************/
private ItemListener ILfpga ()
{
ItemListener IL = new ItemListener ()
{
	public void itemStateChanged( ItemEvent e )
	{
		Counter ff = null;
	    JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
	    if ( obj != null )
	    {
	         for ( int k = 0; k < cb.length - 1; k ++ )
	         {
	             if ( obj.equals ( cb [ k ] ) && ( e.getStateChange() == e.SELECTED ) )
	             {
                     ff = new Counter ( my_java.FPGA, k * 2 ) ;
                     ff.CounterSet();
                     v.add( ff );
                     if ( ( k == 0 ) ||( k == 1 ) )
                     {
                         ff = new Counter ( my_java.FPGA, k * 2 + 1 ) ;
                         ff.CounterSet();
                         v.add( ff );
                     }
			     } // switch
		     } // for
	    } // equal
	}	// item changed
}; // new listener
return IL;
}
/************************************************************************/
//
// listen to the check box for olt tx module's counters
//
/************************************************************************/
private ItemListener ILolttx ()
{
ItemListener IL = new ItemListener ()
{
	public void itemStateChanged( ItemEvent e )
	{
		Counter ff = null;
	    JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
	    if ( obj != null )
	    {
	        for ( int k = 0; k < cb.length - 1; k ++ )
	        {
	            if ( obj.equals ( cb [ k ] ) && ( e.getStateChange() == e.SELECTED ) )
	            {
                    ff = new Counter ( my_java.OLTTX, k ) ;
                    ff.CounterSet();
                    v.add( ff );
			    } // switch
		    } // for
	    } // equal
	}	// item changed
}; // new listener
return IL;
}
/************************************************************************/
//
// listen to the check box for olt rx module's counters
//
/************************************************************************/
private ItemListener ILoltrx ()
{
ItemListener IL = new ItemListener ()
{
	public void itemStateChanged( ItemEvent e )
	{
		Counter ff = null;
	    JCheckBox obj = ( JCheckBox ) e.getItemSelectable();
	    if ( obj != null )
	    {
	        for ( int k = 0; k < cb.length - 1; k ++ )
	        {
	            if ( obj.equals ( cb [ k ] ) && ( e.getStateChange() == e.SELECTED ) )
	            {
	                switch ( k )
	                {
					case 0:
						String ss = dialog ( "grant number", null, all );
            			if ( ( ss != null ) && ( ss.length() > 0 ) )
            			{
                            if ( ss.equals ( "all" ) )
                            {
							     for ( int t =0; t < my_java.MaxTconts; t ++ )
							     {
                                     for ( int y = 0; y < tcontfactor; y ++ )
                                     {
                                        ff = new Counter ( my_java.OLTRX, t ) ;
                                        ff.CounterSet();
                                        v.add( ff );
                                     }
							     }
                            }
                            else
                            {
                                 UtilsCounters.getOptionsPerModule( my_java.RX, ss, v, MyConstantsCounters.Tcont );
                            }
						}
						break;

					case 2:
                        ff = new Counter ( my_java.OLTRX, my_java.MaxTconts * tcontfactor ) ;
                        ff.CounterSet();
                        v.add( ff );
						break;

					case 3:
                        ff = new Counter ( my_java.OLTRX, my_java.MaxTconts * tcontfactor + 1 ) ;
                        ff.CounterSet();
                        v.add( ff );
                        ff = new Counter ( my_java.OLTRX, my_java.MaxTconts * tcontfactor + 2 ) ;
                        ff.CounterSet();
                        v.add( ff );
						break;
					} // switch
			    } // equals
             } // for
	    } // null
	}	// item changed

}; // new listener
return IL;
}
} // class
