package com.example.richa.sugarthrow;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends MainActivity {

    private Connector database;
    private static String TAG = "ProgressActivityTag";
    private Execute executeSql;
    private TableDisplay display = new TableDisplay();
    private TimeKeeper date = new TimeKeeper();
    private String[] names = {"Sugar", "Calories", "Fat", "Saturates", "Carbs", "Salt", "Protein"};
    private Integer[] ids = {R.id.pie_sugar, R.id.pie_calories, R.id.pie_fat, R.id.pie_saturates,
    R.id.pie_carbs, R.id.pie_salt, R.id.pie_protein};
    private TextView points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        createDrawer(toolbar);
        createNavigationView(R.id.nav_progress);

        this.database = MainActivity.getDatabaseConnection();
        SQLiteDatabase db = database.getWritableDatabase();
        if(db.isOpen()) {
            Toast.makeText(this, "Database is open", Toast.LENGTH_SHORT).show();
        }

        executeSql = new Execute(database);

        lineChartCreator("re16621");
        pieChartCreator("re16621");

        points = (TextView)findViewById(R.id.progress_points);
        List<List<String>> userPoints = executeSql.sqlGetFromQuery(SqlQueries.SQL_POINTS, "re16621");
        display.printTable("Points", userPoints);
        points.setText(userPoints.get(0).get(0));

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

        List<List<BigDecimal>> listOfPercentages = new ArrayList<>();
        List<String> foodGroups = new ArrayList<>();
        for(String name : names) {
            foodGroups.add(name);
        }

        List<Integer> foodIds = new ArrayList<>();
        for(Integer id : ids) {
            foodIds.add(id);
        }

        List<BigDecimal> sugarPercentage = findFoodPercentages(sumOfFoods.get(0).get(0), 0);
        listOfPercentages.add(sugarPercentage);
        createPieChart(foodIds.get(0), foodGroups.get(0), listOfPercentages.get(0).get(0).floatValue(),
                listOfPercentages.get(0).get(1).floatValue(), 24);

        for(int i = 1; i < sumOfFoods.get(0).size(); i++) {
            List<BigDecimal> percentages = findFoodPercentages(sumOfFoods.get(0).get(i), i);
            listOfPercentages.add(percentages);
            createPieChart(foodIds.get(i), foodGroups.get(i), listOfPercentages.get(i).get(0).floatValue(),
                    listOfPercentages.get(i).get(1).floatValue(), 16);
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

    private List<BigDecimal> findFoodPercentages(String itemTotal, int col) {

        List<BigDecimal> bdList = new ArrayList<BigDecimal>();
        Float quantity = findQuantity(col);

        Float intake = (Float.parseFloat(itemTotal) / quantity) * 100;
        Float amountLeft = 100 - intake;

        if(amountLeft < 0) {
            amountLeft = 0.0f;
        }

        bdList = addBigDecimalArrayList(intake.toString(), amountLeft.toString());

        return bdList;

    }

    private Float findQuantity(int col) {

        if(col == 0) return 90.0f;
        if(col == 1) return 2000.0f;
        if(col == 2) return 70.0f;
        if(col == 3) return 20.0f;
        if(col == 4) return 260.0f;
        if(col == 5) return 6.0f;
        else return 50.0f;

    }

    private List<BigDecimal> addBigDecimalArrayList(String intake, String amountLeft) {
        List<BigDecimal> bdList = new ArrayList<BigDecimal>();

        BigDecimal bdIntake = new BigDecimal(intake);
        bdIntake = bdIntake.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        BigDecimal bdLeft = new BigDecimal(amountLeft);
        bdLeft = bdLeft.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        bdList.add(bdIntake);
        bdList.add(bdLeft);

        return bdList;
    }

    private void createPieChartVariables(PieChart pieChart, final String name, String[] content,
                                         Integer[] colorIntegers, Float[] percentage, int textSize) {
        final List<String> xEntries = new ArrayList<String>();
        final List<Float> dataset = new ArrayList<Float>();
        List<Integer> colors = new ArrayList<Integer>();

        for(int i = 0; i < content.length; i++) {
            xEntries.add(content[i]);
        }

        for(int i = 0; i < colorIntegers.length; i++) {
            colors.add(colorIntegers[i]);
        }

        for(int i = 0; i < percentage.length; i++) {
            dataset.add(percentage[i]);
        }

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
        ArrayList<PieEntry> yEntries = new ArrayList<PieEntry>();

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
