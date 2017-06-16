package com.example.richa.sugarthrow;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SearchView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.util.*;

public class DiaryActivity extends MainActivity {

    private String TAG = "DiaryActivity";
    private TextView dateText;
    private List<List<String>> regularFoods = new ArrayList<>();
    private List<List<String>> diary = new ArrayList<>();
    private ViewCreator viewCreator = new ViewCreator(this);
    private LayoutCreator layoutCreator = new LayoutCreator(this, viewCreator);
    private LinearLayout diaryLayout;
    private Execute executeSQL;
    private TimeKeeper date = new TimeKeeper();
    private boolean isOpen = false;
    private static String queryRequest;
    private DiaryHandler diaryHandler;
    private PointsHandler pointsHandler;
    private FoodContentsHandler foodContentsHandler;
    private String username;
    private boolean acknowledgeStreak = false;

    /**
     * Getter method invoked in the FoodDatabaseActivity to obtain the
     * query string sent from this activity (from the searchview)
     * @return the query string sent from this activity to the FoodDatabaseActivity
     */
    public static String getRequest() {
        return queryRequest;
    }

    /**
     * Clear the global diary table
     */
    private void clearDiaryEntriesTable() {
        while (!diary.isEmpty()) {
            int size = diary.size();
            int i = 0;
            while (i < size) {
                diary.remove(0);
                i++;
            }
        }
    }

    /**
     * Clear the regular foods table
     */
    private void clearRegularFoodsTable() {
        while (!regularFoods.isEmpty()) {
            int size = regularFoods.size();
            int i = 0;
            while (i < size) {
                regularFoods.remove(0);
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

        // set the layout to the diary_activity.xml file
        setContentView(R.layout.diary_activity);
        setNavigationUsername(username);
        startContent();
    }

    /**
     * Start displaying content for the DiaryActivity
     */
    private void startContent() {

        Log.d(TAG, "Diary Activity commenced");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_diary); // diary item is highligthed

        // create database and instantiate objects
        Connector database = Connector.getInstance(this);

        executeSQL = new Execute(database);
        diaryHandler = new DiaryHandler(database);
        pointsHandler = new PointsHandler(database, username);
        foodContentsHandler = new FoodContentsHandler(database, username);
        diaryLayout = (LinearLayout)findViewById(R.id.diary_entries);

        createClickableAddButtons(); // add button listeners for the 5 regular foods

        dateActivities(); // methods involving the date

        searchActivities(); // methods involving the searchview

        populateRegularFoods(username, false); // populate 5 most regular foods

        // create diary entries (if any) for the corresponding userName
        createDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username);

        if(!diary.isEmpty()) {
            acknowledgeStreak = true;
        }

        checkQuantities();

    }

    private void checkQuantities() {

        HashMap<String, Integer> map = foodContentsHandler.getLastFiveDays(username);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {

            if(entry.getValue() >= 3) {
                // is this food a risk food? i.e. contains over 50% of something?
                List<List<String>> foodContents = foodContentsHandler.createGroupedContents(entry.getKey());
                if(foodContentsHandler.riskFood(foodContents)) {
                    // in what way is it a risk food?
                    String message = "You have been eating a lot of \"" + entry.getKey() + "\"";
                    message = message.concat("\nIt contains: \n");
                    for(int i = 0; i < 7; i++) {
                        if (Double.parseDouble(foodContents.get(i).get(3)) > 50) {
                            message = message.concat("Over 50% of your daily " + foodContents.get(i).get(0) + "\n");
                        }
                    }
                    message = message.concat("\nConsider a healthier option");
                    launchFeedbackActivity(DiaryActivity.this, message, false);
                }
            }
        }

    }


    /**
     * Method invoked which creates the add buttons for the regular foods
     */
    private void createClickableAddButtons() {
        ImageView firstRegAdd = (ImageView)findViewById(R.id.addFirstReg);
        clickToAddOrRemove(firstRegAdd);
        ImageView secondRegAdd = (ImageView)findViewById(R.id.addSecondReg);
        clickToAddOrRemove(secondRegAdd);
        ImageView thirdRegAdd = (ImageView)findViewById(R.id.addThirdReg);
        clickToAddOrRemove(thirdRegAdd);
        ImageView fourthRegAdd = (ImageView)findViewById(R.id.addFourthReg);
        clickToAddOrRemove(fourthRegAdd);
        ImageView fifthRegAdd = (ImageView)findViewById(R.id.addFifthReg);
        clickToAddOrRemove(fifthRegAdd);
    }

    /**
     * Method invokes all the methods involving the date (which appears at the top
     * of the diary activity
     */
    private void dateActivities() {

        // the date text, and left and right arrows in XML file
        dateText = (TextView)findViewById(R.id.date_text);
        ImageView dateLeft = (ImageView)findViewById(R.id.date_left);
        ImageView dateRight = (ImageView)findViewById(R.id.date_right);

        // set the text in the diary to the current date (in the format "DD-MM-YYYY")
        dateText.setText(date.getCurrentDate());

        // change date listener changes the date, next day for dateRight, and previous day
        // for dateLeft
        changeDate(dateLeft);
        changeDate(dateRight);

    }

    /**
     * Method invokes all the methods involving the searchview module (diary_search) in
     * the diary activity.
     */
    private void searchActivities() {

        SearchView searchView = (SearchView)findViewById(R.id.diary_search);

        // if user clicks inside searchview, they will be able to enter their query
        clickSearch(searchView);

        // change the text hint to "Food Search" when the user clicks on the searchview
        searchView.setQueryHint("Food Search");

        // search listener listens for text change and a query submission
        searchListener(searchView);

    }

    //TODO false or true?
    /**
     * QueryTextListener which listens for text change and query submission
     * @param view - the searchview object
     */
    public void searchListener(final SearchView view) {
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // set queryRequest and launch the FoodDatabaseActivity
                queryRequest = query;
                launchActivity(FoodDatabaseActivity.class);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * Allows the user to click anywhere on the searchview, thereby
     * expanding the searchview to accept text input
     * @param view - the searchview object
     */
    public void clickSearch(final SearchView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click, searchView can be clicked anywhere, not just the search icon
            public void onClick(View v) {
                view.setIconified(false);
            }
        });
    }

    /**
     * Update the diary entries
     * @param date - the date in which the diary is updated
     * @param username - the username whose diary is being updated
     * @param row - the id representing the position in the diary
     */
    private void UpdateDiaryEntries(String date, String username, int... row) {

        clearDiaryEntriesTable();
        diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, date, username);

        // if the layout has children, remove these
        if(diaryLayout.getChildCount() > 0) {
            diaryLayout.removeAllViews();
        }

        // if the table "diary" is not empty, then populate the diary
        if(!diary.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < diary.size(); i++) {
                // if a row is specified, then update the diary with drop down open
                if (row.length > 0 && i == row[0]) {
                    populateDiary(diary.get(i).get(1), diary.get(i).get(0), (i), true);
                }
                else {
                    populateDiary(diary.get(i).get(1), diary.get(i).get(0), (i), false);
                }
            }
        }
        else {
            // create no entries layout if the table "diary" is in fact empty
            LinearLayout noEntryWrapper = layoutCreator.createNoEntries("No Entries");
            acknowledgeStreak = false;
            diaryLayout.addView(noEntryWrapper);
        }

    }

    /**
     * Creates the diary entries, if there is not an empty set
     * @param date - the date in which the diary entries are created
     * @param username - the username whose diary is created
     */
    private void createDiaryEntries(String date, String username) {

        clearDiaryEntriesTable();
        diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, date, username);

        if(!diary.get(0).get(0).equals("Empty set")) {
            // create diary entries layout
            LinearLayout diaryEntries = (LinearLayout)findViewById(R.id.no_entry_wrapper);
            diaryEntries.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0));

            // populate the diary
            for(int i = 0; i < diary.size(); i++) {
                populateDiary(diary.get(i).get(1), diary.get(i).get(0), (i), false);
            }
        }
    }

    //TODO somehow make this method shorter
    /**
     * Populates the diary every time an item is added or removed
     * @param quantity - the amount of that particular item in the diary
     * @param foodName - the name of that item in the diary
     * @param row - the position of the item in the diary
     * @param dropDownOpen - boolean referring to whether the drop down is open or not
     */
    private void populateDiary(String quantity, String foodName, int row, boolean dropDownOpen) {

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 ,
                getResources().getDisplayMetrics());

        // create the layouts for the diary
        LinearLayout mainWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout wrapper =  layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout rightWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.WRAP_CONTENT, height, Gravity.NO_GRAVITY, 0, 10, 0, 10);

        LinearLayout leftWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.WRAP_CONTENT, height, Gravity.NO_GRAVITY, 0, 10, 0, 10);

        wrapper.addView(leftWrapper);
        wrapper.addView(rightWrapper);

        mainWrapper.addView(wrapper);

        ImageView arrowUp = viewCreator.createImage(row, R.drawable.ic_keyboard_arrow_up_black,
                25, 40, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, "arrowUp");
        ImageView arrowDown = viewCreator.createImage(row, R.drawable.ic_keyboard_arrow_down_black,
                25, 40, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, "arrowDown");

        leftWrapper.addView(arrowUp);
        leftWrapper.addView(arrowDown);

        TextView food = viewCreator.createText(row, foodName, 220, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL, 0, 18, Color.BLACK, "diaryEntryTag", 0, 0 , 0, 0);

        ImageView minus = viewCreator.createImage(row, R.drawable.ic_remove_circle_black,
                LinearLayout.LayoutParams.MATCH_PARENT, 40,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, "minusTag");
        minus.setColorFilter(getColor(R.color.removeRed));

        clickToAddOrRemove(arrowDown);
        clickToAddOrRemove(arrowUp);
        clickToAddOrRemove(minus);
        clickDropDown(food);

        // add the quantity, cross, food, and minus to the right layout wrapper
        rightWrapper.addView(viewCreator.createText(0, quantity, 20, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL, 0, 18, Color.BLACK, "diaryEntryTag", 0, 0, 0, 0));
        rightWrapper.addView(viewCreator.createText(0, "x", 10, LinearLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL, 0, 18, Color.BLACK, "diaryEntryTag", 10, 0, 10, 0));
        rightWrapper.addView(food);
        rightWrapper.addView(minus);

        if(dropDownOpen) {
            // create the drop down layout if drop down is open
            LinearLayout dropDownWrapper = addDropDownWrapper(row, foodName, food);
            mainWrapper.addView(dropDownWrapper);
        }

        diaryLayout.addView(mainWrapper);

    }

    /**
     * Add the food contents to the drop down wrapper
     * @param row - the id corresponding to where the item is in the diary
     * @param foodName - the food name used to find the food contents
     * @param food - the textview of the food which was clicked on
     * @return linear layout with the nutritional content of food
     */
    public LinearLayout addDropDownWrapper(int row, String foodName, TextView food) {

        LinearLayout dropDownWrapper = showFoodContents(row, foodName);

        // make text scrollable from left to right
        food.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        food.isFocusable();
        food.setSelected(true);
        food.canScrollHorizontally(View.SCROLL_INDICATOR_RIGHT);
        // repeat scroll 3 times
        food.setMarqueeRepeatLimit(3);

        return dropDownWrapper;

    }

    /**
     * Show the food contents when the user opens the drop down
     * @param row - the row corresponding to where the food is in the diary
     * @param foodName - the foodName the user has clicked on
     * @return linear layout containing the food contents
     */
    private LinearLayout showFoodContents(int row, String foodName) {

        // create drop down wrapper and get the grouped contents
        LinearLayout dropDownWrapper = layoutCreator.createDropDownWrapper(row);
        List<List<String>> groupedContents = foodContentsHandler.createGroupedContents(foodName);
        List<LinearLayout> layouts = new ArrayList<>();

        // change the colour depending on the content
        for(int i = 0; i < 7; i++) {
            // if the food item has over 100% of something, change the color to red
            List<Integer> color = new ArrayList<>();
            if(Double.parseDouble(groupedContents.get(i).get(3)) > 100) {
                for(int j = 0; j < 5; j++) {
                    color.add(Color.RED);
                }
            }
            else {
                for (int j = 0; j < 5; j++) {
                    color.add(Color.BLACK);
                }
            }
            layouts.add(layoutCreator.createDropDownLayout(groupedContents.get(i), color));
            dropDownWrapper.addView(layouts.get(i));
        }

        return dropDownWrapper;

    }

    /**
     * Populate the regular foods in the diary
     * @param username - the username of the user whose diary is being populated
     * @param open - boolean representing whether the drop down is open
     * @param row - variable length of rows. If rows is empty, do nothing, if
     *            rows is populated, the first row argument will correspond to the opened row
     */
    private boolean populateRegularFoods(String username, boolean open, int ... row) {

        clearRegularFoodsTable();
        regularFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, username);
        List<TextView> items = getItems();
        List<ImageView> images = getRegularImages();
        List<LinearLayout> layouts = getRegularLayouts();

        int size = regularFoods.size();

        // if not an empty set
        if(!regularFoods.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < regularFoods.size(); i++) {

                // set the visibility of the drop down layout to visible
                items.get(i).setText(regularFoods.get(i).get(3));
                images.get(i).setVisibility(View.VISIBLE);
                clickDropDownRegular(items.get(i), layouts.get(i));

                // create drop down layout and add contents if row is specified
                if(row.length > 0 && i == row[0] && open) {
                    LinearLayout dropDownWrapper = addDropDownWrapper(i, regularFoods.get(i).get(3), items.get(i));
                    layouts.get(i).addView(dropDownWrapper);
                    return true;
                }
                else {
                    // remove the drop down layout if there are no rows specified
                    if(layouts.get(i).getChildCount() > 1) {
                        layouts.get(i).removeViewAt(1);
                        items.get(i).setEllipsize(TextUtils.TruncateAt.END);
                    }
                }
            }

            // make the items invisible or blank if there is less than 5 regular foods
            // that exist
            if(size < 5) {
                for(int i = 4; i >= size; i--) {
                    items.get(i).setText(" ");
                    images.get(i).setVisibility(View.INVISIBLE);
                }
            }
        }
        else {
            // if an empty set, make all the regular foods empty / invisible
            for(int i = 0; i < regularFoods.size(); i++) {
                items.get(i).setText(" ");
                images.get(i).setVisibility(View.INVISIBLE);
            }
        }
        return false;

    }

    /**
     * The linear layouts for the regular foods
     * @return the ArrayList of linear layouts
     */
    private List<LinearLayout> getRegularLayouts() {

        List<LinearLayout> layouts = new ArrayList<>();

        layouts.add((LinearLayout)findViewById(R.id.first_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.second_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.third_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.fourth_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.fifth_reg_layout));

        return layouts;

    }

    /**
     * Gets the "minus" images from the regular foods
     * @return the ArrayList of ImageViews in the regular foods
     */
    private List<ImageView> getRegularImages() {

        List<ImageView> images = new ArrayList<>();

        images.add(((ImageView) findViewById(R.id.addFirstReg)));
        images.add(((ImageView) findViewById(R.id.addSecondReg)));
        images.add(((ImageView) findViewById(R.id.addThirdReg)));
        images.add(((ImageView) findViewById(R.id.addFourthReg)));
        images.add(((ImageView) findViewById(R.id.addFifthReg)));

        return images;

    }

    /**
     * Gets the food items from the regular foods
     * @return the ArrayList of TextViews representing the food items
     */
    private List<TextView> getItems() {

        List<TextView> items = new ArrayList<>();

        items.add(((TextView)findViewById(R.id.firstRegItem)));
        items.add(((TextView)findViewById(R.id.secondRegItem)));
        items.add(((TextView)findViewById(R.id.thirdRegItem)));
        items.add(((TextView)findViewById(R.id.fourthRegItem)));
        items.add(((TextView)findViewById(R.id.fifthRegItem)));

        return items;
    }

    /**
     * Click listener used for when a user clicks on the drop down for the
     * regular foods. If the layout has a child count greater than 1, then it removes
     * the layout.
     * @param text - the
     * @param layout - the layout is passed to the drop down regular
     */
    private void clickDropDownRegular(final TextView text, final LinearLayout layout) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get id of view
                if (v.getId() == R.id.firstRegItem) {
                    if (layout.getChildCount() == 1) populateRegularFoods(username, true, 0);
                    else if (layout.getChildCount() > 1) populateRegularFoods(username, false, 0);
                } else if (v.getId() == R.id.secondRegItem) {
                    if (layout.getChildCount() == 1) populateRegularFoods(username, true, 1);
                    else if (layout.getChildCount() > 1) populateRegularFoods(username, false, 1);
                } else if (v.getId() == R.id.thirdRegItem) {
                    if (layout.getChildCount() == 1) populateRegularFoods(username, true, 2);
                    else if (layout.getChildCount() > 1) populateRegularFoods(username, false, 2);
                } else if (v.getId() == R.id.fourthRegItem) {
                    if (layout.getChildCount() == 1) populateRegularFoods(username, true, 3);
                    else if (layout.getChildCount() > 1) populateRegularFoods(username, false, 3);
                } else {
                    if (layout.getChildCount() == 1) populateRegularFoods(username, true, 4);
                    else if (layout.getChildCount() > 1) populateRegularFoods(username, false, 4);
                }
            }
        });
    }

    /**
     * Click listener for the drop down - when a user clicks on the food in their
     * diary it will prompt this method to see which food was clicked and
     * then set the drop down to open
     * @param text - the textview clicked on
     */
    public void clickDropDown(final TextView text) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getTag() != null && text.getTag().equals("diaryEntryTag") && !isOpen) {
                    UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username, v.getId());
                    isOpen = true;
                }
                else if(text.getTag() != null && text.getTag().equals("diaryEntryTag") && isOpen) {
                    UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username);
                    isOpen = false;
                }
            }
        });
    }

    // TODO points when foods are added
    /**
     * Add food item from the regular foods button
     * @param theDate - the date in which the regular foods was added on
     * @param username - the username whose diary it is
     * @param row - the id of that particular item (position in diary)
     */
    private void addFromRegularFoods(String theDate, String username, int row) {

        // points before are calculated
        String pointsBefore = pointsHandler.getPointsBefore(username);

        clearRegularFoodsTable();
        regularFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, username);

        // insert into the diary based on the regular foods table
        diaryHandler.insertIntoDiary(row, theDate, username, regularFoods, false);

        // update regular foods
        populateRegularFoods(username, false);
        // update diary
        UpdateDiaryEntries(theDate, username);

        int streak = diaryHandler.findLogStreak(date, username);
        if(streak > 0 && !acknowledgeStreak) {
            String feedback = "You have logged foods for " + Integer.toString(streak) +
                    "\n days now. \nWell Done!";
            acknowledgeStreak = true;
            launchFeedbackActivity(DiaryActivity.this, feedback, true);
        }


        // calculate points increase
        pointsHandler.checkForPointsUpdate(pointsBefore, theDate, username, true);

        // reduce points if over a daily amount of something
/*        pointsHandler.pointsReduction(pointsBefore, theDate, username, regularFoods.get(0).get(3));*/

    }

    /**
     * Change the date when the arrows are clicked next to the
     * date text
     * @param image - the arrow image clicked on
     */
    private void changeDate(final ImageView image) {

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getId() == R.id.date_left) {
                    // if pressed down, then change the colour of the image to blue
                    // and change the date
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        String prevDay = date.getPrevDate(dateText.getText().toString());
                        dateText.setText(prevDay);
                        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username);
                    } else {
                        // change the image back to black when the image is released
                        image.setColorFilter(getColor(R.color.black));
                    }
                }
                else if(v.getId() == R.id.date_right) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        String nextDay = date.getNextDate(dateText.getText().toString());
                        dateText.setText(nextDay);
                        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username);
                    }
                    else {
                        image.setColorFilter(getColor(R.color.black));
                    }
                }
                return true;
            }
        });
    }

    // TODO points
    /**
     * Check to see whether inserting will result in food being over the
     * daily allowance, and prompt the user. If they respond yes to the dialog
     * box the food will be added, otherwise it will not be added
     * @param view - the view representing the view clicked on
     */
    private void checkForInsert(View view, int ...row) {

        if(!date.getCurrentDate().equals(dateText.getText().toString())){
            return;
        }

        String foodName;
        if (row.length > 0) {
            foodName = regularFoods.get(row[0]).get(3);
        }
        else {
            foodName = diary.get(view.getId()).get(0);
        }

        String message = generateMessage(foodName);

        if(message.equals("")) {
            if(row.length > 0) {
                addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()),
                        username, row[0]);
            }
            else {
                insert(view);
            }
        }
        else {
            String dialog = "You are about to go over your daily limit of : ";
            dialog = dialog.concat(message);
            if(row.length > 0) {
                openInsertDialog(dialog, view, row[0]);
            }
            else {
                openInsertDialog(dialog, view);
            }
        }

    }

    public String generateMessage(String foodName) {

        // work out the daily total after food contents have been added
        List<HashMap<String, String>> contents =
                foodContentsHandler.findGroupedContentsPlusSum(foodName, username);

        List<Map<String, BigDecimal>> today = foodContentsHandler.findDailyTotal(username);

        String message = "";

        for(int i = 0; i < contents.size(); i++) {
            if(today.get(i).get("intake").floatValue() < 100) {
                if (Double.parseDouble(contents.get(i).get("percentage")) > 100) {
                    message = message.concat(contents.get(i).get("Food group") + "; ");
                }
            }
        }

        return message;



    }

    /**
     * The insertion method which inserts the food item into the diary
     * @param view - the view representing the view clicked on
     */
    private void insert(View view) {

        String pointsBefore = pointsHandler.getPointsBefore(username);

        diaryHandler.insertIntoDiary(view.getId(),
                date.convertDateFormat(dateText.getText().toString()), username, diary, false);
        // update regular foods
        populateRegularFoods(username, false);
        // update diary entries
        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username);

        pointsHandler.checkForPointsUpdate(pointsBefore,
                date.convertDateFormat(dateText.getText().toString()), username, true);

    }

    private void openInsertDialog(String message, final View view, final int ... row) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String foodName, message;
                if(row.length > 0) {
                    foodName = regularFoods.get(row[0]).get(3);
                    message = generateMessage(foodName);
                    addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()),
                            username, row[0]);

                }
                else {
                    foodName = diary.get(view.getId()).get(0);
                    message = generateMessage(foodName);
                    insert(view);
                }
                String feedback = "You have exceeded your daily allowance of : ".concat(message);
                launchFeedbackActivity(DiaryActivity.this, feedback, false);
                // reduce points here
                pointsHandler.decreasePoints(username);

            }
        });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DiaryActivity.this, "Cancelled Insertion", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    // TODO points return issue
    /**
     * Remove the diary entry from the table, update regular foods, diary, and user score
     * @param view - the view that was clicked on
     */
    private void remove(View view) {

        // determine points before update
        String pointsBefore = pointsHandler.getPointsBefore(username);

        List<Map<String, BigDecimal>> totals = foodContentsHandler.findDailyTotal(username);

        diaryHandler.removeFromDiaryEntries(date.convertDateFormat(dateText.getText().toString()),
                username, view.getId());

        // update regular foods
        populateRegularFoods(username, false);
        // update diary entries
        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), username);

        // determine decrease in points
        pointsHandler.checkForPointsUpdate(pointsBefore,
                date.convertDateFormat(dateText.getText().toString()), username, false);

        // return points?
        pointsHandler.pointsReturn(date.convertDateFormat(dateText.getText().toString()),
                username, totals);

    }

    /**
     * Touch listener which listens for when a user clicks to add or remove
     * from the diary
     * @param image - the image that is clicked on
     */
    public void clickToAddOrRemove(final ImageView image) {
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // the minus tag in the diary
                if (v.getTag() != null && v.getTag().equals("minusTag")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        remove(v);
                    }
                }
                // arrow up image in the diary (increases quantity)
                else if(v.getTag() != null && v.getTag().equals("arrowUp")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        checkForInsert(v);
                    }
                }
                // arrow down image in the diary (decreases quantity)
                else if(v.getTag() != null && v.getTag().equals("arrowDown")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        remove(v);
                    }
                }
                // the plus tag in the regular foods
                else {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.d(TAG, "Regular foods item add");
                        actionImageClick(v);
                    }
                }
                return true;
            }
        });
    }

    /**
     * Event listener for the regular foods. If one of the 5 foods is clicked, then it
     * calls addFromRegularFoods depending on which of the items was clicked
     * @param view - the ImageView added to the function, corresponding to one of the
     *             five regular food items
     */
    private void actionImageClick(View view){
        switch (view.getId()) {
            case R.id.addFirstReg:
                checkForInsert(view, 0);
                /*addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), username, 0);*/
                break;
            case R.id.addSecondReg:
                checkForInsert(view, 1);
                //addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), username, 1);
                break;
            case R.id.addThirdReg:
                checkForInsert(view, 2);
               // addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), username, 2);
                break;
            case R.id.addFourthReg:
                checkForInsert(view, 3);
               // addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), username, 3);
                break;
            case R.id.addFifthReg:
                checkForInsert(view, 4);
               // addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), username, 4);
                break;
            default:
                break;
        }
    }

}
