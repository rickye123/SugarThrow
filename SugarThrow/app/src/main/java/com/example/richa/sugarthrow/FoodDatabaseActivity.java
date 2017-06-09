package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.android.volley.toolbox.Volley.newRequestQueue;

public class FoodDatabaseActivity extends DiaryActivity {

    private Connector database;
    private SearchView searchView;
    private Execute executeSQL;
    private TableDisplay display = new TableDisplay();
    private ViewCreator viewCreator = new ViewCreator(this);
    private LayoutCreator layoutCreator = new LayoutCreator(this, viewCreator);
    private LinearLayout searchEntries;
    private Context context;
    private RequestQueue queue;
    private List<List<String>> searchTerms = new ArrayList<>();
    private Map<String, String> searchKeys = new HashMap<>();
    private ContentValues contents;
    private TimeKeeper date = new TimeKeeper();
    private boolean isOpen = false;
    private static int counter = 0;

    public void clearTableContents() {
        while (!searchTerms.isEmpty()) {
            int size = searchTerms.size();
            int i = 0;
            while (i < size) {
                searchTerms.remove(0);
                i++;
            }
        }
    }

    public FoodDatabaseActivity() {



    }

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

        executeSQL = new Execute(database);

        queue = Volley.newRequestQueue(this);

        String query = DiaryActivity.getRequest();
        System.out.println("QUERY IS " + query);
        if(query != null) {
            searchOnline(query);
        }

        searchView = (SearchView)findViewById(R.id.database_searchview);
        searchView.setQuery(searchView.getQuery(), true);
        clickSearch(searchView);

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
                searchOnline(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    public void makeUPCRequest(String id, final ServerCallBack callback) {

        counter++;
        Log.d("COUNTER", String.valueOf(counter));

        Log.d("UPC Request", "another request");

        String upcURL = "https://api.nutritionix.com/v1_1/item?id=" +
                id +
                /*"51c3f38d97c3e6de73cbdcac" +*/
                "&appId=de16f483" +
                "&appKey=ffafe694e6e329a42e7f9d11338a90bd";

        contents = new ContentValues();

        // Request a string response from the provided URL.
        sendUPCRequest(upcURL, callback);

    }

    public void sendUPCRequest(String url, final ServerCallBack callback) {

        StringRequest stringUPCRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try  {
                            JSONObject searchResults = (JSONObject) new JSONTokener(response).nextValue();
                            contents = getUPCContents(searchResults);
                            callback.onSuccess(response);
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

    public ContentValues getUPCContents(JSONObject searchResults) throws JSONException {

        ContentValues values = new ContentValues();

        values.put("calories", searchResults.getString("nf_calories"));
        values.put("fat", searchResults.getString("nf_total_fat"));

        String salt = searchResults.getString("nf_sodium");
        if(salt.equals("null")) {
            values.put("salt", "null");
        }
        else {
            Double saltInGrams = Double.parseDouble(salt) / 1000;
            values.put("salt", saltInGrams.toString());
        }
        values.put("sugar", searchResults.getString("nf_sugars"));
        values.put("protein", searchResults.getString("nf_protein"));
        values.put("saturates", searchResults.getString("nf_saturated_fat"));
        values.put("carbs", searchResults.getString("nf_total_carbohydrate"));
        String brandName = searchResults.getString("brand_name");

        if(brandName.equals("USDA") || brandName.equals("Nutritionix")) {
            values.put("name", searchResults.getString("item_name"));
        }
        else {
            values.put("name", searchResults.getString("brand_name") + " " + searchResults.getString("item_name"));
        }

        return values;
    }

    public void searchOnline(String query) {

        if(query.contains(" ")) {
            query = query.replace(" ", "%20");
        }

        String url ="https://api.nutritionix.com/v1_1/search/" + query + "?results=0%3A50&cal_min=0" +
                "&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2" + "Cbrand_id&appId=de16f483" +
                "&appKey=ffafe694e6e329a42e7f9d11338a90bd";

        // Request a string response from the provided URL.
        sendSearchRequest(url);

    }

    public void sendSearchRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try  {
                            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                            String results = jsonObject.getString("hits");

                            JSONArray hits = new JSONArray(results);
                            if(hits.length() == 0) {
                                Log.d("Empty hits", "empty");
                                if(searchEntries.getChildCount() > 0) {
                                    searchEntries.removeAllViews();
                                    LinearLayout noEntries = layoutCreator.createNoResultsLayout();
                                    searchEntries.addView(noEntries);
                                }
                            }
                            else {
                                Log.d("Searched", "search");
                                boolean populated = false;
                                clearTableContents();
                                searchThroughItems(hits);
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

    public void searchThroughItems(JSONArray hits) throws JSONException {

        boolean populated = false;

        for(int i = 0; i < hits.length(); i++) {

            JSONObject jsonObject = hits.getJSONObject(i);
            String fields = jsonObject.getString("fields");

            JSONObject searchResults = new JSONObject(fields);
            Map<String, String> info = getSearchInformation(searchResults);

            searchTerms.add(new ArrayList<String>());
            searchTerms.get(i).add(info.get("item_id"));

            if(info.get("brand_name").equals("USDA") || info.get("brand_name").equals("Nutritionix")) {
                searchTerms.get(i).add(info.get("item_name"));
            }
            else {
                searchTerms.get(i).add(info.get("brand_name") + " " + info.get("item_name"));
            }

            if(!searchKeys.containsKey(searchTerms.get(i).get(1))) {
                searchKeys.put(searchTerms.get(i).get(1), searchTerms.get(i).get(1));
                if(searchEntries.getChildCount() > 0 && !populated) {
                    searchEntries.removeAllViews();
                    populated = true;
                }
                if(searchResults.length() > 0) {
                    populateSearchFields(searchTerms.get(i).get(1), i, false);
                }
            }
        }
    }

    public Map<String, String> getSearchInformation(JSONObject searchResults) throws JSONException {

        Map<String, String> info = new HashMap<>();

        info.put("item_id", searchResults.getString("item_id"));
        info.put("item_name", searchResults.getString("item_name"));
        info.put("brand_name", searchResults.getString("brand_name"));

        return info;
    }

    public void updateSearchFields(final int openRow, boolean open) {

        // check to see if food is in local database
        if(notInFoodDatabase(openRow)) {
            Log.d("UPC UPDATE SEARCH", String.valueOf(counter));
            makeUPCRequest(searchTerms.get(openRow).get(0), new ServerCallBack() {
                @Override
                public void onSuccess(String result) {
                    executeSQL.sqlInsert("Food", contents);
                    updateSearchFields(openRow, true);
                }
            });
        }
        else {
            if(searchEntries.getChildCount() > 1) {
                searchEntries.removeAllViews();
            }

            for (int i = 0; i < searchTerms.size(); i++) {
                if(i == openRow && open == true) {
                    populateSearchFields(searchTerms.get(i).get(1), i, true);
                }
                else {
                    populateSearchFields(searchTerms.get(i).get(1), i, false);
                }
            }
        }

    }

    public void populateSearchFields(String foodName, int row, boolean isOpen, int ... openRow) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42 ,
                getResources().getDisplayMetrics());

        LinearLayout mainWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout wrapper = layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT, height, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0, 0, 10);

        mainWrapper.addView(wrapper);

        TextView food = viewCreator.createTextInDiary(foodName, 230, row, "addTagFromSearch");
        food.setSingleLine(true);
        food.setEllipsize(TextUtils.TruncateAt.END);
        food.setTag("searchTag");
        ImageView plus = viewCreator.createImageInDiary(row,
                R.drawable.ic_add_circle_black, "plusTag");
        plus.setColorFilter(getColor(R.color.correctGreen));

        clickToAdd(plus);
        clickDropDown(food);

        wrapper.addView(food);
        wrapper.addView(plus);

        // check to see if food exists
        if(isOpen) {
            LinearLayout dropDownWrapper = createDropDownWrapper(row, foodName);
            mainWrapper.addView(dropDownWrapper);
            food.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            food.setMarqueeRepeatLimit(3);
            food.isFocusable();
            food.setSelected(true);
            food.canScrollHorizontally(View.SCROLL_INDICATOR_RIGHT);
        }

        searchEntries.addView(mainWrapper);

    }

    @Override
    public void clickDropDown(final TextView text) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getTag() != null && text.getTag().equals("searchTag") && !isOpen) {
                    updateSearchFields(v.getId(), true);
                    isOpen = true;
                }
                else {
                    updateSearchFields(v.getId(), false);
                    isOpen = false;
                }
            }
        });
    }

    public boolean notInFoodDatabase(int id) {
        List<List<String>> foods = executeSQL.sqlGetAll("Food");
        if(!foods.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < foods.size(); i++) {
                if(foods.get(i).get(1).equals(searchTerms.get(id).get(1))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public void insertIntoDiaryFromSearch(View v, String username) {

        List<List<String>> userName = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_USER,
                username);

        String foodId = findFoodId(v);
        String userId = userName.get(0).get(0);

        ContentValues values = findDiaryValues(date.convertDateFormat(date.getCurrentDate()), foodId, userId);

        executeSQL.sqlInsert("Diary", values);
        updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);

        addStreakPoints(username);

    }


    public void clickToAdd(final ImageView image) {
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if(v.getTag() != null && v.getTag().equals("plusTag")) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        if (notInFoodDatabase(v.getId())) {
                            Log.d("UPC ON ADD", String.valueOf(counter));
                            makeUPCRequest(searchTerms.get(v.getId()).get(0), new ServerCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    executeSQL.sqlInsert("Food", contents);
                                    // insert into diary
                                    insertIntoDiaryFromSearch(v, "re16621");
                                }
                            });
                        } else {
                            insertIntoDiaryFromSearch(v, "re16621");
                        }
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        image.setColorFilter(getColor(R.color.correctGreen));
                    }
                }
                return true;
            }
        });
    }

    public String findFoodId(View v) {

        List<List<String>> foods = executeSQL.sqlGetAll("Food");

        if(!foods.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < foods.size(); i++) {
                if(foods.get(i).get(1).equals(searchTerms.get(v.getId()).get(1))) {
                    return foods.get(i).get(0);
                }
            }
        }
        return "Empty set";
    }

}
