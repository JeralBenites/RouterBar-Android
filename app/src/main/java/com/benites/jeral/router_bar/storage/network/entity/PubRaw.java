package com.benites.jeral.router_bar.storage.network.entity;

import com.benites.jeral.router_bar.model.PubEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PubRaw extends BaseRaw {

    @SerializedName("data")
    private List<PubEntity> pubEntity;

    public List<PubEntity> getPubEntity() {
        return pubEntity;
    }

    public void setPubEntity(List<PubEntity> pubEntity) {
        this.pubEntity = pubEntity;
    }
}
