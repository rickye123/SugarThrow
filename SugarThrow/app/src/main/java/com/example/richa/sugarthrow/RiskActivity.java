package com.example.richa.sugarthrow;

/*
This activity is resposible for the Machine Learning functionality of the app
 */

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RiskActivity extends MainActivity {

    private String username, previousActivity;
    private TableDisplay display = new TableDisplay();
    private Execute executeSQL;
    private ViewCreator viewCreator = new ViewCreator(this);
    private TimeKeeper date = new TimeKeeper();
    private LinearLayout riskLayout, mlLayout;
    private List<List<String>> user;
    private String calories;
    private FoodContentsHandler foodContentsHandler;
    private String exerciseParam;
    private TextView exerciseText, changeText, weeklyCaloriesText;

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

    // start content for activity
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

        riskLayout = (LinearLayout)findViewById(R.id.show_amount);
        user = executeSQL.sqlGetFromQuery(SqlQueries.SQL_USER, username);
        foodContentsHandler = new FoodContentsHandler(database, username);

        exerciseText = (TextView)findViewById(R.id.exercise_text);
        weeklyCaloriesText = (TextView)findViewById(R.id.weekly_calories_text);
        changeText = (TextView)findViewById(R.id.weight_change_text);
        exerciseParam = "Resting Metabolic Rate";

        // create spinner for dropdowns that appear in activity
        createSpinners();

        // dropdown when the user clicks "Daily Amount"
        clickDailyAmount();

        // dropdown appears when user clicks "Weight Change"
        clickWeight();

        // dropdown appears when user clicks "Machine Learning"
        clickMachineLearning();

    }

    /**
     * Show dropdown when user clicks Machine Learning layout
      */
    private void clickMachineLearning() {

        final LinearLayout wrapper = (LinearLayout)findViewById(R.id.ml_wrapper);
        final LinearLayout dropdown = (LinearLayout)findViewById(R.id.ml_dropdown);
        final ImageView dropDownArrow = (ImageView)findViewById(R.id.ml_dropdown_arrow);

        mlLayout = (LinearLayout)findViewById(R.id.ml_add);

        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dropdown.getVisibility() == View.GONE) {
                    wrapper.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropdown.setVisibility(View.VISIBLE);
                    dropdown.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropDownArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else {
                    dropdown.setVisibility(View.GONE);
                    dropdown.setBackgroundResource(R.drawable.border_bottom);
                    wrapper.setBackgroundResource(R.drawable.border_bottom);
                    dropDownArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                    if(mlLayout.getChildCount() > 0) {
                        mlLayout.removeAllViews();
                    }
                }
            }
        });

        final LinearLayout machineLearningSync = (LinearLayout)findViewById(R.id.ml_sync_button);
        final LinearLayout computeMachineLearning = (LinearLayout)findViewById(R.id.ml_sync_layout);

        machineLearningSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeMachineLearning.setVisibility(View.VISIBLE);
                machineLearningSync.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        computeMachineLearning.setVisibility(View.GONE);
                        machineLearningSync.setVisibility(View.VISIBLE);
                        // TODO compute machine learning
                        machineLearning();
                    }

                }, 1500);
            }
        });

    }

    /**
     * Show the dropdown when Daily Amount is clicked
     */
    private void clickDailyAmount() {

        final LinearLayout amountWrapper = (LinearLayout)findViewById(R.id.amount_wrapper);
        final LinearLayout amountDropdown = (LinearLayout)findViewById(R.id.amount_dropdown);
        final ImageView dropDownArrow = (ImageView)findViewById(R.id.amount_dropdown_arrow);

        exerciseText.setText(exerciseParam);

        // dropdown listener
        amountWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amountDropdown.getVisibility() == View.GONE) {
                    amountWrapper.setBackgroundResource(R.drawable.shape_bordertop);
                    amountDropdown.setVisibility(View.VISIBLE);
                    amountDropdown.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropDownArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else {
                    amountDropdown.setVisibility(View.GONE);
                    amountDropdown.setBackgroundResource(R.drawable.border_bottom);
                    amountWrapper.setBackgroundResource(R.drawable.border_top_and_bottom);
                    dropDownArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                    exerciseText.setVisibility(View.GONE);
                    if(riskLayout.getChildCount() > 0) {
                        riskLayout.removeAllViews();
                    }
                }
            }
        });

        // the compute button listener
        final LinearLayout amountSync = (LinearLayout)findViewById(R.id.amount_sync_button);
        final LinearLayout computing = (LinearLayout)findViewById(R.id.amount_sync_layout);
        amountSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computing.setVisibility(View.VISIBLE);
                amountSync.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        computing.setVisibility(View.GONE);
                        amountSync.setVisibility(View.VISIBLE);
                        exerciseText.setText(exerciseParam);
                        exerciseText.setVisibility(View.VISIBLE);
                        calculateAmountOfEachFood(); // calculate how much of each food someone should have
                    }
                }, 1500);

            }
        });

    }

    /**
     * Show the dropdown when the user clicks on the Weight Change layout.
     */
    private void clickWeight() {

        final LinearLayout weightWrapper = (LinearLayout)findViewById(R.id.weight_gain_wrapper);
        final LinearLayout weightDropdown = (LinearLayout)findViewById(R.id.weight_gain_dropdown);
        final ImageView dropDownArrow = (ImageView)findViewById(R.id.weight_gain_dropdown_arrow);
        final LinearLayout dropdown= (LinearLayout)findViewById(R.id.weight_dropdown);

        changeText.setText(exerciseParam);

        // dropdown listener
        weightWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(weightDropdown.getVisibility() == View.GONE) {
                    weightWrapper.setBackgroundResource(R.drawable.shape_borderbottom);
                    weightDropdown.setVisibility(View.VISIBLE);
                    weightDropdown.setBackgroundResource(R.drawable.shape_borderbottom);
                    dropDownArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else {
                    weightDropdown.setVisibility(View.GONE);
                    dropdown.setVisibility(View.GONE);
                    weightDropdown.setBackgroundResource(R.drawable.border_bottom);
                    weightWrapper.setBackgroundResource(R.drawable.border_bottom);
                    changeText.setVisibility(View.GONE);
                    weeklyCaloriesText.setVisibility(View.GONE);
                    dropDownArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                }
            }
        });

        final LinearLayout weightSync = (LinearLayout)findViewById(R.id.weight_gain_sync_button);
        final LinearLayout computingWeight = (LinearLayout)findViewById(R.id.weight_gain_sync_layout);

        // sync listener
        weightSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computingWeight.setVisibility(View.VISIBLE);
                weightSync.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        computingWeight.setVisibility(View.GONE);
                        weightSync.setVisibility(View.VISIBLE);
                        dropdown.setVisibility(View.VISIBLE);
                        changeText.setVisibility(View.VISIBLE);
                        changeText.setText(exerciseParam);
                        // compute the change in weight
                        compute();
                    }
                }, 1500);

            }
        });

    }

    /**
     * Compute the change in weight given today's diet and the diet over the last
     * 7 days
     */
    private void compute() {

        TextView changeDailyWeight = (TextView)findViewById(R.id.daily_change_text);
        TextView newWeight = (TextView)findViewById(R.id.new_weight_text);

        // calculate the number of calories needed
        mifflinStJeor();

        List<List<String>> foods =
                executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ON_DAY,
                        date.convertDateFormat(date.getPrevDate(date.getCurrentDate())), username);

        // if diary empty, set calories to "0", otherwise find the total number of calories
        String cal = (foods.get(0).get(1) == null) ? "0" : foods.get(0).get(1);

        // the change in weight
        double change = (1 / (3500/ (Double.parseDouble(cal) - Double.parseDouble(calories))));
        String changeWeight = String.format(Locale.ENGLISH, "%.4f", change);

        changeDailyWeight.setText(changeWeight);
        double weight = getPounds(user.get(0).get(6)) + change;

        String newDailyWeight = String.format(Locale.ENGLISH, "%.2f", weight);

        newWeight.setText(newDailyWeight);

        // compute the weekly change in weight given the last 7 days and today's diet
        computeWeeklyChange(change);

    }

    /**
     * Compute the weekly change in weight based on the current diet and
     * the diet of the last 7 days
     * @param change - the change in calories, which is used to determine whether
     *               the user is gaining or losing weight
     */
    private void computeWeeklyChange(double change) {

        TextView weeklyMessage;

        // remove views if some already exist
        LinearLayout weeklyWeightChange = (LinearLayout)findViewById(R.id.weight_on_diet);
        if(weeklyWeightChange.getChildCount() > 0) {
            weeklyWeightChange.removeAllViews();
        }

        String weightChange = (change > 0) ? "gain" : "lose";

        // get the last 7 days of calories and the total number of calories the user's allowed
        // over 7 days
        double weeklyCalories = (7 * Double.parseDouble(calories));
        double totalCalories = getLastWeek();

        if(totalCalories > 0) {
            double result = Math.abs(totalCalories - weeklyCalories);
            double weightGain = 3500 / result;
            String weekChange = String.format(Locale.ENGLISH, "%.2f", weightGain * 7);
            String variance = (totalCalories > weeklyCalories) ? "gain" : "lose";
            weeklyMessage = printMessage("Based on this week\'s diet, it will take you " +
                    weekChange + " days to " + variance + " a pound");
            weeklyCaloriesText.setVisibility(View.VISIBLE);
            weeklyCaloriesText.setText(getString(R.string.weekly_calories,
                    String.format(Locale.ENGLISH, "%.2f", totalCalories)));
        }
        else {
            weeklyMessage = printMessage("You do not have enough data for this feature. " +
                    "Be sure to log 7 days of food in a row");
        }
        double dailyWeekChange = Math.abs(1 / change);
        String dailyWeek = String.format(Locale.ENGLISH, "%.2f", dailyWeekChange);
        TextView dailyMessage = printMessage("Based on yesterday\'s diet, it will take you " +
                dailyWeek + " days to " + weightChange + " a pound");

        weeklyWeightChange.addView(weeklyMessage);
        weeklyWeightChange.addView(dailyMessage);

    }

    /**
     * Set the message for the Weight Gain section of the Activity
     * @param text - the String that occupies the TextView
     * @return - TextView that exists in the Weight Gain Section
     */
    private TextView printMessage(String text) {

        TextView message = new TextView(RiskActivity.this);
        message.setText(text);
        message.setTextColor(Color.BLACK);
        message.setTextSize(18);
        message.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return message;

    }

    /**
     * Get the total number of calories from the last 7 days. If the
     * user does not have 7 days worth of food to record, then the method
     * returns 0.
     * @return the total number of calories the user has had over the past 7 days. If
     * the user does not have a full 7 days worth of food recorded, return 0
     */
    private double getLastWeek() {

        String[] days = foodContentsHandler.findPreviousSevenDays();
        for(String day: days) {
            System.out.println("DAY " + day);
        }
        double calorieSum = 0;

        for(int i = 0; i < 7; i++) {
            List<List<String>> contents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ON_DAY,
                    date.convertDateFormat(days[i]), username);
            System.out.println(i + " " + contents.get(0).get(0));
            // if contents for a day are empty, then return 0
            if(contents.get(0).get(0) == null) {
                return 0;
            }
            calorieSum += Double.parseDouble(contents.get(0).get(1));
        }

        return calorieSum;

    }

    /**
     * Create spinners from the corresponding ids in the layout. Set the spinner listners
     */
    private void createSpinners() {

        // create adapters
        ArrayAdapter<CharSequence> spinnerAdapter = createSpinnerAdapter(R.array.exercise_array);

        // create spinners according to id in XML file
        Spinner exerciseSpinner = (Spinner) findViewById(R.id.exercise_spinner);
        Spinner changeSpinner = (Spinner)findViewById(R.id.weight_change_spinner);

        // set spinners
        setSpinner(exerciseSpinner, spinnerAdapter, "Exercise");
        setSpinner(changeSpinner, spinnerAdapter, "Exercise");

    }

    /**
     * Sets which spinner field is selected (first field), and what happens when a spiner
     * is selected
     * @param spinner - the spinner
     * @param adapter - the adapter is used to set the spinner's fields
     * @param prompt - the prompt that appears when nothing is selected
     */
    private void setSpinner(Spinner spinner, SpinnerAdapter adapter, String prompt) {

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        selectSpinner(spinner, prompt);

    }

    /**
     *
     * @param id - the id referenced in the layout
     * @return the adapter containing the dropdown for that spinner
     */
    private ArrayAdapter<CharSequence> createSpinnerAdapter(int id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    /**
     * Set the text for when the spinner is selected
     * @param spinner - the spinner, whose id is accessed
     * @param prompt - the prompt for the spinner if nothing selected
     */
    public void selectSpinner(final Spinner spinner, final String prompt) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selected = parent.getItemAtPosition(position);
                switch(spinner.getId()) {
                    case R.id.exercise_spinner:
                        exerciseParam = selected.toString();
                        break;
                    case R.id.weight_change_spinner:
                        exerciseParam = selected.toString();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setPrompt(prompt);
            }
        });
    }

    /**
     * Determine the age, height, weight, and gender of user and then calculate the St.Mifflin-Jeor
     * equation based on these variables (sets calories variable)
     */
    private void mifflinStJeor() {

        String gender = user.get(0).get(3);
        Map<String, String> dateVariables = date.getReverseDateVariables(user.get(0).get(4));
        int age = date.getAge(dateVariables);

        double height = getHeight(user.get(0).get(5));
        double weight = getWeight(user.get(0).get(6));

        double kcal = calculateCalories(weight, height, age, gender.charAt(0));
        calories = String.format(Locale.ENGLISH, "%.2f", kcal);

    }

    /**
     * Calculate the amount of each food group a person can have and create the Views
     * which appear in the app
     */
    private void calculateAmountOfEachFood() {

        if(riskLayout.getChildCount() > 0) {
            riskLayout.removeAllViews();
        }
        // calculate the number of calories needed
        mifflinStJeor();

        String protein = String.format(Locale.ENGLISH, "%.2f", getProtein(Double.parseDouble(calories)));
        String carbs = String.format(Locale.ENGLISH, "%.2f", getCarbs(Double.parseDouble(calories)));
        String fat = String.format(Locale.ENGLISH, "%.2f", getFat(Double.parseDouble(calories)));
        String sugar = String.format(Locale.ENGLISH, "%.2f", getSugar(Double.parseDouble(calories)));
        String saturates = String.format(Locale.ENGLISH, "%.2f", getSaturates(Double.parseDouble(calories)));
        double salt = 6;

        TextView calorieView = viewCreator.createText(0, "Calories " + calories + " kcal",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "caloriesTag");

        TextView proteinView = viewCreator.createText(0, "Protein " + protein + " g",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "proteinTag");

        TextView fatView = viewCreator.createText(0, "Fat " + fat + " g",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "fatTag");

        TextView carbsView = viewCreator.createText(0, "Carbs " + carbs + " g",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "fatTag");

        TextView sugarView = viewCreator.createText(0, "Sugar " + sugar + " g",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "sugarTag");

        TextView saturatesView = viewCreator.createText(0, "Saturates " + saturates + " g",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "saturatesTag");

        TextView saltView = viewCreator.createText(0, "Salt " + Double.toString(salt) + " g",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 24, Color.BLACK, "saltTag", 0, 0, 0, 10);

        riskLayout.addView(calorieView);
        riskLayout.addView(proteinView);
        riskLayout.addView(fatView);
        riskLayout.addView(carbsView);
        riskLayout.addView(sugarView);
        riskLayout.addView(saturatesView);
        riskLayout.addView(saltView);

    }

    /**
     * Get the number of grams of saturated fat the user can have each day
     * @param calories - the number of calories a person can have a given day
     * @return the number of grams of saturates a user can have each day
     */
    private double getSaturates(double calories) {

        // saturated fat is 9% of calories
        double saturates = calories * 0.09;

        //1g of fat is 9 calories
        saturates = saturates / 9;

        return saturates;

    }

    /**
     * Get the number of grams of fat the user can have based on calories they
     * are allowed each day
     * @param calories - the number of calories a person can have a given day
     * @return the number of grams of fat user can have each day
     */
    private double getFat(double calories) {

        // fat is 30% of calories
        double fat = calories * 0.3;

        //1g of fat is 9 calories
        fat = fat / 9;

        return fat;

    }

    /**
     * Get the number of grams of sugar a user can have based on their calorie intake
     * @param calories - the number of calories a person can have a given day
     * @return the number of grams of sugar user can have each day
     */
    private double getSugar(double calories) {

        // sugar is 15% of calories
        double sugar = calories * 0.15;

        // 1g of sugar is 4 calories
        sugar = sugar / 4;

        return sugar;
    }

    /**
     * Get the number of grams of carbohydrates the user can have each day
     * @param calories - the number of calories a person can have a given day
     * @return the number of grams of carbohydrates a user can have each day
     */
    private double getCarbs(double calories) {

        // carbs is 50% of calories
        double carbs = calories * 0.5;

        // 1g of carbs is 4 calories
        carbs = carbs / 4;

        return carbs;

    }

    /**
     * Get the number of grams of protein a person can have given the number
     * of calories they are allowed
     * @param calories - the number of calories a person can have on a given day
     * @return the number of grams of protein a person can have given the number of calories
     * they are allowed per day
     */
    private double getProtein(double calories) {

        // protein is 20% of calories
        double protein = calories * 0.2;

        // 1g of protein is 4 calories
        protein = protein / 4;

        return protein;

    }

    /**
     * Get the weight in kg from String in form 12.2 (meaning 12 stone 2lb)
     * @param weight - the weight of the user as a string (in the form of 12.2 to
     *               denote 12 stone 2lb).
     * @return - the weight of the user in kg
     */
    private double getWeight(String weight) {

        // split the String where the "." character is
        String[] weightSplit = weight.split("\\.");
        double decimal;

        if(weightSplit.length == 1) {
            decimal = 0;
        }
        else {
            // ensures that .10 is turned into 10 rather than 1
            if(weightSplit[1].equals("01")) {
                decimal = 1;
            }
            else if(weightSplit[1].equals("1")) {
                decimal = 10;
            }
            else {
                decimal = Double.parseDouble(weightSplit[1]);
            }
        }

        double percent = decimal / 14;

        String[] percentSplit = Double.toString(percent).split("\\.");

        String stone = weightSplit[0].concat("." + percentSplit[1]);
        double kgCalculator = 6.35029; // stone to kg

        // return the stone converted to kg
        return Double.parseDouble(stone) * kgCalculator;

    }

    /**
     * Get the weight from a string in pound
     * @param weight - String denoting weight in form 12.2 meaning 12 stone 2lb
     * @return the weight in pounds
     */
    private double getPounds(String weight) {

        // split where the "." character is
        String[] weightSplit = weight.split("\\.");
        double decimal;

        if(weightSplit.length == 1) {
            decimal = 0;
        }
        else {
            // ensures that weight is 10 rather than 1, when .10
            if(weightSplit[1].equals("01")) {
                decimal = 1;
            }
            else if(weightSplit[1].equals("1")) {
                decimal = 10;
            }
            else {
                decimal = Double.parseDouble(weightSplit[1]);
            }
        }

        double percent = decimal / 14;

        String[] percentSplit = Double.toString(percent).split("\\.");

        String stone = weightSplit[0].concat("." + percentSplit[1]);
        double poundCalculator = 14; // stone to pound

        return Double.parseDouble(stone) * poundCalculator;

    }

    /**
     * Get the height of String in cm
     * @param height - String denoting height in the form 6.2 meaning 6 ft 2 inches
     * @return the height in cm from a String
     */
    private double getHeight(String height) {

        // split String at the "." character
        String[] heightSplit = height.split("\\.");
        double decimal;

        if(heightSplit.length == 1) {
            decimal = 0;
        }
        else {
            // ensure that .10 returns 10 rather than 1
            if(heightSplit[1].equals("10")) {
                decimal = 10;
            }
            else {
                decimal = Double.parseDouble(heightSplit[1]);
            }
        }

        double percent = decimal / 12;

        String[] percentSplit = Double.toString(percent).split("\\.");

        String feet = heightSplit[0].concat("." + percentSplit[1]);
        double cmCalculator = 30.48; // Feet to cm calculator

        return Double.parseDouble(feet) * cmCalculator;

    }

    /**
     * Calculate the St.Mifflin-Jeor Equation, which returns the
     * number of calories someone needs given their weight, height, age, and gender
     * @param weight - weight in kg
     * @param height - height in cm
     * @param age - in years
     * @param sex - either M or F
     * @return the number of calories a person needs given their weight, height, age and gender
     */
    private double calculateCalories(double weight, double height, int age, char sex) {

        int sexVal;
        if(sex == 'M') {
            sexVal = 1;
        }
        else {
            sexVal = 0;
        }

        if(exerciseParam == null) {
            exerciseParam = "Resting Metabolic Rate";
        }
        double exercise = getExercise(exerciseParam);

        return ((10 * weight) + (6.25 * height) - (5 * age) + (166 * sexVal) - 161) * exercise;

    }

    /**
     * Calculates the value to appear in equation based on exercise per week
     * @param exercise - the amount of exercise the user does a week
     * @return value used in the St.Mifflin-Jeor equation
     */
    private double getExercise(String exercise) {

        if(exercise.equals("Resting Metabolic Rate")) {
            return 1.0;
        }
        if(exercise.equals("Little or No Exercise")) {
            return 1.2;
        }
        if(exercise.equals("Lightly Active - Exercise 1 to 3 times a week")) {
            return 1.4;
        }
        if(exercise.equals("Moderately Active - Exercise 3 to 5 days a week")) {
            return 1.6;
        }
        if(exercise.equals("Very Active - Exercise 6 to 7 times a week")) {
            return 1.8;
        }
        if(exercise.equals("Extra Active - Very hard exercise or physical job")) {
            return 2.0;
        }
        else {
            return 1.0;
        }

    }

    /**
     * TODO
     */
    private void machineLearning() {

        if(mlLayout.getChildCount() > 0) {
            mlLayout.removeAllViews();
        }

        String today = date.getCurrentDate();
        String yesterday = date.convertDateFormat(date.getPrevDate(today));

        List<List<String>> contents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ON_DAY,
                yesterday, username);

        display.printTable("Contents", contents);

        Map<String, String> group = foodContentsHandler.getContents(contents);
        Map<String, String> required = new HashMap<>();

        // calculate the number of calories needed
        mifflinStJeor();

        required.put("Protein", String.format(Locale.ENGLISH, "%.2f", getProtein(Double.parseDouble(calories))));
        required.put("Carbs", String.format(Locale.ENGLISH, "%.2f", getCarbs(Double.parseDouble(calories))));
        required.put("Fat", String.format(Locale.ENGLISH, "%.2f", getFat(Double.parseDouble(calories))));
        required.put("Sugar", String.format(Locale.ENGLISH, "%.2f", getSugar(Double.parseDouble(calories))));
        required.put("Saturates", String.format(Locale.ENGLISH, "%.2f", getSaturates(Double.parseDouble(calories))));
        required.put("Salt", "6");

        Map<String, String> percentageContribution = findPercentageContribution(group, required);

        String changeInWeight = String.format(Locale.ENGLISH, "%.5f", calculateRegression(percentageContribution));

        addMachineLearningWeightChange(changeInWeight);


    }

    /**
     *
     * @param changeInWeight
     */
    private void addMachineLearningWeightChange(String changeInWeight) {

        TextView change = viewCreator.createText(0, "Change in Weight "  + changeInWeight + " lbs",  LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 20, Color.BLACK, "changeTag", 0, 10, 0, 10);
        change.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        double changeWeight = getPounds(user.get(0).get(6)) + Double.parseDouble(changeInWeight);

        TextView weightChange = viewCreator.createText(0, "Weight "  + String.format(Locale.ENGLISH, "%.2f", changeWeight)
                        + " lbs",
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 20, Color.BLACK, "weightChangeTag", 0, 10, 0, 10);
        weightChange.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        weightChange.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView newWeight = new TextView(RiskActivity.this);

        if(Double.parseDouble(changeInWeight) > 1) {
            String text;
            if(Double.parseDouble(changeInWeight) > 0) {
                text = "Based on yesterday\'s diet, you have gained a pound";
            }
            else {
                text = "Based on yesterday\'s diet, you have lost a pound";
            }

            newWeight.setText(text);
            newWeight.setTextColor(Color.BLACK);
            newWeight.setTextSize(18);
            newWeight.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            newWeight.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        else {
            String text;
            if(Double.parseDouble(changeInWeight) > 0) {
                text = "Based on yesterday\'s diet, it will take you " +
                        String.format(Locale.ENGLISH, "%.2f", Math.abs(1 / Double.parseDouble(changeInWeight)))
                        + " to gain a pound";
            }
            else {
                text = "Based on yesterday\'s diet, it will take you " +
                        String.format(Locale.ENGLISH, "%.2f", Math.abs(1 / Double.parseDouble(changeInWeight)))
                        + " to lose a pound";
            }

            newWeight.setText(text);
            newWeight.setTextColor(Color.BLACK);
            newWeight.setTextSize(18);
            newWeight.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            newWeight.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        mlLayout.addView(change);
        mlLayout.addView(weightChange);
        mlLayout.addView(newWeight);
        machineLearningWeeklyWeightChange();

    }

    private void machineLearningWeeklyWeightChange() {

        double totalCalories = getLastWeek();
        String[] days = foodContentsHandler.findPreviousSevenDays();
        TextView weeklyMessage;

        if(totalCalories > 0) {

            double totalWeightChange = 0;
            List<String> amount = new ArrayList<>();
            for(String day : days) {
                amount.add(getAmountsFromDate(date.convertDateFormat(day)));
                totalWeightChange += Double.parseDouble(getAmountsFromDate(date.convertDateFormat(day)));
            }
            double changeWeight = Math.abs(1 / totalWeightChange);
            if(totalWeightChange > 0) {
                weeklyMessage = printMessage("Based on this week\'s diet, it will take you " +
                String.format(Locale.ENGLISH, "%.2f", changeWeight * 7)  + " days to gain a pound");
            }
            else {
                weeklyMessage = printMessage("Based on this week\'s diet, it will take you " +
                        String.format(Locale.ENGLISH, "%.2f", changeWeight * 7)  + " days to lose a pound");
            }

        }
        else {
            weeklyMessage = printMessage("You do not have enough data for this feature. " +
                    "Be sure to log 7 days of food in a row");
        }

        mlLayout.addView(weeklyMessage);

    }

    private String getAmountsFromDate(String theDate) {

        List<List<String>> contents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ON_DAY,
                theDate, username);

        // calculate the number of calories needed
        mifflinStJeor();

        Map<String, String> required = new HashMap<>();
        required.put("Protein", String.format(Locale.ENGLISH, "%.2f", getProtein(Double.parseDouble(calories))));
        required.put("Carbs", String.format(Locale.ENGLISH, "%.2f", getCarbs(Double.parseDouble(calories))));
        required.put("Fat", String.format(Locale.ENGLISH, "%.2f", getFat(Double.parseDouble(calories))));
        required.put("Sugar", String.format(Locale.ENGLISH, "%.2f", getSugar(Double.parseDouble(calories))));
        required.put("Saturates", String.format(Locale.ENGLISH, "%.2f", getSaturates(Double.parseDouble(calories))));
        required.put("Salt", "6");

        Map<String, String> group = foodContentsHandler.getContents(contents);

        Map<String, String> percentageContribution = findPercentageContribution(group, required);

        return String.format(Locale.ENGLISH, "%.5f", calculateRegression(percentageContribution));

    }

    private double calculateRegression(Map<String, String> percentageContribution) {

        double regression = -1.031 + (0.0006514*Double.parseDouble(percentageContribution.get("Sugar"))) +
                (0.004631*Double.parseDouble(percentageContribution.get("Calories"))) +
                (0.001321*Double.parseDouble(percentageContribution.get("Fat"))) +
                (0.0003925*Double.parseDouble(percentageContribution.get("Saturates"))) +
                (0.002169*Double.parseDouble(percentageContribution.get("Carbs"))) +
                (0.00005999*Double.parseDouble(percentageContribution.get("Salt"))) +
                (0.0009994*Double.parseDouble(percentageContribution.get("Protein")));

        return regression;

    }

    private Map<String, String> findPercentageContribution(Map<String, String> group,
                                                       Map<String, String> required) {

        Map<String, String> percentageContribution = new HashMap<>();

        System.out.println(group.get("Calories"));
        System.out.println(calories);

        percentageContribution.put("Calories", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Calories")) / Double.parseDouble(calories)) * 100));
        percentageContribution.put("Sugar", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Sugar")) / Double.parseDouble(required.get("Sugar"))) * 100));
        percentageContribution.put("Fat", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Fat")) / Double.parseDouble(required.get("Fat"))) * 100));
        percentageContribution.put("Carbs", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Carbs")) / Double.parseDouble(required.get("Carbs"))) * 100));
        percentageContribution.put("Protein", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Protein")) / Double.parseDouble(required.get("Protein"))) * 100));
        percentageContribution.put("Saturates", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Saturates")) / Double.parseDouble(required.get("Saturates"))) * 100));
        percentageContribution.put("Salt", String.format(Locale.ENGLISH, "%.5f",
                (Double.parseDouble(group.get("Salt")) / Double.parseDouble(required.get("Salt"))) * 100));

        return percentageContribution;

    }

}
