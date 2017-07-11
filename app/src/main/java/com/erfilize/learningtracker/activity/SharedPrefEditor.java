package com.erfilize.learningtracker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPrefEditor class is used to access SharedPreferences
 */

public class SharedPrefEditor {

    private SharedPreferences preferences;

    public SharedPrefEditor(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Check if value is null. This is an important issue when working with SharedPreferences,
     * as null keys can cause corruption.
     * @param value the value to be checked
     */
    public void checkNull(String value){
        if ( value == null){
            throw new NullPointerException();
        }
    }

    /**
     * Put String values into SharedPreferences if they are not null.
     * @param key SharedPreferences key
     * @param value String value associated with key
     */

    public void putString(String key, String value){
        checkNull(key);
        checkNull(value);
        preferences.edit().putString(key, value).apply();
    }


    /**
     * Get String value from SharedPreferences which is associated with param key. Return "" if
     * nothing is found.
     * @param key SharedPreferences key
     * @return String value associated with key
     */
    public String getString(String key){
        return preferences.getString(key,"");
    }



}
