package com.example.richa.sugarthrow;

/*
This class is responsible for all the execution of SQL statements,
including insertions, deletions, updates, and selections
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class Execute {

    private Connector database;

    /**
     * Constructor which makes the local Connector object
     * equal to the initialised database
     * @param database - the Connector object (db connection)
     */
    Execute(Connector database) {
        this.database = database;
    }

    /**
     * Create 2D array list of Strings from the results returnd
     * by the SQL query. Cursor object points to first row and
     * then keeps moving down until there are no more rows
     * @param cursor - the cursor object which points to a row
     *               in returned SQL query
     * @return 2D array list of Strings (table
     */
    private List<List<String>> populateTable(Cursor cursor) {
        List<List<String>> table = new ArrayList<>();
        int i = 0;
        do {
            table.add(new ArrayList<String>());
            int j = 0;
            while (j < cursor.getColumnCount()) {
                table.get(i).add(cursor.getString(j));
                j++;
            }
            i++;
        } while(cursor.moveToNext());
        return table;
    }

    /**
     * Get table from SQL query, using ? as placeholders to protect
     * against SQL injection
     * @param SQL - SQL Query as a string
     * @param arguments - variable length of string arguments corresponding to
     *                  the ? placeholders
     * @return - 2D array list of strings (table)
     */
    List<List<String>> sqlGetFromQuery(String SQL, String... arguments) {
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor;

        try {
            // execute SQL query
            cursor = db.rawQuery(SQL, arguments);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }

        // if query returns no rows, return "Empty set"
        if (!cursor.moveToFirst()) {
            List<List<String>> table = new ArrayList<>();
            table.add(new ArrayList<String>());
            table.get(0).add("Empty set");
            return table;
        }
        // populate table with SQL query results
        return populateTable(cursor);

    }

    /**
     * Similar to the sqlGetFromQuery function, except we know that these queries
     * return only one result, so only want the one String to be returned
     * @param SQL - SQL query as string
     * @param arguments - the arguments which will replace the ? placeholders
     * @return string - the string in the SQL query result
     */
    String sqlGetSingleStringFromQuery(String SQL, String... arguments) {
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor;
        try {
            // execute SQL query
            cursor = db.rawQuery(SQL, arguments);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }

        // if there is no row, return "Empty set"
        if (!cursor.moveToFirst()) {
            return "Empty set";
        }

        // get the first string and return it
        String result = cursor.getString(0);
        cursor.close();
        return result;

    }

    /**
     * SQL query which returns a table. Only passed the table name, so will
     * return all the results for that table (used for debugging)
     * @param tableName - the table name as a String
     * @return - 2D array list of Strings (table)
     */
    List<List<String>> sqlGetAll(String tableName) {
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor;
        try {
            // execute SQL, with all clauses equal to null, so that
            // entire table is returned
            cursor = db.query(tableName, null, null, null, null, null, null);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }

        // if no rows returned (likely table doesn't exist), but in this
        // case, return "Empty set"
        if (!cursor.moveToFirst()) {
            List<List<String>> table = new ArrayList<>();
            table.add( new ArrayList<String>());
            table.get(0).add("Empty set");
            return table;
        }
        // populate table with the SQL query results
        return populateTable(cursor);
    }

    /**
     * SQL insert statement
     * @param tableName - the table name whose contents are being entered into
     * @param values - the values being entered into the corresponding table
     */
    void sqlInsert(String tableName, ContentValues values) {

        SQLiteDatabase db = database.getWritableDatabase();
        try {
            // insert the values into the tableName
            db.insert(tableName, null, values);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }
    }

    /**
     * SQL execute query method
     * @param SQL - SQL query as a string
     * @param args - the arguments which replace the ? placeholders
     */
    void sqlExecuteSQL(String SQL, String ... args) {
        SQLiteDatabase db = database.getWritableDatabase();
        try {
            db.execSQL(SQL, args);
        } catch(SQLException sqlException) {
            throw new Error(sqlException);
        }

    }

    /**
     * SQL delete statement
     * @param tableName - the table name whose contents are being deleted
     * @param whereClause - where the contents will be deleted in that table
     * @param args - the arguments replacing the ? placeholders
     */
    void sqlDelete(String tableName, String whereClause, String... args) {
        SQLiteDatabase db = database.getWritableDatabase();
        try {
            // execute SQL delete statement
            db.delete(tableName, whereClause, args);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }
    }


}
