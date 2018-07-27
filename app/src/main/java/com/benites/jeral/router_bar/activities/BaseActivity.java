package com.benites.jeral.router_bar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BaseActivity extends AppCompatActivity {

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loading(FrameLayout flayLoading, boolean show) {
        if (show)
            flayLoading.setVisibility(View.VISIBLE);
        else
            flayLoading.setVisibility(View.GONE);
    }

    public void loading(View flayLoading, boolean show) {
        if (show)
            flayLoading.setVisibility(View.VISIBLE);
        else
            flayLoading.setVisibility(View.GONE);
    }
}
