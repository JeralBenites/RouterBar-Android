package com.benites.jeral.router_bar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.model.PubEntity;
import com.benites.jeral.router_bar.storage.network.ApiClass;
import com.benites.jeral.router_bar.storage.network.RouterApi;
import com.benites.jeral.router_bar.storage.network.entity.PubListRaw;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.benites.jeral.router_bar.storage.preferences.PreferencesHelper.signOut;
import static com.benites.jeral.router_bar.util.Constants.BAR_NAME;
import static com.benites.jeral.router_bar.util.Constants.CODIGO_PETICION_LOCALIZACION;
import static com.benites.jeral.router_bar.util.Constants.LATITUDE;
import static com.benites.jeral.router_bar.util.Constants.LONGITUDE;
import static com.benites.jeral.router_bar.util.Constants.MRADIOUS;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_CHECK_SETTINGS;
import static com.benites.jeral.router_bar.util.Constants.TAG_FASTER_INTERVAL;
import static com.benites.jeral.router_bar.util.Constants.TAG_INTERVAL;
import static com.benites.jeral.router_bar.util.Constants.ZOOM_NORMAL;
import static com.benites.jeral.router_bar.util.Util.PopUPLocationPermission;
import static com.benites.jeral.router_bar.util.Util.SetMapTime;

public class MainMapsActivity extends BaseActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private View customView;
    private GoogleMap mMap;
    private LatLng origin;
    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mUltimaLocalizacion;
    private Marker mCurrentLocationMarker;
    private LocationRequest mSolicitadorLocalizacion;
    private LocationSettingsRequest.Builder settingsApiBuilder;
    private int iCantBares = 0;
    private List<PubEntity> pubEntities = new ArrayList<>();
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
        setContentView(R.layout.activity_main_maps);
        ui();
    }

    public void ui() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "No esta Listo el Mapa", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
        mMap = googleMap;
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setOnMarkerClickListener(marker -> {
                CallPopUp((PubEntity) marker.getTag());
                return false;
            });
            ValidateGps();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void ValidateGps() {
        if (!checkReady()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int locationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (locationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                BuildGoogleLocationAPI();
            } else {
                LocationPermission();
            }
        } else {
            BuildGoogleLocationAPI();
        }
    }


    private void BuildGoogleLocationAPI() {
        if (mGoogleApiClient != null) return;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(connectionResult -> Toast.makeText(MainMapsActivity.this, R.string.google_error_conexion, Toast.LENGTH_SHORT).show())
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
    }

    private void LocationPermission() {
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, CODIGO_PETICION_LOCALIZACION);
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
            mMap.setBuildingsEnabled(true);
        }
        SetMapTime(getApplicationContext(), mMap, null);
        GetBars();
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
        StartTracker();
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
                        resolvable.startResolutionForResult(MainMapsActivity.this, REQUEST_CHECK_SETTINGS);
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

    private void GetBars() {
        mMap.clear();
        int x = pubEntities.size();
        if (iCantBares == 0 || iCantBares != pubEntities.size()) {
            Call<PubListRaw> listCall = ApiClass.getRetrofit().create(RouterApi.class).listPubs();
            listCall.enqueue(new Callback<PubListRaw>() {
                @Override
                public void onResponse(@NonNull Call<PubListRaw> call, @NonNull Response<PubListRaw> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (origin != null) {
                                pubEntities = response.body().getPubEntity();
                                iCantBares = pubEntities.size();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(origin);
                                markerOptions.title(getString(R.string.vCurrentPosition));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                mMap.addMarker(markerOptions);
                                mMap.addCircle(new CircleOptions()
                                        .center(origin)
                                        .radius(MRADIOUS)
                                        .strokeWidth(2)
                                        .strokeColor(Color.CYAN)
                                        .fillColor(Color.TRANSPARENT));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PubListRaw> call, @NonNull Throwable t) {
                    Toast.makeText(MainMapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (origin != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(origin);
                markerOptions.title(getString(R.string.vCurrentPosition));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(markerOptions);
                mMap.addCircle(new CircleOptions()
                        .center(origin)
                        .radius(MRADIOUS)
                        .strokeWidth(2)
                        .strokeColor(Color.CYAN)
                        .fillColor(Color.TRANSPARENT));
            }
        }
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.beerpin);
        for (PubEntity e : pubEntities) {
            if (getDistance(origin, e.getAddress().getCoord().getLatitude(), e.getAddress().getCoord().getLongitud()) <= MRADIOUS) {
                MarkerOptions marker1 = new MarkerOptions()
                        .position(new LatLng(e.getAddress().getCoord().getLatitude(), e.getAddress().getCoord().getLongitud()))
                        .title(e.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 80, 100, false)));
                mMap.addMarker(marker1).setTag(e);
            }
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
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ShowLocation();
        CreateLocationRequest();

    }

    @Override
    public void onConnectionSuspended(int i) {
        switch (i) {
            case 1:
                Toast.makeText(MainMapsActivity.this, "El Servicio Fue matado", Toast.LENGTH_SHORT).show();

            case 2:
                Toast.makeText(MainMapsActivity.this, "El Dispositivo perdio la Conexion", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        ShowLocation();
        return false;
    }


    public float getDistance(LatLng Origin, double DestinyLat, double DestinyLng) {
        Location myLocation = new Location("");
        myLocation.setLatitude(Origin.latitude);
        myLocation.setLongitude(Origin.longitude);
        Location l1 = new Location("");
        l1.setLatitude(DestinyLat);
        l1.setLongitude(DestinyLng);
        return l1.distanceTo(myLocation);
    }

    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.vTextLogaout)
                .setTitle(R.string.vCerrarSession)
                .setPositiveButton(R.string.vAceptar, (dialogInterface, i) -> {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.disconnect();
                    }
                    signOut(this);
                    next(LoginActivity.class, null, true);

                })
                .setNegativeButton(R.string.vCancelar, (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(true);
        dialog.create();
        dialog.show();
    }

    private PubEntity check(List<PubEntity> userList, final String targetName) {
        for (PubEntity o : userList) {
            if (o != null && o.getName().equals(targetName)) {
                return o;
            }
        }
        return null;
    }

    private boolean CallPopUp(PubEntity pubEntity) {
        if (pubEntity != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle(pubEntity.getName())
                    .setCancelable(true);
            LayoutInflater inflater = getLayoutInflater();
            customView = inflater.inflate(R.layout.bar_information, null);
            ImageView imageView = customView.findViewById(R.id.barImageView);
            TextView address = customView.findViewById(R.id.barAddress);
            TextView OpenHour = customView.findViewById(R.id.OpenHour);
            TextView CloseHour = customView.findViewById(R.id.CloseHour);
            TextView vFono = customView.findViewById(R.id.vFono);
            TextView vWasap = customView.findViewById(R.id.vWasap);
            CheckBox chbDelivery = customView.findViewById(R.id.chbDelivery);
            CheckBox chb24Horas = customView.findViewById(R.id.chb24Horas);
            ImageView gpsBar = customView.findViewById(R.id.gpsFromBar);
            ImageView phoneNumber = customView.findViewById(R.id.phone);
            address.setText(pubEntity.getAddress().getStreet());
            OpenHour.setText(pubEntity.getHour().getHourOpen());
            CloseHour.setText(pubEntity.getHour().getHourClose());
            vFono.setText(pubEntity.getSocial().getPhone());
            vWasap.setText(pubEntity.getSocial().getWasap());
            chbDelivery.setChecked(pubEntity.getDelivery());
            chb24Horas.setChecked(pubEntity.getHora24());
            chbDelivery.setEnabled(false);
            chb24Horas.setEnabled(false);
            byte[] decodedString = Base64.decode(pubEntity.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
            imageView.setOnClickListener(v -> {
                /*Bundle bundle = new Bundle();
                bundle.putLong(BAR_ID, bar.getiCodBar());
                Intent intent = new Intent(MainMapActivity.this, CatalogActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);*/
            });
            gpsBar.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putDouble(LATITUDE, pubEntity.getAddress().getCoord().getLatitude());
                bundle.putDouble(LONGITUDE, pubEntity.getAddress().getCoord().getLongitud());
                bundle.putString(BAR_NAME, pubEntity.getName());
                next(MapsRouteActivity.class, bundle, false);
            });
            phoneNumber.setOnClickListener(v -> v.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pubEntity.getSocial().getPhone(), null))));
            dialog.setView(customView);
            dialog.create();
            dialog.show();

        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODIGO_PETICION_LOCALIZACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                BuildGoogleLocationAPI();
            } else {
                LocationPermission();
            }
        }
    }

}
