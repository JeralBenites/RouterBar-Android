package com.benites.jeral.router_bar.storage.network;

import com.benites.jeral.router_bar.model.PubEntity;
import com.benites.jeral.router_bar.model.UserEntity;
import com.benites.jeral.router_bar.storage.network.entity.PubRaw;
import com.benites.jeral.router_bar.storage.network.entity.UserRaw;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RouterApi {

    @POST("Login")
    Call<UserRaw> LoginUser(@Body UserEntity userEntity);

    @GET("pubs")
    Call<PubRaw> ListPubs();

    @Multipart
    @POST("pubs")
    Call<PubRaw> InsertPub(@Part MultipartBody.Part file, @Part("result") PubEntity pubEntity);
}
