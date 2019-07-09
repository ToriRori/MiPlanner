package com.example.miplanner;

import com.example.miplanner.API.ApiEvents;
import com.example.miplanner.API.ApiPatterns;
import com.example.miplanner.API.ApiPermissions;
import com.example.miplanner.API.ApiTasks;
import com.example.miplanner.API.ApiTransfers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.exceptions.Exceptions;

public class RetrofitClient {
    private final static String BASE_URL = "http://planner.skillmasters.ga/";

    private static RetrofitClient instance;

    private Retrofit retrofit;

    private static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setLenient()
            .create();

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null)
            instance = new RetrofitClient();

        return instance;
    }

    public ApiEvents getEventRepository() {
        return retrofit.create(ApiEvents.class);
    }

    public ApiPatterns getEventPatternRepository() {
        return retrofit.create(ApiPatterns.class);
    }

    public ApiTasks getTasksRepository() {  return retrofit.create(ApiTasks.class); }

    public ApiTransfers getTransfersRepository() {  return retrofit.create(ApiTransfers.class);  }

    public ApiPermissions getSharingRepository() {  return  retrofit.create(ApiPermissions.class);  }
}

