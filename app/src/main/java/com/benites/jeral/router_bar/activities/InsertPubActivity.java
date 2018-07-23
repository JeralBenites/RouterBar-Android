package com.benites.jeral.router_bar.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.benites.jeral.router_bar.Fragments.PubFragment;
import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.model.AddressEntity;
import com.benites.jeral.router_bar.model.CoordenatesEntity;
import com.benites.jeral.router_bar.model.HourEntity;
import com.benites.jeral.router_bar.model.PubEntity;
import com.benites.jeral.router_bar.model.SocialEntity;
import com.benites.jeral.router_bar.storage.network.ApiClass;
import com.benites.jeral.router_bar.storage.network.ProgressRequestBody;
import com.benites.jeral.router_bar.storage.network.RouterApi;
import com.benites.jeral.router_bar.storage.network.entity.PubRaw;
import com.benites.jeral.router_bar.util.CameraUtils;
import com.benites.jeral.router_bar.util.MyFileProvider;
import com.benites.jeral.router_bar.util.SnackBarHelper;
import com.benites.jeral.router_bar.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.benites.jeral.router_bar.util.CameraUtils.rotateBitmap;
import static com.benites.jeral.router_bar.util.CameraUtils.scaleDown;
import static com.benites.jeral.router_bar.util.CameraUtils.storeBitmap;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_IMAGE_CAPTURE;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_IMAGE_SELECTED;
import static com.benites.jeral.router_bar.util.Constants.REQUEST_PERMISSIONS;
import static com.benites.jeral.router_bar.util.Constants.TAG_INSERT_PUB_ACTIVITY;

public class InsertPubActivity extends BaseActivity implements View.OnKeyListener, View.OnClickListener {

    private static final String TAG_RETAINED_FRAGMENT = "BarFragment";
    public static double Latitude = 0.0d;
    public static double Longitud = 0.0d;
    public static PubEntity pubEntity = new PubEntity();
    EditText vName;
    EditText vAddres;
    EditText vPhone;
    EditText vWasap;
    TextView tHourOpen;
    TextView tHourClose;
    CheckBox bDelivery;
    CheckBox d24Horas;
    TextView dLatitude;
    TextView dLongitude;
    ImageView vPicture;
    ImageView FotoImage;
    Button btnRegisterBar;
    View flayLoading;
    ImageView openHourImageView;
    ImageView closeHourImageView;
    ImageView vMap;
    Context mContext;
    private String photoPath;
    private PubFragment pubFragment;

    public static void start(Context context) {
        Intent starter = new Intent(context, InsertPubActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_pub);
        ui();
    }

    private void ui() {
        vName = findViewById(R.id.vName);
        vAddres = findViewById(R.id.vAddres);
        vPhone = findViewById(R.id.vPhone);
        vWasap = findViewById(R.id.vWasap);
        tHourOpen = findViewById(R.id.tHourOpen);
        tHourClose = findViewById(R.id.tHourClose);
        bDelivery = findViewById(R.id.bDelivery);
        d24Horas = findViewById(R.id.d24Horas);
        dLatitude = findViewById(R.id.dLatitude);
        dLongitude = findViewById(R.id.dLongitude);
        vPicture = findViewById(R.id.vPicture);
        FotoImage = findViewById(R.id.FotoImage);
        btnRegisterBar = findViewById(R.id.btnRegisterBar);
        flayLoading = findViewById(R.id.flayLoading);
        openHourImageView = findViewById(R.id.openHourImageView);
        closeHourImageView = findViewById(R.id.closeHourImageView);
        vMap = findViewById(R.id.vMap);

        mContext = getApplicationContext();
        dLatitude.setText(String.valueOf(Latitude));
        dLongitude.setText(String.valueOf(Longitud));
        Latitude = 0.0d;
        Longitud = 0.0d;
        validateFragment();

        vName.setOnKeyListener(this);
        vAddres.setOnKeyListener(this);
        vPhone.setOnKeyListener(this);
        tHourOpen.setOnKeyListener(this);
        tHourClose.setOnKeyListener(this);
        bDelivery.setOnKeyListener(this);
        d24Horas.setOnKeyListener(this);
        vWasap.setOnKeyListener(this);
        dLatitude.setOnKeyListener(this);
        dLongitude.setOnKeyListener(this);
        openHourImageView.setOnClickListener(this);
        closeHourImageView.setOnClickListener(this);
        vMap.setOnClickListener(this);
        vPicture.setOnClickListener(this);
        btnRegisterBar.setOnClickListener(this);
    }

    private void validateFragment() {
        //Buscar el fragment retenido en el reinicio del activity.
        FragmentManager fm = getSupportFragmentManager();
        pubFragment = (PubFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);
        if (pubFragment == null) {
            pubFragment = new PubFragment();
            fm.beginTransaction().add(pubFragment, TAG_RETAINED_FRAGMENT).commit();
        } else {
            vName.setText((pubFragment.getPub().getName() != null) ? pubFragment.getPub().getName() : "");
            vAddres.setText((pubFragment.getPub().getAddress().getStreet() != null) ? pubFragment.getPub().getAddress().getStreet() : "");
            vPhone.setText((pubFragment.getPub().getSocial().getPhone() != null) ? pubFragment.getPub().getSocial().getPhone() : "");
            tHourOpen.setText((pubFragment.getPub().getHour().getHourOpen() != null) ? pubFragment.getPub().getHour().getHourOpen() : "");
            tHourClose.setText((pubFragment.getPub().getHour().getHourClose() != null) ? pubFragment.getPub().getHour().getHourClose() : "");
            bDelivery.setChecked(pubFragment.getPub().getDelivery());
            d24Horas.setChecked(pubFragment.getPub().getHora24());
            vWasap.setText((pubFragment.getPub().getSocial().getWasap() != null) ? pubFragment.getPub().getSocial().getWasap() : "");
            dLatitude.setText(String.valueOf(pubFragment.getPub().getAddress().getCoord().getLatitude()));
            dLongitude.setText(String.valueOf(pubFragment.getPub().getAddress().getCoord().getLongitud()));
            photoPath = pubFragment.getvPhotoUrl();
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
            imageBitmap = scaleDown(imageBitmap, 1280, true);
            imageBitmap = rotateBitmap(imageBitmap, photoPath);
            FotoImage.setImageBitmap(imageBitmap);
            storeBitmap(imageBitmap, photoPath);
        }
    }

    private void showLoading() {
        flayLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        flayLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        pubEntity.setName(vName.getText().toString());
        AddressEntity adrress = new AddressEntity();
        CoordenatesEntity coord = new CoordenatesEntity();
        coord.setLongitud(Latitude);
        coord.setLatitude(Longitud);
        adrress.setStreet(vAddres.getText().toString());
        adrress.setCoord(coord);
        pubEntity.setAddress(adrress);
        HourEntity hourEntity = new HourEntity();
        hourEntity.setHourOpen(tHourOpen.getText().toString());
        hourEntity.setHourClose(tHourClose.getText().toString());
        pubEntity.setHour(hourEntity);
        pubEntity.setHora24(d24Horas.isChecked());
        pubEntity.setDelivery(bDelivery.isChecked());
        SocialEntity socialEntity = new SocialEntity();
        socialEntity.setPhone(vPhone.getText().toString());
        socialEntity.setWasap(vWasap.getText().toString());
        pubEntity.setSocial(socialEntity);
        pubEntity.setImage(photoPath);
        pubEntity.setUserRegister("xxxxx");
        pubFragment.setPub(pubEntity);
        pubFragment.setvPhotoUrl(photoPath);
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // https://developer.android.com/reference/android/app/Activity.html#isFinishing()
        // isFinishing.- verifica si el activity esta en el proceso de finalizacion. podria ser por que se esta llamando a finish()
        // comunmente, este metodo se usa en el onPause() para determinar si es solo una pausa o si el app se va a cerrar completamente.
        if (isFinishing()) {
            //si el usuario esta cerrando el app, ya no necesitaremos mas el fragment asi que lo removemos.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(pubFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        dLatitude.setText(String.valueOf(Latitude));
        dLongitude.setText(String.valueOf(Longitud));
        CoordenatesEntity coord = new CoordenatesEntity();
        coord.setLatitude(Latitude);
        coord.setLongitud(Longitud);
        AddressEntity ad = new AddressEntity();
        ad.setCoord(coord);
        pubEntity.setAddress(ad);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dLatitude.setText(String.valueOf(Latitude));
        dLongitude.setText(String.valueOf(Longitud));
        CoordenatesEntity coord = new CoordenatesEntity();
        coord.setLatitude(Latitude);
        coord.setLongitud(Longitud);
        AddressEntity ad = new AddressEntity();
        ad.setCoord(coord);
        pubEntity.setAddress(ad);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openHourImageView:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(InsertPubActivity.this, (timePicker, i, i1) -> {
                    java.util.Date DATE = new java.util.Date();
                    DATE.setTime(timePicker.getDrawingTime());
                    tHourOpen.setText(DateFormat.format("hh:mm:ss", new java.util.Date()).toString());

                }, hour, minute, true);
                mTimePicker.setTitle("Select Hora");
                mTimePicker.show();
                break;
            case R.id.closeHourImageView:
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker1 = new TimePickerDialog(InsertPubActivity.this, (timePicker, i, i1) -> {
                    java.util.Date DATE = new java.util.Date();
                    DATE.setTime(timePicker.getDrawingTime());
                    tHourClose.setText(DateFormat.format("hh:mm:ss", new java.util.Date()).toString());
                }, hour1, minute1, true);
                mTimePicker1.setTitle("Select Hora");
                mTimePicker1.show();
                break;
            case R.id.vPicture:
                takePicture(view);
                break;
            case R.id.vMap:
                startActivity(new Intent(InsertPubActivity.this, MapsSearchActivity.class));
                break;
            case R.id.btnRegisterBar:
                if (Util.checkConnection(mContext)) {
                    if (!vName.getText().toString().isEmpty()) {
                        if (!vAddres.getText().toString().isEmpty()) {
                            if (Latitude != 0.0d) {
                                if (Longitud != 0.0d) {
                                    if (!vPhone.getText().toString().isEmpty()) {
                                        if (!tHourOpen.getText().toString().isEmpty()) {
                                            if (!tHourClose.getText().toString().isEmpty()) {
                                                if (!TextUtils.isEmpty(photoPath)) {
                                                    PubEntity newPubEntity = new PubEntity();
                                                    newPubEntity.setName(vName.getText().toString());
                                                    AddressEntity adrress = new AddressEntity();
                                                    CoordenatesEntity coord = new CoordenatesEntity();
                                                    coord.setLongitud(Latitude);
                                                    coord.setLatitude(Longitud);
                                                    adrress.setCoord(coord);
                                                    adrress.setStreet(vAddres.getText().toString());
                                                    newPubEntity.setAddress(adrress);
                                                    HourEntity hourEntity = new HourEntity();
                                                    hourEntity.setHourOpen(tHourOpen.getText().toString());
                                                    hourEntity.setHourClose(tHourClose.getText().toString());
                                                    newPubEntity.setHour(hourEntity);
                                                    newPubEntity.setHora24(d24Horas.isChecked());
                                                    newPubEntity.setDelivery(bDelivery.isChecked());
                                                    SocialEntity socialEntity = new SocialEntity();
                                                    socialEntity.setPhone(vPhone.getText().toString());
                                                    socialEntity.setWasap(vWasap.getText().toString());
                                                    newPubEntity.setSocial(socialEntity);
                                                    newPubEntity.setImage(photoPath);
                                                    newPubEntity.setUserRegister("xxxxx");
                                                    uploadImage(newPubEntity);
                                                } else {
                                                    SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.string_message_to_attach_file));
                                                }
                                            } else {
                                                SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vtHourClose));
                                            }
                                        } else {
                                            SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vtHourOpen));
                                        }
                                    } else {
                                        SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vvPhone));
                                    }
                                } else {
                                    SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vLongitud));
                                }
                            } else {
                                SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vLatitude));
                            }
                        } else {
                            SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vvAddres));
                        }
                    } else {
                        SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.vvName));
                    }
                } else {
                    SnackBarHelper.showWarningMessage(view, mContext, getString(R.string.string_internet_connection_warning));
                }
                break;
        }
    }

    private void uploadImage(PubEntity pubEntity) {
        showLoading();
        File file = new File(photoPath);
        if (file.exists()) {
            ProgressRequestBody fileBody = new ProgressRequestBody(file, percentage -> Log.v(TAG_INSERT_PUB_ACTIVITY, "Porcentaje: " + percentage));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            Call<PubRaw> request = ApiClass.getRetrofit().create(RouterApi.class).InsertPub(filePart, pubEntity);
            request.enqueue(new Callback<PubRaw>() {
                @Override
                public void onResponse(Call<PubRaw> call, Response<PubRaw> response) {
                    hideLoading();
                    if (response.isSuccessful()) {
                        Log.e(TAG_INSERT_PUB_ACTIVITY, "Se Registro Correctamente");

                    } else {
                        Log.e(TAG_INSERT_PUB_ACTIVITY, "Error: Ocurrio algun problema");
                    }
                    next(PubsActivity.class, null, true);
                }

                @Override
                public void onFailure(Call<PubRaw> call, Throwable t) {
                    hideLoading();
                    Log.e(TAG_INSERT_PUB_ACTIVITY, "Error: " + t.getMessage());
                    t.printStackTrace();
                    next(PubsActivity.class, null, true);
                }
            });
        }
    }

    public void takePicture(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.vTituloOptionCamera)
                .setPositiveButton(R.string.vPickGallery, (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_SELECTED);
                })
                .setNegativeButton(R.string.vTakePhoto, (dialogInterface, i) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermissions();
                    } else {
                        goToCamera();
                    }
                })
                .setCancelable(true);
        dialog.create();
        dialog.show();
    }

    private void checkPermissions() {
        List<String> permissionsToAsk = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToAsk.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToAsk.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionsToAsk.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToAsk.toArray(new String[permissionsToAsk.size()]), REQUEST_PERMISSIONS);
        } else {
            goToCamera();
        }
    }

    private void goToCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoPath = CameraUtils.createImageFile(this);
            if (photoPath != null) {
                File fileImage = new File(photoPath);
                Uri fotoURI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fotoURI = MyFileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", fileImage);
                } else {
                    fotoURI = Uri.fromFile(fileImage);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 2) {
                boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean accessStorePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (accessStorePermission && cameraPermission)
                    goToCamera();
                else
                    Toast.makeText(this, "Los permisos son necesarios.", Toast.LENGTH_SHORT).show();
            } else {
                boolean permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (permission)
                    goToCamera();
                else
                    Toast.makeText(this, "Los permisos son necesarios.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //INICIANDO EL SERVICIO PARA SUBIR LA IMAGEN
            // Guardar la imagen miniatura de la foto.
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Obtener la imagen grande de la foto.
            //Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
            //ivPhoto.setImageBitmap(imageBitmap);

            //Reducir de tamaño y calidad la foto grande.
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
            imageBitmap = scaleDown(imageBitmap, 1280, true);
            imageBitmap = rotateBitmap(imageBitmap, photoPath);

            FotoImage.setImageBitmap(imageBitmap);
            storeBitmap(imageBitmap, photoPath);

            pubEntity.setName(vName.getText().toString());
            AddressEntity adrress = new AddressEntity();
            CoordenatesEntity coord = new CoordenatesEntity();
            coord.setLongitud(Latitude);
            coord.setLatitude(Longitud);
            adrress.setStreet(vAddres.getText().toString());
            adrress.setCoord(coord);
            pubEntity.setAddress(adrress);
            HourEntity hourEntity = new HourEntity();
            hourEntity.setHourOpen(tHourOpen.getText().toString());
            hourEntity.setHourClose(tHourClose.getText().toString());
            pubEntity.setHour(hourEntity);
            pubEntity.setHora24(d24Horas.isChecked());
            pubEntity.setDelivery(bDelivery.isChecked());
            SocialEntity socialEntity = new SocialEntity();
            socialEntity.setPhone(vPhone.getText().toString());
            socialEntity.setWasap(vWasap.getText().toString());
            pubEntity.setSocial(socialEntity);
            pubEntity.setImage(photoPath);
            pubEntity.setUserRegister("xxxx");
            pubFragment.setPub(pubEntity);
            pubFragment.setvPhotoUrl(photoPath);
        }
        if (requestCode == REQUEST_IMAGE_SELECTED && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                photoPath = getPathFromURI(uri);
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageBitmap = scaleDown(imageBitmap, 1280, true);
                imageBitmap = rotateBitmap(imageBitmap, photoPath);

                FotoImage.setImageBitmap(imageBitmap);
                storeBitmap(imageBitmap, photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pubEntity.setName(vName.getText().toString());
            AddressEntity adrress = new AddressEntity();
            CoordenatesEntity coord = new CoordenatesEntity();
            coord.setLongitud(Latitude);
            coord.setLatitude(Longitud);
            adrress.setStreet(vAddres.getText().toString());
            adrress.setCoord(coord);
            pubEntity.setAddress(adrress);
            HourEntity hourEntity = new HourEntity();
            hourEntity.setHourOpen(tHourOpen.getText().toString());
            hourEntity.setHourClose(tHourClose.getText().toString());
            pubEntity.setHour(hourEntity);
            pubEntity.setHora24(d24Horas.isChecked());
            pubEntity.setDelivery(bDelivery.isChecked());
            SocialEntity socialEntity = new SocialEntity();
            socialEntity.setPhone(vPhone.getText().toString());
            socialEntity.setWasap(vWasap.getText().toString());
            pubEntity.setSocial(socialEntity);
            pubEntity.setImage(photoPath);
            pubEntity.setUserRegister("xxxx");
            pubFragment.setPub(pubEntity);
            pubFragment.setvPhotoUrl(photoPath);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public String getPathFromURI(Uri uri) {
        String realPath = "";
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                realPath = cursor.getString(column_index);
            }
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                realPath = cursor.getString(column_index);
            }
        }
        // SDK > 19 (Android 4.4)
        else {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            int columnIndex;
            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    realPath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return realPath;
    }

}
