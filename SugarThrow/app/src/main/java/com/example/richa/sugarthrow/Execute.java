package com.example.richa.sugarthrow;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Execute {

    private Connector database;

    /**
     *
     * @param database
     */
    public Execute(Connector database) {
        this.database = database;
    }

    /**
     *
     * @param c
     * @return
     */
    public List<List<String>> populateTable(Cursor c) {
        List<List<String>> table = new ArrayList<List<String>>();
        int i = 0;
        do {
            table.add(new ArrayList<String>());
            int j = 0;
            while (j < c.getColumnCount()) {
                table.get(i).add(c.getString(j));
                j++;
            }
            i++;
        } while(c.moveToNext());
        return table;
    }

    /**
     *
     * @param SQL
     * @param arguments
     * @return
     */
    public List<List<String>> sqlGetFromQuery(String SQL, String... arguments) {
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(SQL, arguments);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }

        if (!c.moveToFirst()) {
            List<List<String>> table = new ArrayList<List<String>>();
            table.get(0).add("Empty set");
            return table;
        }
        List<List<String>> table = populateTable(c);
        return table;

    }

    /**
     *
     * @param tableName
     * @return
     */
    public List<List<String>> sqlGetAll(String tableName) {

        Cursor c;
        try {
            c = database.query(tableName, null, null, null, null, null, null);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }

        if (!c.moveToFirst()) {
            List<List<String>> table = new ArrayList<List<String>>();
            table.get(0).add("Empty set");
            return table;
        }
        List<List<String>> table = populateTable(c);
        return table;
    }

    /**
     *
     * @param tableName
     * @param values
     */
    public void sqlInsert(String tableName, ContentValues values) {
        SQLiteDatabase db = database.getWritableDatabase();
        try {
            db.insert(tableName, null, values);
        } catch (SQLException sqlException) {
            throw new Error(sqlException);
        }
    }

}
