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

package com.mrapocalypse.screwdshop.frags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import java.util.Date;

/**
 * Created by cedwards on 6/3/2016.
 */
public class QSFrag extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String PREF_CAT_LAYOUT = "qs_layout";
    private static final String PREF_ROWS_PORTRAIT = "qs_rows_portrait";
    private static final String PREF_COLUMNS_PORTRAIT = "qs_columns_portrait";
    private static final String PREF_ROWS_LANDSCAPE = "qs_rows_landscape";
    private static final String PREF_COLUMNS_LANDSCAPE = "qs_columns_landscape";

    private ListPreference mQuickPulldown;
    private ListPreference mSmartPulldown;
    private ListPreference mRowsPortrait;
    private ListPreference mColumnsPortrait;
    private ListPreference mRowsLandscape;
    private ListPreference mColumnsLandscape;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.qs_frag);

        ContentResolver resolver = getActivity().getContentResolver();
        Resources res = getResources();
        int defaultValue;

        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        int quickPulldownValue = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 1, UserHandle.USER_CURRENT);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);

        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

        PreferenceCategory catLayout = (PreferenceCategory) findPreference(PREF_CAT_LAYOUT);

        mRowsPortrait =
                (ListPreference) findPreference(PREF_ROWS_PORTRAIT);
        int rowsPortrait = Settings.System.getInt(resolver,
                Settings.System.QS_ROWS_PORTRAIT, 3);
        mRowsPortrait.setValue(String.valueOf(rowsPortrait));
        mRowsPortrait.setSummary(mRowsPortrait.getEntry());
        mRowsPortrait.setOnPreferenceChangeListener(this);

        mColumnsPortrait =
                (ListPreference) findPreference(PREF_COLUMNS_PORTRAIT);
        int columnsPortrait = Settings.System.getInt(resolver,
                Settings.System.QS_COLUMNS_PORTRAIT, 5);
        mColumnsPortrait.setValue(String.valueOf(columnsPortrait));
        mColumnsPortrait.setSummary(mColumnsPortrait.getEntry());
        mColumnsPortrait.setOnPreferenceChangeListener(this);

        defaultValue = res.getInteger(R.integer.config_qs_num_rows_landscape_default);
        if (defaultValue != 1) {
            mRowsLandscape =
                    (ListPreference) findPreference(PREF_ROWS_LANDSCAPE);
            int rowsLandscape = Settings.System.getInt(resolver,
                    Settings.System.QS_ROWS_LANDSCAPE, defaultValue);
            mRowsLandscape.setValue(String.valueOf(rowsLandscape));
            mRowsLandscape.setSummary(mRowsLandscape.getEntry());
            mRowsLandscape.setOnPreferenceChangeListener(this);
        } else {
            catLayout.removePreference(findPreference(PREF_ROWS_LANDSCAPE));
        }

        mColumnsLandscape =
                (ListPreference) findPreference(PREF_COLUMNS_LANDSCAPE);
        defaultValue = res.getInteger(R.integer.config_qs_num_columns_landscape_default);
        int columnsLandscape = Settings.System.getInt(resolver,
                Settings.System.QS_COLUMNS_LANDSCAPE, defaultValue);
        mColumnsLandscape.setValue(String.valueOf(columnsLandscape));
        mColumnsLandscape.setSummary(mColumnsLandscape.getEntry());
        mColumnsLandscape.setOnPreferenceChangeListener(this);

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        int index;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mQuickPulldown) {
            int quickPulldownValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(resolver, Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN,
                    quickPulldownValue, UserHandle.USER_CURRENT);
            updatePulldownSummary(quickPulldownValue);
            return true;
        } else if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver, Settings.System.QS_SMART_PULLDOWN, smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
            return true;
        } else if (preference == mRowsPortrait) {
            intValue = Integer.valueOf((String) newValue);
            index = mRowsPortrait.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.QS_ROWS_PORTRAIT, intValue);
            preference.setSummary(mRowsPortrait.getEntries()[index]);
            return true;
        } else if (preference == mColumnsPortrait) {
            intValue = Integer.valueOf((String) newValue);
            index = mColumnsPortrait.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.QS_COLUMNS_PORTRAIT, intValue);
            preference.setSummary(mColumnsPortrait.getEntries()[index]);
            return true;
        } else if (preference == mRowsLandscape) {
            intValue = Integer.valueOf((String) newValue);
            index = mRowsLandscape.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.QS_ROWS_LANDSCAPE, intValue);
            preference.setSummary(mRowsLandscape.getEntries()[index]);
            return true;
        } else if (preference == mColumnsLandscape) {
            intValue = Integer.valueOf((String) newValue);
            index = mColumnsLandscape.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.QS_COLUMNS_LANDSCAPE, intValue);
            preference.setSummary(mColumnsLandscape.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else if (value == 3) {
            // quick pulldown always
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary_always));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_left
                    : R.string.quick_pulldown_right);
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary, direction));
        }
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else if (value == 3) {
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
        } else {
            String type = res.getString(value == 1
                    ? R.string.smart_pulldown_dismissable
                    : R.string.smart_pulldown_ongoing);
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }



}
