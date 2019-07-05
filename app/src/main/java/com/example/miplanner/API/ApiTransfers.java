package com.example.miplanner.API;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;

import okhttp3.ResponseBody;
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

public interface ApiTransfers {
    /*@Headers({
            "X-Firebase-Auth: serega_mem"
    })*/
    @GET("/api/v1/export")
    Call<ResponseBody> getCalendar(@Header("X-Firebase-Auth") String tokenID);
}
