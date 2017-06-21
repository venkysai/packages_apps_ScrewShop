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

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.mrapocalypse.screwdshop.prefs.SystemSettingSwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.internal.util.screwd.screwdUtils;


public class FingerprintFrag extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";
    private static final String PREF_QUICK_PULLDOWN_FP = "quick_pulldown_fp";

    private SystemSettingSwitchPreference mFingerprintVib;
    private FingerprintManager mFingerprintManager;
    private SystemSettingSwitchPreference mFpKeystore;
    private SystemSettingSwitchPreference mQuickPulldownFp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fingerprint_frag);

        final ContentResolver resolver = getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

        mFingerprintVib = (SystemSettingSwitchPreference) findPreference(FINGERPRINT_VIB);
        mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
        mFingerprintVib.setOnPreferenceChangeListener(this);

        mFpKeystore = (SystemSettingSwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
        if (Build.BOARD.contains("marlin") || Build.BOARD.contains("sailfish")) {
            prefScreen.removePreference(mFpKeystore);
        } else {
            mFpKeystore.setChecked((Settings.System.getInt(getContentResolver(),
                   Settings.System.FP_UNLOCK_KEYSTORE, 0) == 1));
            mFpKeystore.setOnPreferenceChangeListener(this);
        }

        mQuickPulldownFp = (SystemSettingSwitchPreference) findPreference(PREF_QUICK_PULLDOWN_FP);
        if (getResources().getBoolean(com.android.internal.R.bool.config_supportSystemNavigationKeys)) {
            mQuickPulldownFp.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN_FP, 0) == 1));
            mQuickPulldownFp.setOnPreferenceChangeListener(this);
        } else {
            prefScreen.removePreference(mQuickPulldownFp);
        }

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        } else if (preference == mFpKeystore) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FP_UNLOCK_KEYSTORE, value ? 1 : 0);
            return true;
        } else if (preference == mQuickPulldownFp) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN_FP, value ? 1 : 0);
            return true;
        }
        return false;
    }



    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }

}
