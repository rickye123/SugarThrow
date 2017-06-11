package com.example.richa.sugarthrow;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class InfoActivity extends MainActivity {

   // private Connector database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_info);

        //TODO powered by Nutritionix

    }
}
