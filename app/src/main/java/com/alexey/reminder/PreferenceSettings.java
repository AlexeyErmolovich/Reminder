package com.alexey.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Alexey on 20.04.2016.
 */
public class PreferenceSettings {

    public static final String TEXT_SPEECH = "text_speech";
    public static final String VIBRATION = "vibration";


    private SharedPreferences sharedPreferences;
    private Context context;

    private static PreferenceSettings settings;

    private PreferenceSettings(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceSettings getInstance(Context context) {
        if (settings == null) {
            settings = new PreferenceSettings(context);
        }
        return settings;
    }

    public boolean getBooleanItem(String key){
        return this.sharedPreferences.getBoolean(key, true);
    }

    public void setBooleanItem(String key, boolean value){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }



}
