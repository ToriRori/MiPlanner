
package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatumPermissions {

    @Expose
    private Long id;
    @Expose
    @SerializedName("owner_id")
    private String ownerId;
    @SerializedName("created_at")
    private Long createdAt;
    @SerializedName("updated_at")
    private Long updatedAt;

    @Expose
    private Long user_id;
    @Expose
    private Long entity_id;
    @Expose
    private String name;

    /*Default constructor*/
    public DatumPermissions() {
    }

    public DatumPermissions(Long user_id, Long entity_id, String name) {
        this.user_id = user_id;
        this.entity_id = entity_id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public Long getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(long entity_id) {
        this.entity_id = entity_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*@SerializedName("created_at")
    @Expose
    private Integer createdAt;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("updated_at")
    @Expose
    private Integer updatedAt;

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }*/

}
