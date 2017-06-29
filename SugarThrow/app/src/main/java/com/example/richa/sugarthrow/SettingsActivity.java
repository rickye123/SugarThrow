package com.example.richa.sugarthrow;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends MainActivity {


    private String username;
    private Execute executeSQL;
    private String TAG = "SettingsActivity";
    private boolean weightClicked, profileOpen = false;
    private String stoneAmount, poundAmount;
    private EditText pound, stone;
    private String previousActivity;

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

        final LinearLayout sync = (LinearLayout)findViewById(R.id.sync_layout);
        sync.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    // change colour of layout to show click
                    launchActivity(SyncActivity.class);
                }
                return false;
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

        final LinearLayout profileWrapper = (LinearLayout)findViewById(R.id.profile_wrapper);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView dropdownArrow = (ImageView)findViewById(R.id.profile_dropdown_arrow);

                View dropDown = profileWrapper.getChildAt(1);
                if (dropDown.getVisibility() == View.GONE) {
                    dropDown.setVisibility(View.VISIBLE);

                    if(weightClicked) {
                        setWeight();
                        weightClicked = false;
                    }

                    updateProfile();
                    profileWrapper.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropDown.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropdownArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else {
                    dropDown.setVisibility(View.GONE);
                    dropDown.setBackgroundResource(R.drawable.border_bottom);
                    profileWrapper.setBackgroundResource(R.drawable.border_bottom);
                    dropdownArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                }

            }
        });
    }

    private void setWeight() {

        TextView stoneText =  new TextView(SettingsActivity.this);
        stoneText.setId(R.id.weight_stone_measure);
        TextView poundText = new TextView(SettingsActivity.this);
        poundText.setId(R.id.weight_pound_amount);
        List<List<String>> users = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_SPECIFIC_USER, username);

        System.out.println("STONE AMOUNT " + stoneAmount);

        String weight = users.get(0).get(6);
        String[] weightSplit = weight.split("\\.");
        stoneText.setText(weightSplit[0]);
        stoneText.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.black));
        stoneText.setTextSize(18);
        stoneText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        if(weightSplit.length == 1) {
            poundText.setText("0");
        }
        else {
            poundText.setText(weightSplit[1]);
        }

        poundText.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.black));
        poundText.setTextSize(18);
        poundText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 0, 0, 0);
        poundText.setLayoutParams(layoutParams);

        LinearLayout layout = (LinearLayout)findViewById(R.id.weight_layout);

        layout.removeViewAt(1);

        layout.removeViewAt(1); // remove the stone measure view
        layout.addView(stoneText, 1);

        layout.removeViewAt(3);
        layout.addView(poundText, 3);



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
        else {
            profile.get("heightInches").setText(heightSplit[1]);
        }

        profile.get("heightFeet").setText(heightSplit[0]);


        String weight = users.get(0).get(6);
        String[] weightSplit = weight.split("\\.");

        if(weightSplit.length == 1) {
            profile.get("weightPound").setText("0");
        }
        else {
            profile.get("weightPound").setText(weightSplit[1]);
        }


        profile.get("weightStone").setText(weightSplit[0]);

        clickWeight((TextView)findViewById(R.id.weight_textview));

        profile.get("dob").setText(users.get(0).get(4));
        profile.get("points").setText(users.get(0).get(8));


    }

    private void clickWeight(final TextView text) {

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout layout = (LinearLayout)findViewById(R.id.weight_layout);

                Log.d(TAG, "CLICKED WEIGHT TEXT");
                if(!weightClicked) {

                    TextView poundText = (TextView)findViewById(R.id.weight_pound_amount);
                    TextView stoneText =  (TextView)findViewById(R.id.weight_stone_measure);

                    stone = new EditText(SettingsActivity.this);
                    stone.setText(stoneText.getText().toString());
                    stone.setInputType(InputType.TYPE_CLASS_NUMBER);
                    setEditTextMaxLength(stone, 2);

                    pound = new EditText(SettingsActivity.this);
                    pound.setText(poundText.getText().toString());
                    pound.setInputType(InputType.TYPE_CLASS_NUMBER);
                    setEditTextMaxLength(pound, 2);

                    stoneAmount = stone.getText().toString();
                    poundAmount = pound.getText().toString();

                    layout.removeViewAt(1); // remove the stone measure view
                    layout.addView(stone, 1);

                    layout.removeViewAt(3);
                    layout.addView(pound, 3);

                    Button submit = new Button(SettingsActivity.this);
                    submit.setText(R.string.change_weight);

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stoneAmount = stone.getText().toString();
                            if(Integer.parseInt(pound.getText().toString()) <= 13 &&
                                    Integer.parseInt(pound.getText().toString()) >= 0) {
                                poundAmount = pound.getText().toString();
                            }
                            String updateWeight = stoneAmount.concat("." + poundAmount);
                            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_WEIGHT, updateWeight, username);
                            Toast.makeText(SettingsActivity.this, "Weight updated", Toast.LENGTH_SHORT).show();
                            setWeight();
                            weightClicked = false;
                        }
                    });

                    layout.addView(submit, 1);

                    weightClicked = true;
                }
                else {
                    setWeight();
                    weightClicked = false;
                }
            }
        });

    }

    public void setEditTextMaxLength(EditText editText, int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(filterArray);
    }

    private HashMap<String, TextView> getProfile() {

        HashMap<String, TextView> profileData = new HashMap<>();



        profileData.put("name", (TextView)findViewById(R.id.profile_name));
        profileData.put("username", (TextView)findViewById(R.id.profile_username));
        profileData.put("heightFeet", (TextView)findViewById(R.id.height_feet));
        profileData.put("heightInches", (TextView)findViewById(R.id.height_inches_amount));
        profileData.put("weightStone", (TextView)findViewById(R.id.weight_stone_measure));
        profileData.put("weightPound", (TextView)findViewById(R.id.weight_pound_amount));
        profileData.put("dob", (TextView)findViewById(R.id.dob_profile));
        profileData.put("points", (TextView)findViewById(R.id.profile_points));

        return profileData;

    }

}
