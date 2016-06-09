package com.screwdaosp.screwshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.screwdaosp.screwshop.tabs.Navigation;
import com.screwdaosp.screwshop.tabs.System;
import com.screwdaosp.screwshop.tabs.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;

public class ScrewdShop extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    boolean dblBackToExitPressedOnce = false;
    boolean weAreScrewd = false;

    AppBarLayout appBarLayout;

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppTheme_Dark_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(false); //start collapsed

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        weAreScrewd = areWeScrewd();

        if (weAreScrewd) {
            Snackbar.make(findViewById(R.id.main_content),getString(R.string.screwd_device_success), Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,getString(R.string.screwd_device_failure), Toast.LENGTH_LONG).show();
            android.os.Process.killProcess(android.os.Process.myPid());
            java.lang.System.exit(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        boolean isChecked = settings.getBoolean("checkbox", false);
        MenuItem item = menu.findItem(R.id.theme_mode);
        item.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(findViewById(R.id.main_content),"Nothing to see here yet!!!", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.theme_mode) {
            item.setChecked(!item.isChecked());
            SharedPreferences settings = getSharedPreferences("settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("checkbox", item.isChecked());
            editor.commit();
            toggleTheme(item.isChecked());
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        if (dblBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.dblBackToExitPressedOnce = true;
        Snackbar.make(findViewById(R.id.main_content),R.string.press_back_again, Snackbar.LENGTH_SHORT).show();
        appBarLayout.setExpanded(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dblBackToExitPressedOnce = false;
            }
        }, 2000);
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

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getIntent();
        finish();

        startActivity(intent);
    }
}
