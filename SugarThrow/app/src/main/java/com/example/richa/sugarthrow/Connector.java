package com.example.richa.sugarthrow;

/*

Connector class is used to establish connection with an SQLite database

*/

import android.database.Cursor;
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
    private static String TAG = "Connector";
    private static String DB_NAME = "appdatabase.db";
    private SQLiteDatabase sqliteDatabase;
    private final Context context;
/*    private static Connector database;

    public static Connector getConnection(Context context) {
        if(database == null) {
            database = new Connector(context);
            SQLiteDatabase db = database.getWritableDatabase();
        }
        if(!db.isopen()) {

        }
        return database;
    }*/

    /**
     *
     * @param context
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
        Log.e("Path 1", DB_PATH);
    }

    public void attemptCreate() {
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() {
        try {
            openDataBase();
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    /**
     *
     * @throws IOException
     */
    public void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.

        boolean databaseExists = checkDataBase();
        if(!databaseExists) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    /**
     *
     * @return
     */
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    /**
     *
     * @throws IOException
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
     *
     * @return
     * @throws SQLException
     */
    public boolean openDataBase() throws SQLException {
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

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return sqliteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
}