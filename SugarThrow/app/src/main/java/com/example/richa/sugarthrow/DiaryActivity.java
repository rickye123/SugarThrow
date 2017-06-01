package com.example.richa.sugarthrow;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SearchView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.*;

public class DiaryActivity extends MainActivity {

    private ImageView searchIcon;
    private TextView dateText, firstReg, secondReg, thirdReg, fourthReg, fifthReg;
    private SearchView searchView;
    private Connector database;
    private Execute executeSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        createDrawer(toolbar);
        createNavigationView(R.id.nav_diary);

        this.database = MainActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
        if(db.isOpen() == true) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }

        searchIcon = (ImageView)findViewById(R.id.search_icon);
        clickImageView(searchIcon);

        dateText = (TextView)findViewById(R.id.date_text);
        dateText.setText(getCurrentDate());

        executeSQL = new Execute(database);

        searchView = (SearchView)findViewById(R.id.diary_search);
        searchView.setQueryHint("Food Search");

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230 , getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40 , getResources().getDisplayMetrics());

        populateRegularFoods("re16621");
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.diary_entries);
        TextView tv = new TextView(this);
        tv.setText("1 x Baked Beans");
        tv.setTextSize(24);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        linearLayout.addView(tv);

        TextView tv1 = new TextView(this);
        tv1.setText("1 x Apple");
        tv1.setTextSize(24);
        tv1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        linearLayout.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText("1 x Apple");
        tv2.setTextSize(24);
        tv2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        linearLayout.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText("1 x Apple");
        tv3.setTextSize(24);
        tv3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        linearLayout.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText("1 x Apple");
        tv4.setTextSize(24);
        tv4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        linearLayout.addView(tv4);

        TextView tv5 = new TextView(this);
        tv5.setText("1 x Apple");
        tv5.setTextSize(24);
        tv5.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        linearLayout.addView(tv5);


    }

    /**
     *
     * @param username
     */
    public void populateRegularFoods(String username) {

        List<List<String>> regFood = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, username);
        List<TextView> items = new ArrayList<TextView>();

        items.add((firstReg = (TextView)findViewById(R.id.firstRegItem)));
        items.add((secondReg = (TextView)findViewById(R.id.secondRegItem)));
        items.add((thirdReg = (TextView)findViewById(R.id.thirdRegItem)));
        items.add((fourthReg = (TextView)findViewById(R.id.fourthRegItem)));
        items.add((fifthReg = (TextView)findViewById(R.id.fifthRegItem)));

        for(int i = 0; i < 5; i++) {
            items.get(i).setText(regFood.get(i).get(2));
        }
    }

    /**
     *
     * @return
     */
    public String getCurrentDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        // months count from 0, so add 1
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String theDate = day + "-" + month + "-" + year;
        String formattedDate = theDate.format("%02d-%02d-%d", day, month, year);
        return formattedDate;

    }

    // TODO implement functionality for these image clicks
    public void clickImageView(final ImageView image) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.search_icon:
                        Log.d("CLICK", "search clicked");
                        break;
                    case R.id.diary_image:
                        Log.d("CLICK", "diary image clicked");
                        //launchActivity(MainActivity.class);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
             launchActivity(MainActivity.class);
        } else if (id == R.id.nav_diary) {
           // don't do anything
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
