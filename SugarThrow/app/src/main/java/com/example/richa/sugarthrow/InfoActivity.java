package com.example.richa.sugarthrow;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class InfoActivity extends MainActivity {

/*    private String username;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String username, previousActivity;
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                username = "Username";
            }
            else {
                username = extras.getString("username");
                previousActivity = extras.getString("activity");

            }
        }
        else {
            username = (String)savedInstanceState.getSerializable("username");
            previousActivity = (String)savedInstanceState.getSerializable("activity");
        }

        setContentView(R.layout.info_activity);
        setNavigationUsername(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_info);



    }
}
