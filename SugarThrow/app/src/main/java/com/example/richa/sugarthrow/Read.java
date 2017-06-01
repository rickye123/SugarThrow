package com.example.richa.sugarthrow;

/*
  The Read class is used from the config.properties file
  and the two .data files. It also includes JUnit testing for
  the relevant methods.
*/

import java.util.*;
import java.io.*;
import android.content.Context;

public class Read {

    /**
     * read from config file (.properties) to get the database,
     * user and password so that a connection can be established
     * @param filename - the config file as a string
     * @return - HashMap containing the database, user, and password
     */
    public Map<String, String> readFromConfigFile(Context context, String filename) {

        Properties prop = new Properties();
        InputStream input = null;

        Map<String, String> field = new HashMap<String, String>();

        try {
            input = context.getAssets().open(filename);

            // load a properties file
            prop.load(input);

            field.put("database", prop.getProperty("database"));
            field.put("dbuser", prop.getProperty("dbuser"));
            field.put("dbpassword", prop.getProperty("dbpassword"));
        } catch(IOException readException) {
            throw new Error(readException);
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException readException) {
                    readException.printStackTrace();
                }
            }
        }
        return field;

    }

}