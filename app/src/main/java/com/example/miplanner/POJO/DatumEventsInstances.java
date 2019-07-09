
package com.example.miplanner.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DatumEventsInstances implements Serializable {

    @Expose
    @SerializedName("started_at")
    private Long startedAt;
    @Expose
    @SerializedName("ended_at")
    private Long endedAt;
    @Expose
    @SerializedName("event_id")
    private Long eventId;
    @Expose
    @SerializedName("pattern_id")
    private Long patternId;

    public Long getStartedAt() {
        return startedAt;
    }

    public Long getEndedAt() {
        return endedAt;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getPatternId() {
        return patternId;
    }

    /*@SerializedName("ended_at")
    @Expose
    private Integer endedAt;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("pattern_id")
    @Expose
    private Integer patternId;
    @SerializedName("started_at")
    @Expose
    private Integer startedAt;

    public Integer getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Integer endedAt) {
        this.endedAt = endedAt;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getPatternId() {
        return patternId;
    }

    public void setPatternId(Integer patternId) {
        this.patternId = patternId;
    }

    public Integer getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Integer startedAt) {
        this.startedAt = startedAt;
    }*/

}
