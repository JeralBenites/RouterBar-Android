package com.benites.jeral.router_bar.storage.network.entity;

import com.benites.jeral.router_bar.model.PubEntity;
import com.google.gson.annotations.SerializedName;

public class PubEntityRaw extends BaseRaw {

    @SerializedName("data")
    private PubEntity pubEntity;

    public PubEntity getPubEntity() {
        return pubEntity;
    }

    public void setPubEntity(PubEntity pubEntity) {
        this.pubEntity = pubEntity;
    }
}