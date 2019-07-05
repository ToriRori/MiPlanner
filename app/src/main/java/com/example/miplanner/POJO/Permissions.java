
package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;

public class Permissions {

    @Expose
    private int count;
    @Expose
    private DatumPermissions[] data;
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

    public DatumPermissions[] getData() {
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

    /*@SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("data")
    @Expose
    private List<DatumEvents> data = null;
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

    public List<DatumEvents> getData() {
        return data;
    }

    public void setData(List<DatumEvents> data) {
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
