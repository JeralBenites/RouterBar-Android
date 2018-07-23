package com.benites.jeral.router_bar.storage.network;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.benites.jeral.router_bar.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/*
 * Created by Jeral Benites  on 30/09/2017.
 */

public class ProgressRequestBody extends RequestBody {

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private File mFile;
    private UploadCallbacks mListener;

    public ProgressRequestBody(final File file, final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        Log.v("ProgressRequestBody", "content type : " + Util.getMimeType(file.getAbsolutePath()));
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long uploaded = 0;

        try (FileInputStream in = new FileInputStream(mFile)) {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer, 0, read);
                handler.post(new ProgressUpdater(uploaded, fileLength));
            }
        }
    }

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
    }

    private class ProgressUpdater implements Runnable {

        private long mUploaded;
        private long mTotal;

        ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int) (100 * mUploaded / mTotal));
        }
    }
}
