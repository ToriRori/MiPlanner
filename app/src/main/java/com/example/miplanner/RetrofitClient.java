package com.example.miplanner;

import com.example.miplanner.API.ApiEvents;
import com.example.miplanner.API.ApiPatterns;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
}
