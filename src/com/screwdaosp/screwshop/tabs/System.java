package com.screwdaosp.screwshop.tabs;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsProto.MetricsEvent;

//import com.screwdaosp.screwshop.R;

/**
 * Created by cedwards on 6/3/2016.
 */
public class System extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.system_tab);
        PreferenceScreen prefSet = getPreferenceScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }
}
