package com.mrapocalypse.screwdshop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by MrApocalypse on 9/6/2016.
 */
public class Launcher extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent launch = new Intent(this, ScrewdShop.class);
        //startActivity(launch);
        //finish();

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new ScrewdShop()).commit();
    }
}
