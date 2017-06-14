package com.example.richa.sugarthrow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SaveSharedPreference {

    private static final String PREF_USER_NAME = "username";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    static void setUserName(Context context, String username) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_NAME, username);
        editor.apply();
    }

    static String getUserName(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME, "");
    }

    static void clearUsername(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

}
