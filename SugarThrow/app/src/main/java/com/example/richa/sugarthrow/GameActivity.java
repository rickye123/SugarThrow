package com.example.richa.sugarthrow;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.List;

public class GameActivity extends MainActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                username = "username";
            }
            else {
                username = extras.getString("username");
            }
        }
        else {
            username = (String)savedInstanceState.getSerializable("username");
        }

        setContentView(R.layout.game_activity);
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
        createNavigationView(R.id.nav_game);

        Connector database = Connector.getInstance(this);
        Execute executeSQL = new Execute(database);
        TimeKeeper date = new TimeKeeper();

        List<List<String>> numberOfFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY,
                date.convertDateFormat(date.getCurrentDate()), username);

        if(numberOfFoods.size() < 7) {
            String message = "You have only logged " + numberOfFoods.size() + " today. You need " +
                    "to log at least 7 foods to play the game";
            launchFeedbackActivity(GameActivity.this, message, false);
        }
        else {
            launchActivity(UnityGame.class);
        }

    }

}
