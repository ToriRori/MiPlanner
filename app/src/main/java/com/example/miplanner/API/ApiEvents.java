package com.example.miplanner.API;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;

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
import scala.util.parsing.combinator.testing.Str;

public interface ApiEvents {
    @Headers({
            "X-Firebase-Auth: serega_mem"
    })
    @GET("/api/v1/events")
    Call<Events> getEventsByInterval(@Query("from") Long from, @Query("to") Long to);

    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/events")
    Call<Events> getEventsById(@Query("id") Long[] id, @Header("X-Firebase-Auth") String tokenID);

    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/events/instances")
    Call<EventsInstances> getInstancesByInterval(@Query("from") Long from, @Query("to") Long to, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "X-Firebase-Auth: serega_mem"
    })
    @GET("/api/v1/events/instances")
    Call<EventsInstances> getInstancesById(@Query("id") Long[] id);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "X-Firebase-Auth: serega_mem"
    })
    @POST("/api/v1/events")
    Call<Events> save(@Body DatumEvents event);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
    //        "X-Firebase-Auth: serega_mem"
    })
    @PATCH("/api/v1/events/{id}")
    Call<Events> update(@Path("id") Long id, @Body DatumEvents event, @Header("X-Firebase-Auth") String tokenID);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            //"X-Firebase-Auth: serega_mem"
    })
    @DELETE("/api/v1/events/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("X-Firebase-Auth") String tokenID);
}
