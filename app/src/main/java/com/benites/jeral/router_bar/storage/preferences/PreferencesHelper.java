
package com.benites.jeral.router_bar.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {

    private static final String USER_PREFERENCES = "mynotesPreferences";
    private static final String PREFERENCES_USERNAME = USER_PREFERENCES + ".username";
    private static final String PREFERENCES_PASSWORD = USER_PREFERENCES + ".password";
    private static final String PREFERENCES_STATE = USER_PREFERENCES + ".state";
    private static final String PREFERENCES_TOKEN = USER_PREFERENCES + ".token";

    private PreferencesHelper() {
        //no instance
    }

    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREFERENCES_USERNAME);
        editor.remove(PREFERENCES_PASSWORD);
        editor.remove(PREFERENCES_STATE);
        editor.apply();
    }

    public static void saveSession(Context context, String username, String password, String state) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCES_USERNAME, username);
        editor.putString(PREFERENCES_PASSWORD, password);
        editor.putString(PREFERENCES_STATE, state);
        editor.apply();
    }

    public static void saveBLSession(Context context, String username, String token, String state) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCES_USERNAME, username);
        editor.putString(PREFERENCES_TOKEN, token);
        editor.putString(PREFERENCES_STATE, state);
        editor.apply();
    }

    public static String getUserSession(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCES_USERNAME, null);
    }

    public static String getTokenSession(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCES_TOKEN, null);
    }

    public static boolean isSignedIn(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFERENCES_USERNAME);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }
}
