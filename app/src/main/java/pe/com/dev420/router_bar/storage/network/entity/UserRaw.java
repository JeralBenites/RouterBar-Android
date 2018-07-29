package pe.com.dev420.router_bar.storage.network.entity;

import com.google.gson.annotations.SerializedName;

import pe.com.dev420.router_bar.model.UserEntity;

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
