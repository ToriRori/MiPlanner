
package com.example.miplanner.POJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventsInstances {
    @Expose
    private int count;
    @Expose
    private DatumEventsInstances[] data;
    @Expose
    private String message;
    @Expose
    private Long offset;
    @Expose
    private int status;
    @Expose
    private boolean success;

    public int getCount() {
        return count;
    }

    public DatumEventsInstances[] getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Long getOffset() {
        return offset;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    /*@SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("data")
    @Expose
    private List<DatumEventsInstances> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<DatumEventsInstances> getData() {
        return data;
    }

    public void setData(List<DatumEventsInstances> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }*/

}
