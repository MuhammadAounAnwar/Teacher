package com.ono.cas.teacher.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DtoPublishers {
    @SerializedName("joined")
    @Expose
    private boolean joined;
    @SerializedName("publishers")
    @Expose
    private List<DtoPublisher> publishers = new ArrayList<>();

    public DtoPublishers() {
    }

    public DtoPublishers(boolean joined, List<DtoPublisher> publishers) {
        this.joined = joined;
        this.publishers = publishers;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public List<DtoPublisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<DtoPublisher> publishers) {
        this.publishers = publishers;
    }

    @Override
    public String toString() {
        return "DtoPublishers{" +
                "joined=" + joined +
                ", publishers=" + publishers +
                '}';
    }
}
