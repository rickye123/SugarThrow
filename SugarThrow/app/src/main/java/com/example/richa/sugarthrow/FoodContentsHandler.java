package com.example.richa.sugarthrow;

/*
This class is handles all the methods that involve the nutritional information
of food, such as the nutritional information that appears when a user
clicks on a food item to see its contents.
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodContentsHandler extends MainActivity {

    private Execute executeSQL;

    /**
     * Constructor which is passed an initialised database (Connector object)
     * so that SQL statements can be executed
     * @param database - the initialised database
     */
    public FoodContentsHandler(Connector database) {
        executeSQL = new Execute(database);
    }

    /**
     * Find the amount of food left based on the intake
     * @param intake - floating point number corresponding to the amount
     *               of a particular food the user has had
     * @return amountLeft (float) - the amount of food left based on intake
     */
    public Float findAmountLeft(Float intake) {

        // amountLeft is 100% - percentage intake
        Float amountLeft = 100 - intake;

        // if amountLeft is less than 0, make the percentage equal to 0
        if(amountLeft < 0) {
            amountLeft = 0.0f;
        }

        return amountLeft;
    }

    /**
     * Find the percentage of daily intake for a particular food the
     * user has had
     * @param itemTotal - the itemTotal returned from the SQL query
     * @param quantity - the daily quantity the user is allowed
     * @return intake - the percentage intake the user has had
     */
    public Float findIntake(String itemTotal, Float quantity) {

        Float intake;

        // if the user has had no items that day, then the result will
        // return null. Hence, if null, the itemTotal is 0
        if(itemTotal == null) {
            intake = (0 / quantity) * 100;
        }
        else {
            // percentage intake is total divided by quantity * 100
            intake = (Float.parseFloat(itemTotal) / quantity) * 100;
        }

        return intake;

    }

    /**
     * Find the percentage intake and the amount left for a particular food
     * @param itemTotal - the String returned from SQL corresponding to the amount
     *                  of a particular food group is in the food
     * @param col - the column refers to the position the item is in the SQL result, e.g.
     *            the first column is sugar, second is calories, etc. (see findQuantity)
     * @return hashmap corresponding to the intake and amountLeft
     * of a particular food group
     */
    public Map<String, BigDecimal> findFoodPercentages(String itemTotal, int col) {

        Float quantity = findQuantity(col);
        Float intake = findIntake(itemTotal, quantity);
        Float amountLeft = findAmountLeft(intake);

        return addBigDecimalArrayList(intake.toString(), amountLeft.toString());

    }

    /**
     * Create the hashamp corresponding to the intake and amountLeft of a particular food
     * @param intake - the percentage intake of a food as a string
     * @param amountLeft - the daily amount left (as a percentage) as a string
     * @return the hashmap corresponding to the daily amount left
     * and the percentage intake
     */
    private Map<String, BigDecimal> addBigDecimalArrayList(String intake, String amountLeft) {
        Map<String, BigDecimal> bdList = new HashMap<>();

        // the intake, rounded based on where the number is, e.g. 0.45 - 0.5
        BigDecimal bdIntake = new BigDecimal(intake);
        bdIntake = bdIntake.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        BigDecimal bdLeft = new BigDecimal(amountLeft);
        bdLeft = bdLeft.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        bdList.put("intake", bdIntake);
        bdList.put("amountLeft", bdLeft);

        return bdList;
    }

    /**
     * Find the quantity a person is allowed based on a particular column
     * @param col - the column corresponding to the quantity a user can have
     * @return float - the quantity depending on the column
     */
    private Float findQuantity(int col) {

        if(col == 0) return 90.0f; // sugar
        if(col == 1) return 2000.0f; // calories
        if(col == 2) return 70.0f; // fat
        if(col == 3) return 20.0f; // saturates
        if(col == 4) return 260.0f; // carbs
        if(col == 5) return 6.0f; // salt
        else return 50.0f; // protein

    }

    /**
     * Create the grouped contents which will be returned when a user clicks a food (drop down
     * list containing the nutritional info)
     * @param foodName - the food name as a string is usewd to find the information for a particular
     *                 food group
     * @return 2D array list of Strings - the contents of each food group
     */
    public List<List<String>> createGroupedContents(String foodName) {

        // use the foodname to find the contents of the food
        List<List<String>> foodContents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_FOOD,
                foodName);

        // Create ArrayList of Strings containing the data for each food group
        List<List<String>> groupedContents = new ArrayList<>();
        groupedContents.add(findFoodContents("Calories", foodContents.get(0).get(2), " ", 2000));
        groupedContents.add(findFoodContents("Sugar", foodContents.get(0).get(3), "g", 90));
        groupedContents.add(findFoodContents("Fat", foodContents.get(0).get(4), "g", 70));
        groupedContents.add(findFoodContents("Saturates", foodContents.get(0).get(5), "g", 20));
        groupedContents.add(findFoodContents("Carbs", foodContents.get(0).get(6), "g", 260));
        groupedContents.add(findFoodContents("Salt", foodContents.get(0).get(7), "g", 6));
        groupedContents.add(findFoodContents("Protein", foodContents.get(0).get(8), "g", 50));

        return groupedContents;

    }

    /**
     * Find the food contents when a dropdown menu is created. The array list of Strings
     * returned corresponds to the foodGroup, quantity, measure, amount, and percentage, e.g.
     * (Sugar, 30, g, 20, %)
     * @param foodGroup - the String corresponding to the foodGroup
     * @param quantity - the quantity of that food, in g, unless calories (in kcal)
     * @param measure - empty for calories, or g for the others
     * @param amount - the percentage corresponding to daily intake
     * @return List<String> - returns the array list corresponding to food contents, e.g.
     * (Sugar, 30, g, 20, %)
     */
    public List<String> findFoodContents(String foodGroup, String quantity, String measure,
                                         double amount) {

        List<String> group = new ArrayList<>();
        double num;

        group.add(foodGroup); // the food group, e.g. "Sugar"
        group.add(quantity); // quantity
        group.add(measure); // measure is empty as calorie is a measurement

        if(!quantity.equals("null")) {
            // if quantity is not null, then it can be parsed as Double
            num = Double.parseDouble(quantity);
        }
        else {
            // if null, then quantity is 0
            num = 0;
        }
        double percentage = ((num) / amount) * 100;
        // format string as a floating point number with .2 precision
        group.add(String.format(Locale.ENGLISH, "%.2f", percentage));

        return group;
    }

    // TODO findContentsPlusSumAmount
/*    public HashMap<String, String> findContentsPlusSumAmount(String foodGroup, String quantity,
                                                             double amount, double sumAmount) {

        HashMap<String, String> group = new HashMap<>();

        double num;

        group.put("Food group", foodGroup);

        if(!quantity.equals("null")) {
            num = Double.parseDouble(quantity);
        }
        else {
            num = 0;
        }
        double percentage = ((sumAmount + num) / amount) * 100;
        String s = String.format("%.2f", percentage);

        group.put("percentage", s);

        return group;
    }*/



}
