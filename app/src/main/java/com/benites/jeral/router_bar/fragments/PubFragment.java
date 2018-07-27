package com.benites.jeral.router_bar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.benites.jeral.router_bar.model.PubEntity;

/**
 * Created by Jeral Benites on 17/04/2018  [2Mas].
 */
public class PubFragment extends Fragment {

    String vPhotoUrl;
    private PubEntity pub;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String getvPhotoUrl() {
        return vPhotoUrl;
    }

    public void setvPhotoUrl(String vPhotoUrl) {
        this.vPhotoUrl = vPhotoUrl;
    }

    public PubEntity getPub() {
        return pub;
    }

    public void setPub(PubEntity pub) {
        this.pub = pub;
    }
}
