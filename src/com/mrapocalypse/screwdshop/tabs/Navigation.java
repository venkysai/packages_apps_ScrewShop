/*
 * Copyright (C) 2016 Screw'd AOSP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mrapocalypse.screwdshop.tabs;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.internal.util.screwd.screwdUtils;

//import com.mrapocalypse.screwdshop.R;

/**
 * Created by cedwards on 6/3/2016.
 */
public class Navigation extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{

    private static final String KEY_ONEPLUS_GESTURES = "oneplus_gestures";
    private static final String KEY_ONEPLUS_GESTURES_PACKAGE_NAME = "com.cyanogenmod.settings.device";

    private PreferenceScreen mOneplusGestures;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.nav_tab);

        PreferenceScreen prefSet = getPreferenceScreen();

        mOneplusGestures = (PreferenceScreen) findPreference(KEY_ONEPLUS_GESTURES);
        if (!screwdUtils.isPackageInstalled(getActivity(), KEY_ONEPLUS_GESTURES_PACKAGE_NAME)) {
            prefSet.removePreference(mOneplusGestures);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        return true;
    }


    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }


}
