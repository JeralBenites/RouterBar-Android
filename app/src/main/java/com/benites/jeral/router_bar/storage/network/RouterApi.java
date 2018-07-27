package com.benites.jeral.router_bar.storage.network;

import com.benites.jeral.router_bar.model.CustomerEntity;
import com.benites.jeral.router_bar.model.PubEntity;
import com.benites.jeral.router_bar.model.UserEntity;
import com.benites.jeral.router_bar.storage.network.entity.CustomerRaw;
import com.benites.jeral.router_bar.storage.network.entity.PubEntityRaw;
import com.benites.jeral.router_bar.storage.network.entity.PubListRaw;
import com.benites.jeral.router_bar.storage.network.entity.Route.RouteResponse;
import com.benites.jeral.router_bar.storage.network.entity.UserRaw;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RouterApi {

    @POST("Login")
    Call<UserRaw> loginUser(@Body UserEntity userEntity);

    @GET("pubs")
    Call<PubListRaw> listPubs();

    @POST("pubs/listByName")
    Call<PubListRaw> listPubsbyName(@Body PubEntity pubEntity);

    @Multipart
    @POST("pubs")
    Call<PubEntityRaw> insertPub(@Part MultipartBody.Part file, @Part("result") PubEntity pubEntity);

    @POST("customers")
    Call<CustomerRaw> insertCustomer(@Body CustomerEntity customerEntity);

    @GET("maps/api/directions/json")
    Call<RouteResponse> getDistanceDuration(
            @Query(value = "origin", encoded = true) String origin,
            @Query(value = "destination", encoded = true) String destination,
            @Query("sensor") String sensor,
            @Query("mode") String mode);
}
