package com.screwdaosp.screwshop.tabs;

import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.screwdaosp.screwshop.R;

/**
 * Created by cedwards on 6/3/2016.
 */
public class System extends PreferenceFragment {

    public System() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.system_tab);
    }
}
