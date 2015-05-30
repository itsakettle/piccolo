package ie.itsakettle.piccolo.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import android.net.Uri;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.widget.RemoteViews;

import ie.itsakettle.piccolo.R;
import ie.itsakettle.piccolo.analytics.Statistics;
import ie.itsakettle.piccolo.analytics.Visualisation;
import ie.itsakettle.piccolo.contentprovider.DatabaseAccessScreenLog;


public class ScreenLogService extends Service {

    private BroadcastReceiver rec;
    SharedPreferences prefs;
    Uri onURI;
    String onHour;
    String onDate;
    Long onTime;
    TimeZone onTZ;
    private int notifID = R.string.screenlog_notification_id;
    private static final String LOGCAT_ID = "Screen Log Service";
    private static final String actionUpdateNotification = "it.itsakettle.piccolo.screenlogservice.updatenotification";
    private static final String actionDisplayNotification = "it.itsakettle.piccolo.screenlogservice.displaynotification";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private static final int iNotificationUpdateSeconds = 60;
    private static final int iSecondsPerDay = 24*60*60;

    /**
     * This logs interaction turning on.
     *
     * @param time - The on time.
     * @param date - The date of the on as a string.
     * @param hour - The hour of the on as a string
     * @return
     */
    private void logOn(long time, String date, String hour, TimeZone tz) {

        ContentValues values = new ContentValues();
        values.put(DatabaseAccessScreenLog.KEY_TIME_ON, time / 1000);
        values.put(DatabaseAccessScreenLog.KEY_DAY, date);
        values.put(DatabaseAccessScreenLog.KEY_HOUR, hour);

        onURI = this.getContentResolver().insert(DatabaseAccessScreenLog.CONTENT_URI, values);
        onHour = hour;
        onDate = date;
        onTime = time;
        onTZ = tz;
        Log.i(LOGCAT_ID, "On record inserted.");


    }


    /**
     * updates a record with an off
     *
     * @param time   - The time to register the log off
     * @param record - The record to turn off.
     */

    private void logOff(long time, Uri record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseAccessScreenLog.KEY_TIME_OFF, time / 1000);
        values.put(DatabaseAccessScreenLog.KEY_DELTA, (time - onTime) / 1000);
        this.getContentResolver().update(record, values, null, null);
        Log.i(LOGCAT_ID, "Off record inserted.");
    }

    /**
     * The difficulty with this is that the off time may not be in
     * the same hour as the on time. It might not be the same day.
     * So to tackle this we use this function which checks whether the
     * day and the hour are the same. If they are not then the old record
     * is closed with an off time of the hour and a new record is added
     * that starts in the new hour.
     *
     * @param milliTime
     */
    private void offLogic(long milliTime) {


        if (onURI != null) {
             /*check if a new record is needed
            by checking is the hour the same*/
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(milliTime);
            String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = dateFormat.format(cal.getTime());
            TimeZone tz = cal.getTimeZone();

            /*If the time zone is different to the on time zone then remove the
            on record. Use get offset which doesn't include daylight saving time
            adjustments
             */
            if (onTZ.getRawOffset() != tz.getRawOffset()) {
                /*Delete the on record and return*/

                this.getContentResolver().delete(onURI, null, null);

                onURI = null;
                onHour = null;
                onDate = null;
                onTime = null;
                onTZ = null;
                Log.i(LOGCAT_ID, "Timezone mismatch.");
                return;

            }

            if (onHour.equals(hour) && onDate.equals(date)) {
                /* In this case we just need to update the off time*/
                logOff(cal.getTimeInMillis(), onURI);
                onURI = null;
                onHour = null;
                onTime = null;
                Log.i(LOGCAT_ID, "Straight forward off record finished.");
            } else {
                /*In this case we need to close off the existing record
                and then create a new on record and call offLogic again
                 */
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
                /* Use cal to hold the new off time - use hour and date of the
                  the record we are about to close to figure out its end time  */
                try {
                    cal.setTime(dateFormat.parse(onDate + " " + onHour));
                } catch (Exception e) {
                    Log.e(this.LOGCAT_ID, e.getMessage());
                }
                /*we want the end time in cal, so we add an hour*/
                cal.add(Calendar.HOUR_OF_DAY, 1);
                logOff(cal.getTimeInMillis(), onURI);

                /*Now create a new on record which starts
                at the end time of the one we just closed off*/
                hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(cal.getTime());
                logOn(cal.getTimeInMillis(), date, hour, onTZ);
                Log.i(LOGCAT_ID, "Complex off record finished.");
                /*Now start again - Recursive*/
                offLogic(milliTime);

            }

        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mBuilder = new NotificationCompat.Builder(this).setVisibility(Notification.VISIBILITY_SECRET)
                .setSmallIcon(R.drawable.piccolo);


        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Log.i("ScreenLog Service", "Created");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(actionUpdateNotification);
        intentFilter.addAction(actionDisplayNotification);
        rec = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() == Intent.ACTION_SCREEN_ON) {

                    /*The screen has just been turned on so we create a record
                    for it and leave the off field blank*/

                    Calendar cal = Calendar.getInstance();
                    cal.getTimeZone();
                    long milliTime = cal.getTimeInMillis();
                    String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = dateFormat.format(cal.getTime());
                    TimeZone tz = cal.getTimeZone();
                    logOn(milliTime, date, hour, tz);

                }

                if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
                    /*we may need to split it into two records */
                    Calendar cal = Calendar.getInstance();
                    long milliTime = cal.getTimeInMillis();
                    offLogic(milliTime);
                }

                if (intent.getAction() == actionUpdateNotification) {
                    updateNotification();

                }

                if (intent.getAction() == actionDisplayNotification) {
                    Log.i("ScreenLog Service", "Display Notification Action Received.");
                   displayNotification();

                }

            }


        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(rec, intentFilter);


    }

    private void updateNotification() {
        Notification n = getNotification();
        n.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(notifID, n);
    }


    private void displayNotification() {
        Log.i("ScreenLog Service", "Attempting to display notification.");
        Notification n = getNotification();
        mNotificationManager.notify(notifID,n);
    }

    private Notification getNotification() {

        Statistics stats = new Statistics(this.getApplicationContext());
        Visualisation vis = new Visualisation(this.getApplicationContext());
        Bitmap b = vis.DailyGraph(stats.getDailyProfile(new Date()));
        /*RemoteViews*/
        RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.screenlog_persistent_notification);
        rv.setImageViewBitmap(R.id.ivGraph, b);
        mBuilder.setContent(rv);
        Log.i("ScreenLog Service", "Notification Built.");
        return (mBuilder.build());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean foreground = sharedPref.getBoolean(getString(R.string.pref_foreground_service_key), false);

        if (foreground) {
            Notification n = getNotification();
            n.flags |= Notification.FLAG_NO_CLEAR;

            try {
                Log.i("ScreenLog Service", "Attempting to put service into foreground.");
                startForeground(notifID, n);

            } catch (Exception e) {
                Log.e("ScreenLog Service", e.toString());
            }

            //Need to set up an alarm to update the notification every 10 minutes

            Intent i = new Intent(actionUpdateNotification);
            mPendingIntent = PendingIntent.getBroadcast(this, notifID, i, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar cal = Calendar.getInstance();
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), iNotificationUpdateSeconds * 1000, mPendingIntent);

            // We want this service to continue running until it is explicitly
            // stopped, so return sticky.




        }
        else
        {
            //Get the time specified in settings to display the notification
            String sNotifTime = sharedPref.getString(getString(R.string.pref_notification_schedule_key), "20:00");
            /*Get the hour and minute and set calendar time. If the time has passed on this day
            *then the notif should appear immediately
            */

            String sHour = sNotifTime.substring(0, 2);
            String sMin =  sNotifTime.substring(3, 5);

            int hour = Integer.parseInt(sHour);
            int min = Integer.parseInt(sMin);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND,0);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.HOUR_OF_DAY,hour);
            cal.set(Calendar.MINUTE,min);

            Intent i = new Intent(actionDisplayNotification);
            mPendingIntent = PendingIntent.getBroadcast(this, notifID, i, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), iSecondsPerDay * 1000, mPendingIntent);

            Log.i("ScreenLog Service", "Service started in background. Notification at " + cal.getTime().toString());
        }

        return START_STICKY;

    }


	
	@Override
	public void onDestroy()
	{	super.onDestroy();
		Log.i("ScreenLog Service", "Destroyed");
		this.unregisterReceiver(rec);
		mAlarmManager.cancel(mPendingIntent);
		
	}
	
	
	
	

}

