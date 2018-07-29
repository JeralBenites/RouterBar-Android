package pe.com.dev420.router_bar.activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import pe.com.dev420.router_bar.fragments.PubFragment;
import pe.com.dev420.router_bar.model.AddressEntity;
import pe.com.dev420.router_bar.model.CoordenatesEntity;
import pe.com.dev420.router_bar.model.HourEntity;
import pe.com.dev420.router_bar.model.PubEntity;
import pe.com.dev420.router_bar.model.SocialEntity;
import pe.com.dev420.router_bar.storage.network.ApiClass;
import pe.com.dev420.router_bar.storage.network.ProgressRequestBody;
import pe.com.dev420.router_bar.storage.network.RouterApi;
import pe.com.dev420.router_bar.storage.network.entity.PubEntityRaw;
import pe.com.dev420.router_bar.util.SnackBarHelper;
import pe.com.dev420.router_bar.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pe.com.dev420.router_bar.storage.preferences.PreferencesHelper.getIDSession;
import static pe.com.dev420.router_bar.util.CameraUtils.rotateBitmap;
import static pe.com.dev420.router_bar.util.CameraUtils.storeBitmap;
import static pe.com.dev420.router_bar.util.Constants.TAG_INSERT_PUB_ACTIVITY;
import static pe.com.dev420.router_bar.util.Constants.TAG_RETAINED_FRAGMENT;
import static pe.com.dev420.router_bar.util.Util.scaleDown;

public class InsertPubActivity extends BaseMediaActivity
        implements View.OnKeyListener,
        View.OnClickListener {

    public static double Latitude = 0.0d;
    public static double Longitud = 0.0d;
    private PubEntity pubEntity;
    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    private String street = "";
    private EditText pubNameEditText, pubPhoneEditText;
    private TextView pubHourOpenTextView, pubHourCloseTextView, pubLatitudeTextView, pubLongitudeTextView;
    private CheckBox pubDeliveryCheckBox, pub24CheckBox;
    private ImageView searchLocImageView, pubCameraImageView, pubImageImageView;

    private FrameLayout flayLoading;
    private Button registerButton;
    private Context mContext;
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
        enabledBack();
        setupMedia();
    }

    private ScrollView parentView;

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().remove(pubFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCoord();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCoord();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        pubFragment.setPub(setData());
        return false;
    }

    private void ui() {
        pubNameEditText = findViewById(R.id.pubNameEditText);
        pubPhoneEditText = findViewById(R.id.pubPhoneEditText);
        pubHourOpenTextView = findViewById(R.id.pubHourOpenTextView);
        pubHourCloseTextView = findViewById(R.id.pubHourCloseTextView);
        pubLatitudeTextView = findViewById(R.id.pubLatitudeTextView);
        pubLongitudeTextView = findViewById(R.id.pubLongitudeTextView);
        pubDeliveryCheckBox = findViewById(R.id.pubDeliveryCheckBox);
        pub24CheckBox = findViewById(R.id.pub24CheckBox);
        searchLocImageView = findViewById(R.id.searchLocImageView);
        pubCameraImageView = findViewById(R.id.pubCameraImageView);
        pubImageImageView = findViewById(R.id.pubImageImageView);
        registerButton = findViewById(R.id.registerButton);
        flayLoading = findViewById(R.id.flayLoading);
        parentView = findViewById(R.id.parentView);
        mContext = getApplicationContext();
        pubEntity = new PubEntity();
        Latitude = 0.0d;
        Longitud = 0.0d;
        pubLatitudeTextView.setText(String.valueOf(Latitude));
        pubLongitudeTextView.setText(String.valueOf(Longitud));
        validateFragment();
        setListener();
    }

    @Override
    public void onClick(View view) {
        pubFragment.setPub(setData());
        switch (view.getId()) {
            case R.id.pubHourOpenTextView:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(InsertPubActivity.this, (timePicker, i, i1) -> {
                    java.util.Date DATE = new java.util.Date();
                    DATE.setTime(timePicker.getDrawingTime());
                    pubHourOpenTextView.setText(DateFormat.format("hh:mm:ss", new java.util.Date()).toString());

                }, hour, minute, true);
                mTimePicker.setTitle("Select Hora");
                mTimePicker.show();
                break;
            case R.id.pubHourCloseTextView:
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker1 = new TimePickerDialog(InsertPubActivity.this, (timePicker, i, i1) -> {
                    java.util.Date DATE = new java.util.Date();
                    DATE.setTime(timePicker.getDrawingTime());
                    pubHourCloseTextView.setText(DateFormat.format("hh:mm:ss", new java.util.Date()).toString());
                }, hour1, minute1, true);
                mTimePicker1.setTitle("Select Hora");
                mTimePicker1.show();
                break;
            case R.id.pubCameraImageView:
                takePicture(view);
                break;
            case R.id.searchLocImageView:
                next(MapsSearchActivity.class, null, false);
                break;
            case R.id.registerButton:
                if (validatePub()) uploadImage();
                break;
        }
    }

    private boolean validatePub() {
        if (!Util.checkConnection(mContext)) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.string_internet_connection_warning));
            return false;
        }
        if (pubNameEditText.getText().toString().isEmpty()) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.vvName));
            return false;
        }
        if (Latitude == 0.0d) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.vLatitude));
            return false;
        }
        if (Longitud == 0.0d) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.vLongitud));
            return false;
        }
        if (pubPhoneEditText.getText().toString().isEmpty()) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.vvPhone));
            return false;
        }
        if (pubHourOpenTextView.getText().toString().isEmpty()) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.vtHourOpen));
            return false;
        }
        if (pubHourCloseTextView.getText().toString().isEmpty()) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.vtHourClose));
            return false;
        }
        if (TextUtils.isEmpty(currentPhotoPath)) {
            SnackBarHelper.showWarningMessage(parentView, mContext, getString(R.string.string_message_to_attach_file));
            return false;
        }
        return true;
    }

    public void takePicture(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.vTituloOptionCamera)
                .setPositiveButton(R.string.vPickGallery, (dialogInterface, i) -> {
                    gotoPhoto();
                   /* Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_SELECTED);*/
                })
                .setNegativeButton(R.string.vTakePhoto, (dialogInterface, i) -> {
                    gotoCamera();
                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermissions();
                    } else {
                        goToCamera();
                    }*/
                })
                .setCancelable(true);
        dialog.create();
        dialog.show();
    }

    private void gotoPhoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivityForResult(galleryIntent, ACTION_GALLERY_PHOTO);
    }

   /* private void gotoCamera() {

       boolean cameraAvailable= intentHelper.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE);
        if(!cameraAvailable)return;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            File f = null;
            try {
                f = setUpPhotoFile();
                currentPhotoPath = f.getAbsolutePath();

                Uri photoUri= FileProvider.getUriForFile(this,getApplicationContext().getPackageName(),f);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                currentPhotoPath = null;
            }
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    private void uploadImage() {
        loading(flayLoading, true);
        File file = new File(currentPhotoPath);
        if (file.exists()) {
            ProgressRequestBody fileBody = new ProgressRequestBody(file, percentage ->
                    Log.v(TAG_INSERT_PUB_ACTIVITY, "Percentage :" + percentage)
            );
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            Call<PubEntityRaw> request = ApiClass.getRetrofit().create(RouterApi.class).insertPub(filePart, setData());
            request.enqueue(new Callback<PubEntityRaw>() {
                @Override
                public void onResponse(Call<PubEntityRaw> call, Response<PubEntityRaw> response) {
                    currentPhotoPath = null;
                    if (response.isSuccessful())
                        Log.e(TAG_INSERT_PUB_ACTIVITY, "Se Registro Correctamente");
                    else
                        Log.e(TAG_INSERT_PUB_ACTIVITY, "Error: Ocurrio algun problema");
                    loading(flayLoading, false);
                    next(PubsActivity.class, null, true);
                }

                @Override
                public void onFailure(Call<PubEntityRaw> call, Throwable t) {
                    loading(flayLoading, false);
                    Log.e(TAG_INSERT_PUB_ACTIVITY, "Error: " + t.getMessage());
                    currentPhotoPath = null;
                    next(PubsActivity.class, null, true);
                }
            });
        }
    }

    private void setListener() {
        pubHourOpenTextView.setOnClickListener(this);
        pubHourCloseTextView.setOnClickListener(this);
        searchLocImageView.setOnClickListener(this);
        pubCameraImageView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        pubDeliveryCheckBox.setOnClickListener(this);
        pub24CheckBox.setOnClickListener(this);

        pubNameEditText.setOnKeyListener(this);
        pubPhoneEditText.setOnKeyListener(this);
        pubHourOpenTextView.setOnKeyListener(this);
        pubHourCloseTextView.setOnKeyListener(this);
    }

    private void validateFragment() {
        FragmentManager fm = getSupportFragmentManager();
        pubFragment = (PubFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);
        if (pubFragment == null) {
            pubFragment = new PubFragment();
            fm.beginTransaction().add(pubFragment, TAG_RETAINED_FRAGMENT).commit();
        } else {
            if (pubFragment.getPub().getName() != null)
                pubNameEditText.setText(pubFragment.getPub().getName());
            if (pubFragment.getPub().getSocial().getPhone() != null)
                pubPhoneEditText.setText(pubFragment.getPub().getSocial().getPhone());
            if (pubFragment.getPub().getHour().getHourOpen() != null)
                pubHourOpenTextView.setText(pubFragment.getPub().getHour().getHourOpen());
            if (pubFragment.getPub().getHour().getHourClose() != null)
                pubHourCloseTextView.setText(pubFragment.getPub().getHour().getHourClose());
            if (pubFragment.getPub().getDelivery() != null)
                pubDeliveryCheckBox.setChecked(pubFragment.getPub().getDelivery());
            if (pubFragment.getPub().getHora24() != null)
                pub24CheckBox.setChecked(pubFragment.getPub().getHora24());
            if (pubFragment.getPub().getHora24() != null)
                pub24CheckBox.setChecked(pubFragment.getPub().getHora24());
            if (pubFragment.getPub().getAddress().getLoc().getCoordinates() != null) {
                pubLatitudeTextView.setText(String.valueOf(pubFragment.getPub().getAddress().getLoc().getCoordinates()[1]));
                pubLongitudeTextView.setText(String.valueOf(pubFragment.getPub().getAddress().getLoc().getCoordinates()[0]));
            }
            if (pubFragment.getPub().getImage() != null) {
                currentPhotoPath = pubFragment.getPub().getImage();
                Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
                imageBitmap = scaleDown(imageBitmap, 1280, true);
                imageBitmap = rotateBitmap(imageBitmap, currentPhotoPath);
                pubImageImageView.setImageBitmap(imageBitmap);
                storeBitmap(imageBitmap, currentPhotoPath);
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (Longitud != 0.0d && Latitude != 0.0d)
            getStreet();
    }

    private PubEntity setData() {
        if (Longitud != 0.0d && Latitude != 0.0d)
            getStreet();
        CoordenatesEntity coord = new CoordenatesEntity();
        Double[] array = new Double[2];
        array[0] = Longitud;
        array[1] = Latitude;
        coord.setType("Point");
        coord.setCoordinates(array);
        AddressEntity adrress = new AddressEntity();
        adrress.setLoc(coord);
        adrress.setStreet(street);
        HourEntity hourEntity = new HourEntity();
        hourEntity.setHourOpen(pubHourOpenTextView.getText().toString());
        hourEntity.setHourClose(pubHourCloseTextView.getText().toString());
        SocialEntity socialEntity = new SocialEntity();
        socialEntity.setPhone(pubPhoneEditText.getText().toString());
        PubEntity entity = new PubEntity();
        entity.setAddress(adrress);
        entity.setHour(hourEntity);
        entity.setSocial(socialEntity);
        entity.setName(pubNameEditText.getText().toString());
        entity.setHora24(pub24CheckBox.isChecked());
        entity.setDelivery(pubDeliveryCheckBox.isChecked());
        entity.setImage(currentPhotoPath);
        entity.setUserRegister(getIDSession(mContext));
        return entity;
    }

      /*  private void checkPermissions() {
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
    }*/



   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Reducir de tamaño y calidad la foto grande.
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoPath);
            imageBitmap = rotateBitmap(imageBitmap, photoPath);
            imageBitmap = scaleDown(imageBitmap, 1280, true);

            FotoImage.setImageBitmap(imageBitmap);
            storeBitmap(imageBitmap, photoPath);
            pubFragment.setPub(setData());
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
            pubFragment.setPub(setData());
            pubFragment.setvPhotoUrl(photoPath);
        }
    }*/


    private void gotoCamera() {

        boolean cameraAvailable = intentHelper.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE);
        if (!cameraAvailable) return;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File f = null;
        try {
            f = createImageFile();
            currentPhotoPath = f.getAbsolutePath();

            Uri photoUri = FileProvider.getUriForFile(this, "com.benites.android.fileprovider", f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            currentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_GALLERY_PHOTO: {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        processPhotoGallery(data.getData());
                    }
                }
                break;
            }

            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    processPhoto();
                }
                break;
            }
        }
    }

    private void setCoord() {
        pubLatitudeTextView.setText(String.valueOf(Latitude));
        pubLongitudeTextView.setText(String.valueOf(Longitud));
        CoordenatesEntity coord = new CoordenatesEntity();
        Double[] array = new Double[2];
        array[0] = Longitud;
        array[1] = Latitude;
        coord.setType("Point");
        coord.setCoordinates(array);
        AddressEntity ad = new AddressEntity();
        ad.setLoc(coord);
        ad.setStreet(street);
        pubEntity.setAddress(ad);
    }

    @Override
    protected void renderPhoto() {
        super.renderPhoto();
        Bitmap bitmap = imageHelper.bitmapByPath(pubImageImageView.getWidth(),
                pubImageImageView.getHeight(), currentPhotoPath);
        bitmap = scaleDown(bitmap, 1280, true);
        bitmap = rotateBitmap(bitmap, currentPhotoPath);
        storeBitmap(bitmap, currentPhotoPath);
        pubImageImageView.setImageBitmap(bitmap);
    }

    private void getStreet() {
        try {
            List<Address> addresses = geocoder.getFromLocation(Latitude, Longitud, 1);
            if (addresses != null) {
                Address a = addresses.get(0);
                street = a.getThoroughfare() + "-" + a.getSubThoroughfare() + "/" + a.getSubLocality() + "/" + a.getLocality() + "/" + a.getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
