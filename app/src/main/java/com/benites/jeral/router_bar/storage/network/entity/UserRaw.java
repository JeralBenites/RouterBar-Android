package com.benites.jeral.router_bar.storage.network.entity;

import com.benites.jeral.router_bar.model.UserEntity;
import com.google.gson.annotations.SerializedName;

public class UserRaw extends BaseRaw {

    @SerializedName("data")
    private UserEntity userEntity;

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
