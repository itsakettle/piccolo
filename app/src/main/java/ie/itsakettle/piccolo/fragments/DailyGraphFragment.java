package ie.itsakettle.piccolo.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ie.itsakettle.piccolo.R;
import ie.itsakettle.piccolo.adapters.DailyGraphAdapter;
import ie.itsakettle.piccolo.adapters.ScreenLogAdapter;
import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;


public class DailyGraphFragment extends Fragment  {

    private ListView lv;
    private ScreenLogAdapter adapter;
    private Date[] dates;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*Need to figure out number of days available and produce array of thes dates*/
        Cursor c = this.getActivity().getContentResolver().query(
                DatabaseAccessScreenLog.CONTENT_URI, new String[] {"MIN(TIME_ON) AS MIN_TIME_ON"}
                ,null,null,null);
        c.moveToFirst();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(c.getLong(c.getColumnIndex("MIN_TIME_ON"))*1000);
        //Set the hour, minutes,seconds and milliseconds

        cal.set(Calendar.HOUR_OF_DAY,1);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        ArrayList<Date > alDates = new ArrayList<Date>();
        Calendar calToday = Calendar.getInstance();

        calToday.set(Calendar.HOUR_OF_DAY,1);
        calToday.set(Calendar.MINUTE,0);
        calToday.set(Calendar.SECOND,0);
        calToday.set(Calendar.MILLISECOND,0);

        while(cal.getTimeInMillis() <= calToday.getTimeInMillis())
        {
            alDates.add(new Date(cal.getTimeInMillis()));
            cal.add(Calendar.DATE,1);
        }

        dates = new Date[alDates.size()];
        this.dates = (Date[]) alDates.toArray(dates);
        c.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
         View root = inflater.inflate(R.layout.screen_log_fragment, container, false);
         lv = (ListView) root.findViewById(R.id.lvScreenLog);
         lv.setEmptyView(root.findViewById(R.id.pbScreenLog));

         lv.setAdapter(new DailyGraphAdapter(this.getActivity().getApplicationContext(),R.layout.list_element_daily_graph,dates));

         return root;


    }




}
