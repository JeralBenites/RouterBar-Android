package pe.com.dev420.router_bar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import pe.com.dev420.router_bar.model.AddressEntity;
import pe.com.dev420.router_bar.model.CoordenatesEntity;
import pe.com.dev420.router_bar.model.PubEntity;
import pe.com.dev420.router_bar.storage.network.ApiClass;
import pe.com.dev420.router_bar.storage.network.RouterApi;
import pe.com.dev420.router_bar.storage.network.entity.PubEntityRaw;
import pe.com.dev420.router_bar.storage.network.entity.PubListRaw;
import pe.com.dev420.router_bar.ui.dialog.PubInformation;
import pe.com.dev420.router_bar.ui.dialog.PubInformationDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pe.com.dev420.router_bar.storage.preferences.PreferencesHelper.signOut;
import static pe.com.dev420.router_bar.util.Constants.BAR_NAME;
import static pe.com.dev420.router_bar.util.Constants.CODIGO_PETICION_LOCALIZACION;
import static pe.com.dev420.router_bar.util.Constants.LATITUDE;
import static pe.com.dev420.router_bar.util.Constants.LONGITUDE;
import static pe.com.dev420.router_bar.util.Constants.MRADIOUS;
import static pe.com.dev420.router_bar.util.Constants.REQUEST_CHECK_SETTINGS;
import static pe.com.dev420.router_bar.util.Constants.TAG_FASTER_INTERVAL;
import static pe.com.dev420.router_bar.util.Constants.TAG_INTERVAL;
import static pe.com.dev420.router_bar.util.Constants.ZOOM_NORMAL;
import static pe.com.dev420.router_bar.util.Util.PopUPLocationPermission;
import static pe.com.dev420.router_bar.util.Util.SetMapTime;

public class MainMapsActivity extends BaseActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PubInformation {
    public PubEntity pubEntity, PubEntityInfo;
    private FrameLayout layout_loading;
    private GoogleMap mMap;
    private LatLng origin;
    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Location mUltimaLocalizacion;
    private Marker mCurrentLocationMarker;
    private LocationRequest mSolicitadorLocalizacion;
    private LocationSettingsRequest.Builder settingsApiBuilder;
    private List<PubEntity> pubEntities = new ArrayList<>();
    private PubInformationDialog dialog;
    private LocationCallback myLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            mUltimaLocalizacion = result.getLastLocation();
            ShowLocation();
            GetBars();
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
        layout_loading = findViewById(R.id.flayLoading);
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
                String mark = (String) marker.getTag();
                if (!mark.equals("no_id"))
                    CallPopUp(mark);
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
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
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
                        .zoom(16.0f)
                        .bearing(90)
                        .tilt(60)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                GetBars();
            } else {
                mCurrentLocationMarker.setPosition(origin);
            }
            mMap.setBuildingsEnabled(true);
        }
        SetMapTime(getApplicationContext(), mMap, null);
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

    private PubEntity setLtng() {
        if (mUltimaLocalizacion != null) {
            CoordenatesEntity coordenatesEntity = new CoordenatesEntity();
            Double[] array = new Double[2];
            array[1] = mUltimaLocalizacion.getLatitude();
            array[0] = mUltimaLocalizacion.getLongitude();
            coordenatesEntity.setCoordinates(array);
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setLoc(coordenatesEntity);
            addressEntity.setRadius(.004d);
            pubEntity = new PubEntity();
            pubEntity.setAddress(addressEntity);
        } else {
            pubEntity = new PubEntity();
        }
        return pubEntity;
    }

    private void GetBars() {
        mMap.clear();
        Call<PubListRaw> listCall = ApiClass.getRetrofit().create(RouterApi.class).listPubsByCoordenates(setLtng());
        listCall.enqueue(new Callback<PubListRaw>() {
            @Override
            public void onResponse(@NonNull Call<PubListRaw> call, @NonNull Response<PubListRaw> response) {
                if (response.isSuccessful()) {
                    pubEntities = response.body().getPubEntity();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(origin);
                    markerOptions.title(getString(R.string.vCurrentPosition));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(markerOptions).setTag("no_id");
                    mMap.addCircle(new CircleOptions()
                            .center(origin)
                            .radius(MRADIOUS)
                            .strokeWidth(2)
                            .strokeColor(Color.CYAN)
                            .fillColor(Color.TRANSPARENT));
                    setNearPubs(pubEntities);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PubListRaw> call, @NonNull Throwable t) {
                Toast.makeText(MainMapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNearPubs(List<PubEntity> nearPubs) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.beerpin);
        for (PubEntity e : nearPubs) {
            MarkerOptions marker1 = new MarkerOptions()
                    .position(new LatLng(e.getAddress().getLoc().getCoordinates()[1], e.getAddress().getLoc().getCoordinates()[0]))
                    .title(e.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 80, 100, false)));
            mMap.addMarker(marker1).setTag(e.get_id());
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
        GetBars();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        ShowLocation();
        return false;
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

    private boolean CallPopUp(String id) {
        loading(layout_loading, true);
        Call<PubEntityRaw> listCall = ApiClass.getRetrofit().create(RouterApi.class).listPubsById(id);
        listCall.enqueue(new Callback<PubEntityRaw>() {
            @Override
            public void onResponse(Call<PubEntityRaw> call, Response<PubEntityRaw> response) {
                if (response.isSuccessful()) {
                    loadDialog(response.body().getPubEntity());
                }
                loading(layout_loading, false);
            }

            @Override
            public void onFailure(Call<PubEntityRaw> call, Throwable t) {
                loading(layout_loading, false);
                Toast.makeText(MainMapsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return false;
    }

    private void loadDialog(PubEntity pubEntity) {
        PubEntityInfo = pubEntity;
        dialog = new PubInformationDialog();
        dialog.show(getSupportFragmentManager(), "PubInformationDialogFragment");
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

    @Override
    public void goCatalog(PubEntity pubEntity) {
        /*Bundle bundle = new Bundle();
        bundle.putLong(BAR_ID, pubEntity.getiCodBar());
        Intent intent = new Intent(MainMapsActivity.this, CatalogActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);*/
    }

    @Override
    public void goGps(PubEntity pubEntity) {
        Bundle bundle = new Bundle();
        bundle.putDouble(LATITUDE, pubEntity.getAddress().getLoc().getCoordinates()[1]);
        bundle.putDouble(LONGITUDE, pubEntity.getAddress().getLoc().getCoordinates()[0]);
        bundle.putString(BAR_NAME, pubEntity.getName());
        next(MapsRouteActivity.class, bundle, false);
    }

    @Override
    public void goPhone(PubEntity pubEntity) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pubEntity.getSocial().getPhone(), null)));
    }
}
