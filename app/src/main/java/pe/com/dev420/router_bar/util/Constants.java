package pe.com.dev420.router_bar.util;

import com.benites.jeral.router_bar.BuildConfig;

import pe.com.dev420.router_bar.activities.InsertPubActivity;
import pe.com.dev420.router_bar.activities.LoginActivity;
import pe.com.dev420.router_bar.activities.SplashActivity;

/**
 * Creado por jBenites el dia 20/05/2017 .
 */

public class Constants {


    //Symbols
    public static final String EMPTY = "";
    public static final String EQUALS = "=";
    public static final String QUESTION_MARK = "?";
    public static final String SLASH = "/";
    public static final String AND = "&";
    public static final String SPACE = " ";
    public static final String COMMA = ",";

    /*TAG ACTIVITIES*/
    public static final String TAG_LOGIN_ACTIVITY = LoginActivity.class.getSimpleName();
    public static final String TAG_MAIN_MAP_ACTIVITY = SplashActivity.class.getSimpleName();
    public static final String TAG_INSERT_PUB_ACTIVITY = InsertPubActivity.class.getSimpleName();
    public static final String TAG_RETAINED_FRAGMENT = "PubFragment";

    public static final String BAR_ID = "barId";
    public static final String RESPONSE_UPLOAD = "barLatLng";

    public static final String ANDROID_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".ANDROID";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String BAR_NAME = "barname";
    /*PERMISOS*/
    public static final int CODIGO_PETICION_LOCALIZACION = 1000;
    /*MAPS*/
    public static final int TAG_INTERVAL = 400;
    public static final int TAG_FASTER_INTERVAL = 200;
    public static final float ZOOM_NORMAL = 12f;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final float ZOOM_CLICKED_MARKER = 17f;
    public static final int TIME_ZOOM_CLICKED_MARKER = 1300;

    public static final int REQUEST_PLAY_SERVICES = 222;
    public final static int REQUEST_LOCATION = 777;
    public final static int FINE_LOCATION_RQ = 1502;
    public final static int COARSE_LOCATION_RQ = 1998;
    /*GOOLGE MAPS API*/
    public static final String GOOGLEDIRECTION_API_BASE = "https://maps.googleapis.com/";
    public static final String GOOGLEDIRECTION_SENSOR = "false";
    public static final String GOOGLEDIRECTION_MODE_WALKING = "walking";
    public static final String GOOGLEDIRECTION_MODE_DRIVING = "driving";
    public static final String ZERO = "0";
    public static final String UNO = "1";
    /*CAMERA*/
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_SELECTED = 2;
    public static final int REQUEST_PERMISSIONS = 300;
    public static final boolean WRAPINSCROOLVIEW = true;
    public static final Double MRADIOUS = 400d;
    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

}
