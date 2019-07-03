package com.example.miplanner.Activities.InfoEvent;
import java.util.HashMap;

public class TasksMap extends HashMap<String, String> {

    static final String NAME = "name";
    static final String TIME = "time";
    static final String ID = "id";
    // Конструктор с параметрами
    public TasksMap(String name, String time, String id) {
        super();
        super.put(NAME, name);
        super.put(TIME, time);
        super.put(ID, id);
    }
}