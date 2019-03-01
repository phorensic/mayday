package com.phorensic.mayday;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


// FloatingView Settings
public class FloatingViewSettingsFragment extends PreferenceFragmentCompat {

    // FloatingViewSettingsFragment Generation
    // @return FloatingViewSettingsFragment
    public static FloatingViewSettingsFragment newInstance() {
        final FloatingViewSettingsFragment fragment = new FloatingViewSettingsFragment();
        return fragment;
    }

    // Constructor
    public FloatingViewSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_floatingview, null);
    }
}
