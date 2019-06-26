package com.example.miplanner.API;

import android.util.Log;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumEventsInstances;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;
import com.example.miplanner.RetrofitClient;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsService {
    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private Events eventResponse;
    private List<DatumEventsInstances> eventsInstancesResponse;

    public List<DatumEventsInstances> getAll(Calendar from, Calendar to) {
        retrofitClient.getEventRepository().getInstancesByInterval(from.getTimeInMillis(), to.getTimeInMillis()).enqueue(new Callback<EventsInstances>() {
            @Override
            public void onResponse(Call<EventsInstances> call, Response<EventsInstances> response) {
                if (response.isSuccessful())
                    if (response.body() != null) {
                        eventsInstancesResponse = Arrays.asList(response.body().getData());
                    }
                else eventResponse = null;
            }
            @Override
            public void onFailure(Call<EventsInstances> call, Throwable throwable) {
            }
        });
        return eventsInstancesResponse;
    }

    public Events save(DatumEvents event) {
        retrofitClient.getEventRepository().save(event).enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                Log.d("___POST Response", response.code() + "");

                Log.d("________id", response.body().getData()[0].getId() + "");

                if (response.isSuccessful())
                    eventResponse = response.body();
                else eventResponse = null;
            }

            @Override
            public void onFailure(Call<Events> call, Throwable throwable) {
            }
        });

        return eventResponse;
    }

    public Events update(Long id, DatumEvents event) {
        retrofitClient.getEventRepository().update(id, event).enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (response.isSuccessful())
                    eventResponse = response.body();
                else eventResponse = null;
            }

            @Override
            public void onFailure(Call<Events> call, Throwable throwable) {
            }
        });

        return eventResponse;
    }

    public void delete(Long id) {
        retrofitClient.getEventRepository().delete(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
            }
        });
    }

    public Events getEventResponse() {
        return eventResponse;
    }
}
