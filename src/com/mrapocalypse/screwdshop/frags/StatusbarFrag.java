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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.EditText;

import java.util.Date;

import com.android.internal.util.screwd.screwdUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

/**
 * Created by cedwards on 6/3/2016.
 */
public class StatusbarFrag extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String FORCE_EXPANDED_NOTIFICATIONS = "force_expanded_notifications";
    private static final String PREF_CLOCK_STYLE = "clock_style";
    private static final String PREF_AM_PM_STYLE = "status_bar_am_pm";
    private static final String PREF_CLOCK_DATE_DISPLAY = "clock_date_display";
    private static final String PREF_CLOCK_DATE_STYLE = "clock_date_style";
    private static final String PREF_CLOCK_DATE_POSITION = "clock_date_position";
    private static final String PREF_CLOCK_DATE_FORMAT = "clock_date_format";
    private static final String STATUS_BAR_CLOCK = "status_bar_show_clock";
    private static final String STATUS_BAR_CLOCK_SECONDS = "status_bar_clock_seconds";
    private static final String MISSED_CALL_BREATH = "missed_call_breath";
    private static final String VOICEMAIL_BREATH = "voicemail_breath";
    private static final String SMS_BREATH = "sms_breath";
    private static final String BREATHING_NOTIFICATIONS = "breathing_notifications";
    private static final String SHOW_CARRIER_LABEL = "status_bar_show_carrier";
    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String STATUS_BAR_CARRIER_COLOR = "status_bar_carrier_color";
    private static final String KEY_SCREWD_LOGO_COLOR = "status_bar_screwd_logo_color";
    //private static final String KEY_SCREWD_LOGO_STYLE = "status_bar_screwd_logo_style";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String BATTERY_TILE_STYLE = "battery_tile_style";
    private static final String STATUS_BAR_CHARGE_COLOR = "status_bar_charge_color";
    private static final String FORCE_CHARGE_BATTERY_TEXT = "force_charge_battery_text";
    private static final String TEXT_CHARGING_SYMBOL = "text_charging_symbol";
    private static final String STATUS_BAR_SHOW_TICKER = "status_bar_show_ticker";
    private static final String PREF_STATUS_BAR_WEATHER = "status_bar_weather";
    private static final String PREF_CATEGORY_INDICATORS = "pref_cat_icons";
    private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";

    static final int DEFAULT_STATUS_CARRIER_COLOR = 0xffffffff;

    public static final int CLOCK_DATE_STYLE_LOWERCASE = 1;
    public static final int CLOCK_DATE_STYLE_UPPERCASE = 2;
    private static final int CUSTOM_CLOCK_DATE_FORMAT_INDEX = 18;
    private static final int STATUS_BAR_BATTERY_STYLE_PORTRAIT = 0;
    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    private SwitchPreference mEnableNC;
    private SwitchPreference mForceExpanded;
    private ListPreference mClockStyle;
    private ListPreference mClockAmPmStyle;
    private ListPreference mClockDateDisplay;
    private ListPreference mClockDateStyle;
    private ListPreference mClockDatePosition;
    private ListPreference mClockDateFormat;
    private SwitchPreference mStatusBarClock;
    private SwitchPreference mStatusBarClockSeconds;
    private SwitchPreference mMissedCallBreath;
    private SwitchPreference mVoicemailBreath;
    private SwitchPreference mSmsBreath;
    private PreferenceGroup mBreathingNotifications;
    private PreferenceScreen mCustomCarrierLabel;
    private ListPreference mShowCarrierLabel;
    private String mCustomCarrierLabelText;
    private ColorPickerPreference mCarrierColorPicker;
    private ColorPickerPreference mScrewdLogoColor;
    //private ListPreference mScrewdLogoStyle;
    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;
    private int mBatteryTileStyleValue;
    private ListPreference mBatteryTileStyle;
    private int mStatusBarBatteryValue;
    private int mStatusBarBatteryShowPercentValue;
    private ColorPickerPreference mChargeColor;
    private SwitchPreference mForceChargeBatteryText;
    private ListPreference mTextChargingSymbol;
    private int mTextChargingSymbolValue;
    private ListPreference mShowTicker;
    private ListPreference mStatusBarWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_frag);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            // do something
        }

        PreferenceCategory categoryIndicators = (PreferenceCategory) prefSet.findPreference(PREF_CATEGORY_INDICATORS);

        mEnableNC = (SwitchPreference) findPreference(STATUS_BAR_NOTIF_COUNT);
        mEnableNC.setOnPreferenceChangeListener(this);
        int EnableNC = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_NOTIF_COUNT, 0);
        mEnableNC.setChecked(EnableNC != 0);

        mForceExpanded = (SwitchPreference) findPreference(FORCE_EXPANDED_NOTIFICATIONS);
        mForceExpanded.setOnPreferenceChangeListener(this);
        int ForceExpanded = Settings.System.getInt(getContentResolver(),
                FORCE_EXPANDED_NOTIFICATIONS, 0);
        mForceExpanded.setChecked(ForceExpanded != 0);

        mClockStyle = (ListPreference) findPreference(PREF_CLOCK_STYLE);
        mClockStyle.setOnPreferenceChangeListener(this);
        mClockStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_STYLE,
                0)));
        mClockStyle.setSummary(mClockStyle.getEntry());

        mClockAmPmStyle = (ListPreference) prefSet.findPreference(PREF_AM_PM_STYLE);
        mClockAmPmStyle.setOnPreferenceChangeListener(this);
        mClockAmPmStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE,
                0)));
        boolean is24hour = DateFormat.is24HourFormat(getActivity());
        if (is24hour) {
            mClockAmPmStyle.setSummary(R.string.status_bar_am_pm_info);
        } else {
            mClockAmPmStyle.setSummary(mClockAmPmStyle.getEntry());
        }
        mClockAmPmStyle.setEnabled(!is24hour);

        mClockDateDisplay = (ListPreference) findPreference(PREF_CLOCK_DATE_DISPLAY);
        mClockDateDisplay.setOnPreferenceChangeListener(this);
        mClockDateDisplay.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_DISPLAY,
                0)));
        mClockDateDisplay.setSummary(mClockDateDisplay.getEntry());

        mClockDateStyle = (ListPreference) findPreference(PREF_CLOCK_DATE_STYLE);
        mClockDateStyle.setOnPreferenceChangeListener(this);
        mClockDateStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_STYLE,
                0)));
        mClockDateStyle.setSummary(mClockDateStyle.getEntry());

        mClockDatePosition = (ListPreference) findPreference(PREF_CLOCK_DATE_POSITION);
        mClockDatePosition.setOnPreferenceChangeListener(this);
        mClockDatePosition.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_POSITION,
                0)));
        mClockDatePosition.setSummary(mClockDatePosition.getEntry());

        mClockDateFormat = (ListPreference) findPreference(PREF_CLOCK_DATE_FORMAT);
        mClockDateFormat.setOnPreferenceChangeListener(this);
        if (mClockDateFormat.getValue() == null) {
            mClockDateFormat.setValue("EEE");
        }

        parseClockDateFormats();

        mStatusBarClock = (SwitchPreference) prefSet.findPreference(STATUS_BAR_CLOCK);
        mStatusBarClock.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarClock.setOnPreferenceChangeListener(this);

        mStatusBarClockSeconds = (SwitchPreference) findPreference(STATUS_BAR_CLOCK_SECONDS);
        mStatusBarClockSeconds.setChecked((Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK_SECONDS, 0) == 1));
        mStatusBarClockSeconds.setOnPreferenceChangeListener(this);

        boolean mClockDateToggle = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_DISPLAY, 0) != 0;
        if (!mClockDateToggle) {
            mClockDateStyle.setEnabled(false);
            mClockDatePosition.setEnabled(false);
            mClockDateFormat.setEnabled(false);
        }

        mMissedCallBreath = (SwitchPreference) findPreference(MISSED_CALL_BREATH);
        mVoicemailBreath = (SwitchPreference) findPreference(VOICEMAIL_BREATH);
        mSmsBreath = (SwitchPreference) findPreference(SMS_BREATH);

        mBreathingNotifications = (PreferenceGroup) findPreference(BREATHING_NOTIFICATIONS);

        Context context = getActivity();
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE)) {

            mMissedCallBreath.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.KEY_MISSED_CALL_BREATH, 0) == 1);
            mMissedCallBreath.setOnPreferenceChangeListener(this);

            mVoicemailBreath.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.KEY_VOICEMAIL_BREATH, 0) == 1);
            mVoicemailBreath.setOnPreferenceChangeListener(this);

            mSmsBreath.setChecked(Settings.Global.getInt(getContentResolver(),
                    Settings.Global.KEY_SMS_BREATH, 0) == 1);
            mSmsBreath.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mMissedCallBreath);
            prefSet.removePreference(mVoicemailBreath);
            prefSet.removePreference(mSmsBreath);
            prefSet.removePreference(mBreathingNotifications);
        }

        mShowCarrierLabel = (ListPreference) findPreference(SHOW_CARRIER_LABEL);
        int showCarrierLabel = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_SHOW_CARRIER, 1);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);

        mCustomCarrierLabel = (PreferenceScreen) findPreference(CUSTOM_CARRIER_LABEL);
        if (TelephonyManager.getDefault().isMultiSimEnabled()) {
            prefSet.removePreference(mCustomCarrierLabel);
        } else {
            updateCustomLabelTextSummary();
        }

        mCarrierColorPicker = (ColorPickerPreference) findPreference(STATUS_BAR_CARRIER_COLOR);
        mCarrierColorPicker.setOnPreferenceChangeListener(this);
        int carrColor = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CARRIER_COLOR, DEFAULT_STATUS_CARRIER_COLOR);
        String carrHexColor = String.format("#%08x", (0xffffffff & carrColor));
        mCarrierColorPicker.setSummary(carrHexColor);
        mCarrierColorPicker.setNewPreviewColor(carrColor);

        /*
        mScrewdLogoStyle = (ListPreference) findPreference(KEY_SCREWD_LOGO_STYLE);
        int screwdLogoStyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SCREWD_LOGO_STYLE, 0,
                UserHandle.USER_CURRENT);
        mScrewdLogoStyle.setValue(String.valueOf(screwdLogoStyle));
        mScrewdLogoStyle.setSummary(mScrewdLogoStyle.getEntry());
        mScrewdLogoStyle.setOnPreferenceChangeListener(this);

        */

        // Aicp logo color
        mScrewdLogoColor =
            (ColorPickerPreference) prefSet.findPreference(KEY_SCREWD_LOGO_COLOR);
        mScrewdLogoColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_SCREWD_LOGO_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mScrewdLogoColor.setSummary(hexColor);
        mScrewdLogoColor.setNewPreviewColor(intColor);

        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBatteryValue = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(Integer.toString(mStatusBarBatteryValue));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);
        mStatusBarBatteryShowPercentValue = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(Integer.toString(mStatusBarBatteryShowPercentValue));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);

        int chargeColor = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_CHARGE_COLOR, Color.WHITE);
        mChargeColor = (ColorPickerPreference) findPreference("status_bar_charge_color");
        mChargeColor.setNewPreviewColor(chargeColor);
        mChargeColor.setOnPreferenceChangeListener(this);

        mForceChargeBatteryText = (SwitchPreference) findPreference(FORCE_CHARGE_BATTERY_TEXT);
        mForceChargeBatteryText.setChecked((Settings.Secure.getInt(resolver,
                Settings.Secure.FORCE_CHARGE_BATTERY_TEXT, 0) == 1));
        mForceChargeBatteryText.setOnPreferenceChangeListener(this);

        mTextChargingSymbol = (ListPreference) findPreference(TEXT_CHARGING_SYMBOL);
        mTextChargingSymbolValue = Settings.Secure.getInt(resolver,
                Settings.Secure.TEXT_CHARGING_SYMBOL, 0);
        mTextChargingSymbol.setValue(Integer.toString(mTextChargingSymbolValue));
        mTextChargingSymbol.setSummary(mTextChargingSymbol.getEntry());
        mTextChargingSymbol.setOnPreferenceChangeListener(this);

        mBatteryTileStyle = (ListPreference) findPreference(BATTERY_TILE_STYLE);
        mBatteryTileStyleValue = Settings.Secure.getInt(resolver,
                Settings.Secure.BATTERY_TILE_STYLE, 0);
        mBatteryTileStyle.setValue(Integer.toString(mBatteryTileStyleValue));
        mBatteryTileStyle.setSummary(mBatteryTileStyle.getEntry());
        mBatteryTileStyle.setOnPreferenceChangeListener(this);

        enableStatusBarBatteryDependents();

        mShowTicker = (ListPreference) findPreference(STATUS_BAR_SHOW_TICKER);
        mShowTicker.setOnPreferenceChangeListener(this);
        int tickerMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER,
                0, UserHandle.USER_CURRENT);
        mShowTicker.setValue(String.valueOf(tickerMode));
        mShowTicker.setSummary(mShowTicker.getEntry());

        // Status bar weather
        mStatusBarWeather = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_WEATHER);
        if (mStatusBarWeather != null && (!screwdUtils.isPackageInstalled(getActivity(),WEATHER_SERVICE_PACKAGE))) {
            categoryIndicators.removePreference(mStatusBarWeather);
        } else {
            int temperatureShow = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                UserHandle.USER_CURRENT);
            mStatusBarWeather.setValue(String.valueOf(temperatureShow));
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(mStatusBarWeather.getEntry());
            }
            mStatusBarWeather.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        AlertDialog dialog;
        ContentResolver resolver = getActivity().getContentResolver();
        if  (preference == mEnableNC) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), STATUS_BAR_NOTIF_COUNT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mForceExpanded) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), FORCE_EXPANDED_NOTIFICATIONS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mClockAmPmStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockAmPmStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, val);
            mClockAmPmStyle.setSummary(mClockAmPmStyle.getEntries()[index]);
            return true;
        } else if (preference == mClockStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_STYLE, val);
            mClockStyle.setSummary(mClockStyle.getEntries()[index]);
            return true;
        } else if (preference == mClockDateDisplay) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDateDisplay.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_DISPLAY, val);
            mClockDateDisplay.setSummary(mClockDateDisplay.getEntries()[index]);
            if (val == 0) {
                mClockDateStyle.setEnabled(false);
                mClockDatePosition.setEnabled(false);
                mClockDateFormat.setEnabled(false);
            } else {
                mClockDateStyle.setEnabled(true);
                mClockDatePosition.setEnabled(true);
                mClockDateFormat.setEnabled(true);
            }
            return true;
        } else if (preference == mClockDateStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_STYLE, val);
            mClockDateStyle.setSummary(mClockDateStyle.getEntries()[index]);
            parseClockDateFormats();
            return true;
        } else if (preference == mClockDatePosition) {
            int val = Integer.parseInt((String) newValue);
            int index = mClockDatePosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_POSITION, val);
            mClockDatePosition.setSummary(mClockDatePosition.getEntries()[index]);
            parseClockDateFormats();
            return true;
        } else if (preference == mStatusBarClock) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mStatusBarClockSeconds) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK_SECONDS,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mClockDateFormat) {
            int index = mClockDateFormat.findIndexOfValue((String) newValue);

            if (index == CUSTOM_CLOCK_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.clock_date_string_edittext_title);
                alert.setMessage(R.string.clock_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(
                    getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(getActivity().getContentResolver(),
                            Settings.System.STATUSBAR_CLOCK_DATE_FORMAT, value);

                        return;
                    }
                });

                alert.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        return;
                    }
                });
                dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_CLOCK_DATE_FORMAT, (String) newValue);
                }
            }
            return true;
        } else if (preference == mMissedCallBreath) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), MISSED_CALL_BREATH,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVoicemailBreath) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), VOICEMAIL_BREATH,
                    value ? 1 : 0);
            return true;
        } else if (preference == mSmsBreath) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(), SMS_BREATH,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowCarrierLabel) {
            int showCarrierLabel = Integer.valueOf((String) newValue);
            int index = mShowCarrierLabel.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.
                STATUS_BAR_SHOW_CARRIER, showCarrierLabel);
            mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntries()[index]);
            return true;
        } else if (preference == mCarrierColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, intHex);
            return true;
        } else if (preference == mScrewdLogoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_SCREWD_LOGO_COLOR, intHex);
            return true;
        /*} else if (preference == mScrewdLogoStyle) {
            int screwdLogoStyle = Integer.valueOf((String) newValue);
            int index = mScrewdLogoStyle.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(
                    resolver, Settings.System.STATUS_BAR_SCREWD_LOGO_STYLE, screwdLogoStyle,
                    UserHandle.USER_CURRENT);
            mScrewdLogoStyle.setSummary(mScrewdLogoStyle.getEntries()[index]);
            return true;*/
        } else if (preference == mStatusBarBattery) {
            mStatusBarBatteryValue = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            mStatusBarBattery.setSummary(
                    mStatusBarBattery.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.STATUS_BAR_BATTERY_STYLE, mStatusBarBatteryValue);
            enableStatusBarBatteryDependents();
            return true;
        } else if (preference == mStatusBarBatteryShowPercent) {
            mStatusBarBatteryShowPercentValue = Integer.valueOf((String) newValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
            mStatusBarBatteryShowPercent.setSummary(
                    mStatusBarBatteryShowPercent.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.STATUS_BAR_SHOW_BATTERY_PERCENT, mStatusBarBatteryShowPercentValue);
            enableStatusBarBatteryDependents();
            return true;
        } else if (preference.equals(mChargeColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.Secure.putInt(resolver,
                    Settings.Secure.STATUS_BAR_CHARGE_COLOR, color);
            return true;
        } else if  (preference == mForceChargeBatteryText) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.Secure.putInt(resolver,
                    Settings.Secure.FORCE_CHARGE_BATTERY_TEXT, checked ? 1:0);
            return true;
         } else if (preference == mBatteryTileStyle) {
            mBatteryTileStyleValue = Integer.valueOf((String) newValue);
            int index = mBatteryTileStyle.findIndexOfValue((String) newValue);
            mBatteryTileStyle.setSummary(
                    mBatteryTileStyle.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.BATTERY_TILE_STYLE, mBatteryTileStyleValue);
            return true;
        } else if (preference == mTextChargingSymbol) {
            mTextChargingSymbolValue = Integer.valueOf((String) newValue);
            int index = mTextChargingSymbol.findIndexOfValue((String) newValue);
            mTextChargingSymbol.setSummary(
                    mTextChargingSymbol.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.TEXT_CHARGING_SYMBOL, mTextChargingSymbolValue);
            return true;
        } else if (preference.equals(mShowTicker)) {
            int tickerMode = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode,
                    UserHandle.USER_CURRENT);
            int index = mShowTicker.findIndexOfValue((String) newValue);
            mShowTicker.setSummary(mShowTicker.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarWeather) {
            int temperatureShow = Integer.valueOf((String) newValue);
            int index = mStatusBarWeather.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                   Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP,
                   temperatureShow, UserHandle.USER_CURRENT);
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(
                mStatusBarWeather.getEntries()[index]);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mCustomCarrierLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setSelection(input.getText().length());
            alert.setView(input);
            alert.setPositiveButton(getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(resolver, Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void enableStatusBarBatteryDependents() {
        if (mStatusBarBatteryValue == STATUS_BAR_BATTERY_STYLE_HIDDEN) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mForceChargeBatteryText.setEnabled(false);
            mChargeColor.setEnabled(false);
            mTextChargingSymbol.setEnabled(false);
        } else if (mStatusBarBatteryValue == STATUS_BAR_BATTERY_STYLE_TEXT) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mForceChargeBatteryText.setEnabled(false);
            mChargeColor.setEnabled(false);
            mTextChargingSymbol.setEnabled(true);
        } else if (mStatusBarBatteryValue == STATUS_BAR_BATTERY_STYLE_PORTRAIT) {
            mStatusBarBatteryShowPercent.setEnabled(true);
            mChargeColor.setEnabled(true);
            mForceChargeBatteryText.setEnabled(mStatusBarBatteryShowPercentValue == 2 ? false : true);
            //relying on the mForceChargeBatteryText isChecked state is glitchy
            //you need to click it twice to update the mTextChargingSymbol setEnabled state
            //then the mForceChargeBatteryText isChecked state is incorrectly taken inverted
            //so till a fix let's keep mTextChargingSymbol enabled by default
            //mTextChargingSymbol.setEnabled((mStatusBarBatteryShowPercentValue == 0 && !mForceChargeBatteryText.isChecked())
            //|| (mStatusBarBatteryShowPercentValue == 1 && !mForceChargeBatteryText.isChecked()) ? false : true);
            mTextChargingSymbol.setEnabled(true);
        } else {
            mStatusBarBatteryShowPercent.setEnabled(true);
            mChargeColor.setEnabled(true);
            mForceChargeBatteryText.setEnabled(mStatusBarBatteryShowPercentValue == 2 ? false : true);
            //mTextChargingSymbol.setEnabled((mStatusBarBatteryShowPercentValue == 0 && !mForceChargeBatteryText.isChecked())
            //|| (mStatusBarBatteryShowPercentValue == 1 && !mForceChargeBatteryText.isChecked()) ? false : true);
            mTextChargingSymbol.setEnabled(true);
        }
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
            getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);

        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }

    private void parseClockDateFormats() {
        String[] dateEntries = getResources().getStringArray(R.array.clock_date_format_entries_values);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_DATE_STYLE, 0);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == CLOCK_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == CLOCK_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mClockDateFormat.setEntries(parsedDateEntries);
    }


    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }



}
