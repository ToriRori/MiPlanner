package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatumTasks {
    @Expose
    private Long id;
    @Expose
    @SerializedName("created_at")
    private Long createdAt;
    @SerializedName("updated_at")
    private Long updatedAt;

    @Expose
    private String details;
    @Expose
    private long deadline_at;
    @Expose
    private String name;
    @Expose
    private String status;
    @Expose
    private long parent_id;

    /*Default constructor*/
    public DatumTasks() {
    }

    public DatumTasks(String details, long parent_id, long deadline_at, String name, String status) {
        this.details = details;
        this.parent_id = parent_id;
        this.name = name;
        this.status = status;
        this.deadline_at = deadline_at;
    }

    public Long getId() {
        return id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getDeadline_at() {
        return deadline_at;
    }

    public void setDeadline_at(long deadline_at) {
        this.deadline_at = deadline_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
