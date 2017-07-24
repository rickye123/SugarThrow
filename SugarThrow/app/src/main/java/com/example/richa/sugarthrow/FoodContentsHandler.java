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

    private float sugarQuantity, caloriesQuantity, saturatesQuantity,
    fatQuantity, carbsQuantity, proteinQuantity, saltQuantity;
    private List<Float> quantities = new ArrayList<>();
    private TimeKeeper date = new TimeKeeper();

    private Execute executeSQL;

    /**
     * Constructor which is passed an initialised database (Connector object)
     * so that SQL statements can be executed
     * @param database - the initialised database
     */
    public FoodContentsHandler(Connector database, String username) {

        executeSQL = new Execute(database);

        List<List<String>> hasGoals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOAL, username);

        if(!hasGoals.get(0).get(0).equals("Empty set")) {
            List<List<String>> goals = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_GOALS, username);
            setQuantitiesFromGoals(goals);
        }
        else {
            sugarQuantity = 90.0f;
            caloriesQuantity = 2000.0f;
            saturatesQuantity = 20.0f;
            fatQuantity = 70.0f;
            carbsQuantity = 260.0f;
            proteinQuantity = 50.0f;
            saltQuantity = 6.0f;
        }

        setQuantitiesList();

    }

    /**
     * Get the last five days in a string array
     * @return - reversed array showing previus five dates
     */
    public String[] findPreviousFiveDays() {

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

    public String[] findPreviousSevenDays() {

        String prevDay = date.getCurrentDate();
        String[] days = new String[7];

        for(int i = 0; i < 7; i++) {
            String day = date.getPrevDate(prevDay);
            days[i] = day;
            prevDay = day;
        }

        reverseArray(days);

        return days;

    }

    /**
     * Reverse an array of Strings
     * @param inputArray - the array which will be reversed
     */
    private void reverseArray(String inputArray[]) {
        String temp;

        for (int i = 0; i < inputArray.length/2; i++)
        {
            temp = inputArray[i];
            inputArray[i] = inputArray[inputArray.length-1-i];
            inputArray[inputArray.length-1-i] = temp;
        }

    }

    /**
     * Set the quantities of each food group
     */
    private void setQuantitiesList() {

        quantities.add(sugarQuantity);
        quantities.add(caloriesQuantity);
        quantities.add(saturatesQuantity);
        quantities.add(fatQuantity);
        quantities.add(carbsQuantity);
        quantities.add(proteinQuantity);
        quantities.add(saltQuantity);

    }

    /**
     * Get the list of quantities for each food group
     * @return - list of floats containing daily allowance for each food group
     */
    public List<Float> getQuantitiesList() {
        return quantities;
    }

    /**
     * Sets the quantities based on the goals set by the user
     * @param goals - the goal quantities set by the user
     */
    private void setQuantitiesFromGoals(List<List<String>> goals ) {

        // set sugar
        if(!(goals.get(0).get(0).equals(""))) sugarQuantity = Float.parseFloat(goals.get(0).get(0));
        else sugarQuantity = 90.0f;

        // set calories
        if(!(goals.get(0).get(1).equals(""))) caloriesQuantity = Float.parseFloat(goals.get(0).get(1));
        else caloriesQuantity = 2000.0f;

        // set saturates
        if(!(goals.get(0).get(2).equals(""))) saturatesQuantity = Float.parseFloat(goals.get(0).get(2));
        else saturatesQuantity = 20.0f;

        // set fat
        if(!(goals.get(0).get(3).equals(""))) fatQuantity = Float.parseFloat(goals.get(0).get(3));
        else fatQuantity = 70.0f;

        // set salt
        if(!(goals.get(0).get(4).equals(""))) saltQuantity = Float.parseFloat(goals.get(0).get(4));
        else saltQuantity = 6.0f;

        // set protein
        if(!(goals.get(0).get(5).equals(""))) proteinQuantity = Float.parseFloat(goals.get(0).get(5));
        else proteinQuantity = 50.0f;

        // set carbs
        if(!(goals.get(0).get(6).equals(""))) carbsQuantity = Float.parseFloat(goals.get(0).get(6));
        else carbsQuantity = 260.0f;

    }

    /**
     * Find the amount of food left based on the intake
     * @param intake - floating point number corresponding to the amount
     *               of a particular food the user has had
     * @return amountLeft (float) - the amount of food left based on intake
     */
    private Float findAmountLeft(Float intake) {

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
    private Float findIntake(String itemTotal, Float quantity) {

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
     * Find the current daily total of foods contributing to daily allowance
     * @param username - the user's username used to find the diary entries
     * @param theDate - the date selected
     * @return a list of hashmaps containing the daily totals for a particular user
     */
    public List<Map<String, BigDecimal>> findDailyTotal(String theDate, String username) {

        List<Map<String, BigDecimal>> dailyTotals = new ArrayList<>();

        System.out.println("DATE " + theDate);
        System.out.println("USERNAME " + username);
        System.out.println("SQL " + SqlQueries.SQL_SELECT_DIARY_ON_DAY);
        List<List<String>> sumOfFoods =
                executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ON_DAY, theDate, username);
        int size = sumOfFoods.get(0).size();




        for(int i = 0; i < size; i++) {
            System.out.println(sumOfFoods.get(0).get(i));
            dailyTotals.add(findFoodPercentages(sumOfFoods.get(0).get(i), i));
        }

        return dailyTotals;

    }

    /**
     * Create the quantities that appear in the Goals activity from the
     * 2D array list of goals for that particular user
     * @param goals - the goals for a user
     * @return a 2D array list containing the goals and their title
     */
    public List<List<String>> createQuantities(List<List<String>> goals) {

        List<List<String>> quantities = new ArrayList<>();

        quantities.add(new ArrayList<String>());
        quantities.get(0).add("Sugar - ");
        quantities.get(0).add(goals.get(0).get(0));
        quantities.get(0).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(1).add("Calories - ");
        quantities.get(1).add(goals.get(0).get(1));
        quantities.get(1).add("kcal");
        quantities.add(new ArrayList<String>());
        quantities.get(2).add("Saturates - ");
        quantities.get(2).add(goals.get(0).get(2));
        quantities.get(2).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(3).add("Fat - ");
        quantities.get(3).add(goals.get(0).get(3));
        quantities.get(3).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(4).add("Salt - ");
        quantities.get(4).add(goals.get(0).get(4));
        quantities.get(4).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(5).add("Protein - ");
        quantities.get(5).add(goals.get(0).get(5));
        quantities.get(5).add("g");
        quantities.add(new ArrayList<String>());
        quantities.get(6).add("Carbs - ");
        quantities.get(6).add(goals.get(0).get(6));
        quantities.get(6).add("g");

        return quantities;

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

        if(col == 0) return sugarQuantity; // sugar
        if(col == 1) return caloriesQuantity; // calories
        if(col == 2) return fatQuantity; // fat
        if(col == 3) return saturatesQuantity; // saturates
        if(col == 4) return carbsQuantity; // carbs
        if(col == 5) return saltQuantity; // salt
        else return proteinQuantity; // protein

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
        groupedContents.add(findFoodContents("Calories", foodContents.get(0).get(2), " ",
                caloriesQuantity));
        groupedContents.add(findFoodContents("Sugar", foodContents.get(0).get(3), "g",
                sugarQuantity));
        groupedContents.add(findFoodContents("Fat", foodContents.get(0).get(4), "g",
                fatQuantity));
        groupedContents.add(findFoodContents("Saturates", foodContents.get(0).get(5), "g",
                saturatesQuantity));
        groupedContents.add(findFoodContents("Carbs", foodContents.get(0).get(6), "g",
                carbsQuantity));
        groupedContents.add(findFoodContents("Salt", foodContents.get(0).get(7), "g",
                saltQuantity));
        groupedContents.add(findFoodContents("Protein", foodContents.get(0).get(8), "g",
                proteinQuantity));

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

    /**
     * Used to determine whether a food is one in which a certain food group
     * is higher than 50% of daily allowance
     * @param foodContents - the foodcontents of a certain food
     * @return true if that food has a food group that is over daily allowance
     */
    public boolean riskFood(List<List<String>> foodContents) {
        for(int i = 0; i < 7; i++) {
            if (Double.parseDouble(foodContents.get(i).get(3)) > 50) {
                // this is a risk food
                return true;
            }
        }
        return false;
    }

    /**
     * Get the food eaten over the last 5 days
     * @param username - the user's username used to find the food they've eaten
     * @return the hashmap containing the amount of a food that has been eaten
     * over the past five days
     */
    public HashMap<String, Integer> getLastFiveDays(String username) {

        String[] days = findPreviousFiveDays();
        HashMap<String, Integer> map = new HashMap<>();

        for(int i = 0; i < 5; i++) {
            List<List<String>> groups = executeSQL.sqlGetFromQuery(SqlQueries.SQL_GROUP_QUANTITY,
                    date.convertDateFormat(days[i]), username);
            if(!groups.get(0).get(0).equals("Empty set")) {
                for (int j = 0; j < groups.size(); j++) {
                    int count;
                    if(map.containsKey(groups.get(j).get(0))) {
                        count = map.get(groups.get(j).get(0));
                        count = count + Integer.parseInt(groups.get(j).get(8));
                    }
                    else {
                        count = Integer.parseInt(groups.get(j).get(8));
                    }
                    map.put(groups.get(j).get(0), count);
                }
            }

        }

        return map;


    }

    /**
     * Find total amount following the addition of a certain food
     * @param foodGroup - the foodgroup, e.g. sugar
     * @param quantity - the amount of that food
     * @param amount - the daily amount of that food group
     * @param sumAmount - the sum amount for that user
     * @return the hashmap containing this information
     */
    private HashMap<String, String> findContentsPlusSumAmount(String foodGroup, String quantity,
                                                             double amount, String sumAmount) {

        HashMap<String, String> group = new HashMap<>();

        double num;
        double sum;

        group.put("Food group", foodGroup);

        if(quantity == null || quantity.equals("null")) {
            num = 0;
        }
        else {
            num = Double.parseDouble(quantity);
        }

        if(sumAmount == null || sumAmount.equals("null")) {
            sum = 0;
        }
        else {
            sum = Double.parseDouble(sumAmount);
        }

        double percentage = ((sum + num) / amount) * 100;
        String s = String.format(Locale.ENGLISH, "%.2f", percentage);

        group.put("percentage", s);

        return group;
    }

    /**
     * Find the grouped contents for foods plus the sum total contributing to the allowance
     * for that day
     * @param foodName - the foodname used to find the contents of that food
     * @param username - the username to find the diary entries
     * @return a list of hashmaps containing the grouped contents plus their sum of daily allowance
     */
    public List<HashMap<String, String>> findGroupedContentsPlusSum(String foodName, String username,
                                                                    String theDate) {

        List<List<String>> foodContents = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_FOOD,
                foodName);
        List<List<String>> sumOfFood = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ON_DAY,
                theDate, username);

        List<HashMap<String, String>> groupedContents = new ArrayList<>();
        groupedContents.add(findContentsPlusSumAmount("Sugar", foodContents.get(0).get(3),
                sugarQuantity, sumOfFood.get(0).get(0)));
        groupedContents.add(findContentsPlusSumAmount("Calories", foodContents.get(0).get(2),
                caloriesQuantity, sumOfFood.get(0).get(1)));
        groupedContents.add(findContentsPlusSumAmount("Fat", foodContents.get(0).get(4),
                fatQuantity, sumOfFood.get(0).get(2)));
        groupedContents.add(findContentsPlusSumAmount("Saturates", foodContents.get(0).get(5),
                saturatesQuantity, sumOfFood.get(0).get(3)));
        groupedContents.add(findContentsPlusSumAmount("Carbs", foodContents.get(0).get(6),
                carbsQuantity, sumOfFood.get(0).get(4)));
        groupedContents.add(findContentsPlusSumAmount("Salt", foodContents.get(0).get(7),
                saltQuantity, sumOfFood.get(0).get(5)));
        groupedContents.add(findContentsPlusSumAmount("Protein", foodContents.get(0).get(8),
                proteinQuantity, sumOfFood.get(0).get(6)));

        return groupedContents;

    }

    /**
     *
     * @param contents
     * @return
     */
    public Map<String, String> getContents(List<List<String>> contents) {

        Map<String, String> group = new HashMap<>();

        /* If a contents row is equal to null, then set its value to 0
         * otherwise, use the value of the row
         */
        group.put("Sugar", contents.get(0).get(0) == null ? "0" : contents.get(0).get(0));
        group.put("Calories", contents.get(0).get(1) == null ? "0" : contents.get(0).get(1));
        group.put("Fat", contents.get(0).get(2) == null ? "0" : contents.get(0).get(2));
        group.put("Saturates", contents.get(0).get(3) == null ? "0" : contents.get(0).get(3));
        group.put("Carbs", contents.get(0).get(4) == null ? "0" : contents.get(0).get(4));
        group.put("Salt", contents.get(0).get(5) == null ? "0" : contents.get(0).get(5));
        group.put("Protein", contents.get(0).get(6) == null ? "0" : contents.get(0).get(6));

        return group;

    }

}
