package com.ptrf.android.weather;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Application settings activity.
 *
 */
public class SettingsActivity extends PreferenceActivity {
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
