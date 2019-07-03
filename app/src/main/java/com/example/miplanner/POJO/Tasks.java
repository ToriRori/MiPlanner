package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;

public class Tasks {
    @Expose
    private int count;
    @Expose
    private DatumTasks[] data;
    @Expose
    private String message;
    @Expose
    private int offset;
    @Expose
    private int status;
    @Expose
    private boolean success;

    public int getCount() {
        return count;
    }

    public DatumTasks[] getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getOffset() {
        return offset;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

}
