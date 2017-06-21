package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiskActivity extends MainActivity {

    private String username;
    private RequestQueue queue;
    private ContentValues contents;
    private Map<String, String> values;
    private String TAG = "RiskActivity";
    private Execute executeSQL;
    private List<List<String>> userInfo;
    private TableDisplay display = new TableDisplay();
    private String userId, contentId, foodId;
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

        queue = Volley.newRequestQueue(this);
        Connector database = Connector.getInstance(this);
        executeSQL = new Execute(database);
        serverDatabaseHandler = new ServerDatabaseHandler(this);

        userInfo = executeSQL.sqlGetFromQuery(SqlQueries.SQL_USER, username);
        display.printTable("User info", userInfo);

        setUserId();

        // add users by clicking this sync button
        clickSyncToAddUsers();

        LinearLayout sync2 = (LinearLayout)findViewById(R.id.sync_button_2);
        sync2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertEverything("2017-06-21");
            }
        });



    }

    private void clickSyncToAddUsers() {
        LinearLayout syncButton = (LinearLayout)findViewById(R.id.sync_button);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psuedoInsert();
            }
        });
    }

    private void insertIntoUsers() {

        Map<String, String> params = serverDatabaseHandler.setUserParams(username);
        String url = "http://sugarthrow.hopto.org/my_server/select.php";
        serverDatabaseHandler.select(url, params, "Users", new ServerCallBack() {
            @Override
            public void onSuccess(String result) {
                contents = serverDatabaseHandler.getContents();
                if(contents == null) {
                    // insert into Users table
                    Map<String, String> userContents = serverDatabaseHandler.setUserContents(userInfo);
                    String url = "http://sugarthrow.hopto.org/my_server/insert_into.php";
                    serverDatabaseHandler.insert(url, userContents, new ServerCallBack() {
                        @Override
                        public void onSuccess(String result) {

                        }
                    });
                }
            }
        });

    }

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
                            Toast.makeText(RiskActivity.this, "Contents Added", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }


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

    private void setContentId(String theDate) {

        // Get the userId from the Online database, not the local one
        Map<String, String> params = serverDatabaseHandler.setContentParams(userId, theDate);
        String url = "http://sugarthrow.hopto.org/my_server/select.php";
        serverDatabaseHandler.select(url, params, "Contents", new ServerCallBack() {
            @Override
            public void onSuccess(String result) {
                contents = serverDatabaseHandler.getContents();
                if(contents != null) {
                    contentId = contents.get("contentId").toString();
                }
            }
        });

    }

    private void setFoodId(String foodName) {

        // Get the userId from the Online database, not the local one
        Map<String, String> params = serverDatabaseHandler.setFoodParams(foodName);
        String url = "http://sugarthrow.hopto.org/my_server/select.php";
        serverDatabaseHandler.select(url, params, "Food", new ServerCallBack() {
            @Override
            public void onSuccess(String result) {
                contents = serverDatabaseHandler.getContents();
                if(contents != null) {
                    foodId = contents.get("foodId").toString();
                }
            }
        });
    }


    private void insertEverything(String theDate) {

        setContentId(theDate);

        List<List<String>> entry = executeSQL.sqlGetFromQuery(SqlQueries.SQL_DIARY_ENTRY,
                theDate, username);

        for (int i = 0; i < entry.size(); i++) {

            final String foodName = entry.get(i).get(0);
            Log.d(TAG, "SELECTING FOOD");
            Map<String, String> params = serverDatabaseHandler.setFoodParams(foodName);
            String foodURL = "http://sugarthrow.hopto.org/my_server/select.php";
            serverDatabaseHandler.select(foodURL, params, "Food", new ServerCallBack() {
                @Override
                public void onSuccess(String result) {
                    contents = serverDatabaseHandler.getContents();
                    if (contents == null) {
                        // insert food into table
                        List<List<String>> foodInfo =
                                executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_FOOD, foodName);
                        String url = "http://sugarthrow.hopto.org/my_server/insert_into_food.php";
                        Map<String, String> foodContents = serverDatabaseHandler.setFoodContents(foodInfo);
                        Log.d(TAG, "INSERTING FOOD");
                        serverDatabaseHandler.insert(url, foodContents, new ServerCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                setFoodId(foodName);
                                Log.d(TAG, "INSERTING DIARY");
                                String url = "http://sugarthrow.hopto.org/my_server/insert_into_diary.php";
                                Map<String, String> diaryContents =
                                        serverDatabaseHandler.setDiaryContents(contentId, userId, foodId);
                                serverDatabaseHandler.insert(url, diaryContents, new ServerCallBack() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Log.d(TAG, "Completed Diary insert");
                                    }
                                });
                            }
                        });
                    }
                    setFoodId(foodName);

                    Log.d(TAG, "INSERTING DIARY");
                    String url = "http://sugarthrow.hopto.org/my_server/insert_into_diary.php";
                    Map<String, String> diaryContents =
                            serverDatabaseHandler.setDiaryContents(contentId, userId, foodId);
                    serverDatabaseHandler.insert(url, diaryContents, new ServerCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "COMPLETED DIARY INSERT");
                        }
                    });
                }
            });

        }
    }

    private void psuedoInsert() {

        List<List<String>> dates = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DATES, username);

        for(int i = 0; i < 1; i++) {
            List<List<String>> rows = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ENTRIES,
                    dates.get(i).get(0), username);
            Map<String, String> values = serverDatabaseHandler.setContentContents(rows,
                    userId, dates.get(i).get(0));
            final String theDate = dates.get(i).get(0);
            System.out.println(theDate);

            // check if contents already in table...
            insertIntoContents(theDate, values);


        }

    }



}
