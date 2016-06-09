package com.screwdaosp.screwshop.tabs;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.screwdaosp.screwshop.R;

/**
 * Created by cedwards on 6/3/2016.
 */
public class Navigation extends PreferenceFragment {

    public Navigation() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nav_tab);
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_ui, container, false);
        return rootView;
    }
    */


}
