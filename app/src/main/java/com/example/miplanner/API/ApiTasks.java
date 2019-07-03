package com.example.miplanner.API;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;
import com.example.miplanner.POJO.Tasks;

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

public interface ApiTasks {
    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/tasks")
    Call<Tasks> getTasksList(@Header("X-Firebase-Auth") String tokenID);

    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/tasks")
    Call<Tasks> getTasksByEventId(@Query("event_id") Long id, @Header("X-Firebase-Auth") String tokenID);

    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/tasks/{id}")
    Call<Tasks> getTaskById(@Path("id") Long id, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            //"X-Firebase-Auth: serega_mem"
    })
    @POST("/api/v1/tasks")
    Call<Tasks> save(@Query("event_id") Long id, @Body DatumTasks task, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
    //        "X-Firebase-Auth: serega_mem"
    })
    @PATCH("/api/v1/tasks/{id}")
    Call<Events> update(@Path("id") Long id, @Body DatumTasks task, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            //"X-Firebase-Auth: serega_mem"
    })
    @DELETE("/api/v1/tasks/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("X-Firebase-Auth") String tokenID);
}
