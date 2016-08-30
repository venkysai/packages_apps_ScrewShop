package com.screwdaosp.screwshop;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.android.settings.SettingsPreferenceFragment;

import com.android.internal.logging.MetricsProto.MetricsEvent;


public class Launcher extends SettingsPreferenceFragment   {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent launch = new Intent(getActivity(), ScrewdShop.class);
        startActivity(launch);
        finish();
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }
}