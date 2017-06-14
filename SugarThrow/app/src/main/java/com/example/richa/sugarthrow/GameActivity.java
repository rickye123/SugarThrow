package com.example.richa.sugarthrow;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class GameActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_game);

        Connector database = LoginActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
        if (db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }
    }
}
