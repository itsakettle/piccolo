package ie.itsakettle.piccolo.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;

public class ScreenLogAdapter extends SimpleCursorAdapter {
	
	private static final String LOGCAT_ID = "Activate_fragment_adapter";
	private Cursor c;
	private int[] to;
	private String[] from;
	private Context context;

	private int textViewResourceID;
	
	@Override
	public Cursor swapCursor(Cursor c)
	{
		this.c=c;
		return	super.swapCursor(c);
		
	}
	
	public ScreenLogAdapter(Context context, int textViewResourceId, String[] _from, int[] _to) {
		// TODO Auto-generated constructor stub
		super(context,textViewResourceId,null,_from,_to);
		this.to = _to;
		this.from=_from;
		this.context=context;
		textViewResourceID=textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		View v = convertView;
		LayoutParams lp;
		
		c.moveToPosition(position);
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceID, null);
			}
		
		
		for(int i=0;i<to.length;i++)
		{
			TextView tv = (TextView) v.findViewById(to[i]);
			lp = tv.getLayoutParams();
			lp.width=0;
			tv.setLayoutParams(lp);
			if(from[i]==DatabaseAccessScreenLog.KEY_TIME_ON || from[i]== DatabaseAccessScreenLog.KEY_TIME_OFF)
			{
				long time=c.getInt( c.getColumnIndex(from[i]) );
				
				Date d=new Date(time*1000 );
				tv.setText(sdf.format( d ));

			}
			else if (from[i] == DatabaseAccessScreenLog.KEY_DELTA)
            {
                long mins = TimeUnit.SECONDS.convert(c.getInt( c.getColumnIndex(from[i]) )*1000, TimeUnit.MILLISECONDS);
                tv.setText(String.valueOf(mins));
            }
            else
            {
				tv.setText( c.getString(c.getColumnIndex(from[i])) );
			}
			
		}
	//Log.i(this.LOGCAT_ID,"");
	return v;
	
	}
	
	
	
}
