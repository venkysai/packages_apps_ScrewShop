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

package com.mrapocalypse.screwdshop;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.design.widget.Snackbar;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;

import com.mrapocalypse.screwdshop.tabs.Navigation;
import com.mrapocalypse.screwdshop.tabs.System;
import com.mrapocalypse.screwdshop.tabs.UI;

import android.preference.PreferenceFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.SettingsPreferenceFragment;

import com.mrapocalypse.screwdshop.util.Root;

/**
 * Created by MrApocalypse on 9/6/2016.
 */


public class ScrewdShop extends SettingsPreferenceFragment {

    ViewPager mViewPager;
    ViewGroup mContainer;
    PagerSlidingTabStrip mTabs;
    SectionsPagerAdapter mSectionsPagerAdapter;
    boolean weAreScrewd = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;

        View view = inflater.inflate(R.layout.screwd_main, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabs.setViewPager(mViewPager);

        setHasOptionsMenu(true);

        weAreScrewd = areWeScrewd();

        if (weAreScrewd) {
            //Snackbar.make(view.findViewById(R.id.main_content),getString(R.string.screwd_device_success), Snackbar.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getContext(),getString(R.string.screwd_device_failure), Toast.LENGTH_LONG).show();
            android.os.Process.killProcess(android.os.Process.myPid());
            java.lang.System.exit(1);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart_ui:
                Root.runCommand("pkill -f com.android.systemui");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new UI();
            frags[1] = new System();
            frags[2] = new Navigation();
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private String[] getTitles() {
        String titleString[];
        titleString = new String[]{
                getString(R.string.ui_tab),
                getString(R.string.system_tab),
                getString(R.string.navigation_tab)};

        return titleString;
    }

    public static boolean areWeScrewd() {
        Process p = null;
        String flavor = "";
        try {
            p = new ProcessBuilder("/system/bin/getprop", "ro.build.flavor").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null) {
                flavor= line;
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (flavor.contains("screwd")) {
            return true;
        } else {
            return false;
        }
    }

    private static class SummaryProvider implements SummaryLoader.SummaryProvider {

        private final Context mContext;
        private final SummaryLoader mSummaryLoader;

        public SummaryProvider(Context context, SummaryLoader summaryLoader) {
            mContext = context;
            mSummaryLoader = summaryLoader;
        }

        @Override
        public void setListening(boolean listening) {
            if (listening) {
                mSummaryLoader.setSummary(this, mContext.getString(R.string.screwd_settings_summary_title));
            }
        }
    }

    public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY
            = new SummaryLoader.SummaryProviderFactory() {
        @Override
        public SummaryLoader.SummaryProvider createSummaryProvider(Activity activity,
                                                                   SummaryLoader summaryLoader) {
            return new SummaryProvider(activity, summaryLoader);
        }
    };

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
     }

}


