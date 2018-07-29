package pe.com.dev420.router_bar.storage.network.entity;

import com.google.gson.annotations.SerializedName;

import pe.com.dev420.router_bar.model.CustomerEntity;

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
