package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.*;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.*;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView searchIcon, diaryImage, playSugar;
    private Context context;
    private static Connector database;
    private Cursor c = null;
    private Execute executeSQL;
    private Display display = new Display();
/*    private final String SQL_REGULAR_FOOD = "SELECT User.userName AS uname, Diary.theDate as entryDate, Food.name " +
            "AS foodName, Food.foodId as fid, COUNT(*) AS quantity FROM Diary INNER JOIN " +
            "Food ON Diary.foodId = Food.foodId INNER JOIN User ON User.userID = Diary.userId " +
            "WHERE User.userName = ? GROUP BY Diary.foodId ORDER BY quantity DESC LIMIT 5";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createDrawer(toolbar);
        createNavigationView(R.id.nav_home);

        searchIcon = (ImageView)findViewById(R.id.search_icon);
        clickImageView(searchIcon);

        diaryImage = (ImageView)findViewById(R.id.diary_image);
        clickImageView(diaryImage);

        playSugar = (ImageView)findViewById(R.id.play_sugar_image);
        clickImageView(playSugar);

        database = new Connector(MainActivity.this);
        database.attemptCreate();
        database.openConnection();

        executeSQL = new Execute(database);

        List<List<String>> users = executeSQL.sqlGetAll("User");
        display.printTable("Users", users);

        List<List<String>> food = executeSQL.sqlGetAll("Food");
        display.printTable("Food", food);

    }

    public static Connector getDatabaseConnection() {
        return database;
    }

    /**
     *
     * @param menuId
     */
    public void createNavigationView(int menuId) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(menuId).setChecked(true);
    }

    /**
     *
     * @param toolbar
     */
    public void createDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    /**
     *
     * @param image
     */
    // TODO implement functionality for these image clicks
    public void clickImageView(final ImageView image) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.search_icon:
                        Log.d("CLICK", "search clicked");
                        executeSQL.sqlGetAll("User");
                        break;
                    case R.id.diary_image:
                        Log.d("CLICK", "diary image clicked");
                        List<List<String>> regFood = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, "re16621");
                        display.printTable("Regular Foods", regFood);
                        break;
                    case R.id.play_sugar_image:
                        Log.d("CLICK", "play sugar throw clicked");

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
    private void launchActivity(Class className) {
        Intent intent = new Intent(this, className);
        startActivity(intent);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_diary) {
            launchActivity(DiaryActivity.class);
        } else if (id == R.id.nav_game) {

        } else if (id == R.id.nav_progress) {

        } else if (id == R.id.nav_risk) {

        } else if (id == R.id.nav_database) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_info) {

        }
         else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
