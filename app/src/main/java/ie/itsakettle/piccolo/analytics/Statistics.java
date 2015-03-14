package ie.itsakettle.piccolo.analytics;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;

/**
 * This class has methods for computing various statistics of the interaction data
 *
 * Created by wtr on 22/02/15.
 */
public class Statistics {

    private Context context;

    public Statistics(Context context)
    {
        this.context = context;
    }

    public Number[]  getDailyProfile(Date today )
    {
        Uri mUri = DatabaseAccessScreenLog.CONTENT_URI ;
        String[] proj = {DatabaseAccessScreenLog.KEY_HOUR,DatabaseAccessScreenLog.KEY_DELTA};
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String whereDate = dateFormat.format(today);
        String where = DatabaseAccessScreenLog.KEY_DAY + " = '" + whereDate +"'";
        String sortOrder = DatabaseAccessScreenLog.KEY_HOUR + " asc";
        Cursor c = context.getContentResolver().query(mUri,proj,where,null,sortOrder);

        /* Now loop through the cursor and calculate the amount of interaction in each hour */

        Float[] profile = new Float[24];

        /*Initialize array */
        for(int i = 0; i<24; i++)
        {
            profile[i] = new Float(0);

        }

        /*Now loop through cursor adding to profile */


        c.moveToFirst();
        while (c.isAfterLast() == false) {
            profile[c.getInt(c.getColumnIndex(DatabaseAccessScreenLog.KEY_HOUR))]
                    += c.getFloat(c.getColumnIndex(DatabaseAccessScreenLog.KEY_DELTA));
            c.moveToNext();
        }

        /*Convert to minutes*/
        for(int i = 0; i<24; i++)
        {
            profile[i] = profile[i]/60;

        }
        c.close();
        return profile;
    }

    public Number[] getAverageDailyProfile()
    {
    return null;
    }


}
