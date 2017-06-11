package com.example.richa.sugarthrow;

/*
This class is responsible for all the point updates
 */

public class PointsHandler extends MainActivity {

    private Execute executeSQL;
    private TimeKeeper date = new TimeKeeper();

    /**
     * Constructor is passed an initialised database and
     * allows for SQL exceution to occur
     * @param database - the initialised database (Connector object)
     */
    public PointsHandler(Connector database) {
        executeSQL = new Execute(database);
    }

    /**
     * Check to see if a point can be updated. Points can only be updated
     * when an item is inserted or removed on the current date
     * @param pointsBefore - the String containing the number of points the user had
     *                     before the insertion / deletion took place
     * @param theDate - theDate that the insertion / deletion affected
     * @param username - the username
     * @param update -
     */
    public void checkForPointsUpdate(String pointsBefore, String theDate, String username,
                                     boolean update) {

        // if date is equal to the current date, points can be updated
        if(date.convertDateFormat(date.getCurrentDate()).equals(theDate)) {
            String pointsAfter = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK,
                    date.convertDateFormat(date.getCurrentDate()), username);

            // if points before and after the insertion occurred are different, then
            // the points can be updated
            if(!pointsBefore.equals(pointsAfter)) {
                if(update) {
                    implementPoints(username, true);
                }
                else {
                    implementPoints(username, false);
                }
            }
        }
    }

    public void increasePoints(String username, String points) {
        updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_1);
        if(Integer.parseInt(points) % 10 == 0) {
            updatePoints(username, SqlQueries.SQL_INCREMENT_POINTS_10);
        }
    }

    public String getPointsBefore(String username) {
        return executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK,
                date.convertDateFormat(date.getCurrentDate()), username);
    }

    public void implementPoints(String username, boolean increasePoints) {
        String points = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK, date.convertDateFormat(date.getCurrentDate()), username);
        if(increasePoints) {
            increasePoints(username, points);
        }
        else {
            if(Integer.parseInt(points) <= 0) {
                updatePoints(username, SqlQueries.SQL_SET_POINTS_0);
            } else {
                if((Integer.parseInt(points) + 1) % 10 == 0) {
                    updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_10);
                }
                updatePoints(username, SqlQueries.SQL_DECREMENT_POINTS_1);
            }
        }

    }


    public void updatePoints(String userName, String SQL) {
        executeSQL.sqlExecuteSQL(SQL, userName);
    }

}
