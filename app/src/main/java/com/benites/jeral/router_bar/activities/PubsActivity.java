package com.benites.jeral.router_bar.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.benites.jeral.router_bar.R;
import com.benites.jeral.router_bar.model.PubEntity;
import com.benites.jeral.router_bar.storage.network.ApiClass;
import com.benites.jeral.router_bar.storage.network.RouterApi;
import com.benites.jeral.router_bar.storage.network.entity.PubRaw;
import com.benites.jeral.router_bar.ui.adapters.PubAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PubsActivity extends BaseActivity {

    FloatingActionButton fab;
    private PubAdapter pubAdapter;
    private RecyclerView pubRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PubEntity> pubEntities;
    private View flayLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubs);
        ui();
        loadData();
    }

    private void ui() {
        flayLoading = findViewById(R.id.flayLoading);
        pubRecyclerView = findViewById(R.id.pubRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        pubRecyclerView.setLayoutManager(mLayoutManager);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                next(InsertPubActivity.class, null, false)
        );
    }

    private void loadData() {
        showLoading();
        Call<PubRaw> pubRawCall = ApiClass.getRetrofit().create(RouterApi.class).ListPubs();
        pubRawCall.enqueue(new Callback<PubRaw>() {
            @Override
            public void onResponse(Call<PubRaw> call, Response<PubRaw> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    PubRaw c = response.body();
                    pubEntities = c.getPubEntity();
                    pubAdapter = new PubAdapter(pubEntities, PubsActivity.this.getApplicationContext());
                    pubRecyclerView.setAdapter(pubAdapter);
                }
            }

            @Override
            public void onFailure(Call<PubRaw> call, Throwable t) {
                hideLoading();
                System.out.println(t.getMessage());
            }
        });
    }

    private void showLoading() {
        flayLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        flayLoading.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
    }
}
