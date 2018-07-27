package com.benites.jeral.router_bar.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.storage.preferences.PreferencesHelper;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import static com.benites.jeral.router_bar.util.Util.getBitmapFromAssets;

public class SplashActivity extends BaseActivity {

    ImageView logoImageView;
    ProgressBar loadingIndicatorView;
    TextView loadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Fabric.with(this, new Crashlytics());
        ui();
        asyncTask();
    }

    private void ui() {
        logoImageView = findViewById(R.id.logoImageView);
        loadingIndicatorView = findViewById(R.id.loadingIndicatorView);
        loadingTextView = findViewById(R.id.loadingTextView);

        logoImageView.setImageBitmap(getBitmapFromAssets("images/logo.png", this));
    }

    private void asyncTask() {
        CheckingServicesTask checkingServicesTask = new CheckingServicesTask(1);
        checkingServicesTask.execute((Void) null);
    }

    private class CheckingServicesTask extends AsyncTask<Void, Void, Integer> {
        private int code;

        CheckingServicesTask(int code) {
            this.code = code;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (PreferencesHelper.isSignedIn(SplashActivity.this))
                code = Integer.parseInt(PreferencesHelper.getStateSession(SplashActivity.this));
            else
                code = 0;
            return code;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            switch (success) {
                case 1:
                    next(PubsActivity.class, null, true);
                    break;
                case 2:
                    next(MainMapsActivity.class, null, true);
                    break;
                default:
                    next(LoginActivity.class, null, true);
                    break;
            }
        }
    }

}
