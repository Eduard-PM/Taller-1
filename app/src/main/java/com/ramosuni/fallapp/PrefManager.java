package com.ramosuni.fallapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "fall_app_prefs";
    private static final String KEY_IS_COLLECTING = "isCollecting";

    public static void setCollecting(Context context, boolean isCollecting) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_COLLECTING, isCollecting).apply();
    }

    public static boolean isCollecting(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_COLLECTING, false);
    }
}
