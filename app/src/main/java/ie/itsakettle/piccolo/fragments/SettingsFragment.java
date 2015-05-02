package ie.itsakettle.piccolo.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import ie.itsakettle.piccolo.R;

/**
 * Created by wtr on 02/05/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }

}
