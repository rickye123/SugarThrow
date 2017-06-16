package com.example.richa.sugarthrow;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends MainActivity {


    private String username;
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
            }
        }
        else {
            username = (String)savedInstanceState.getSerializable("username");
        }

        setContentView(R.layout.settings_activity);
        setNavigationUsername(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_settings);

        Connector database = LoginActivity.getDatabaseConnection();
        executeSQL = new Execute(database);


        final LinearLayout profile = (LinearLayout)findViewById(R.id.profile_layout);
        clickLayout(profile);
        final LinearLayout goals = (LinearLayout)findViewById(R.id.goals_layout);

        goals.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    // change colour of layout to show click
                    launchActivity(GoalsActivity.class);
                }
                return false;
            }
        });

        final LinearLayout colour = (LinearLayout)findViewById(R.id.colour_layout);
        colour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, FeedbackActivityPopup.class);
                intent.putExtra("message", "Test Feedback Message");
                startActivity(intent);
            }
        });
        TextView text = (TextView)findViewById(R.id.settings_sign_out_text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SignOutPopup.class));
            }
        });

    }


    public void clickLayout(final LinearLayout layout) {


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dropDown = layout.getChildAt(1);
                if (dropDown.getVisibility() == View.GONE) {
                    dropDown.setVisibility(View.VISIBLE);


                    updateProfile();
                    layout.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropDown.setBackgroundResource(R.drawable.shape_borderbottom);
                }
                else {
                    dropDown.setVisibility(View.GONE);
                    dropDown.setBackgroundResource(R.drawable.border_bottom);
                    layout.setBackgroundResource(R.drawable.border_bottom);

                }

            }
        });
    }

    private void updateProfile() {

        List<List<String>> users = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_SPECIFIC_USER, username);

        HashMap<String, TextView> profile = getProfile();

        profile.get("username").setText(users.get(0).get(1));
        profile.get("name").setText(users.get(0).get(2));

        String height = users.get(0).get(5);
        String[] heightSplit = height.split("\\.");

        if(heightSplit.length == 1) {
            profile.get("heightInches").setText("0");
        }

        profile.get("heightFeet").setText(heightSplit[0]);


        String weight = users.get(0).get(6);
        String[] weightSplit = weight.split("\\.");

        if(weightSplit.length == 1) {
            profile.get("weightPound").setText("0");
        }

        profile.get("weightStone").setText(weightSplit[0]);


        profile.get("dob").setText(users.get(0).get(4));
        profile.get("points").setText(users.get(0).get(8));


    }

    private HashMap<String, TextView> getProfile() {

        HashMap<String, TextView> profileData = new HashMap<>();

        profileData.put("name", (TextView)findViewById(R.id.profile_name));
        profileData.put("username", (TextView)findViewById(R.id.profile_username));
        profileData.put("heightFeet", (TextView)findViewById(R.id.height_feet));
        profileData.put("heightInches", (TextView)findViewById(R.id.height_inches));
        profileData.put("weightStone", (TextView)findViewById(R.id.weight_stone_measure));
        profileData.put("weightPound", (TextView)findViewById(R.id.weight_pound_measure));
        profileData.put("dob", (TextView)findViewById(R.id.dob_profile));
        profileData.put("points", (TextView)findViewById(R.id.profile_points));

        return profileData;

    }

}
