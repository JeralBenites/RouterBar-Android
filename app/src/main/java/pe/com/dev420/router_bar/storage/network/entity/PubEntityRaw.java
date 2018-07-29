package pe.com.dev420.router_bar.storage.network.entity;

import com.google.gson.annotations.SerializedName;

import pe.com.dev420.router_bar.model.PubEntity;

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