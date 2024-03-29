package pe.com.dev420.router_bar.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import pe.com.dev420.router_bar.util.helpers.ImageHelper;
import pe.com.dev420.router_bar.util.helpers.IntentHelper;
import pe.com.dev420.router_bar.util.media.AlbumStorageDirFactory;
import pe.com.dev420.router_bar.util.media.BaseAlbumDirFactory;
import pe.com.dev420.router_bar.util.media.FroyoAlbumDirFactory;

/**
 * Created by emedinaa on 24/02/17.
 */

public abstract class BaseMediaActivity extends AppCompatActivity {

    protected static final int ACTION_TAKE_PHOTO = 1;
    protected static final int ACTION_GALLERY_PHOTO = 2;
    protected static final String JPEG_FILE_PREFIX = "IMG_";
    protected static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "Media";
    private final int REQUEST_WRITE_STORAGE = 100;

    protected AlbumStorageDirFactory albumStorageDirFactory = null;
    protected String currentPhotoPath;
    protected IntentHelper intentHelper;
    protected ImageHelper imageHelper;

    protected void setupMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            albumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            albumStorageDirFactory = new BaseAlbumDirFactory();
        }
        intentHelper = new IntentHelper();
        imageHelper = new ImageHelper();

        checkPermissions();
    }

    /* check Permission */
    protected void checkPermissions() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return "album_tmp";
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = albumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(TAG, "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /*private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }*/

    private File createImageFile() {

        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        return pictureFile;
    }

    protected File setUpPhotoFile() {
        File f = createImageFile();
        currentPhotoPath = f.getAbsolutePath();

        return f;
    }

    protected void processPhoto() {
        if (currentPhotoPath != null) {
            renderPhoto();
            // currentPhotoPath = null;
        }
    }

    protected void processPhotoGallery(Uri photoUri) {
        currentPhotoPath = pathByUri(photoUri);
        if (currentPhotoPath != null) {
            renderPhoto();
            /// currentPhotoPath = null;
        }
    }

    private String pathByUri(Uri uri) {
        String path;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor == null || cursor.getCount() < 1) {
            path = null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        if (columnIndex < 0) { // no column index
            path = null;
        }
        File file = new File(cursor.getString(columnIndex));
        cursor.close();
        path = file.getAbsolutePath();
        return path;
    }

    protected void renderPhoto() {
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void loading(FrameLayout flayLoading, boolean show) {
        if (show)
            flayLoading.setVisibility(View.VISIBLE);
        else
            flayLoading.setVisibility(View.GONE);
    }

    protected void enabledBack() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void loading(View flayLoading, boolean show) {
        if (show)
            flayLoading.setVisibility(View.VISIBLE);
        else
            flayLoading.setVisibility(View.GONE);
    }

    protected void next(Class<?> activityClass, Bundle bundle, boolean destroy) {
        Intent intent = new Intent(this, activityClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (destroy) {
            finish();
        }
    }
}
