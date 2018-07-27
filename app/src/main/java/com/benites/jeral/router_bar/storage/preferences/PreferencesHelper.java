
package com.benites.jeral.router_bar.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.benites.jeral.router_bar.storage.network.entity.UserRaw;


public class PreferencesHelper {

    private static final String USER_PREFERENCES = "mynotesPreferences";
    private static final String PREFERENCES_ID = USER_PREFERENCES + ".id";
    private static final String PREFERENCES_EMAIL = USER_PREFERENCES + ".username";
    private static final String PREFERENCES_PASSWORD = USER_PREFERENCES + ".password";
    private static final String PREFERENCES_STATE = USER_PREFERENCES + ".state";
    private static final String PREFERENCES_TOKEN = USER_PREFERENCES + ".token";
    private static final String TOKEN = "";

    private PreferencesHelper() {
        //no instance
    }

    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREFERENCES_ID);
        editor.remove(PREFERENCES_EMAIL);
        editor.remove(PREFERENCES_TOKEN);
        editor.remove(PREFERENCES_STATE);
        editor.apply();
    }

    public static void saveSession(Context context, String username, String password, String state) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCES_EMAIL, username);
        editor.putString(PREFERENCES_PASSWORD, password);
        editor.putString(PREFERENCES_STATE, state);
        editor.apply();
    }

    public static void saveBLSession(Context context, UserRaw userRaw) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCES_ID, userRaw.getUserEntity().get_id());
        editor.putString(PREFERENCES_EMAIL, userRaw.getUserEntity().getEmail());
        editor.putString(PREFERENCES_TOKEN, TOKEN);
        editor.putString(PREFERENCES_STATE, userRaw.getUserEntity().getState());
        editor.apply();
    }

    public static String getUserSession(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCES_EMAIL, null);
    }

    public static String getIDSession(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCES_ID, null);
    }

    public static String getStateSession(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCES_STATE, null);
    }
    public static String getTokenSession(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCES_TOKEN, null);
    }

    public static boolean isSignedIn(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFERENCES_EMAIL);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }
}
