package com.example.richa.sugarthrow;

/*
This class is responsible for all the point updates
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointsHandler extends MainActivity {

    private Execute executeSQL;
    private TimeKeeper date = new TimeKeeper();
    private FoodContentsHandler foodContentsHandler;
    private String username;

    public String getPoints() {
        List<List<String>> points = executeSQL.sqlGetFromQuery(SqlQueries.SQL_POINTS, username);

        return points.get(0).get(0);

    }


    public String getDailyPoints() {
        String points =  executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_SIZE_DIARY,
                date.convertDateFormat(date.getCurrentDate()), username);

        if(points.equals("Empty set")) {
            points = "0";
        }
        return points;
    }

    /**
     * Constructor is passed an initialised database and
     * allows for SQL exceution to occur
     * @param database - the initialised database (Connector object)
     */
    public PointsHandler(Connector database, String username) {
        executeSQL = new Execute(database);
        this.username = username;
        foodContentsHandler = new FoodContentsHandler(database, username);
    }

    /**
     * Check to see if a point can be updated. Points can only be updated
     * when an item is inserted or removed on the current date
     * @param pointsBefore - the String containing the number of points the user had
     *                     before the insertion / deletion took place
     * @param theDate - theDate that the insertion / deletion affected
     * @param username - the username
     * @param add - true if points are being added, false if they are being removed
     */
    public void checkForPointsUpdate(String pointsBefore, String theDate, String username,
                                     boolean add) {

        // if date is equal to the current date, points can be updated
        if(date.convertDateFormat(date.getCurrentDate()).equals(theDate)) {
            String pointsAfter = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK,
                    date.convertDateFormat(date.getCurrentDate()), username);

            // if points before and after the insertion occurred are different, then
            // the points can be updated

            if(!pointsBefore.equals(pointsAfter)) {
                if(add) {
                    implementPoints(username, true);
                }
                else {
                    implementPoints(username, false);
                }
            }
        }
    }

    // TODO
    public void pointsReduction(String pointsBefore, String theDate,
                                String username, String foodName) {
        // if date is equal to the current date, points can be updated
        if(date.convertDateFormat(date.getCurrentDate()).equals(theDate)) {

            String pointsAfter = getPoints();

            // use the foodname to find the contents of the food
            List<HashMap<String, String>> groupedContents =
                    foodContentsHandler.findGroupedContentsPlusSum(foodName, username);
            deductPointsBasedOnContent(username, pointsAfter, groupedContents);

        }
    }

    // TODO
    public void pointsReturn(String theDate, String username,
                             List<Map<String, BigDecimal>> contents) {

        // if date is equal to the current date, points can be updated
        if(date.convertDateFormat(date.getCurrentDate()).equals(theDate)) {
            returnPointsIfNecessary(username, contents);
        }

    }

    private void returnPointsIfNecessary(String username,
                                         List<Map<String, BigDecimal>> previousContents) {

        List<Map<String, BigDecimal>> currentContents = foodContentsHandler.findDailyTotal(username);
        String points = getPoints();

        for(int i = 0; i < currentContents.size(); i++) {
            if(previousContents.get(i).get("intake").floatValue() > 100) {

                if(currentContents.get(0).get("intake").floatValue() < 100) {
                    // give back points
                    updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);
                    points = getPoints();
                }
            }
        }

    }


    public void deductPointsBasedOnContent(String username, String points, List<HashMap<String, String>> contents) {

        int currentPoints = Integer.parseInt(getPoints());

        if(Double.parseDouble(contents.get(0).get("percentage")) > 100) {
            // reduce 10 points for sugar
            updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_5);
            currentPoints -= 5;
        }

        for(int i = 1; i < contents.size(); i++) {
            if(Double.parseDouble(contents.get(i).get("percentage")) > 100) {
                // remove 5 points
                updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_1);
                currentPoints -= 1;
            }
        }

    }


    public void increasePoints(String username) {
        updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);
        String dailyPoints = getDailyPoints();

        if(Integer.parseInt(dailyPoints) != 0 && Integer.parseInt(dailyPoints) % 10 == 0) {
            updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_10);
        }
    }

    public String getPointsBefore(String username) {
        return executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK,
                date.convertDateFormat(date.getCurrentDate()), username);
    }

    public void implementPoints(String username, boolean increasePoints) {
        String points = getPoints();
        String dailyPoints = getDailyPoints();

        if(increasePoints) {
            increasePoints(username);
        }
        else {
            updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_1);
            points = getPoints();
            if(Integer.parseInt(points) <= 0) {
                updatePoints(username, SqlQueries.SQL_SET_POINTS_0);
            } else {
                if((Integer.parseInt(dailyPoints) + 1) % 10 == 0) {
                    updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_10);
                }
            }
        }
        String pointsAgain = getPoints();


    }

    public void decreasePoints(String username) {

        String points = getPoints();
        updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_1);
        points = getPoints();
        if(Integer.parseInt(points) <= 0) {
            updatePoints(username, SqlQueries.SQL_SET_POINTS_0);
        }

    }


    public void updatePoints(String userName, String SQL) {
        executeSQL.sqlExecuteSQL(SQL, userName);
    }

}
