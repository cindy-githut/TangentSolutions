package com.solutions.tangent.tangentsolutions;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesData {

    private static final String mypreference = "mypref";

    public static SharedPreferences getSharedPreference(Activity activity){

        return activity.getSharedPreferences(mypreference, MODE_PRIVATE);

    }

    public static SharedPreferences.Editor storeSharedPreference(Activity activity){

        return activity.getSharedPreferences(mypreference, MODE_PRIVATE).edit();

    }
}
