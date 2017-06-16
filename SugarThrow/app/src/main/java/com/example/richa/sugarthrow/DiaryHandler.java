package com.example.richa.sugarthrow;

/*
This class handles all the insertions and deletions from a user's diary.
 */

import android.content.ContentValues;
import java.util.List;

public class DiaryHandler extends DiaryActivity {

    private Execute executeSQL;

    /**
     * Constructor used to establish the database connection
     * @param database - the Connector object containing the
     *                 initialised database
     */
    public DiaryHandler(Connector database) {
        executeSQL = new Execute(database);
    }

    /**
     * The method invoked which inserts items into the user's diary on a particular date
     * @param id - the id corresponding to a certain position in the search / diary (as an int)
     * @param theDate - the date in which the insertion occurs (as a String)
     * @param username - the corresponding user as a String
     * @param content - the 2D array list of contents (searchTerms for search and diaryContents
     *                for diary)
     * @param fromSearch - boolean representing whether the insertion came from the search or not
     */
    public void insertIntoDiary(int id, String theDate, String username, List<List<String>> content,
                                boolean fromSearch) {

        // get user table to find the userId
        List<List<String>> userName = executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_USER,
                username);
        String userId = userName.get(0).get(0);
        String foodId;

        // if insertion came from search entries, find the foodId in the search contents
        if(fromSearch) {
            foodId = findFoodId(id, content);
        }
        else {
            // find the foodId based on where it is in the diary
            foodId = content.get(id).get(2);
        }

        // find the diary values corresponding to the foodId, userId, and date
        ContentValues values = findDiaryValues(theDate, foodId, userId);

        // insert values into "Diary" table
        executeSQL.sqlInsert("Diary", values);

    }

    /**
     *
     * @return
     */
    public int findLogStreak(TimeKeeper date, String username) {

        String current = date.getCurrentDate();
        int streak = 1;
        boolean logged = true;

        while(logged) {

            String log = executeSQL.sqlGetSingleStringFromQuery(SqlQueries.SQL_STREAK,
                    date.convertDateFormat(current), username);

            current = date.getPrevDate(current);

            if(Integer.parseInt(log) == 0) {
                logged = false;
            }
            else {
                streak++;
            }

        }
        return streak;

    }

    /**
     * Populates the ContentValues for when a user is entering into the "Diary"
     * table
     * @param date - the date which will be changed
     * @param foodId - the foodId corresponding to a food
     * @param userId - the userId corresponding to a user
     * @return ContentValues - the map corresponding to the date, foodId, and userId
     */
    public ContentValues findDiaryValues(String date, String foodId, String userId) {

        ContentValues values = new ContentValues();
        values.put("theDate", date);
        values.put("foodId", foodId);
        values.put("userId", userId);

        return values;
    }

    /**
     * Method invoked to remove an item from the diary entries on a particular date
     * @param theDate - the date in which the deletion will occur
     * @param username - the username whose contents will be deleted
     * @param id - the id referring to the where an item is in a search / diary
     */
    public void removeFromDiaryEntries(String theDate, String username, int id) {

        List<List<String>> diary = executeSQL.sqlGetFromQuery(SqlQueries.SQL_IN_DIARY, theDate,
                username);

        String foodName = diary.get(id).get(0); // the foodName in the diary

        // find the diaryId
        List<List<String>> diaryEntries =
                executeSQL.sqlGetFromQuery(SqlQueries.SQL_SELECT_DIARY_ENTRY,
                theDate, username, foodName);

        String diaryId = diaryEntries.get(0).get(0);

        // execute SQL delete statement
        executeSQL.sqlDelete("Diary", "diaryId = ?", diaryId);

    }

    /**
     * Find the foodId in the searchTerms table
     * @param id - the id corresponding to a particular row in the searchTerms table
     * @param searchTerms - the table (2D array list of Strings) referring to the
     *                    results returned from a search query
     * @return String - foodId of a particular item, or "Empty set" if the food isn't found
     */
    public String findFoodId(int id, List<List<String>> searchTerms) {

        // get all the results from the "Food" table
        List<List<String>> foods = executeSQL.sqlGetAll("Food");

        if(!foods.get(0).get(0).equals("Empty set")) {
            for(int i = 0; i < foods.size(); i++) {
                // if the item in the "Food" table equals to the item in the
                // searchTerms table, then return that food's id
                if(foods.get(i).get(1).equals(searchTerms.get(id).get(1))) {
                    return foods.get(i).get(0);
                }
            }
        }
        return "Empty set";
    }

}
