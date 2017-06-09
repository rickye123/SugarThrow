package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SearchView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.*;

public class DiaryActivity extends MainActivity {

    private ImageView searchIcon, firstRegAdd, secondRegAdd, thirdRegAdd, fourthRegAdd,
    fifthRegAdd, minus, dateLeft, dateRight;
    private TextView number, food, cross;
    private List<List<String>> regularFoods = new ArrayList<List<String>>();
    private List<List<String>> diary = new ArrayList<List<String>>();
    private TextView dateText, firstReg, secondReg, thirdReg, fourthReg, fifthReg;
    private SearchView searchView;
    private Connector database;
    private ViewCreator viewCreator = new ViewCreator(this);
    private LayoutCreator layoutCreator = new LayoutCreator(this, viewCreator);
    private LinearLayout diaryLayout;
    private Execute executeSQL;
    private TableDisplay display = new TableDisplay();
    private TimeKeeper date = new TimeKeeper();
    private boolean isOpen = false;
    String[] places = {"First", "Second", "Third", "Fourth", "Fifth"};
    String[] placesLower = {"first", "second", "third", "fourth", "fifth"};
    List<List<String>> foodContents = new ArrayList<List<String>>();
    private static String queryRequest;

    public void clearRegularFoodsTable() {
        while (!regularFoods.isEmpty()) {
            int size = regularFoods.size();
            int i = 0;
            while (i < size) {
                regularFoods.remove(0);
                i++;
            }
        }
    }

    public static String getRequest() {
        return queryRequest;
    }

    public void clearTableContents(List<List<String>> table) {
        while (!table.isEmpty()) {
            int size = table.size();
            int i = 0;
            while (i < size) {
                table.remove(0);
                i++;
            }
        }
    }

    public void clearList(List<String> table) {
        while(!table.isEmpty()) {
            table.remove(0);
        }
    }

    public void clearDiaryEntriesTable() {
        while (!diary.isEmpty()) {
            int size = diary.size();
            int i = 0;
            while (i < size) {
                diary.remove(0);
                i++;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_activity);
        startContent();

    }

    public void startContent() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        createDrawer(toolbar);
        createNavigationView(R.id.nav_diary);

        this.database = MainActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
/*        if(db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }*/

        createClickableAddButtons();

        dateText = (TextView)findViewById(R.id.date_text);
        dateText.setText(date.getCurrentDate());

        dateLeft = (ImageView)findViewById(R.id.date_left);
        changeDate(dateLeft);
        dateRight = (ImageView)findViewById(R.id.date_right);
        changeDate(dateRight);

        executeSQL = new Execute(database);

        searchView = (SearchView)findViewById(R.id.diary_search);
        searchView.setQueryHint("Food Search");
        clickSearch(searchView);
        // perform set on query text listener event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryRequest = query;
                launchActivity(FoodDatabaseActivity.class);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        populateRegularFoods("re16621", false);

        diaryLayout = (LinearLayout)findViewById(R.id.diary_entries);

        createDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");

    }

    public void clickSearch(final SearchView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setIconified(false);
            }
        });
    }



    private void createClickableAddButtons() {
        firstRegAdd = (ImageView)findViewById(R.id.addFirstReg);
        clickToAddOrRemove(firstRegAdd);
        secondRegAdd = (ImageView)findViewById(R.id.addSecondReg);
        clickToAddOrRemove(secondRegAdd);
        thirdRegAdd = (ImageView)findViewById(R.id.addThirdReg);
        clickToAddOrRemove(thirdRegAdd);
        fourthRegAdd = (ImageView)findViewById(R.id.addFourthReg);
        clickToAddOrRemove(fourthRegAdd);
        fifthRegAdd = (ImageView)findViewById(R.id.addFifthReg);
        clickToAddOrRemove(fifthRegAdd);
    }

    private void UpdateDiaryEntries(String date, String username, int... row) {
        clearDiaryEntriesTable();
        diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, date, username);
        display.printTable("Diary Entries", diary);

        if(diaryLayout.getChildCount() > 0) {
            diaryLayout.removeAllViews();
        }
        if(!diary.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < diary.size(); i++) {
                if (row.length > 0 && i == row[0]) {
                    populateDiary(diary.get(i).get(1), diary.get(i).get(0), (i), true);
                }
                else {
                    populateDiary(diary.get(i).get(1), diary.get(i).get(0), (i), false);
                }
            }
        }
        else {
            LinearLayout noEntryWrapper = layoutCreator.createNoEntries();
            diaryLayout.addView(noEntryWrapper);
        }

    }

    private void createDiaryEntries(String date, String username) {

        clearDiaryEntriesTable();
        diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, date, username);

        if(!diary.get(0).get(0).equals("Empty set")) {
            LinearLayout diaryEntries = (LinearLayout)findViewById(R.id.no_entry_wrapper);
            diaryEntries.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0));
            for(int i = 0; i < diary.size(); i++) {
                populateDiary(diary.get(i).get(1), diary.get(i).get(0), (i), false);
            }
        }
    }


    public void populateDiary(String quantity, String foodName, int row, boolean dropDownOpen) {

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 ,
                getResources().getDisplayMetrics());

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20 ,
                getResources().getDisplayMetrics());

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

        number = viewCreator.createTextInDiary(quantity, 20, 0, "diaryEntryTag", 0, 0, 0, 0);
        cross = viewCreator.createTextInDiary("x", 10, 0, "diaryEntryTag", 10, 0, 10, 0);
        food = viewCreator.createTextInDiary(foodName, 220, row, "diaryEntryTag");
        food.setSingleLine(true);
        food.setEllipsize(TextUtils.TruncateAt.END);
        minus = viewCreator.createImageInDiary(row, R.drawable.ic_remove_circle_black, "minusTag");
        minus.setColorFilter(getColor(R.color.removeRed));

        clickToAddOrRemove(arrowDown);
        clickToAddOrRemove(arrowUp);
        clickToAddOrRemove(minus);
        clickDropDown(food);


        rightWrapper.addView(number);
        rightWrapper.addView(cross);
        rightWrapper.addView(food);
        rightWrapper.addView(minus);

        if(dropDownOpen) {
            LinearLayout dropDownWrapper = createDropDownWrapper(row, foodName);
            mainWrapper.addView(dropDownWrapper);
            food.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            food.setMarqueeRepeatLimit(3);
            food.isFocusable();
            food.setSelected(true);
            food.canScrollHorizontally(View.SCROLL_INDICATOR_RIGHT);
        }

        diaryLayout.addView(mainWrapper);

    }

    public List<String> findFoodContents(String foodGroup, String quantity, String measure,
                                         double amount) {

        List<String> group = new ArrayList<String>();
        double num;

        group.add(foodGroup);
        group.add(quantity); // quantity
        group.add(measure); // measure is empty as calorie is a measurement

        if(!quantity.equals("null")) {
            num = Double.parseDouble(quantity);
        }
        else {
            num = 0;
        }
        double percentage = ((num) / amount) * 100;
        String s = String.format("%.2f", percentage);
        group.add(s);

        return group;
    }

    public List<String> findContentsPlusSumAmount(String foodGroup, String quantity, String measure,
                                         double amount, double sumAmount) {

        List<String> group = new ArrayList<String>();

        // find the sum for that foodGroup
        List<List<String>> sumOfFood = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SUM_OF_FOODS,
                "re16621", date.convertDateFormat(dateText.getText().toString()));

        double num;

        group.add(foodGroup);
        group.add(quantity); // quantity
        group.add(measure); // measure is empty as calorie is a measurement

        if(!quantity.equals("null")) {
            num = Double.parseDouble(quantity);
        }
        else {
            num = 0;
        }
        double percentage = ((sumAmount + num) / amount) * 100;
        String s = String.format("%.2f", percentage);
        Log.d("PERCENTAGE", s);
        group.add(s);

        return group;
    }

    public List<List<String>> createGroupedContents(String foodName) {

        // use the foodname to find the contents of the food
        clearTableContents(foodContents);
        foodContents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_FOOD, foodName);

        List<String> calories = findFoodContents("Calories", foodContents.get(0).get(2), " ", 2000);
        List<String> sugar = findFoodContents("Sugar", foodContents.get(0).get(3), "g", 90);
        List<String> fat = findFoodContents("Fat", foodContents.get(0).get(4), "g", 70);
        List<String> saturates = findFoodContents("Saturates", foodContents.get(0).get(5), "g", 20);
        List<String> carbs = findFoodContents("Carbs", foodContents.get(0).get(6), "g", 260);
        List<String> salt = findFoodContents("Salt", foodContents.get(0).get(7), "g", 6);
        List<String> protein = findFoodContents("Protein", foodContents.get(0).get(8), "g", 50);

        List<List<String>> groupedContents = new ArrayList<List<String>>();
        groupedContents.add(calories);
        groupedContents.add(sugar);
        groupedContents.add(fat);
        groupedContents.add(saturates);
        groupedContents.add(carbs);
        groupedContents.add(salt);
        groupedContents.add(protein);

        return groupedContents;

    }

    public LinearLayout createDropDownWrapper(int row, String foodName) {

        LinearLayout dropDownWrapper = layoutCreator.createDropDownWrapper(row, foodName);
        List<List<String>> groupedContents = createGroupedContents(foodName);
        List<LinearLayout> layouts = new ArrayList<LinearLayout>();

        for(int i = 0; i < 7; i++) {
            List<Integer> color = new ArrayList<Integer>();
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

    public List<LinearLayout> createLayoutList(List<List<String>> group, List<Integer> color) {
        List<LinearLayout> layouts = new ArrayList<LinearLayout>();

        for (int i = 0; i < 7; i++) {
            layouts.add(layoutCreator.createDropDownLayout(group.get(i), color));
        }

        return layouts;

    }

    /**
     *
     * @param username
     */
    public boolean populateRegularFoods(String username, boolean open, int ... row) {

        clearRegularFoodsTable();
        regularFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, username);
        List<TextView> items = new ArrayList<TextView>();
        List<ImageView> images = getRegularImages();
        List<LinearLayout> layouts = getRegularLayouts();

        items.add((firstReg = (TextView)findViewById(R.id.firstRegItem)));
        items.add((secondReg = (TextView)findViewById(R.id.secondRegItem)));
        items.add((thirdReg = (TextView)findViewById(R.id.thirdRegItem)));
        items.add((fourthReg = (TextView)findViewById(R.id.fourthRegItem)));
        items.add((fifthReg = (TextView)findViewById(R.id.fifthRegItem)));

        int size =  regularFoods.size();
        int sizeLeft =  5 - size;

        if(!regularFoods.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < regularFoods.size(); i++) {
                items.get(i).setText(regularFoods.get(i).get(2));
                images.get(i).setVisibility(View.VISIBLE);
                clickDropDownRegular(items.get(i), layouts.get(i));
                if(row.length > 0 && i == row[0] && open) {
                    LinearLayout dropDownWrapper = createDropDownWrapper(i, regularFoods.get(i).get(2));
                    layouts.get(i).addView(dropDownWrapper);
                    items.get(i).setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    items.get(i).setMarqueeRepeatLimit(3);
                    items.get(i).isFocusable();
                    items.get(i).setSelected(true);
                    items.get(i).canScrollHorizontally(View.SCROLL_INDICATOR_RIGHT);
                    return true;
                }
                else {
                    if(layouts.get(i).getChildCount() > 1) {
                        layouts.get(i).removeViewAt(1);
                        items.get(i).setEllipsize(TextUtils.TruncateAt.END);
                    }
                }
            }
            if(size < 5) {
                for(int i = 4; i >= size; i--) {
                    items.get(i).setText(" ");
                    images.get(i).setVisibility(View.INVISIBLE);
                }
            }


        }
        else {
            for(int i = 0; i < regularFoods.size(); i++) {
                items.get(i).setText(" ");
                images.get(i).setVisibility(View.INVISIBLE);
            }
        }
        return false;

    }

    public List<LinearLayout> getRegularLayouts() {

        List<LinearLayout> layouts = new ArrayList<>();

        layouts.add((LinearLayout)findViewById(R.id.first_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.second_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.third_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.fourth_reg_layout));
        layouts.add((LinearLayout)findViewById(R.id.fifth_reg_layout));

        return layouts;

    }

    public List<ImageView> getRegularImages() {

        List<ImageView> images = new ArrayList<>();

        ImageView firstImage = (ImageView)findViewById(R.id.addFirstReg);

        images.add(((ImageView) findViewById(R.id.addFirstReg)));
        images.add(((ImageView) findViewById(R.id.addSecondReg)));
        images.add(((ImageView) findViewById(R.id.addThirdReg)));
        images.add(((ImageView) findViewById(R.id.addFourthReg)));
        images.add(((ImageView) findViewById(R.id.addFifthReg)));

        return images;

    }



    public void updatePoints(String userName, String SQL) {

        executeSQL.sqlExecuteSQL(SQL, userName);
    }

    public ContentValues findDiaryValues(String date, String foodId, String userId) {

        ContentValues values = new ContentValues();
        values.put("theDate", date);
        values.put("foodId", foodId);
        values.put("userId", userId);

        return values;
    }

    public void insertFromRegularFoods(String date, String foodId, String userId) {

        ContentValues values = findDiaryValues(date, foodId, userId);
        executeSQL.sqlInsert("Diary", values);
    }

    public void clickDropDownRegular(final TextView text, final LinearLayout layout) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.firstRegItem) {
                    if(layout.getChildCount() == 1) populateRegularFoods("re16621", true, 0);
                    else if (layout.getChildCount() > 1) populateRegularFoods("re16621", false, 0);
                }
                else if(v.getId() == R.id.secondRegItem) {
                    if(layout.getChildCount() == 1) populateRegularFoods("re16621", true, 1);
                    else if (layout.getChildCount() > 1) populateRegularFoods("re16621", false, 1);
                }
                else if(v.getId() == R.id.thirdRegItem) {
                    if(layout.getChildCount() == 1) populateRegularFoods("re16621", true, 2);
                    else if (layout.getChildCount() > 1) populateRegularFoods("re16621", false, 2);
                }
                else if(v.getId() == R.id.fourthRegItem) {
                    if(layout.getChildCount() == 1) populateRegularFoods("re16621", true, 3);
                    else if (layout.getChildCount() > 1) populateRegularFoods("re16621", false, 3);
                }
                else {
                    if(layout.getChildCount() == 1) populateRegularFoods("re16621", true, 4);
                    else if (layout.getChildCount() > 1) populateRegularFoods("re16621", false, 4);
                }
            }
        });
    }

    public void setVisibilityOnClick(LinearLayout layout) {
        if(layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
        }
        else {
            layout.setVisibility(View.GONE);
        }
    }


    public void clickDropDown(final TextView text) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getTag() != null && text.getTag().equals("diaryEntryTag") && isOpen == false) {
                    UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621", v.getId());
                    isOpen = true;
                }
                else if(text.getTag() != null && text.getTag().equals("diaryEntryTag") && isOpen == true) {
                    UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");
                    isOpen = false;
                }
                else {

                }
            }
        });
    }

    public void addFromRegularFoods(String theDate, String username, int row) {

        clearRegularFoodsTable();
        regularFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, username);

        String foodId = regularFoods.get(row).get(3); // this is the first foodId

        List<List<String>> userName = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_USER,
                username);

        insertFromRegularFoods(theDate, foodId, userName.get(0).get(0));
        populateRegularFoods(username, false);

        UpdateDiaryEntries(theDate, username);

        if(date.convertDateFormat(date.getCurrentDate()).equals(theDate)) {
            updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);
            addStreakPoints(username);
        }

    }

    public void addStreakPoints(String username) {
        String points = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK, date.convertDateFormat(date.getCurrentDate()), username);
        if(Integer.parseInt(points) % 10 == 0) {
            updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_10);
        }
    }


    public void removeFromDiaryEntries(String theDate, String username, int id) {

        clearDiaryEntriesTable();
        diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, theDate, username);

        String foodName = diary.get(id).get(0);
        List<List<String>> diaryEntries = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ENTRY,
                theDate, username, foodName);
        String diaryId = diaryEntries.get(0).get(0);

        executeSQL.sqlDelete("Diary", "diaryId = ?", diaryId);
        populateRegularFoods(username, false);
        String points = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_POINTS, username);

        if(Integer.parseInt(points) <= 0) {
            updatePoints(username, SqlQueries.SQL_SET_POINTS_0);
        } else {
            if(Integer.parseInt(points) % 10 == 0) {
                updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_10);
            }
            updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_1);
        }
        UpdateDiaryEntries(theDate, username);
    }

    // TODO implement functionality for these image clicks
    public void changeDate(final ImageView image) {

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(v.getId()) {
                    case R.id.date_left:
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            image.setColorFilter(getColor(R.color.cyan));
                            String prevDay = date.getPrevDate(dateText.getText().toString());
                            dateText.setText(prevDay);
                            UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");
                        }
                        else {
                            image.setColorFilter(getColor(R.color.black));
                        }
                        break;
                    case R.id.date_right:
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            image.setColorFilter(getColor(R.color.cyan));
                            String nextDay = date.getNextDate(dateText.getText().toString());
                            dateText.setText(nextDay);
                            UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");
                        }
                        else {
                            image.setColorFilter(getColor(R.color.black));
                        }
                        break;
                    default:
                        Log.d("CLICK", "nothing clicked");
                        break;
                }
                return true;
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.date_left:
                        image.setImageResource(R.drawable.chevron_list);
                        String prevDay = date.getPrevDate(dateText.getText().toString());
                        dateText.setText(prevDay);
                        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");

                        break;
                    case R.id.date_right:
                        String nextDay = date.getNextDate(dateText.getText().toString());
                        dateText.setText(nextDay);
                        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");
                        break;
                    default:
                        Log.d("CLICK", "nothing clicked");
                        break;
                }
            }
        });
    }

    public void clickToAddOrRemove(final ImageView image) {
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getTag() != null && v.getTag().equals("minusTag")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        removeFromDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621", v.getId());
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        image.setColorFilter(getColor(R.color.removeRed));
                    }
                }
                else if(v.getTag() != null && v.getTag().equals("arrowUp")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        System.out.println("DATE SHOWING IS " + date.convertDateFormat(dateText.getText().toString()));
                        insertIntoDiaryFromDiary(v.getId(),
                                date.convertDateFormat(dateText.getText().toString()), "re16621");
                        UpdateDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        image.setColorFilter(getColor(R.color.black));
                    }
                }
                else if(v.getTag() != null && v.getTag().equals("arrowDown")) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        removeFromDiaryEntries(date.convertDateFormat(dateText.getText().toString()),
                                "re16621", v.getId());
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        image.setColorFilter(getColor(R.color.black));
                    }
                }
                else {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        image.setColorFilter(getColor(R.color.cyan));
                        actionImageClick(v);
                    }
                    else {
                        image.setColorFilter(getColor(R.color.correctGreen));
                        image.setImageResource(R.drawable.ic_add_circle_black);
                    }
                }
                return true;
            }
        });
    }

    public void insertIntoDiaryFromDiary(int id, String theDate, String username) {

        List<List<String>> userName = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_USER,
                username);

        List<List<String>> diaryContents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY,
                theDate, username);

        display.printTable("Diary contents", diaryContents);

        String userId = userName.get(0).get(0);
        String foodId = diaryContents.get(id).get(2);

        ContentValues values = findDiaryValues(theDate, foodId, userId);

        executeSQL.sqlInsert("Diary", values);
        populateRegularFoods(username, false);
        updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);

        addStreakPoints(username);

    }

    private void actionImageClick(View v){
        switch (v.getId()) {
            case R.id.addFirstReg:
                addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), "re16621", 0);
                break;
            case R.id.addSecondReg:
                addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), "re16621", 1);
                break;
            case R.id.addThirdReg:
                addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), "re16621", 2);
                break;
            case R.id.addFourthReg:
                addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), "re16621", 3);
                break;
            case R.id.addFifthReg:
                addFromRegularFoods(date.convertDateFormat(dateText.getText().toString()), "re16621", 4);
                break;
            default:
                // the user's action was not recognized.
                Log.d("CLICK", "nothin clicked");
                break;
        }
    }

}
