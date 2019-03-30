package com.radioknit.mminewapp.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nishant on 24/4/17.
 */

public class TempSharedPreference {

    private static final String TAG = "TempSharedPreference";

    private static final String KEY_TEMP_APP_SETTING_SHARE_PREF = "TempSharedPreference";
    private static final int PRIVATE_MODE = 0;

    private static final String KEY_PAIRED_DEVICE_ADDRESS = "PairedDeviceAddress";
    private static final String KEY_COMPULSORY_STOP = "CompulsoryStop";
    private static final String KEY_PARKING_FLOOR = "ParkingFloor";
    private static final String KEY_HOME_FLOOR = "HomeFloor";
    private static final String KEY_FIREMAN_FLOOR = "FiremanFloor";

    private static final String KEY_USER_EMAIL = "Email";
    private static final String KEY_PASSWORD = "Password";

    public static String getKeyUserEmail(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF, PRIVATE_MODE);
        String userEmail = sharedPreferences.getString(KEY_USER_EMAIL , null);
        return userEmail;
    }

    public static void setKeyUserEmail(Context context, String strEmail){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, strEmail);
        editor.commit();
    }

    public static String getKeyPassword(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF, PRIVATE_MODE);
        String password = sharedPreferences.getString(KEY_PASSWORD , null);
        return password;
    }

    public static void setKeyPassword(Context context, String strPassword){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, strPassword);
        editor.commit();
    }

    public static String getPairedDeviceAddress(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF, PRIVATE_MODE);
        String appVersionCode = sharedPref.getString(KEY_PAIRED_DEVICE_ADDRESS, null);
        return  appVersionCode;
    }

    public static void setPairedDeviceAddress(Context context, String appVersionCode){
        SharedPreferences sharedPref = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PAIRED_DEVICE_ADDRESS, appVersionCode);
        editor.commit();
    }

    public static String getKeyCompulsoryStop(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        String compulsoryStop =  sharedPreferences.getString(KEY_COMPULSORY_STOP, null);
        return compulsoryStop;
    }

   public static void setKeyCompulsoryStop(Context context, String compulsoryStop){
       SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putString(KEY_COMPULSORY_STOP, compulsoryStop);
       editor.commit();
   }

    public static String getKeyParkingFloor(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        String parkingFloor =  sharedPreferences.getString(KEY_PARKING_FLOOR, null);
        return parkingFloor;
    }

    public static void setKeyParkingFloor(Context context, String paekingFloor){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PARKING_FLOOR, paekingFloor);
        editor.commit();
    }

    public static String getKeyHomeFloor(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        String homeFloor =  sharedPreferences.getString(KEY_HOME_FLOOR, null);
        return homeFloor;
    }

    public static void setKeyHomeFloor(Context context, String homeFloor){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_HOME_FLOOR, homeFloor);
        editor.commit();
    }

    public static String getKeyFiremanFloor(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        String firemanFloor =  sharedPreferences.getString(KEY_FIREMAN_FLOOR, null);
        return firemanFloor;
    }

    public static void setKeyFiremanFloor(Context context, String firemanFloor){
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_TEMP_APP_SETTING_SHARE_PREF,PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_HOME_FLOOR, firemanFloor);
        editor.commit();
    }
    
}
