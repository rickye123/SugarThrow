package com.example.richa.sugarthrow;

/*
This is the main activity class, which is called when the app is launched
 */

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.ImageView;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener {

    //TODO solve the static connector issue
    private static Connector database;
    private Execute executeSQL;
    private TableDisplay display = new TableDisplay();
    private FoodContentsHandler foodContentsHandler;
    private String username;

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

    public void setNavigationUsername(String username) {
        NavigationView navView= (NavigationView)findViewById(R.id.nav_view);
        View view = navView.getHeaderView(0);
        TextView text = (TextView)view.findViewById(R.id.navigation_username);
        text.setText(username);
    }


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

        Connector database = new Connector(this);
        database.attemptCreate();
        database.openConnection();
        executeSQL = new Execute(database);
        foodContentsHandler = new FoodContentsHandler(database, username);

        // handle the links to the diary, search, and game on the homapage
        handleLinks();

        // the HUD contains the percentage intake of sugar the user has left and their total points
        populateHUD();

    }

    public String getUsername() {
        return username;
    }

    /**
     *
     */
    private void handleLinks() {

        ImageView diaryImage = (ImageView)findViewById(R.id.diary_image);
        ImageView playSugar = (ImageView)findViewById(R.id.play_sugar_image);
        ImageView searchFoods = (ImageView)findViewById(R.id.search_database_image);

        //
        clickImageView(diaryImage);

        //
        clickImageView(searchFoods);

        //
        clickImageView(playSugar);
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

    /**
     * Getter method returning the initialised database connection
     * @return Connector object - the database connection
     */
    public static Connector getDatabaseConnection() {
        return database;
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
     * @param image - the ImageView that will be clicked
     */
    // TODO implement functionality for these image clicks
    public void clickImageView(final ImageView image) {

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ID: ", Integer.toString(v.getId()));
                switch (v.getId()) {
                    case R.id.search_database_image:
                        Log.d("CLICK", "search clicked");
                        launchActivity(FoodDatabaseActivity.class);
                        break;
                    case R.id.diary_image:
                        Log.d("CLICK", "diary image clicked");
                        launchActivity(DiaryActivity.class);
                        break;
                    case R.id.play_sugar_image:
                        Log.d("CLICK", "play sugar throw clicked");
                        launchActivity(UnityPlayerActivity.class);
                        break;
                    default:
                        // If we got here, the user's action was not recognized.
                        // Invoke the superclass to handle it.
                        Log.d("CLICK", "nothin clicked");
                        break;
                }
            }
        });
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
        } else if (id == R.id.nav_game && !(className.equals(UnityPlayerActivity.class))) {
            launchActivity(UnityPlayerActivity.class);
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

        if (id == R.id.action_settings) {
            Log.d("OPTIONS", "Settings clicked");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
