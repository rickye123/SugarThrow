package com.example.richa.sugarthrow;

/*
This activity is where the user syncs their data with the online database
 */

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.List;
import java.util.Map;

public class SyncActivity extends MainActivity {

    private String username;
    private ContentValues contents;
    private String TAG = "SyncActivity";
    private Execute executeSQL;
    private String userId;
    private ServerDatabaseHandler serverDatabaseHandler;

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

        setContentView(R.layout.sync_activity);
        setNavigationUsername(username);

        startContent();

    }

    // start displaying the content of the activity
    private void startContent() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_settings);

        Connector database = Connector.getInstance(this);
        executeSQL = new Execute(database);
        serverDatabaseHandler = new ServerDatabaseHandler(this);



        clickSyncToAddUsers();

    }

    /**
     * The set the userId based on the one that exists on the server
     */
    private void setUserId() {

        // Get the userId from the Online database, not the local one
        Map<String, String> params = serverDatabaseHandler.setUserParams(username);
        String url = "http://sugarthrow.hopto.org/my_server/select.php";
        serverDatabaseHandler.select(url, params, "Users", new ServerCallBack() {
            @Override
            public void onSuccess(String result) {
                contents = serverDatabaseHandler.getContents();
                if(contents != null) {
                    userId = contents.get("userId").toString();
                }
            }
        });

    }

    /**
     * Click the sync button to commence insertion
     */
    private void clickSyncToAddUsers() {
        final LinearLayout syncing = (LinearLayout)findViewById(R.id.syncing_layout);
        final LinearLayout syncButton = (LinearLayout)findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the userId from the Online database, not the local one
                Map<String, String> params = serverDatabaseHandler.setUserParams(username);
                String url = "http://sugarthrow.hopto.org/my_server/select.php";
                serverDatabaseHandler.select(url, params, "Users", new ServerCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        contents = serverDatabaseHandler.getContents();
                        if(contents != null) {
                            userId = contents.get("userId").toString();
                            syncing.setVisibility(View.VISIBLE);
                            syncButton.setVisibility(View.GONE);
                            insertContents();

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    syncing.setVisibility(View.GONE);
                                    syncButton.setVisibility(View.VISIBLE);
                                }

                            }, 6000);
                        }
                    }
                });



            }
        });
    }

    /**
     * Insert contents from local database into contents table on server
     * @param theDate - the date for the contents table
     * @param values - the values inserted into the contents table
     */
    private void insertIntoContents(final String theDate, final Map<String, String> values) {

        Map<String, String> params = serverDatabaseHandler.setContentParams(userId, theDate);
        String url = "http://sugarthrow.hopto.org/my_server/select_where.php";
        Log.d(TAG, "SELECTING CONTENTS");
        serverDatabaseHandler.select(url, params, "Contents", new ServerCallBack() {
            @Override
            public void onSuccess(String result) {
                contents = serverDatabaseHandler.getContents();
                if(contents == null) {
                    // insert into Contents table
                    String url = "http://sugarthrow.hopto.org/my_server/insert_into_contents.php";
                    Log.d(TAG, "INSERTING CONTENTS");
                    serverDatabaseHandler.insert(url, values, new ServerCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(SyncActivity.this, "Sync Complete", Toast.LENGTH_SHORT).show();
                                }

                            }, 6000);
                        }
                    });

                }
                else {
                    String url = "http://sugarthrow.hopto.org/my_server/update_contents.php";
                    Log.d(TAG, "UPDATING CONTENTS");
                    serverDatabaseHandler.update(url, values, new ServerCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(SyncActivity.this, "Sync Complete", Toast.LENGTH_SHORT).show();
                                }

                            }, 6000);
                        }
                    });
                }


            }
        });
    }

    /**
     * Insert contents
     */
    private void insertContents() {

        List<List<String>> dates = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DATES, username);

        for(int i = 0; i < dates.size(); i++) {
            List<List<String>> rows = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ENTRIES,
                    dates.get(i).get(0), username);
            Map<String, String> values = serverDatabaseHandler.setContentContents(rows,
                    userId, dates.get(i).get(0));
            final String theDate = dates.get(i).get(0);

            // check if contents already in table...
            insertIntoContents(theDate, values);


        }

    }

}
