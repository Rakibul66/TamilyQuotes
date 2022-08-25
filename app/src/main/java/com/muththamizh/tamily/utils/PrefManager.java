package com.muththamizh.tamily.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private String SP_NAME = "QuotesApp";
    public static String SOUND_OFF_ON = "SOUND_OFF_ON";
    public static String IS_ADMIN_LOGGED_IN = "IS_ADMIN_LOGGED_IN";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context mContext;

    public PrefManager(Context ctx) {
        this.mContext = ctx;
        sharedPreferences = mContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static PrefManager getInstance(Context context){
        return new PrefManager(context);
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,true);
    }

    public boolean isAdminLoggedIn(String key){
        return sharedPreferences.getBoolean(key,false);
    }



    public void saveBoolean(String key,boolean value){
        editor.putBoolean(key,value);
        editor.commit();
        editor.apply();
    }



}
