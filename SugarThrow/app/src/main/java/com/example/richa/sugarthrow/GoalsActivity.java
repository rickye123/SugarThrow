package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class GoalsActivity extends MainActivity {

    private EditText sugar, calories, fat, saturates, salt, protein, carbs, allowance, reduceSugar;
    private String username;
    private Execute executeSQL;
    private ViewCreator viewCreator = new ViewCreator(this);
    private LayoutCreator layoutCreator = new LayoutCreator(this, viewCreator);
    private TableDisplay display = new TableDisplay();
    private LinearLayout layout, weeklyLayout;
    private List<List<String>> globalGoals = new ArrayList<>();
    private List<List<String>> weeklyGoals = new ArrayList<>();
    private boolean quantOpen, weeklyOpen = false;

    private void clearGlobalGoalsTable() {
        while (!globalGoals.isEmpty()) {
            int size = globalGoals.size();
            int i = 0;
            while (i < size) {
                globalGoals.remove(0);
                i++;
            }
        }
    }

    private void clearWeeklyGoalsTable() {
        while (!weeklyGoals.isEmpty()) {
            int size = weeklyGoals.size();
            int i = 0;
            while (i < size) {
                weeklyGoals.remove(0);
                i++;
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

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

        setContentView(R.layout.goals_activity);
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
        layout = (LinearLayout)findViewById(R.id.goal_entries);
        weeklyLayout = (LinearLayout)findViewById(R.id.weekly_goal_entries);

        assignEditTextValues();

        clearGlobalGoalsTable();

        createSetQuantitiesDropdown();
        createSetWeeklyDropdown();

        initialiseGoalLayout();
        initialiseWeeklyGoalsLayout();

        updateQuantities();
        pressUpdateWeeklyGoals();

        display.printTable("Global goals", globalGoals);

    }

    /**
     * Create the goal layout
     */
    private void initialiseGoalLayout() {

        List<List<String>> goals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOALS, username);

        if(!goals.get(0).get(0).equals("Empty set") && !isEmptyGoals(goals)) {

            LinearLayout diaryEntries = (LinearLayout)findViewById(R.id.daily_no_entry_wrapper);
            diaryEntries.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0));

            List<List<String>> quantity = getQuantites(goals);

            int row = 0;
            // if all fields are empty, don't display goal
            for(int i = 0; i < goals.get(0).size(); i++) {

                if(!(goals.get(0).get(i).equals("") || goals.get(0).get(i) == null)) {
                    globalGoals.add(new ArrayList<String>());
                    globalGoals.get(row).add(quantity.get(i).get(0));
                    globalGoals.get(row).add(quantity.get(i).get(1));
                    layout.addView(createGoalLayout(row, quantity.get(i).get(0) + " "
                            + quantity.get(i).get(1) + " " + quantity.get(i).get(2), "removeGoalTag"));
                    row++;
                }
            }
        }
    }

    private void updateWeeklyGoalsLayout() {

        if(weeklyLayout.getChildCount() > 0) {
            weeklyLayout.removeAllViews();
        }

        clearWeeklyGoalsTable();

        List<List<String>> sugar = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_WEEKLY_SUGAR, username);
        if(!sugar.get(0).get(0).equals("Empty set") && !isEmptyGoals(sugar)) {

            List<List<String>> quantity = getWeeklyQuantities(sugar);

            int row = 0;
            // if all fields are empty, don't display goal
            for(int i = 0; i < sugar.get(0).size(); i++) {

                if(!(sugar.get(0).get(i).equals("") || sugar.get(0).get(i) == null)) {
                    weeklyGoals.add(new ArrayList<String>());
                    weeklyGoals.get(row).add(quantity.get(i).get(0));
                    weeklyGoals.get(row).add(quantity.get(i).get(1));
                    weeklyLayout.addView(createGoalLayout(row, quantity.get(i).get(0) + " "
                            + quantity.get(i).get(1), "removeWeeklyGoalTag"));
                    row++;
                }
            }
        }
        else {
            // create no entries layout if the table "diary" is in fact empty
            LinearLayout noEntryWrapper = layoutCreator.createNoEntries("No Weekly Goals");
            weeklyLayout.addView(noEntryWrapper);
        }




    }

    private void initialiseWeeklyGoalsLayout() {

        List<List<String>> sugar = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_WEEKLY_SUGAR, username);

        if(!sugar.get(0).get(0).equals("Empty set") && !isEmptyGoals(sugar)) {

            LinearLayout diaryEntries = (LinearLayout)findViewById(R.id.weekly_no_entry_wrapper);
            diaryEntries.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0));

            List<List<String>> quantity = getWeeklyQuantities(sugar);

            int row = 0;
            // if all fields are empty, don't display goal
            for(int i = 0; i < sugar.get(0).size(); i++) {

                if(!(sugar.get(0).get(i).equals("") || sugar.get(0).get(i) == null)) {
                    weeklyGoals.add(new ArrayList<String>());
                    weeklyGoals.get(row).add(quantity.get(i).get(0));
                    weeklyGoals.get(row).add(quantity.get(i).get(1));
                    weeklyLayout.addView(createGoalLayout(row, quantity.get(i).get(0) + " "
                            + quantity.get(i).get(1), "removeWeeklyGoalTag"));
                    row++;
                }
            }
        }

    }

    /**
     * Creates the set quantities drop down which appears when the user
     * clicks the "set quantities" text
     */
    private void createSetQuantitiesDropdown() {
        LinearLayout setQuantities = (LinearLayout)findViewById(R.id.set_quantities);
        setQuantities.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView quantText = (TextView)findViewById(R.id.set_quantities_text);
                LinearLayout quantitiesLayout = (LinearLayout) findViewById(R.id.set_quantities_layout);

                // if not open, make layout visiable
                if(!quantOpen) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        quantText.setTextColor(Color.BLUE);
                        quantitiesLayout.setVisibility(View.VISIBLE);
                        quantOpen = true;
                    }
                }
                else {
                    // remove the layout if the user clicks when the layout is open
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        quantText.setTextColor(Color.BLACK);
                        quantitiesLayout.setVisibility(View.GONE);
                        quantOpen = false;
                    }
                }
                return false;
            }
        });

    }

    /**
     * Creates the dropdown listener for the Set Weekly goals layout. The
     * layout will appear when the user clicks "Set Weekly Goals"
     */
    private void createSetWeeklyDropdown() {

        LinearLayout setWeekly = (LinearLayout)findViewById(R.id.set_weekly_goals);
        setWeekly.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                TextView weeklyText = (TextView)findViewById(R.id.set_weekly_text);
                LinearLayout layout = (LinearLayout) findViewById(R.id.weekly_goals);

                if(!weeklyOpen) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        weeklyText.setTextColor(Color.BLUE);
                        layout.setVisibility(View.VISIBLE);
                        weeklyOpen = true;
                    }
                }
                else {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        weeklyText.setTextColor(Color.BLACK);
                        layout.setVisibility(View.GONE);
                        weeklyOpen = false;
                    }
                }
                return false;
            }
        });


    }

    /**
     * Check to see if a goal is empty
     * @param list
     * @return
     */
    private boolean isEmptyGoals(List<List<String>> list) {

        for (List<String> goals : list) {
            for(String goal : goals) {
                if(!goal.equals("")) {
                    return false;
                }
            }
        }
        return true;

    }

    /**
     * Update the goal layout whenever a change is made
     */
    private void updateGoalLayout() {

        if(layout.getChildCount() > 0) {
            layout.removeAllViews();
        }

        clearGlobalGoalsTable();

        List<List<String>> goals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOALS, username);
        if(!goals.get(0).get(0).equals("Empty set") && !isEmptyGoals(goals)) {
            List<List<String>> quantity = getQuantites(goals);

            int row = 0;
            // if all fields are empty, don't display goal
            for(int i = 0; i < goals.get(0).size(); i++) {

                if(!(goals.get(0).get(i).equals("") || goals.get(0).get(i) == null)) {
                    globalGoals.add(new ArrayList<String>());
                    globalGoals.get(row).add(quantity.get(i).get(0));
                    globalGoals.get(row).add(quantity.get(i).get(1));
                    layout.addView(createGoalLayout(row, quantity.get(i).get(0) + " "
                            + quantity.get(i).get(1) + " " + quantity.get(i).get(2), "removeGoalTag"));
                    row++;
                }
            }
        }
        else {
            // create no entries layout if the table "diary" is in fact empty
            LinearLayout noEntryWrapper = layoutCreator.createNoEntries("No Daily Goals");
            layout.addView(noEntryWrapper);
        }

    }

    private List<List<String>> getWeeklyQuantities(List<List<String>> sugar) {

        List<List<String>> quantities = new ArrayList<>();
        quantities.add(new ArrayList<String>());
        quantities.get(0).add("Allowance - ");
        quantities.get(0).add(sugar.get(0).get(0));
        quantities.add(new ArrayList<String>());
        quantities.get(1).add("Reduction - ");
        quantities.get(1).add(sugar.get(0).get(1));

        return quantities;

    }

    /**
     * Create the quantities list
     * @param goals - 2D array list of Strings in which the goals values
     *              are found
     * @return 2D array list containing the name of the goal and the value of the goal
     */
    private List<List<String>> getQuantites(List<List<String>> goals) {

        List<List<String>> quantities = new ArrayList<>();

        quantities.add(new ArrayList<String>());
        quantities.get(0).add("Sugar - ");
        quantities.get(0).add(goals.get(0).get(0));
        quantities.get(0).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(1).add("Calories - ");
        quantities.get(1).add(goals.get(0).get(1));
        quantities.get(1).add("kcal");
        quantities.add(new ArrayList<String>());
        quantities.get(2).add("Saturates - ");
        quantities.get(2).add(goals.get(0).get(2));
        quantities.get(2).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(3).add("Fat - ");
        quantities.get(3).add(goals.get(0).get(3));
        quantities.get(3).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(4).add("Salt - ");
        quantities.get(4).add(goals.get(0).get(4));
        quantities.get(4).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(5).add("Protein - ");
        quantities.get(5).add(goals.get(0).get(5));
        quantities.get(5).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(6).add("Carbs - ");
        quantities.get(6).add(goals.get(0).get(6));
        quantities.get(6).add("g");

        return quantities;


    }

    /**
     * Create the goal layout
     * @param row - the position in the goal list, used for removing entries
     * @param goalName - the name of the goal that appears in the list
     */
    private LinearLayout createGoalLayout(int row, String goalName, String tag) {

        LinearLayout wrapper =  layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0, 0, 10);

        TextView goal = viewCreator.createText(row, goalName, 220, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL, 0, 18, Color.BLACK, "goalTag", 0, 0 , 0, 0);

        ImageView minus = viewCreator.createImage(row, R.drawable.ic_remove_circle_black,
                40, 50, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, tag);
        minus.setColorFilter(getColor(R.color.removeRed));

        clickToScrollText(goal);
        clickToRemoveGoal(minus);

        wrapper.addView(goal);
        wrapper.addView(minus);

        return wrapper;

    }

    /**
     * If text is too long for the layout, and is truncated,
     * then the user can click it and the text will begin
     * to scroll from right to left
     * @param text
     */
    private void clickToScrollText(final TextView text) {

        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getTag().equals("goalTag")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        // make text scrollable from left to right
                        text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        text.isFocusable();
                        text.setSelected(true);
                        text.canScrollHorizontally(View.SCROLL_INDICATOR_RIGHT);
                        // repeat scroll 3 times
                        text.setMarqueeRepeatLimit(3);
                    }
                }
                return false;
            }
        });

    }

    /**
     * Touch listener that listens for a click of the minus button,
     * which results in a goal being removed from the list
     * @param image
     */
    private void clickToRemoveGoal(ImageView image) {

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getTag().equals("removeGoalTag")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        // remove image at position in table
                        remove(v.getId());
                        // update the goals list
                        updateGoalLayout();
                    }
                }
                else if(v.getTag().equals("removeWeeklyGoalTag")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        // remove from the weekly goals layout
                        removeFromWeekly(v.getId());
                        updateWeeklyGoalsLayout();
                    }
                }

                return false;
            }
        });

    }

    /**
     * Removes the goals from the Goals list
     * @param row - the position of the Goal in the Goals list
     */
    private void remove(int row) {

        // get the userId so that the row in "Goals" can be accessed
        String userId = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_USER, username);

        String updateParameter = "target";

        // remove the "-" at the end of the field so that the updateparameter
        // is, for example, "targetSugar" and not "targetSugar - "
        String[] split = globalGoals.get(row).get(0).split(" - ");
        updateParameter= updateParameter.concat(split[0]);

        // update table
        executeSQL.sqlExecuteSQL("UPDATE Goals SET " + updateParameter +
                " = " + "\'\'" + " WHERE userId = ?", userId);

    }

    private void removeFromWeekly(int row) {

        // get the userId so that the row in "Goals" can be accessed
        String userId = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_USER, username);

        String updateParameter = weeklyGoals.get(row).get(0);

        updateParameter = updateParameter.toLowerCase();

        String split[] = updateParameter.split(" - ");

        executeSQL.sqlExecuteSQL("UPDATE Sugar SET " + split[0] + " = "
                        + "\'\'" + "WHERE userId = ?", userId);


        display.printTable("Weekly Goals", weeklyGoals);

    }

    private void pressUpdateWeeklyGoals() {

        Button updateWeeklyGoals = (Button)findViewById(R.id.update_weekly_goals);

        updateWeeklyGoals.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    // validate reduction that it is a percentage
                    List<List<String>> sugar = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_SUGAR, username);
                    if(sugar.get(0).get(0).equals("Empty set")) {
                        // insert into table if no goals for that userId exists
                        ContentValues values = getWeeklyGoals();
                        executeSQL.sqlInsert("Sugar", values);
                        // update weekly goals layout
                        updateWeeklyGoalsLayout();
                    }
                    else {
                        updateWeeklyGoals();
                        // update weekly goals layout
                        updateWeeklyGoalsLayout();
                    }


                }

                return false;
            }
        });

    }

    private ContentValues getWeeklyGoals() {

        ContentValues values = new ContentValues();

        // get the user id from the username
        String userId = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_SELECT_USER, username);

        values.put("userId", userId);
        values.put("allowance", allowance.getText().toString());
        values.put("reduction", reduceSugar.getText().toString());

        return values;

    }

    /**
     * Update the daily quantities based on the contents in the
     * "Set quantities" drop down. When the "Update Quantities" button
     * is pressed, first check to see that the user doesn't already have
     * a goal entry. If they haven't insert into table, if they have, update
     * existing goals
     */
    private void updateQuantities() {
        Button updateQuantities = (Button)findViewById(R.id.update_quantities);
        updateQuantities.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                    List<List<String>> goals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOAL, username);
                    if(goals.get(0).get(0).equals("Empty set")) {
                        // insert into table if no goals for that userId exists
                        ContentValues values = getQuantityValues();
                        executeSQL.sqlInsert("Goals", values);
                        updateGoalLayout();
                    }
                    else {
                        // update table if goals for that user do exist
                        updateGoals();
                        updateGoalLayout();
                    }
                }
                return false;
            }
        });
    }

    private void updateWeeklyGoals() {

        // get the user id from the username in order to update table
        String userId = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_USER, username);

        if(!allowance.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_ALLOWANCE, allowance.getText().toString(), userId);
        }
        if(!reduceSugar.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_REDUCTION, reduceSugar.getText().toString(), userId);
        }

    }

    /**
     * Update the "Goals" table, provided that the text inside the edit text is not
     * empty
     */
    private void updateGoals() {

        // get the user id from the username in order to update table
        String userId = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_USER, username);

        if(!sugar.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_SUGAR_GOAL, sugar.getText().toString(), userId);
        }
        if(!calories.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_CALORIES_GOAL, calories.getText().toString(), userId);
        }
        if(!saturates.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_SATURATES_GOAL, saturates.getText().toString(), userId);
        }
        if(!fat.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_FAT_GOAL, fat.getText().toString(), userId);
        }
        if(!protein.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_PROTEIN_GOAL, protein.getText().toString(), userId);
        }
        if(!carbs.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_CARBS_GOAL, carbs.getText().toString(), userId);
        }
        if(!salt.getText().toString().equals("")) {
            executeSQL.sqlExecuteSQL(SqlQueries.SQL_UPDATE_SALT_GOAL, salt.getText().toString(), userId);
        }

    }

    /**
     * Get the quantities when the user clicks update quantities button
     * and store them in a ContentValues object
     * @return - ContentValues object containing the data that will be passed to the
     * "Goals" table
     */
    private ContentValues getQuantityValues() {

        ContentValues values = new ContentValues();

        // get the user id from the username
        String userId = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_SELECT_USER, username);

        // add contents to the values field
        values.put("userId", userId);
        values.put("targetSugar", sugar.getText().toString());
        values.put("targetCalories", calories.getText().toString());
        values.put("targetFat",fat.getText().toString() );
        values.put("targetSalt", salt.getText().toString());
        values.put("targetSaturates", saturates.getText().toString());
        values.put("targetCarbs", carbs.getText().toString());
        values.put("targetProtein", protein.getText().toString());

        return values;

    }

    /**
     * Assign the edit text values in the goals activity
     */
    private void assignEditTextValues() {

        sugar = (EditText)findViewById(R.id.sugar_input);
        calories = (EditText)findViewById(R.id.calories_input);
        fat = (EditText)findViewById(R.id.fat_input);
        saturates = (EditText)findViewById(R.id.saturates_input);
        salt = (EditText)findViewById(R.id.salt_input);
        protein = (EditText)findViewById(R.id.protein_input);
        carbs = (EditText)findViewById(R.id.carbs_input);
        allowance = (EditText)findViewById(R.id.weekly_allowance_sugar);
        reduceSugar = (EditText)findViewById(R.id.weekly_reduce_sugar);

    }


}
