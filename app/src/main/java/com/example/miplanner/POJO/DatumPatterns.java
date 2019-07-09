
package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DatumPatterns implements Serializable {

    @Expose
    private Long id;
    private Long createdAt;
    private Long updatedAt;

    @Expose
    private Long duration;
    @Expose
    @SerializedName("ended_at")
    private Long endedAt;
    @Expose
    private String exrule;
    @Expose
    private String rrule;
    @Expose
    @SerializedName("started_at")
    private Long startedAt;
    @Expose
    private String timezone;

    /*Default constructor*/
    public DatumPatterns() {
    }

    public DatumPatterns(Long duration, Long endedAt, String exrule, String rrule, Long startedAt, String timezone) {
        this.duration = duration;
        this.endedAt = endedAt;
        this.exrule = exrule;
        this.rrule = rrule;
        this.startedAt = startedAt;
        this.timezone = timezone;
    }

    public Long getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Long endedAt) {
        this.endedAt = endedAt;
    }

    public String getExrule() {
        return exrule;
    }

    public void setExrule(String exrule) {
        this.exrule = exrule;
    }

    public String getRrule() {
        return rrule;
    }

    public void setRrule(String rrule) {
        this.rrule = rrule;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /*@SerializedName("created_at")
    @Expose
    private Integer createdAt;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("ended_at")
    @Expose
    private Integer endedAt;
    @SerializedName("exrule")
    @Expose
    private String exrule;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rrule")
    @Expose
    private String rrule;
    @SerializedName("started_at")
    @Expose
    private Integer startedAt;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("updated_at")
    @Expose
    private Integer updatedAt;

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Integer endedAt) {
        this.endedAt = endedAt;
    }

    public String getExrule() {
        return exrule;
    }

    public void setExrule(String exrule) {
        this.exrule = exrule;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRrule() {
        return rrule;
    }

    public void setRrule(String rrule) {
        this.rrule = rrule;
    }

    public Integer getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Integer startedAt) {
        this.startedAt = startedAt;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Integer updatedAt) {
        this.updatedAt = updatedAt;
    }*/

}
