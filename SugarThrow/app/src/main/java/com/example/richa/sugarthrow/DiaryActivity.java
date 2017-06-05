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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
    private Display display = new Display();
    private TimeKeeper date = new TimeKeeper();
    private boolean isOpen = false;
    List<List<String>> foodContents = new ArrayList<List<String>>();

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
        if(db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }

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

        populateRegularFoods("re16621");

        diaryLayout = (LinearLayout)findViewById(R.id.diary_entries);

        createDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621");

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
        System.out.println("DATE IN SQL QUERY " + date);
        diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, date, username);
        display.printTable("Diary Entries", diary);

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

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42 ,
                getResources().getDisplayMetrics());

        LinearLayout mainWrapper = layoutCreator.createBasicLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        LinearLayout wrapper = layoutCreator.createBasicLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT, height, Gravity.NO_GRAVITY, 0, 0, 0, 10);

        mainWrapper.addView(wrapper);

        number = viewCreator.createTextInDiary(quantity, 20, 0, 0, 0, 0, 0);
        cross = viewCreator.createTextInDiary("x", 10, 0, 10, 0, 10, 0);
        food = viewCreator.createTextInDiary(foodName, 250, row);
        minus = viewCreator.createImageInDiary(row, R.drawable.ic_remove_circle_black, "minusTag");

        clickToAddOrRemove(minus);
        clickDropDown(food);

        wrapper.addView(number);
        wrapper.addView(cross);
        wrapper.addView(food);
        wrapper.addView(minus);

        if(dropDownOpen == true) {
            LinearLayout dropDownWrapper = createDropDownWrapper(row, foodName);
            mainWrapper.addView(dropDownWrapper);
        }

        diaryLayout.addView(mainWrapper);

    }

    public List<String> findFoodContents(String foodGroup, String quantity, String measure,
                                         double amount, double sumAmount) {

        List<String> group = new ArrayList<String>();

        group.add(foodGroup);
        group.add(quantity); // quantity
        group.add(measure); // measure is empty as calorie is a measurement


        double num = Double.parseDouble(quantity);
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

        // find the sum for that foodGroup
        List<List<String>> sumOfFood = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SUM_OF_FOODS,
                "re16621", date.convertDateFormat(dateText.getText().toString()));

        List<String> calories = findFoodContents("Calories", foodContents.get(0).get(2), " ", 2000,
                Double.parseDouble(sumOfFood.get(0).get(0)));
        List<String> sugar = findFoodContents("Sugar", foodContents.get(0).get(3), "g", 90,
                Double.parseDouble(sumOfFood.get(0).get(1)));
        List<String> fat = findFoodContents("Fat", foodContents.get(0).get(4), "g", 70,
                Double.parseDouble(sumOfFood.get(0).get(2)));
        List<String> saturates = findFoodContents("Saturates", foodContents.get(0).get(5), "g", 20,
                Double.parseDouble(sumOfFood.get(0).get(3)));
        List<String> carbs = findFoodContents("Carbs", foodContents.get(0).get(6), "g", 260,
                Double.parseDouble(sumOfFood.get(0).get(4)));
        List<String> salt = findFoodContents("Salt", foodContents.get(0).get(7), "g", 6,
                Double.parseDouble(sumOfFood.get(0).get(5)));
        List<String> protein = findFoodContents("Protein", foodContents.get(0).get(8), "g", 50,
                Double.parseDouble(sumOfFood.get(0).get(6)));

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
                System.out.println("THIS ONE IS " + i);
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
    public void populateRegularFoods(String username) {

        clearRegularFoodsTable();
        regularFoods = executeSQL.sqlGetFromQuery(SqlQueries.SQL_REGULAR_FOOD, username);
        List<TextView> items = new ArrayList<TextView>();

        items.add((firstReg = (TextView)findViewById(R.id.firstRegItem)));
        items.add((secondReg = (TextView)findViewById(R.id.secondRegItem)));
        items.add((thirdReg = (TextView)findViewById(R.id.thirdRegItem)));
        items.add((fourthReg = (TextView)findViewById(R.id.fourthRegItem)));
        items.add((fifthReg = (TextView)findViewById(R.id.fifthRegItem)));

        for(int i = 0; i < 5; i++) {
            items.get(i).setText(regularFoods.get(i).get(2));
        }
    }

    public void updatePoints(String userName, String SQL) {

        executeSQL.sqlExecuteSQL(SQL, userName);
    }

    public void insertFromRegularFoods(String date, String foodId, String userId) {

        ContentValues values = new ContentValues();
        values.put("theDate", date);
        values.put("foodId", foodId);
        values.put("userId", userId);
        executeSQL.sqlInsert("Diary", values);
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
        populateRegularFoods(username);

        UpdateDiaryEntries(theDate, username);
        System.out.println("CURRENT DATE " + date.convertDateFormat(date.getCurrentDate()));
        System.out.println("theDate " + theDate);
        if(date.convertDateFormat(date.getCurrentDate()).equals(theDate)) {
            updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);
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
        populateRegularFoods(username);
        String points = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_POINTS, username);
        System.out.println(points);
        if(Integer.parseInt(points) <= 0) {
            updatePoints(username, SqlQueries.SQL_SET_POINTS_0);
        } else {
            updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_1);
        }
        UpdateDiaryEntries(theDate, username);
    }

    // TODO implement functionality for these image clicks
    public void changeDate(final ImageView image) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.date_left:
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag().equals("minusTag")) {
                    removeFromDiaryEntries(date.convertDateFormat(dateText.getText().toString()), "re16621", v.getId());
                } else {
                    actionImageClick(v);
                }
            }
        });
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
