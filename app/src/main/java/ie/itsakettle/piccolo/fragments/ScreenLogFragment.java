package ie.itsakettle.piccolo.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ie.itsakettle.piccolo.R;
import ie.itsakettle.piccolo.adapters.ScreenLogAdapter;
import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;


public class ScreenLogFragment extends Fragment implements
LoaderManager.LoaderCallbacks<Cursor> {

    private ListView lv;
    private ScreenLogAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
         View root = inflater.inflate(R.layout.screen_log_fragment, container, false);
         lv = (ListView) root.findViewById(R.id.lvScreenLog);
         lv.setEmptyView(root.findViewById(R.id.pbScreenLog));
         filldata();
         return root;


    }




    private void filldata()
    {
         // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { DatabaseAccessScreenLog.KEY_ID,DatabaseAccessScreenLog.KEY_TIME_ON,
                DatabaseAccessScreenLog.KEY_TIME_OFF,DatabaseAccessScreenLog.KEY_DELTA
                ,DatabaseAccessScreenLog.KEY_DAY,DatabaseAccessScreenLog.KEY_HOUR};
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.leScreenLogID,R.id.leScreenLogOn,R.id.leScreenLogOff
                                ,R.id.leScreenDelta,R.id.leScreenDay,R.id.leScreenHour};

        getLoaderManager().initLoader(3, null, this);
        adapter = new ScreenLogAdapter(this.getActivity().getApplicationContext(),R.layout.list_element_screen_log , from, to);

        lv.setAdapter(adapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int _id, Bundle arg1) {
CursorLoader cursorLoader;

        switch(_id)
        {
        case 3:
         String[] projection = {  DatabaseAccessScreenLog.KEY_ID,DatabaseAccessScreenLog.KEY_TIME_ON,
                 DatabaseAccessScreenLog.KEY_TIME_OFF,DatabaseAccessScreenLog.KEY_DELTA
                 ,DatabaseAccessScreenLog.KEY_DAY,DatabaseAccessScreenLog.KEY_HOUR};
             cursorLoader = new CursorLoader(this.getActivity(),
                DatabaseAccessScreenLog.CONTENT_URI, projection, null, null, null);
            break;
     default:
         // An invalid id was passed in
         cursorLoader=null;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        adapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
         adapter.swapCursor(null);

    }

    public void restartLoader()
    {
        this.getLoaderManager().restartLoader(3, null, this);
    }



}
