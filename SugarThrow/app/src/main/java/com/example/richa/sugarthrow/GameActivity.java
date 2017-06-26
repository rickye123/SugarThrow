package com.example.richa.sugarthrow;

/*
This activity determines whether the user can play the game
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import java.util.List;

public class GameActivity extends MainActivity {

    private String username;
    private Execute executeSQL;
    private TimeKeeper date = new TimeKeeper();

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

    // start content when page loads
    private void startContent() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        createDrawer(toolbar);
        createNavigationView(R.id.nav_game);

        Connector database = Connector.getInstance(this);
        executeSQL = new Execute(database);

        createGame();

        TextView button = (TextView)findViewById(R.id.play_game);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame();
            }
        });

    }

    /**
     * Called to determine whether the user is able to play the game.
     * The user must log at least 7 foods in order to play the game.
     */
    private void createGame() {

        List<List<String>> numberOfFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY,
                date.convertDateFormat(date.getCurrentDate()), username);

        if(numberOfFoods.size() < 7) {
            String message;
            if(numberOfFoods.size()  == 1) {
                message = "You have only logged " + numberOfFoods.size() + " food today. You need " +
                        "to log at least 7 foods to play the game";
            }
            else {
                message = "You have only logged " + numberOfFoods.size() + " foods today. You need " +
                        "to log at least 7 foods to play the game";
            }
            launchFeedbackActivity(GameActivity.this, message, false);
        }
        else {
            launchActivity(UnityGame.class);
        }

    }

}
