package com.example.richa.sugarthrow;

/*
This activity is resposible for the Machine Learning functionality of the app
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class RiskActivity extends MainActivity {

    private String username, previousActivity;
    private Execute executeSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setContentView(R.layout.risk_activity);
        setNavigationUsername(username);

        startContent();

    }

    private void startContent() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_risk);

        Connector database = Connector.getInstance(this);
        executeSQL = new Execute(database);

    }

}
