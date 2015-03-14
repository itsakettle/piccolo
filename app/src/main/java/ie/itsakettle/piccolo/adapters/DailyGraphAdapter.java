package ie.itsakettle.piccolo.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ie.itsakettle.piccolo.R;
import ie.itsakettle.piccolo.analytics.Statistics;
import ie.itsakettle.piccolo.analytics.Visualisation;
import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;

public class DailyGraphAdapter extends ArrayAdapter<Date> {

	private static final String LOGCAT_ID = "Daily Graph Adapter";
	private Context context;

	private int textViewResourceId;
    private Statistics stats;
    private Visualisation vis;


	public DailyGraphAdapter(Context context, int textViewResourceId,Date[] dates) {
		// TODO Auto-generated constructor stub
        super(context,textViewResourceId,dates);
		this.context=context;
		this.textViewResourceId=textViewResourceId;
        stats = new Statistics(context);
        vis = new Visualisation(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		LayoutParams lp;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);
			}

            Date d = this.getItem(position);
		    TextView tv = (TextView) v.findViewById(R.id.dgleDate);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(d);
            tv.setText(date);

            ImageView imv = (ImageView) v.findViewById(R.id.leivGraph);
            imv.setImageBitmap(vis.DailyGraph(stats.getDailyProfile(d)));


	//Log.i(this.LOGCAT_ID,"");
	return v;

	}
	
	
	
}
