package com.benites.jeral.router_bar.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.benites.jeral.router_bar.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import static com.benites.jeral.router_bar.util.Constants.*;

/**
 * Creado por Jeral Benites el dia 01/10/2017 papu.
 */

public class Util {

    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean checkConnection(Context context) {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) != null ? ((ConnectivityManager) Objects.requireNonNull(context.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() : null) != null;
    }

    public static void SetTitle(android.support.v7.app.ActionBar suporteBar, String Titulo) {
        if (suporteBar != null) {
            suporteBar.setDisplayShowTitleEnabled(true);
            suporteBar.setTitle(Titulo);
        }
    }

    public static boolean gpsEstaActivo(Context context) {
        int locationMode;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                thePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return thePath;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean checkGooglePlayServices(Activity activity) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result, REQUEST_PLAY_SERVICES).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     *
     * @param location The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    public static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }


    /*public static void SetMapTime(Context ctx, GoogleMap mMap, ImageView imageView) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int hours = c.get(Calendar.HOUR_OF_DAY);
        if (hours >= 5 && hours <= 12) {
            if (imageView != null) {
                Bitmap bImage = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.skymor);
                imageView.setImageBitmap(bImage);
            }
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            ctx, R.raw.mapstyleday));
        } else if (hours >= 12 && hours <= 17) {
            if (imageView != null) {
                Bitmap bImage = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.skyaft);
                imageView.setImageBitmap(bImage);
            }
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            ctx, R.raw.mapstyleday));
        } else if (hours >= 17 && hours <= 24) {
            if (imageView != null) {
                Bitmap bImage = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.skynight);
                imageView.setImageBitmap(bImage);
            }
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            ctx, R.raw.mapstylenight));
        } else if (hours >= 0 && hours <= 5) {
            if (imageView != null) {
                Bitmap bImage = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.skynight);
                imageView.setImageBitmap(bImage);
            }
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            ctx, R.raw.mapstylenight));
        }
    }
*/

    public static void PopUPLocationPermission(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.gps_dialog_titulo)
                .setMessage(R.string.gps_dialog_descripcion)
                .setPositiveButton(R.string.gps_dialog_boton, (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                })
                .setCancelable(true);
        dialog.create();
        dialog.show();
    }

}
