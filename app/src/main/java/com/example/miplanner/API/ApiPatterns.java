package com.example.miplanner.API;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;
import com.example.miplanner.POJO.Patterns;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPatterns {
    @Headers({
            "X-Firebase-Auth: serega_mem"
    })
    @GET("/api/v1/patterns")
    Call<Patterns> getPatternsById(@Query("event_id") Long id);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "X-Firebase-Auth: serega_mem"
    })
    @POST("/api/v1/patterns")
    Call<Patterns> save(@Query("event_id") Long eventId, @Body DatumPatterns eventPattern);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "X-Firebase-Auth: serega_mem"
    })
    @PATCH("/api/v1/patterns/{id}")
    Call<Patterns> update(@Path("id") Long id, @Body DatumPatterns eventPattern);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "X-Firebase-Auth: serega_mem"
    })
    @DELETE("/api/v1/patterns/{id}")
    Call<Void> delete(@Path("id") Long id);
}
