package com.example.richa.sugarthrow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgressActivity extends MainActivity {

    //private static String TAG = "ProgressActivityTag";
    private Execute executeSql;
    private TableDisplay display = new TableDisplay();
    private TimeKeeper date = new TimeKeeper();
    private String[] names = {"Sugar", "Calories", "Fat", "Saturates", "Carbs", "Salt", "Protein"};
    private Integer[] ids = {R.id.pie_sugar, R.id.pie_calories, R.id.pie_fat, R.id.pie_saturates,
    R.id.pie_carbs, R.id.pie_salt, R.id.pie_protein};
    private TextView sugar, calories, salt, fat, carbs, protein, saturates;
    private FoodContentsHandler foodContentsHandler;
    private String username;

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

        setContentView(R.layout.progress_activity);
        setNavigationUsername(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createDrawer(toolbar);
        createNavigationView(R.id.nav_progress);

        Connector database = LoginActivity.getDatabaseConnection();
        executeSql = new Execute(database);
        foodContentsHandler = new FoodContentsHandler(database, username);

        getDailyAmounts();
        setDailyAmounts();

        lineChartCreator(username);
        pieChartCreator(username);

        setPoints();

    }

    private void setPoints() {

        TextView points = (TextView)findViewById(R.id.progress_points);
        List<List<String>> userPoints = executeSql.sqlGetFromQuery(SqlQueries.SQL_POINTS, username);
        display.printTable("Points", userPoints);
        points.setText(userPoints.get(0).get(0));


    }

    private void getDailyAmounts() {

        sugar = (TextView)findViewById(R.id.progress_sugar_amount);
        calories = (TextView)findViewById(R.id.progress_calories_amount);
        salt = (TextView)findViewById(R.id.progress_salt_amount);
        fat = (TextView)findViewById(R.id.progress_fat_amount);
        carbs = (TextView)findViewById(R.id.progress_carbs_amount);
        protein = (TextView)findViewById(R.id.progress_protein_amount);
        saturates = (TextView)findViewById(R.id.progress_saturates_amount);

    }

    private void setDailyAmounts() {

        List<Float> quantities = foodContentsHandler.getQuantitiesList();

        sugar.setText(String.format(Locale.ENGLISH, "%.2f g", quantities.get(0)));
        calories.setText(String.format(Locale.ENGLISH, "%.2f kcal", quantities.get(1)));
        saturates.setText(String.format(Locale.ENGLISH, "%.2f g", quantities.get(2)));
        fat.setText(String.format(Locale.ENGLISH, "%.2f g", quantities.get(3)));
        carbs.setText(String.format(Locale.ENGLISH, "%.2f g", quantities.get(4)));
        protein.setText(String.format(Locale.ENGLISH, "%.2f g", quantities.get(5)));
        salt.setText(String.format(Locale.ENGLISH, "%.2f g", quantities.get(6)));

    }

    private String[] findPreviousFiveDays() {

        String prevDay = date.getCurrentDate();
        String[] days = new String[5];
        days[0] = prevDay;

        for(int i = 1; i < 5; i++) {
            String day = date.getPrevDate(prevDay);
            days[i] = day;
            prevDay = day;
        }

        reverseArray(days);

        return days;
    }

    private void reverseArray(String inputArray[]) {
        String temp;

        for (int i = 0; i < inputArray.length/2; i++)
        {
            temp = inputArray[i];
            inputArray[i] = inputArray[inputArray.length-1-i];
            inputArray[inputArray.length-1-i] = temp;
        }

    }

    private void lineChartCreator(String userName) {

        String[] days = findPreviousFiveDays();
        List<Entry> yValues = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            String dayAmount = executeSql.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK, date.convertDateFormat(days[i]), userName);
            yValues.add(new Entry(i, Float.parseFloat(dayAmount)));
        }

        LineChart lineChart = (LineChart)findViewById(R.id.line_chart);
        LineDataSet set1 = new LineDataSet(yValues, "Streak");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.setDescription(" ");
    }

    private void pieChartCreator(String userName) {

        List<List<String>> sumOfFoods = executeSql.sqlGetFromQuery(SqlQueries.SQL_SELECT_CURRENT_DIARY, userName);
        int size = sumOfFoods.get(0).size();

        for(int i = 0; i < size; i++) {
            if (sumOfFoods.get(0).get(i) == (null)) {
                sumOfFoods.get(0).remove(i);
                sumOfFoods.get(0).add(i, "0");
            }
        }

        List<Map<String, BigDecimal>> listOfPercentages = new ArrayList<>();
        List<String> foodGroups = new ArrayList<>();
        Collections.addAll(foodGroups, names);

        List<Integer> foodIds = new ArrayList<>();
        Collections.addAll(foodIds, ids);

        Map<String, BigDecimal> sugarPercentage = foodContentsHandler.findFoodPercentages(sumOfFoods.get(0).get(0), 0);
        listOfPercentages.add(sugarPercentage);
        createPieChart(foodIds.get(0), foodGroups.get(0), listOfPercentages.get(0).get("intake").floatValue(),
                listOfPercentages.get(0).get("amountLeft").floatValue(), 24);

        for(int i = 1; i < sumOfFoods.get(0).size(); i++) {
            Map<String, BigDecimal> percentages = foodContentsHandler.findFoodPercentages(sumOfFoods.get(0).get(i), i);
            listOfPercentages.add(percentages);
            createPieChart(foodIds.get(i), foodGroups.get(i), listOfPercentages.get(i).get("intake").floatValue(),
                    listOfPercentages.get(i).get("amountLeft").floatValue(), 16);
        }

    }

    private void createPieChart(int id, String name, float intake, float amountLeft, int textSize) {
        PieChart pieChart = (PieChart)findViewById(id);
        createPieChartVariables(pieChart, name,
                new String[] {"Daily amount", "Amount Left"},
                new Integer[] {Color.GREEN, Color.CYAN},
                new Float[] {intake, amountLeft},
                textSize);
    }

    private void createPieChartVariables(PieChart pieChart, final String name, String[] content,
                                         Integer[] colorIntegers, Float[] percentage, int textSize) {
        final List<String> xEntries = new ArrayList<>();
        final List<Float> dataset = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        Collections.addAll(xEntries, content);
        Collections.addAll(colors, colorIntegers);
        Collections.addAll(dataset, percentage);

        addPieChart(pieChart, name, xEntries, dataset, colors, textSize);
        pieChartListener(pieChart, dataset, xEntries, name);

    }

    private void pieChartListener(PieChart pieChart, final List<Float> dataset,
                                  final List<String> xEntries,
                                  final String name) {
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("(sum): ");
                String percent = e.toString().substring(pos1 + 7);

                for(int i = 0; i < dataset.size(); i++) {
                    if(dataset.get(i) == Float.parseFloat(percent)) {
                        pos1 = i;
                        break;
                    }
                }
                String amount = xEntries.get(pos1);
                Toast.makeText(ProgressActivity.this, name + ": " + amount + "\n"
                        + "Percentage: " + percent + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // do nothing
            }
        });
    }

    private void addPieChart(PieChart pieChart, String name, List<String> xEntries,
                             List<Float> dataset, List<Integer> colors, int textSize) {

        // create pie entries from the dataset
        ArrayList<PieEntry> yEntries = new ArrayList<>();

        if(dataset.get(0) > 100) {
            dataset.remove(1);
            colors.remove(1);
            colors.remove(0);
            xEntries.remove(1);
            colors.add(Color.RED);
        }
        else if(dataset.get(0) == 0) {
            dataset.remove(0);
            colors.remove(0);
            xEntries.remove(0);
        }

        for (int i = 0; i < dataset.size(); i++) {
            yEntries.add(new PieEntry(dataset.get(i), i));
        }

        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDescription(" ");
        pieChart.setRotationEnabled(false);

        PieDataSet pieDataset = new PieDataSet(yEntries, name);
        pieDataset.setSliceSpace(2);
        pieDataset.setValueTextSize(textSize);
        pieDataset.setValueTextColor(Color.WHITE);

        pieDataset.setColors(colors);

        // create pie data object
        PieData pieData = new PieData(pieDataset);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

}
