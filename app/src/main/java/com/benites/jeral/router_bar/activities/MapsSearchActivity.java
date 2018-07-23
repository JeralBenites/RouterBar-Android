package com.benites.jeral.router_bar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.benites.jeral.router_bar.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.tasks.Task;

import static com.benites.jeral.router_bar.util.Constants.CODIGO_PETICION_LOCALIZACION;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_CHECK_SETTINGS;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_LOCATION;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_PLAY_SERVICES;
import static com.benites.jeral.router_bar.util.Constants.TAG_FASTER_INTERVAL;
import static com.benites.jeral.router_bar.util.Constants.TAG_INTERVAL;
import static com.benites.jeral.router_bar.util.Constants.ZOOM_NORMAL;
import static com.benites.jeral.router_bar.util.Util.PopUPLocationPermission;

public class MapsSearchActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener {

    protected LocationManager locationManager;
    LatLng origin;
    RelativeLayout MapView;
    private GoogleMap mMap;
    private Location mUltimaLocalizacion;
    private Marker mCurrentLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mSolicitadorLocalizacion;
    private LocationSettingsRequest.Builder settingsApiBuilder;
    private LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            mUltimaLocalizacion = result.getLastLocation();
            ShowLocation();
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            if (!locationAvailability.isLocationAvailable()) {
                StopTracker();
                CheckGpsSettingsClient();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ui();
        ValidateGps();
    }

    private void ui() {
        MapView = findViewById(R.id.MapView);

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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setOnMapClickListener(latLng -> {
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latLng.latitude, latLng.longitude)).title("Nuevo Bar");
                mMap.clear();
                mMap.addMarker(marker);
                InsertPubActivity.Latitude = latLng.latitude;
                InsertPubActivity.Longitud = latLng.longitude;
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void ValidateGps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                BuildGoogleLocationAPI();
            } else {
                LocationPermission();
            }
        } else {
            BuildGoogleLocationAPI();
        }
    }


    private void BuildGoogleLocationAPI() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
                        mUltimaLocalizacion = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        ShowLocation();
                        CreateLocationRequest();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        switch (i) {
                            case 1:
                                Toast.makeText(MapsSearchActivity.this, "El Servicio Fue matado", Toast.LENGTH_SHORT).show();

                            case 2:
                                Toast.makeText(MapsSearchActivity.this, "El Dispositivo perdio la Conexion", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> Toast.makeText(MapsSearchActivity.this, R.string.google_error_conexion, Toast.LENGTH_SHORT).show())
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void LocationPermission() {
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, CODIGO_PETICION_LOCALIZACION);
    }

    @SuppressLint("RestrictedApi")
    private void CreateLocationRequest() throws SecurityException {
        mSolicitadorLocalizacion = new LocationRequest();
        mSolicitadorLocalizacion.setInterval(TAG_INTERVAL);
        mSolicitadorLocalizacion.setFastestInterval(TAG_FASTER_INTERVAL);
        mSolicitadorLocalizacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mSolicitadorLocalizacion.setMaxWaitTime(100);
        settingsApiBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mSolicitadorLocalizacion);
        CheckGpsSettingsClient();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mSolicitadorLocalizacion, this);
    }

    private void CheckGpsSettingsClient() {
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(settingsApiBuilder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> StartTracker());
        task.addOnFailureListener(this, e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsSearchActivity.this, REQUEST_CHECK_SETTINGS);
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

    private void ShowLocation() {
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
            //SetMapTime(getApplicationContext(), mMap, null);
        }
    }


    private void StartTracker() throws SecurityException {
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mSolicitadorLocalizacion, myLocationCallback, null);
    }

    private void StopTracker() throws SecurityException {
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLAY_SERVICES) {
            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            CheckGpsSettingsClient();
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
}
