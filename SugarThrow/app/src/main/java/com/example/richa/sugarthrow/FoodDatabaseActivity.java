package com.example.richa.sugarthrow;

/*
This class represents the FoodDatabase Activity, in which the user can search
for foods using the Nutritionix API
 */

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDatabaseActivity extends DiaryActivity {

    private SearchView searchView;
    private Execute executeSQL;
    private String TAG = "FoodDatabaseActivity";
   // private TableDisplay display = new TableDisplay();
    private ViewCreator viewCreator = new ViewCreator(this);
    private LayoutCreator layoutCreator = new LayoutCreator(this, viewCreator);
    private LinearLayout searchEntries;
    private RequestQueue queue;
    private List<List<String>> searchTerms = new ArrayList<>();
    private Map<String, String> searchKeys = new HashMap<>();
    private ContentValues contents;
    private TimeKeeper date = new TimeKeeper();
    private boolean isOpen = false;
    private static int counter = 0;
    private DiaryHandler diaryHandler;
    private PointsHandler pointsHandler;
    private String username;
    private boolean acknowledgeStreak = false;

    /**
     * Clear searchTerms table
     */
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

        setContentView(R.layout.food_database_activity);
        setNavigationUsername(username);

        startContents();

    }

    /**
     * Start content when the activity loads
     */
    private void startContents() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_database);

        Connector database = LoginActivity.getDatabaseConnection();
        executeSQL = new Execute(database);
        diaryHandler = new DiaryHandler(database);
        pointsHandler = new PointsHandler(database, username);

        // Volley used to make requests to Nutritionix API
        queue = Volley.newRequestQueue(this);

        String query = DiaryActivity.getRequest();
        if(query != null) {
            searchOnline(query);
        }

        searchView = (SearchView)findViewById(R.id.database_searchview);
        searchView.setQuery(searchView.getQuery(), true);
        clickSearch(searchView);

        // search for food on submit of searchview
        searchForFood();

        searchEntries = (LinearLayout)findViewById(R.id.food_search_entries);

        // clear previous results if clear button pressed
        clearResults();

    }

    /**
     * Clears the results of the search on click of the "CLEAR" button
     */
    private void clearResults() {

        Button clearResults = (Button)findViewById(R.id.clear_results);
        clearResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEntries.removeAllViews();
                LinearLayout noEntries = layoutCreator.createNoResultsLayout();
                searchEntries.addView(noEntries);
            }
        });

    }

    /**
     * Search for foods in the database
     */
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

    /**
     * Make a UPC request (only have 200 of these a day)
     * @param id - the id of the food in the Nutritionix database
     * @param callback - the ServerCallBack method that waits for the request to be
     *                 returned
     */
    public void makeUPCRequest(String id, final ServerCallBack callback) {

        String upcURL = "https://api.nutritionix.com/v1_1/item?id=" +
                id +
                /*"51c3f38d97c3e6de73cbdcac" +*/
                "&appId=de16f483" +
                "&appKey=ffafe694e6e329a42e7f9d11338a90bd";

        contents = new ContentValues();

        // Request a string response from the provided URL.
        sendUPCRequest(upcURL, callback);

    }

    /**
     * Send the UPC request (only have 200 of these a day)
     * @param url - the URL that was created (included the id of the food)
     * @param callback - the callback method which needs to wait for the response
     *                 to be returned before anything else is called
     */
    public void sendUPCRequest(String url, final ServerCallBack callback) {

        // send Volley request
        StringRequest stringUPCRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // try parse the request into JSON object
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
                Log.d(TAG, "Volley UPC Request didn\'t work");
            }
        });

        queue.add(stringUPCRequest);
    }

    /**
     * Get the contents of the UPC search (nutritional context of food)
     * @param searchResults - the search results obtained from the UPC request
     * @return the content values containing the nutritional info of the food
     * @throws JSONException - throws JSON exception
     */
    public ContentValues getUPCContents(JSONObject searchResults) throws JSONException {

        ContentValues values = new ContentValues();

        values.put("calories", searchResults.getString("nf_calories"));
        values.put("fat", searchResults.getString("nf_total_fat"));

        // salt is parsed as double, so need to ensure this isn't parsed when null
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

        // remove USDA and Nutritionix from the Brand names
        if(brandName.equals("USDA") || brandName.equals("Nutritionix")) {
            values.put("name", searchResults.getString("item_name"));
        }
        else {
            values.put("name", searchResults.getString("brand_name") + " " + searchResults.getString("item_name"));
        }

        return values;
    }

    /**
     * Search request based on query (have 5000 of these a day)
     * @param query - the query the user sent
     */
    public void searchOnline(String query) {

        // if query contains any spaces, replace these with %20
        if(query.contains(" ")) {
            query = query.replace(" ", "%20");
        }

        String url ="https://api.nutritionix.com/v1_1/search/" + query + "?results=0%3A50&cal_min=0" +
                "&cal_max=50000&fields=item_name%2Cbrand_name%2Citem_id%2" + "Cbrand_id&appId=de16f483" +
                "&appKey=ffafe694e6e329a42e7f9d11338a90bd";

        // Request a string response from the provided URL.
        sendSearchRequest(url);

    }

    /**
     * Send the Search request (only have 5000 of these a day)
     * @param url - the URL which was passed (including the query from the user)
     */
    public void sendSearchRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try  {
                            // create initial JSON object and put it into a String
                            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                            String results = jsonObject.getString("hits");

                            JSONArray hits = new JSONArray(results);
                            // empty search results, create no search entries layout
                            if(hits.length() == 0) {
                                Log.d("Empty hits", "empty");
                                // if there are results already, remove these results
                                if(searchEntries.getChildCount() > 0) {
                                    searchEntries.removeAllViews();
                                    LinearLayout noEntries = layoutCreator.createNoResultsLayout();
                                    searchEntries.addView(noEntries);
                                }
                            }
                            else {
                                Log.d("Searched", "search");
                                clearTableContents();
                                // search through the items
                                searchThroughItems(hits);
                            }
                        } catch (JSONException foodDatabaseException) {
                            foodDatabaseException.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Search didn\'t work");
            }
        });
        queue.add(stringRequest);
    }

    /**
     * Search through all the items and populate the searchTerms table
     * @param hits - the JSON array containing all the different search results
     * @throws JSONException - throw an Exception if the JSON object is not parsed properly
     */
    public void searchThroughItems(JSONArray hits) throws JSONException {

        boolean populated = false;

        // cycle through results and populate table
        for(int i = 0; i < hits.length(); i++) {

            JSONObject jsonObject = hits.getJSONObject(i);
            String fields = jsonObject.getString("fields");

            JSONObject searchResults = new JSONObject(fields);
            Map<String, String> info = getSearchInformation(searchResults);

            searchTerms.add(new ArrayList<String>());
            searchTerms.get(i).add(info.get("item_id"));

            // only pass item_name to searchTerms when brand_name is USDA or Nutritionix
            if(info.get("brand_name").equals("USDA") || info.get("brand_name").equals("Nutritionix")) {
                searchTerms.get(i).add(info.get("item_name"));
            }
            else {
                searchTerms.get(i).add(info.get("brand_name") + " " + info.get("item_name"));
            }

            // create a hashmap that removes duplicate entries
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

    /**
     * Get the item_id, brand_name, and item_name from search results
     * @param searchResults - the search results
     * @return the item_id, item_name, and brand_name from the search results
     * @throws JSONException - throw JSON exception
     */
    public Map<String, String> getSearchInformation(JSONObject searchResults) throws JSONException {

        Map<String, String> info = new HashMap<>();

        info.put("item_id", searchResults.getString("item_id"));
        info.put("item_name", searchResults.getString("item_name"));
        info.put("brand_name", searchResults.getString("brand_name"));

        return info;
    }

    /**
     * Update the search fields when user clicks drop down
     * @param openRow - the row which has been opened
     * @param open - boolean (true if opened, false otherwise)
     */
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
            // remove all views of that entry
            if(searchEntries.getChildCount() > 1) {
                searchEntries.removeAllViews();
            }

            // populate the search fields
            for (int i = 0; i < searchTerms.size(); i++) {
                // populate search fields with a drop down open
                if(i == openRow && open) {
                    populateSearchFields(searchTerms.get(i).get(1), i, true);
                }
                else {
                    populateSearchFields(searchTerms.get(i).get(1), i, false);
                }
            }
        }

    }

    /**
     * Populate the search fields. If isOpen is true, then populate fields with
     * a drop down on a particular item open
     * @param foodName - the foodName clicked on or being cycled through
     * @param row - the row (position in the search)
     * @param isOpen - boolean representing whether drop down is open or not
     */
    public void populateSearchFields(String foodName, int row, boolean isOpen) {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 ,
                getResources().getDisplayMetrics());

        LinearLayout mainWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout wrapper = layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT, height,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0, 0, 10);

        mainWrapper.addView(wrapper);

        TextView food = viewCreator.createText(row, foodName, 230, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL, 0, 18, Color.BLACK, "searchTag", 0, 0 , 0, 0);

        ImageView plus = viewCreator.createImage(row, R.drawable.ic_add_circle_black,
                LinearLayout.LayoutParams.MATCH_PARENT, 40,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, "plusTag");
        plus.setColorFilter(getColor(R.color.correctGreen));

        clickToAdd(plus);
        clickDropDown(food);

        wrapper.addView(food);
        wrapper.addView(plus);

        if(isOpen) {
            LinearLayout dropDownWrapper = addDropDownWrapper(row, foodName, food);
            mainWrapper.addView(dropDownWrapper);
        }

        searchEntries.addView(mainWrapper);

    }

    /**
     * Overrides the DiaryActivity method. Click listener that listens for
     * when a food item is clicked, in which a drop down opens
     * @param text - the textview clicked on
     */
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

    /**
     * Check to see whether food is in the database (using the searchTerms table)
     * @param id - the id (position of the food in the search results)
     * @return false if the food is in the database, else true
     */
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

    /**
     * Method invoked when the user clicks the plus in the search entries,
     * thereby adding a food to the database
     * @param image - the imageview that was clicked on
     */
    public void clickToAdd(final ImageView image) {
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if(v.getTag() != null && v.getTag().equals("plusTag")) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));

                        // if not in the database, make UPC request, get the UPC results
                        // and add the data to the food table
                        if (notInFoodDatabase(v.getId())) {
                            Log.d("UPC ON ADD", String.valueOf(counter));
                            makeUPCRequest(searchTerms.get(v.getId()).get(0), new ServerCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    executeSQL.sqlInsert("Food", contents);
                                    // insert into diary
                                    checkForInsert(v);
                                    //insert(v);
                                }
                            });
                        } else {
                            // if in the database, simply insert into diary
                            //insert(v);
                            checkForInsert(v);
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

    /**
     * Insert the food into the diary using the diaryHandler, and then update points
     * @param view - the view whose id is being referenced
     */
    public void insert(View view) {
        String pointsBefore = pointsHandler.getPointsBefore(username);
        String foodName = searchTerms.get(view.getId()).get(1);

        diaryHandler.insertIntoDiary(view.getId(),
                date.convertDateFormat(date.getCurrentDate()),username, searchTerms, true);

        pointsHandler.checkForPointsUpdate(pointsBefore,
                date.convertDateFormat(date.getCurrentDate()), username, true);

        Toast.makeText(FoodDatabaseActivity.this, foodName + " added to diary",
                Toast.LENGTH_SHORT).show();

        int streak = diaryHandler.findLogStreak(date, username);
        if(streak > 1 && !acknowledgeStreak) {
            String feedback = "You have logged foods for " + Integer.toString(streak) +
                    "\n days now. \nWell Done!";
            acknowledgeStreak = true;
            launchFeedbackActivity(FoodDatabaseActivity.this, feedback, true);
        }

    }

    /**
     * Check to see whether inserting will result in food being over the
     * daily allowance, and prompt the user. If they respond yes to the dialog
     * box the food will be added, otherwise it will not be added
     * @param view - the view representing the view clicked on
     */
    private void checkForInsert(View view) {

        String foodName = searchTerms.get(view.getId()).get(1);

        String message = generateMessage(foodName);

        if(message.equals("")) {
            insert(view);
        }
        else {
            String dialog = "You are about to go over your daily limit of : ";
            dialog = dialog.concat(message);
            openInsertDialog(dialog, view);
        }

    }

    /**
     * Dialog box appears when user goes over their daily allowance for a food
     * @param message - the message passed to the dialog box
     * @param view - the view is passed to the dialog box to determine its id
     */
    public void openInsertDialog(String message, final View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String message = generateMessage(searchTerms.get(view.getId()).get(1));
                insert(view);
                String feedback = "You have exceeded your daily allowance of : ".concat(message);
                launchFeedbackActivity(FoodDatabaseActivity.this, feedback, false);
                pointsHandler.decreasePoints(username);
            }
        });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FoodDatabaseActivity.this, "Cancelled Insertion", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}