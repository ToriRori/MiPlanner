package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;

public class RequestPermission {
    @Expose
    private String action;

    @Expose
    private Long entity_id;

    @Expose
    private String entity_type;

    public RequestPermission(String action, Long entity_id, String entity_type) {
        this.action = action;
        this.entity_id = entity_id;
        this.entity_type = entity_type;
    }
}
