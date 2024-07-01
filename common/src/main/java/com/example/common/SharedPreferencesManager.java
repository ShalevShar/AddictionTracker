package com.example.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SharedPreferencesManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_RELAPSE_ITEMS = "RelapseItems";
    private static final String KEY_FIRST_TIME_LAUNCH = "FirstTimeLaunch";
    protected static final String KEY_FIRST_LAUNCH_DATE = "FirstLaunchDate";
    protected static final String USER_NAME = "USER_NAME";
    protected static final String USER_AGE = "USER_AGE";
    protected static final String DATE_JOINED = "DATE_JOINED";
    protected static final int KEY_REHAB_TARGET = 12;

    SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_LAUNCH, true);
    }

    public void saveFirstLaunchDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIRST_LAUNCH_DATE, date);
        editor.putBoolean(KEY_FIRST_TIME_LAUNCH, false); // Set first time launch to false
        editor.apply();
    }

    public void saveRelapseItems(List<RelapseItem> relapseItemList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(relapseItemList);
        editor.putString(KEY_RELAPSE_ITEMS, json);
        editor.apply();
    }

    public List<RelapseItem> loadRelapseItems() {
        String json = sharedPreferences.getString(KEY_RELAPSE_ITEMS, null);
        Type type = new TypeToken<ArrayList<RelapseItem>>() {}.getType();
        List<RelapseItem> relapseItemList = gson.fromJson(json, type);

        if (relapseItemList == null) {
            relapseItemList = new ArrayList<>();
        }

        return relapseItemList;
    }

    public void saveRehabTarget(int target) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(String.valueOf(KEY_REHAB_TARGET), target);
        editor.apply();
    }

    public void saveName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, name);
        editor.apply();
    }
    public void saveAge(String age) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_AGE, age);
        editor.apply();
    }
    public void saveDateJoined() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        editor.putString(DATE_JOINED, formattedDate);
        editor.apply();
    }
}
