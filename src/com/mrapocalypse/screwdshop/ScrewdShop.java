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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContentResolver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v13.app.FragmentPagerAdapter;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;
import java.util.Random;

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

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.SettingsPreferenceFragment;

import com.mrapocalypse.screwdshop.util.Root;
import com.android.internal.util.screwd.screwdUtils;

/**
 * Created by MrApocalypse on 9/6/2016.
 */


public class ScrewdShop extends SettingsPreferenceFragment {

    private static final String LEAN_PACKAGE_NAME = "com.screwdaosp.lean";
    private static final Intent LEAN_PACKAGE_INTENT = new Intent().setComponent(new ComponentName(LEAN_PACKAGE_NAME, "com.screwdaosp.lean.MainActivity"));

    ViewPager mViewPager;
    View view;
    boolean weHaveLean;
    ViewGroup mContainer;
    PagerSlidingTabStrip mTabs;
    SectionsPagerAdapter mSectionsPagerAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (isLeanAlive()) {
            mContainer = container;
            view = inflater.inflate(R.layout.screwd_main, container, false);
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mTabs.setViewPager(mViewPager);

            setHasOptionsMenu(true);
        } else {
            view = inflater.inflate(R.layout.nothing_to_see, container, false);
        }

        return view;
    }

    public boolean isLeanAlive() {
        PackageManager pm = getActivity().getPackageManager();
        boolean result = false;
        try {
            PackageInfo pi = pm.getPackageInfo(LEAN_PACKAGE_NAME,PackageManager.GET_META_DATA);
            ApplicationInfo ai = pi.applicationInfo;
            boolean installed = screwdUtils.isPackageInstalled(getActivity(), LEAN_PACKAGE_NAME);
            boolean enabled = ai.enabled;
             if (enabled && installed) {
              result = true;
             } else {
              result = false;
             }
        } catch (PackageManager.NameNotFoundException n) {
          //Cant find Lean anyway..
          result = false;
        }

        return result;

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
            case R.id.about:
                if (isLeanAlive()) {
                   startActivity(LEAN_PACKAGE_INTENT);
                }
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

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCREWD;
    }

}


