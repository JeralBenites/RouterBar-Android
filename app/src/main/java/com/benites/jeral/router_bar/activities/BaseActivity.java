package com.benites.jeral.router_bar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Objects;

public class BaseActivity extends AppCompatActivity {

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

    protected void enabledBack() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
