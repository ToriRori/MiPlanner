package com.example.miplanner.Fragments.Permissions;
import android.support.annotation.NonNull;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import org.w3c.dom.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersMap extends HashMap<String, String> {

    static final String NAME = "name";
    static final String TYPE = "type";
    static final String RAW_TYPE = "raw_type";
    static final String ENTITY_ID = "entity_id";
    static final String ID = "id";
    // Конструктор с параметрами
    public UsersMap(String name, String raw_type, Long entity_id, Long id, String type) {
        super();
        super.put(NAME, name);
        super.put(RAW_TYPE, raw_type);
        super.put(ENTITY_ID, Long.toString(entity_id));
        super.put(ID, Long.toString(id));
        super.put(TYPE, type);
    }
}