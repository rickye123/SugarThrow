package com.example.richa.sugarthrow;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class RiskActivity extends MainActivity {

    private String username;

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
            }
        }
        else {
            username = (String)savedInstanceState.getSerializable("username");
        }

        setContentView(R.layout.risk_activity);
        setNavigationUsername(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_risk);

        Connector database = LoginActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
        if (db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }
    }
}
