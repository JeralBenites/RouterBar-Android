package pe.com.dev420.router_bar.storage.network.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import pe.com.dev420.router_bar.model.PubEntity;

public class PubListRaw extends BaseRaw {

    @SerializedName("data")
    private List<PubEntity> pubEntity;

    public List<PubEntity> getPubEntity() {
        return pubEntity;
    }

    public void setPubEntity(List<PubEntity> pubEntity) {
        this.pubEntity = pubEntity;
    }
}
