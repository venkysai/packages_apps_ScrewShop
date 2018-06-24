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
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.app.Fragment;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.provider.Settings;


import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.mrapocalypse.screwdshop.prefs.CustomSeekBarPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.internal.utils.du.DUActionUtils;
import com.android.internal.util.screwd.screwdUtils;

/**
 * Created by cedwards on 6/3/2016.
 */
public class MiscFrag extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";
    private static final String PREF_SS_SETTINGS_SUMMARY = "ss_settings_summary";
    private static final String FLASHLIGHT_ON_CALL = "flashlight_on_call";

    private ListPreference mTorchPowerButton;
    private Preference mCustomSummary;
    private String mCustomSummaryText;
    private ListPreference mFlashlightOnCall;
       
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.misc_frag);

				PreferenceScreen prefSet = getPreferenceScreen();

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        if (!DUActionUtils.deviceSupportsFlashLight(getContext())) {
            Preference toRemove = prefScreen.findPreference(TORCH_POWER_BUTTON_GESTURE);
            if (toRemove != null) {
                prefScreen.removePreference(toRemove);
            }
        } else {
            mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
            int mTorchPowerButtonValue = Settings.Secure.getInt(resolver,
                    Settings.Secure.TORCH_POWER_BUTTON_GESTURE, 0);
            mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
            mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
            mTorchPowerButton.setOnPreferenceChangeListener(this);
        }


        mCustomSummary = (Preference) prefScreen.findPreference(PREF_SS_SETTINGS_SUMMARY);
        updateCustomSummaryTextString();
			
          
        Preference FlashOnCall = findPreference("flashlight_on_call");
        
        if (!com.mrapocalypse.screwdshop.util.Utils.deviceSupportsFlashLight(getActivity())) {
            prefSet.removePreference(FlashOnCall);
        } else {
						mFlashlightOnCall = (ListPreference) findPreference(FLASHLIGHT_ON_CALL);
        		int flashlightValue = Settings.System.getInt(getContentResolver(),
                Settings.System.FLASHLIGHT_ON_CALL, 0);
        		mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
        		mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
        		mFlashlightOnCall.setOnPreferenceChangeListener(this);
				}
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) newValue);
            int index = mTorchPowerButton.findIndexOfValue((String) newValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.Secure.putInt(resolver, Settings.Secure.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            if (mTorchPowerButtonValue == 1) {
                //if doubletap for torch is enabled, switch off double tap for camera
                Settings.Secure.putInt(resolver, Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED,
                        1);
            }
            return true;
        } else if (preference == mFlashlightOnCall) {
            int flashlightValue = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(resolver,
                Settings.System.FLASHLIGHT_ON_CALL, flashlightValue);
            mFlashlightOnCall.setValue(String.valueOf(flashlightValue));
            mFlashlightOnCall.setSummary(mFlashlightOnCall.getEntry());
           return true;
       }
        return false;
    }



    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCREWD;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mCustomSummary) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_summary_title);
            alert.setMessage(R.string.custom_summary_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomSummaryText) ? "" : mCustomSummaryText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(resolver, Settings.System.SS_SETTINGS_SUMMARY, value);
                            updateCustomSummaryTextString();
                        }
                    });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        } else {
            return super.onPreferenceTreeClick(preference);
        }
        return false;
    }

    private void updateCustomSummaryTextString() {
        mCustomSummaryText = Settings.System.getString(
                getActivity().getContentResolver(), Settings.System.SS_SETTINGS_SUMMARY);

        if (TextUtils.isEmpty(mCustomSummaryText)) {
            mCustomSummary.setSummary(R.string.screw_shop_summary_title);
        } else {
            mCustomSummary.setSummary(mCustomSummaryText);
        }
    }

}
