package com.example.richa.sugarthrow;

/*
Connector class is used to establish connection with an SQLite database
*/

import android.database.SQLException;
import android.database.sqlite.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.util.Log;

public class Connector extends SQLiteOpenHelper {

    private String DB_PATH = null;
    private static String DB_NAME = "mydatabase.db";
    private SQLiteDatabase sqliteDatabase;
    private final Context context;

    /**
     * Constructor to find the path of the database
     * @param context - the content in which the Connection started
     */
    public Connector(Context context) {
        super(context, DB_NAME, null, 1);

        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.context = context;
        Log.d("Path 1", DB_PATH);
    }

    /**
     * Attempt to create database
     */
    void attemptCreate() {
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open database connection
     */
    void openConnection() {
        openDataBase();
    }

    /**
     * Try create database connectio
     * @throws IOException - exception if the database is not copied
     */
    private void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.

        boolean databaseExists = checkDataBase();
        if(!databaseExists) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
            }
            catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    /**
     * Check whether the database exists at the specified path
     * @return - whether the database exists
     */
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    /**
     * Copy the database if one already exists
     * @throws IOException - input exception if not copied successfully
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * Open the database at the specified location
     * @return boolean indicating whether the database was returned successfully
     * @throws SQLException - exception thrown if database not found
     */
    private boolean openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        sqliteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return sqliteDatabase != null;

    }

    @Override
    public synchronized void close() {
        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}