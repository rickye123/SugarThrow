package com.example.richa.sugarthrow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Iterator;
import java.util.List;

import static com.android.volley.toolbox.Volley.newRequestQueue;

public class FoodDatabaseActivity extends DiaryActivity {

    private Connector database;
    private SearchView searchView;
    private Execute executeSQL;
    private Display display = new Display();
    private ViewCreator viewCreator = new ViewCreator(this);
    private LayoutCreator layoutCreator = new LayoutCreator(this, viewCreator);
    private LinearLayout searchEntries;
    private Context context;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_database_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        createDrawer(toolbar);
        createNavigationView(R.id.nav_database);

        this.database = MainActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
        if(db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }

        executeSQL = new Execute(database);

        queue = Volley.newRequestQueue(this);

        searchView = (SearchView)findViewById(R.id.database_searchview);
        searchView.setQuery(searchView.getQuery(), true);

        searchForFood();

        searchEntries = (LinearLayout)findViewById(R.id.food_search_entries);





        Button button1 = (Button)findViewById(R.id.button1);
        clickButton(button1);



    }

    public void clickButton(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ID: ", Integer.toString(v.getId()));
                switch (v.getId()) {
                    case R.id.button1:
                        Log.d("CLICK", "button clicked");


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

    public void searchForFood() {
        // perform set on query text listener event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("TEXT SUBMITTED");
                // do something on text submit
                System.out.println("QUERY IS : " + searchView.getQuery());

                if(createSearchEntries(query) == false) {
                    // search in online database
                    searchOnline(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("TEXT CHANGED");
                return false;
            }
        });

    }

    public void makeUPCRequest(String id) {
        String upcURL = "https://api.nutritionix.com/v1_1/item?id=" +
                id +
                /*"51c3f38d97c3e6de73cbdcac" +*/
                "&appId=de16f483" +
                "&appKey=ffafe694e6e329a42e7f9d11338a90bd";
        // Request a string response from the provided URL.
        StringRequest stringUPCRequest = new StringRequest(Request.Method.GET, upcURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try  {
                            JSONObject first = (JSONObject) new JSONTokener(response).nextValue();

                            if(first.length() == 0) {
                                return;
                            }
                            String calories = first.getString("nf_calories");
                            String itemName = first.getString("item_name");
                            String brandName = first.getString("brand_name");
                            System.out.println("CALORIES " + calories);

                        } catch (JSONException foodDatabaseException) {
                            foodDatabaseException.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn\'t work");
            }
        });
        queue.add(stringUPCRequest);
    }

    public void searchOnline(String query) {

        if(query.contains(" ")) {
            query = query.replace(" ", "%20");
        }

        String url ="https://api.nutritionix.com/v1_1/search/" +
                query +
                "?results=0%3A50&cal_min=0&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2" +
                "Cbrand_id" +
                "&appId=de16f483" +
                "&appKey=ffafe694e6e329a42e7f9d11338a90bd";



        System.out.println(url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try  {
                            JSONObject first = (JSONObject) new JSONTokener(response).nextValue();
                            String hits = first.getString("hits");
                            System.out.println(hits);

                            JSONArray jsonArray = new JSONArray(hits);
                            System.out.println(jsonArray.length());
                            if(jsonArray.length() == 0) {
                                return;
                            }
                            System.out.println("JSON ARRAY SIZE " + jsonArray.length());
                            boolean populated = false;
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fields = jsonObject.getString("fields");
                                JSONObject array = new JSONObject(fields);
                                String id = array.getString("item_id");
                                String itemName = array.getString("item_name");
                                String brandName = array.getString("brand_name");
                                System.out.println("id " + id);
                                System.out.println("itemName " + itemName);
                                System.out.println("brandName " + brandName);
                                System.out.println();
                                if(searchEntries.getChildCount() > 0 && populated == false) {
                                    searchEntries.removeAllViews();
                                    populated = true;
                                }
                                System.out.println("i " + i);
                                if(array.length() == 0) {

                                } else {
                                    populateSearchFields(brandName + " " + itemName, i);
                                }

                            }
                        } catch (JSONException foodDatabaseException) {
                            foodDatabaseException.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn\'t work");
            }
        });

        queue.add(stringRequest);

    }

    public boolean createSearchEntries(String query) {

        String query1 = "%".concat(searchView.getQuery().toString());
        String query2 = searchView.getQuery().toString().concat("%");

        List<List<String>> search = executeSQL.sqlGetFromQuery(SqlQueries.SQL_INTERNAL_FOOD_SEARCH, query1, query2);
        display.printTable("Internal search for " + searchView.getQuery().toString(), search);

        if(searchEntries.getChildCount() > 0) {
            searchEntries.removeAllViews();
        }
        if(!search.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < search.size(); i++) {
                // populate the search entries
                populateSearchFields(search.get(i).get(0), i);
            }
            return true;
        }
        return false;
    }

    public void populateSearchFields(String foodName, int row) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42 ,
                getResources().getDisplayMetrics());

        LinearLayout mainWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout wrapper = layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT, height, Gravity.NO_GRAVITY, 0, 0, 0, 10);

        mainWrapper.addView(wrapper);

        TextView food = viewCreator.createTextInDiary(foodName, 250, row);
        food.setSingleLine(true);
        // truncate at the end of text so text doesn't overflow
        food.setEllipsize(TextUtils.TruncateAt.END);
        ImageView plus = viewCreator.createImageInDiary(row,
                R.drawable.ic_add_circle_black, "plusTag");

        wrapper.addView(food);
        wrapper.addView(plus);

        searchEntries.addView(mainWrapper);

    }

}
