package pe.com.dev420.router_bar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.benites.jeral.router_bar.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import pe.com.dev420.router_bar.storage.network.ApiClass;
import pe.com.dev420.router_bar.storage.network.RouterApi;
import pe.com.dev420.router_bar.storage.network.entity.Route.RouteResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pe.com.dev420.router_bar.util.Constants.BAR_NAME;
import static pe.com.dev420.router_bar.util.Constants.CODIGO_PETICION_LOCALIZACION;
import static pe.com.dev420.router_bar.util.Constants.GOOGLEDIRECTION_MODE_DRIVING;
import static pe.com.dev420.router_bar.util.Constants.GOOGLEDIRECTION_MODE_WALKING;
import static pe.com.dev420.router_bar.util.Constants.GOOGLEDIRECTION_SENSOR;
import static pe.com.dev420.router_bar.util.Constants.LATITUDE;
import static pe.com.dev420.router_bar.util.Constants.LONGITUDE;
import static pe.com.dev420.router_bar.util.Constants.REQUEST_CHECK_SETTINGS;
import static pe.com.dev420.router_bar.util.Constants.TAG_FASTER_INTERVAL;
import static pe.com.dev420.router_bar.util.Constants.TAG_INTERVAL;
import static pe.com.dev420.router_bar.util.Constants.ZOOM_NORMAL;
import static pe.com.dev420.router_bar.util.GoogleMapsHelper.decodePoly;
import static pe.com.dev420.router_bar.util.Util.PopUPLocationPermission;

public class MapsRouteActivity extends BaseActivity
        implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    // private ImageView skyTheme;
    //private FloatingActionButton btnDriving, btnWalking;
    //private TextView DistanciaTextView, TiempoTextView;
    private Double latitude, longitude;
    private ArrayList<LatLng> MarkerPoints;
    private String ModeRoute = "", pubName;
    private GoogleApiClient mGoogleApiClient;
    private Location mUltimaLocalizacion;
    private LatLng origin, dest;
    private Polyline line;
    private LocationRequest mSolicitadorLocalizacion;
    private Marker mCurrentLocationMarker;
    private LocationSettingsRequest.Builder settingsApiBuilder;
    private SupportMapFragment mapFragment;
    private LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            mUltimaLocalizacion = result.getLastLocation();
            mostrarLocalizacion();
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            if (!locationAvailability.isLocationAvailable()) {
                StopTracker();
                verificarConfiguracionLocalizacion();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_route);
        ui();
        extras();
    }

    private void extras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            latitude = bundle.getDouble(LATITUDE, 0.0d);
            longitude = bundle.getDouble(LONGITUDE, 0.0d);
            pubName = bundle.getString(BAR_NAME, "");
        }
    }

    private void ui() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ModeRoute = GOOGLEDIRECTION_MODE_DRIVING;
        //  skyTheme = findViewById(R.id.skyTheme);
        //btnWalking = findViewById(R.id.btnWalk);
        // btnDriving = findViewById(R.id.btnDriving);
        //DistanciaTextView = findViewById(R.id.DistanciaTextView);
        //TiempoTextView = findViewById(R.id.TiempoTextView);
        MarkerPoints = new ArrayList<>();
        // btnDriving.setOnClickListener(v -> {
        //  ModeRoute = GOOGLEDIRECTION_MODE_DRIVING;
        //   DrawRoute(ModeRoute);
        // });
        // btnWalking.setOnClickListener(v -> {
        //   ModeRoute = GOOGLEDIRECTION_MODE_WALKING;
        //    DrawRoute(ModeRoute);
        // });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
            // SetMapTime(getApplicationContext(), mMap, skyTheme);
            validarPermisoLocalizacion();
        }
    }

    private void validarPermisoLocalizacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                configurarGoogleLocationAPI();
            } else {
                perdirPermisoLocalizacion();
            }
        } else {
            configurarGoogleLocationAPI();
        }
    }

    private void configurarGoogleLocationAPI() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(connectionResult -> Toast.makeText(MapsRouteActivity.this, R.string.google_error_conexion, Toast.LENGTH_SHORT).show())
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODIGO_PETICION_LOCALIZACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configurarGoogleLocationAPI();
            } else {
                perdirPermisoLocalizacion();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopTracker();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mUltimaLocalizacion = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mostrarLocalizacion();
        crearLocationRequest();

    }

    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case 1:
                Toast.makeText(this, "El Servicio Fue matado", Toast.LENGTH_SHORT).show();

            case 2:
                Toast.makeText(this, "El Dispositivo perdio la Conexion", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mUltimaLocalizacion = location;
        mostrarLocalizacion();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mostrarLocalizacion();
        return false;
    }

    private void DrawRoute(String type) {
        Call<RouteResponse> call = ApiClass.getRetrofitRoute().create(RouterApi.class).getDistanceDuration(
                origin.latitude + "," + origin.longitude,
                dest.latitude + "," + dest.longitude,
                GOOGLEDIRECTION_SENSOR
                , type);

        call.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(@NonNull Call<RouteResponse> call, @NonNull Response<RouteResponse> response) {
                if (line != null) {
                    line.remove();
                }
                if (response.body() != null) {
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        //DistanciaTextView.setText(response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText());
                        // TiempoTextView.setText(response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText());
                        List<LatLng> list = decodePoly(response.body().getRoutes().get(0).getOverviewPolyline().getPoints());
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(20)
                                .color(Color.GREEN)
                                .geodesic(true)
                        );
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RouteResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void perdirPermisoLocalizacion() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, CODIGO_PETICION_LOCALIZACION);
    }

    private void mostrarLocalizacion() {
        if (mUltimaLocalizacion != null) {
            origin = new LatLng(mUltimaLocalizacion.getLatitude(), mUltimaLocalizacion.getLongitude());
            if (mCurrentLocationMarker == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(origin);
                markerOptions.title(getString(R.string.vCurrentPosition));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mCurrentLocationMarker = mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_NORMAL));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(origin)
                        .zoom(15.6f)
                        .bearing(90)
                        .tilt(60)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                mCurrentLocationMarker.setPosition(origin);
            }
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            dest = new LatLng(latitude, longitude);
            mMap.addMarker(
                    new MarkerOptions()
                            .position(dest)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .title(pubName)
            );
            //SetMapTime(getApplicationContext(), mMap, skyTheme);
        }
    }


    @SuppressLint("RestrictedApi")
    private void crearLocationRequest() throws SecurityException {
        mSolicitadorLocalizacion = new LocationRequest();
        mSolicitadorLocalizacion.setInterval(TAG_INTERVAL);
        mSolicitadorLocalizacion.setFastestInterval(TAG_FASTER_INTERVAL);
        mSolicitadorLocalizacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mSolicitadorLocalizacion.setMaxWaitTime(100);

        settingsApiBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mSolicitadorLocalizacion);

        verificarConfiguracionLocalizacion();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mSolicitadorLocalizacion, this);
    }

    private void verificarConfiguracionLocalizacion() {
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(settingsApiBuilder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            StartTracker();
            if (ModeRoute.equals("")) {
                ModeRoute = GOOGLEDIRECTION_MODE_WALKING;
            }
            DrawRoute(ModeRoute);
        });

        task.addOnFailureListener(this, e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsRouteActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        sendEx.printStackTrace();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    PopUPLocationPermission(getApplicationContext());
                    break;
            }
        });
    }


    private void StartTracker() throws SecurityException {
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mSolicitadorLocalizacion, myLocationCallback, null);
    }

    private void StopTracker() throws SecurityException {
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


}
