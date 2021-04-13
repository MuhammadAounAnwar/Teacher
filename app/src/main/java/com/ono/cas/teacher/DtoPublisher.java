package com.ono.cas.teacher;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class DtoPublisher {
    @SerializedName("video")
    private boolean video;
    @SerializedName("audio")
    private boolean audio;
    @SerializedName("screen")
    private boolean screen;
    @SerializedName("id")
    private BigInteger id;

    @Override
    public String toString() {
        return "DtoPublisher{" +
                "video=" + video +
                ", audio=" + audio +
                ", screen=" + screen +
                ", id='" + id + '\'' +
                '}';
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
