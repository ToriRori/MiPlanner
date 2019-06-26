package com.example.miplanner.API;

import android.util.Log;

import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.Patterns;
import com.example.miplanner.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsPatternsService {

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private Patterns eventPatternResponse = new Patterns();

    /*public EventPatternResponse getAll(Long from, Long to) {
        retrofitClient.getEventPatternRepository().findFromTo(params).enqueue(new Callback<EventPatternResponse>() {
            @Override
            public void onResponse(Call<EventPatternResponse> call, Response<EventPatternResponse> response) {
                if (response.isSuccessful())
                    eventPatternResponse = response.body();
                else eventPatternResponse = null;
            }
            @Override
            public void onFailure(Call<EventPatternResponse> call, Throwable throwable) {
            }
        });
        return eventPatternResponse;
    }*/

    public Patterns save(Long eventId, DatumPatterns eventPattern) {
        retrofitClient.getEventPatternRepository().save(eventId, eventPattern).enqueue(new Callback<Patterns>() {
            @Override
            public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                Log.d("_POST Pattern Response", response.code() + "");

                if (response.isSuccessful())
                    eventPatternResponse = response.body();
                else eventPatternResponse = null;
            }

            @Override
            public void onFailure(Call<Patterns> call, Throwable throwable) {
            }
        });

        return eventPatternResponse;
    }

    public Patterns update(Long id, DatumPatterns eventPattern) {
        retrofitClient.getEventPatternRepository().update(id, eventPattern).enqueue(new Callback<Patterns>() {
            @Override
            public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                if (response.isSuccessful())
                    eventPatternResponse = response.body();
                else eventPatternResponse = null;
            }

            @Override
            public void onFailure(Call<Patterns> call, Throwable throwable) {
            }
        });

        return eventPatternResponse;
    }

    public void delete(Long id) {
        retrofitClient.getEventPatternRepository().delete(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
            }
        });
    }

}
