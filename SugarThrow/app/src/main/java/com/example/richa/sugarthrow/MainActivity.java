package com.example.richa.sugarthrow;

/*
This is the main activity class, which is called when the app is launched
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener {

    private Execute executeSQL;
    private FoodContentsHandler foodContentsHandler;
    private String username;
    private TimeKeeper date = new TimeKeeper();
    private PointsHandler pointsHandler;
    private boolean acknowledgeOnTrack, acknowledgeAchieved = false;

    // invoked when activity starts
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

        // set the layout to the home_activity.xml file
        setContentView(R.layout.home_activity);
        setNavigationUsername(username);

        startContent();
    }

    /**
     * Set the username in the navigation drawer
     * @param username - the username of the user
     */
    public void setNavigationUsername(String username) {

        // get the navigation view
        NavigationView navView= (NavigationView)findViewById(R.id.nav_view);

        // get the navigation view's first child
        View view = navView.getHeaderView(0);

        // set the text for the username
        TextView text = (TextView)view.findViewById(R.id.navigation_username);
        text.setText(username);
    }

    /**
     * Method used to launch the feedback popups
     * @param className - the class the popup is being called from
     * @param message - the message that appears in the popup
     * @param positive - boolean to determine whether the message is positive or negative
     */
    public void launchFeedbackActivity(Context className, String message, boolean positive) {

        Intent intent = new Intent(className, FeedbackActivityPopup.class);
        intent.putExtra("message", message);
        intent.putExtra("positive", positive);
        startActivity(intent);

    }

    /**
     * Start the content for the MainActivity
     */
    private void startContent() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        createDrawer(toolbar);
        createNavigationView(R.id.nav_home);

        // creates the image carousel on the home page
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPageAndroid);
        ImageSlider adapterView = new ImageSlider(this);
        viewPager.setAdapter(adapterView);

        // set the layout wrapping the "Example Healthy Meal" to be transparent
        LinearLayout healthyMealLayout = (LinearLayout)findViewById(R.id.example_meal);
        healthyMealLayout.getBackground().setAlpha(100);

        // get instance of database
        Connector database = Connector.getInstance(this);
        database.attemptCreate();
        database.openConnection();
        executeSQL = new Execute(database);
        foodContentsHandler = new FoodContentsHandler(database, username);
        pointsHandler = new PointsHandler(database, username);

        // handle the links to the diary, search, and game on the homapage
        handleLinks();

        // handle the tip and fact drop downs
        handleDropDowns();

        // the HUD contains the percentage intake of sugar the user has left and their total points
        populateHUD();

        // links to external websites on the home page
        hyperLinks();

        // check to see if daily goals on track
        if(!acknowledgeOnTrack) {
            dailyGoalsOnTrack();
        }
        // check to see if daily goals achieved
        if(!acknowledgeAchieved) {
            dailyGoalsAchieved();
        }

    }

    /**
     * Return user's username
     * @return - username as a string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Handle the links on the home page
     */
    private void handleLinks() {

        ImageView diaryImage = (ImageView)findViewById(R.id.diary_image);
        TextView diaryText = (TextView)findViewById(R.id.diary);
        ImageView diaryChevron = (ImageView)findViewById(R.id.diary_chevron);

        ImageView playSugar = (ImageView)findViewById(R.id.play_sugar_image);
        TextView playSugarText = (TextView)findViewById(R.id.play_sugar);
        ImageView playSugarChevron = (ImageView)findViewById(R.id.play_sugar_chevron);

        ImageView searchFoods = (ImageView)findViewById(R.id.search_database_image);
        TextView searchFoodsText = (TextView)findViewById(R.id.search_database);
        ImageView searchFoodsChevron = (ImageView)findViewById(R.id.search_database_chevron);

        // click the links which will take you to the diary activity
        clickLinks(diaryImage);
        clickLinks(diaryText);
        clickLinks(diaryChevron);

        // click the links which will take you to the Food database activity
        clickLinks(searchFoods);
        clickLinks(searchFoodsText);
        clickLinks(searchFoodsChevron);

        // click the links which will take you to the Sugar throw (UnityGame) activity
        clickLinks(playSugar);
        clickLinks(playSugarText);
        clickLinks(playSugarChevron);
    }

    /**
     * Handle the hyperlinks linking to external websites
     */
    private void hyperLinks() {

        // Link to Chatelaine.com, where there is a detailed example of a healthy diet
        LinearLayout visitChatelaine = (LinearLayout)findViewById(R.id.visit_chatelaine);
        visitChatelaine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent();
                redirect.setAction(Intent.ACTION_VIEW);
                redirect.addCategory(Intent.CATEGORY_BROWSABLE);
                redirect.setData(Uri.parse("http://www.chatelaine.com/health/healthy-recipes-health/low-sugar-meal-plan/"));
                startActivity(redirect);
            }
        });

        // Link to NHS Choices, where tips for reducing sugar are given
        LinearLayout visitNHS = (LinearLayout)findViewById(R.id.visit_nhs);
        visitNHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent();
                redirect.setAction(Intent.ACTION_VIEW);
                redirect.addCategory(Intent.CATEGORY_BROWSABLE);
                redirect.setData(Uri.parse("http://www.nhs.uk/Livewell/Goodfood/Pages/how-to-cut-down-on-sugar-in-your-diet.aspx"));
                startActivity(redirect);
            }
        });


    }

    /**
     * Used to handle the drop downs that exist on the homepage
     */
    private void handleDropDowns() {

        // drop down for the fact on the homepage
        LinearLayout fact = (LinearLayout)findViewById(R.id.fact_layout);
        final LinearLayout factDropdown = (LinearLayout)findViewById(R.id.fact_dropdown);
        final ImageView arrowDownFact = (ImageView)findViewById(R.id.arrow_fact);
        fact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(factDropdown.getVisibility() == View.GONE) {
                    factDropdown.setVisibility(View.VISIBLE);
                    arrowDownFact.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else {
                    factDropdown.setVisibility(View.GONE);
                    arrowDownFact.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                }
            }
        });

        // drop down for the Tip on the homepage
        LinearLayout tip = (LinearLayout)findViewById(R.id.tip_layout);
        final LinearLayout tipDropdown = (LinearLayout)findViewById(R.id.tip_dropdown);
        final ImageView arrowDownTip = (ImageView)findViewById(R.id.arrow_tip);
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipDropdown.getVisibility() == View.GONE) {
                    tipDropdown.setVisibility(View.VISIBLE);
                    arrowDownTip.setImageResource(R.drawable.ic_keyboard_arrow_up_black);
                }
                else {
                    tipDropdown.setVisibility(View.GONE);
                    arrowDownTip.setImageResource(R.drawable.ic_keyboard_arrow_down_black);
                }
            }
        });


    }

    /**
     * Create the HUD (Heads Up Display) which contains the amount of sugar
     * the user has left (daily amount) and the number of points they have
     */
    private void populateHUD() {

        TextView globalPoints = (TextView)findViewById(R.id.total_points);
        TextView globalSugar = (TextView)findViewById(R.id.daily_sugar_left);

        // update the points in the HUD with the user's points
        String points = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_POINTS, username);
        globalPoints.setText(getString(R.string.hud_points, points));

        clickHUD(globalPoints);
        clickHUD(globalSugar);

        // find the total amount of each food group the user has had
        List<List<String>> sumOfFoods =
                executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_CURRENT_DIARY, username);

        // return the amount of sugar the user has left
        Map<String, BigDecimal> sugarPercentage =
                foodContentsHandler.findFoodPercentages(sumOfFoods.get(0).get(0), 0);
        globalSugar.setText(getString(R.string.hud_percent,
                sugarPercentage.get("amountLeft").toString(), "%"));

        // change the text color to red if the user has had over 100% of their daily sugar intake
        if(sugarPercentage.get("amountLeft").compareTo(BigDecimal.ZERO) < 0) {
            globalSugar.setTextColor(getColor(R.color.removeRed));
        }
    }

    private void clickHUD(final TextView item) {

        item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String text = item.getText().toString();
                if(v.getId() == R.id.total_points) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Toast.makeText(MainActivity.this,
                                "Total points: " + text, Toast.LENGTH_SHORT).show();
                    }
                }
                else if (v.getId() == R.id.daily_sugar_left) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Toast.makeText(MainActivity.this,
                                "Daily Percentage of Sugar Left : " + text, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

    }

    /**
     * Create the navigation drawer (side menu) and change the menu
     * item selected
     * @param menuId - the id referring to the drawabale image in the menu
     */
    public void createNavigationView(int menuId) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        // set which menu item is selected
        navigationView.getMenu().findItem(menuId).setChecked(true);
    }

    /**
     * Create the toolbar containing the burger nav icon (which can be toggled)
     * @param toolbar - the Toolbar object within the corresponding layout
     */
    public void createDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Event listener that looks for ImageView clicks in the Activity
     * @param view - the View that will be clicked
     */
    public void clickLinks(final View view) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.search_database_image:
                    case R.id.search_database:
                    case R.id.search_database_chevron:
                        launchActivity(FoodDatabaseActivity.class);
                        break;
                    case R.id.diary_chevron:
                    case R.id.diary_image:
                    case R.id.diary:
                        launchActivity(DiaryActivity.class);
                        break;
                    case R.id.play_sugar_image:
                    case R.id.play_sugar_chevron:
                    case R.id.play_sugar:
                        playGame(MainActivity.this);
/*                        launchActivity(UnityGame.class);*/
                        break;
                    default:
                        // If we got here, the user's action was not recognized.
                        // Invoke the superclass to handle it.
                        break;
                }
            }
        });
    }

    public void playGame(Context context) {

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
            launchFeedbackActivity(context, message, false);
        }
        else {
            launchActivity(UnityGame.class);
        }

    }

    /**
     * Feedback popup that appears after 8 and before 10 that says a user's
     * goals are on track
     */
    private void dailyGoalsOnTrack() {

        if(checkTime("20:00:00", "21:59:59")) {
            // if any of your goals are under, looks like you're on track to reach that goal
            String hasGoals = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_SELECT_GOAL,
                    username);
            if(!hasGoals.equals("Empty set")) {
                List<List<String>> goals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOALS,
                        username);
                List<List<String>> quantities = foodContentsHandler.createQuantities(goals);
                List<Map<String, BigDecimal>> total = foodContentsHandler.findDailyTotal(username);

                for(int i = 0; i < quantities.size(); i++) {
                    if(!(goals.get(0).get(i).equals("") || goals.get(0).get(i) == null)) {
                        if (total.get(i).get("intake").floatValue() < 100) {
                            String message = "Looks like you're on track to reach a goal!\n";
                            message = message.concat(quantities.get(i).get(0) + " " + quantities.get(i).get(1) +
                                    " " + quantities.get(i).get(2));
                            launchFeedbackActivity(MainActivity.this, message, true);
                            acknowledgeOnTrack = true;
                        }
                    }
                }
            }
        }

    }

    /**
     * Feedback popup that appears after 10 and before midnight saying that the user has achieved
     * their goal (if they have achieved them)
     */
    private void dailyGoalsAchieved() {

        if(checkTime("22:00:00", "23:59:59")) {
            String hasGoals = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_SELECT_GOAL,
                    username);
            if(!hasGoals.equals("Empty set")) {
                List<List<String>> goals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOALS,
                        username);
                List<List<String>> quantities = foodContentsHandler.createQuantities(goals);
                List<Map<String, BigDecimal>> total = foodContentsHandler.findDailyTotal(username);

                for(int i = 0; i < quantities.size(); i++) {
                    if(!(goals.get(0).get(i).equals("") || goals.get(0).get(i) == null)) {
                        if (total.get(i).get("intake").floatValue() < 100) {
                            String message = "You reached your daily goal!\n";
                            message = message.concat(quantities.get(i).get(0) + " " + quantities.get(i).get(1) +
                                    " " + quantities.get(i).get(2));
                            message = message.concat("\nYou've earned an extra 2 points");
                            pointsHandler.updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_2);
                            launchFeedbackActivity(MainActivity.this, message, true);
                            acknowledgeAchieved = true;
                        }
                    }
                }
            }
        }

    }

    /**
     * Check the time between two intervals
     * @param timeAfter - the time after the current time
     * @param timeBefore - the time before the current time
     * @return - true if the current time is between the intervals
     */
    private boolean checkTime(String timeBefore, String timeAfter) {

        try {
            Date time1 = new java.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(timeBefore);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar1.add(Calendar.DATE, 1);

            Date time2 = new java.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(timeAfter);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            Date d = new java.text.SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).parse(date.getCurrentTime());
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date currentTime = calendar3.getTime();
            if (currentTime.after(calendar1.getTime()) && currentTime.before(calendar2.getTime())) {
                return true;
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * Launches the new activity
     * @param className - the activity class to launch
     */
    public void launchActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Method to control the links in the navigation drawer
     * @param item - the MenuItem used to determine which item has been clicked
     *             in the navigation drawer
     * @return true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        Class className = this.getClass();

        // handles navigation item view clicks here
        if (id == R.id.nav_home && !(className.equals(MainActivity.class))) {
            launchActivity(MainActivity.class);
        } else if (id == R.id.nav_diary && !(className.equals(DiaryActivity.class))) {
            launchActivity(DiaryActivity.class);
        } else if (id == R.id.nav_game && !(className.equals(UnityGame.class) || className.equals(GameActivity.class))) {
            launchActivity(GameActivity.class);
        } else if (id == R.id.nav_progress && !(className.equals(ProgressActivity.class))) {
            launchActivity(ProgressActivity.class);
        } else if (id == R.id.nav_risk && !(className.equals(RiskActivity.class))) {
            launchActivity(RiskActivity.class);
        } else if (id == R.id.nav_database && !(className.equals(FoodDatabaseActivity.class))) {
            launchActivity(FoodDatabaseActivity.class);
        } else if (id == R.id.nav_settings && !(className.equals(SettingsActivity.class))) {
            launchActivity(SettingsActivity.class);
        } else if (id == R.id.nav_info && !(className.equals(InfoActivity.class))) {
            launchActivity(InfoActivity.class);
        } else if (id == R.id.nav_help && !(className.equals(HelpActivity.class))) {
            launchActivity(HelpActivity.class);
        }

        // close drawer on selection of item
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Class className = this.getClass();

        if (id == R.id.action_settings) {
            if (!(className.equals(SettingsActivity.class))) {
                launchActivity(SettingsActivity.class);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
