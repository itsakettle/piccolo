package ie.itsakettle.piccolo.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.prefs.Preferences;

import ie.itsakettle.piccolo.R;
import ie.itsakettle.piccolo.activities.PiccoloMain;

/**
 * Created by wtr on 02/05/15.
 */
public class SettingsFragment extends PreferenceFragment {

    private Preference.OnPreferenceChangeListener prefScreenLogToggleListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        final PiccoloMain activity =  (PiccoloMain) this.getActivity();


        prefScreenLogToggleListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                    boolean bAllowChange = true;

                    //If the screenlog service is running then stop and restart it. Otherwise do nothing.
                    if (PiccoloMain.isServiceRunning(ie.itsakettle.piccolo.services.ScreenLogService.class, activity)) {
                        activity.toggleScreenLog();
                        activity.toggleScreenLog();
                    }

                return bAllowChange;
            }
        };

        Preference notifSchedPref = findPreference(getString(R.string.pref_notification_schedule_key));
        notifSchedPref.setOnPreferenceChangeListener(prefScreenLogToggleListener);

        Preference notifTypePref = findPreference(getString(R.string.pref_notification_type_key));
        notifTypePref.setOnPreferenceChangeListener(prefScreenLogToggleListener);

        Preference foregroundPref = findPreference(getString(R.string.pref_foreground_service_key));
        foregroundPref.setOnPreferenceChangeListener(prefScreenLogToggleListener);

    }

}
