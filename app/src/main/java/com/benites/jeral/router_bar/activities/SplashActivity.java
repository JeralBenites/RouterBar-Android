package com.benites.jeral.router_bar.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.model.PubEntity;
import com.benites.jeral.router_bar.storage.preferences.PreferencesHelper;
import com.wang.avi.AVLoadingIndicatorView;

public class SplashActivity extends BaseActivity {

    ImageView logoImageView;
    ProgressBar loadingIndicatorView;
    TextView loadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ui();
        asyncTask();
    }

    private void ui() {
        logoImageView = findViewById(R.id.logoImageView);
        loadingIndicatorView = findViewById(R.id.loadingIndicatorView);
        loadingTextView = findViewById(R.id.loadingTextView);
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
                code = 1;
            else
                code = 2;
            return code;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            switch (success) {
                case 1:
                    next(PubsActivity.class, null, true);
                    break;
                case 2:
                    next(LoginActivity.class, null, true);
                    break;
                case 4:
                    //toggle();
                    //showMessage(getString(R.string.Error));
                    break;
            }
        }
    }

}
