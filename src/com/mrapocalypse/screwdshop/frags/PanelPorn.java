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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.util.screwd.screwdUtils;
import com.android.internal.logging.nano.MetricsProto;

import com.mrapocalypse.screwdshop.prefs.SystemSettingSwitchPreference;
import com.mrapocalypse.screwdshop.prefs.CustomSeekBarPreference;
/**
 * Created by cedwards on 6/3/2016.
 */
public class PanelPorn extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private ListPreference mVolumeDialogStroke;
    private Preference mVolumeDialogStrokeColor;
    private Preference mVolumeDialogStrokeThickness;
    private Preference mVolumeDialogDashWidth;
    private Preference mVolumeDialogDashGap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.panel_porn);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mVolumeDialogStroke =
                (ListPreference) findPreference(Settings.System.VOLUME_DIALOG_STROKE);
        mVolumeDialogStroke.setOnPreferenceChangeListener(this);
        mVolumeDialogStrokeColor = findPreference(Settings.System.VOLUME_DIALOG_STROKE_COLOR);
        mVolumeDialogStrokeThickness =
                findPreference(Settings.System.VOLUME_DIALOG_STROKE_THICKNESS);
        mVolumeDialogDashWidth = findPreference(Settings.System.VOLUME_DIALOG_STROKE_DASH_WIDTH);
        mVolumeDialogDashGap = findPreference(Settings.System.VOLUME_DIALOG_STROKE_DASH_GAP);
        updateVolumeDialogDependencies(mVolumeDialogStroke.getValue());


    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }



    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	    ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mVolumeDialogStroke) {
            updateVolumeDialogDependencies((String) newValue);
            return true;
        }

        return false;
    }


    private void updateVolumeDialogDependencies(String volumeDialogStroke) {
        if (volumeDialogStroke.equals("0")) {
            mVolumeDialogStrokeColor.setEnabled(false);
            mVolumeDialogStrokeThickness.setEnabled(false);
            mVolumeDialogDashWidth.setEnabled(false);
            mVolumeDialogDashGap.setEnabled(false);
        } else if (volumeDialogStroke.equals("1")) {
            mVolumeDialogStrokeColor.setEnabled(false);
            mVolumeDialogStrokeThickness.setEnabled(true);
            mVolumeDialogDashWidth.setEnabled(true);
            mVolumeDialogDashGap.setEnabled(true);
        } else {
            mVolumeDialogStrokeColor.setEnabled(true);
            mVolumeDialogStrokeThickness.setEnabled(true);
            mVolumeDialogDashWidth.setEnabled(true);
            mVolumeDialogDashGap.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }



   @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCREWD;
    }

}
