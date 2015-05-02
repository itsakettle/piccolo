package ie.itsakettle.piccolo.activities;

import android.app.Activity;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import java.io.File;

import ie.itsakettle.piccolo.R;
import ie.itsakettle.piccolo.contentprovider.DBHelper;
import ie.itsakettle.piccolo.fragments.DailyGraphFragment;
import ie.itsakettle.piccolo.fragments.NavigationDrawerFragment;
import ie.itsakettle.piccolo.fragments.ScreenLogFragment;
import ie.itsakettle.piccolo.fragments.SettingsFragment;
import ie.itsakettle.piccolo.services.ScreenLogService;
import ie.itsakettle.piccolo.utilities.DatabaseBackupFileNameFilter;
import ie.itsakettle.piccolo.utilities.DatabaseExport;
import ie.itsakettle.piccolo.utilities.DatabaseImport;


public class PiccoloMain extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    SharedPreferences prefs;
    private String[] importFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piccolo_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        prefs =  PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.contains("ie.itsakettle.piccolo.screen_log"))
        {
            prefs.edit().putBoolean("ie.itsakettle.piccolo.screen_log", false).commit();

        }
    
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment f;
        switch(position) {
            case 0:
                f = new ScreenLogFragment();
                break;
            case 1:
                f = new DailyGraphFragment();
                break;
            case 2:
                f = new SettingsFragment();
                break;
            default:
                f = new ScreenLogFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.piccolo_main, menu);
            restoreActionBar();

            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    /*
    I got this solution from http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
     There is no consensus on this, so I'm going with this for now...the alternative is a shared pref like before or
     a static flag in the service class..but these don't help if the service is destroyed as onDestroy isn't
     guaranteed to be called.....I think.
     */

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {

            //Check if the screen log service is running.

            if ( isServiceRunning(ie.itsakettle.piccolo.services.ScreenLogService.class)  ) {
                menu.findItem(R.id.ScreenLogMenuItem).setTitle("Turn screen log off");
            } else {
                menu.findItem(R.id.ScreenLogMenuItem).setTitle("Turn screen log on");
            }
            return true;
        }

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()== R.id.ScreenLogMenuItem)
        {
            activateScreenLog();

        }
        else if(item.getItemId() == R.id.DatabaseExportMenuItem)
        {
            new DatabaseExport(this).execute(DBHelper.DATABASE_NAME);
        }
        else if(item.getItemId() == R.id.DatabaseImportMenuItem)
        {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Select file:");

            //Get the files

            File backUpDir = Environment.getExternalStorageDirectory();
            importFiles = backUpDir.list(new DatabaseBackupFileNameFilter());
            b.setItems(importFiles,new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    importDatabase(which);

                }
            });

            if(importFiles!=null)
            {
                b.create();
                b.show();
            }

        }

        return super.onOptionsItemSelected(item);


    }

    private void importDatabase(int file) {

        try {
            new DatabaseImport(this).execute(importFiles[file]);

            //	actFrag.restartLoader();
            //ansFrag.restartLoader();
            //qFrag.restartLoader();
            //slFrag.restartLoader();
            //this.getContentResolver().notifyChange(DatabaseAccessAnswers.CONTENT_URI, null);
            //this.getContentResolver().notifyChange(DatabaseAccessQuestions.CONTENT_URI, null);
            //this.getContentResolver().notifyChange(DatabaseAccessScreenLog.CONTENT_URI, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean activateScreenLog()
    {
        boolean screenLogActive = isServiceRunning(ie.itsakettle.piccolo.services.ScreenLogService.class);
        if(!screenLogActive )
        {
            Intent i = new Intent(this,ScreenLogService.class);
            this.startService(i);
            invalidateOptionsMenu();

            return true;
        }
        else
        {
            Intent i = new Intent(this,ScreenLogService.class);
            boolean b = this.stopService(i);
            invalidateOptionsMenu();
            return false;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_piccolo_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((PiccoloMain) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
