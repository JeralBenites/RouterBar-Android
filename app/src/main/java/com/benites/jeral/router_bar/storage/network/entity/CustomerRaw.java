package com.benites.jeral.router_bar.storage.network.entity;

import com.benites.jeral.router_bar.model.CustomerEntity;
import com.google.gson.annotations.SerializedName;

public class CustomerRaw extends BaseRaw {

    @SerializedName("data")
    private CustomerEntity userEntity;

    public CustomerEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(CustomerEntity userEntity) {
        this.userEntity = userEntity;
    }
}
