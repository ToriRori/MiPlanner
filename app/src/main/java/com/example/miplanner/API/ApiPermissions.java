package com.example.miplanner.API;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPermissions;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;
import com.example.miplanner.POJO.Permissions;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPermissions {
    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/grant")
    Call<Void> grantPermission(@Query("action") String action, @Query("entity_id") Long entity_id, @Query("entity_type") String entity_type, @Query("user_id") String user_id, @Header("X-Firebase-Auth") String tokenID);

    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/permissions")
    Call<Permissions> getAllPermissions(@Header("X-Firebase-Auth") String tokenID);

    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/share/{token}")
    Call<Void> activateShareLink(@Query("token") String token, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            //"X-Firebase-Auth: serega_mem"
    })
    @POST("/api/v1/share")
    Call<String> getShareLink(@Body DatumPermissions[] permissions, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            //"X-Firebase-Auth: serega_mem"
    })
    @DELETE("/api/v1/permissions/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("X-Firebase-Auth") String tokenID);
}
