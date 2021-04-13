package com.ono.cas.teacher.janusclientapi;

import android.view.View;

import java.math.BigInteger;

public class JanusUserDetail {
    private boolean isScreenSharing;
    private boolean isVideoEnable;
    private boolean isLoudSpeakerEnable;
    private boolean isMicEnable;
    private boolean isSelected;
    private BigInteger handleId;
    private String userName;
    private View view;
    private BigInteger feedId;
    private boolean selfMute;

    @Override
    public String toString() {
        return "JanusUserDetail{" +
                "isScreenSharing=" + isScreenSharing +
                ", isVideoEnable=" + isVideoEnable +
                ", isLoudSpeakerEnable=" + isLoudSpeakerEnable +
                ", isMicEnable=" + isMicEnable +
                ", isSelected=" + isSelected +
                ", handleId=" + handleId +
                ", userName='" + userName + '\'' +
                ", view=" + view +
                ", feedId=" + feedId +
                ", selfMute=" + selfMute +
                '}';
    }

    public boolean isScreenSharing() {
        return this.isScreenSharing;
    }

    public void setScreenSharing(final boolean screenSharing) {
        this.isScreenSharing = screenSharing;
    }

    public boolean isSelfMute() {
        return this.selfMute;
    }

    public void setSelfMute(final boolean selfMute) {
        this.selfMute = selfMute;
    }

    public BigInteger getFeedId() {
        return feedId;
    }

    public void setFeedId(BigInteger feedId) {
        this.feedId = feedId;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isVideoEnable() {
        return isVideoEnable;
    }

    public void setVideoEnable(boolean videoEnable) {
        isVideoEnable = videoEnable;
    }

    public boolean isLoudSpeakerEnable() {
        return isLoudSpeakerEnable;
    }

    public void setLoudSpeakerEnable(boolean loudSpeakerEnable) {
        isLoudSpeakerEnable = loudSpeakerEnable;
    }

    public boolean isMicEnable() {
        return isMicEnable;
    }

    public void setMicEnable(boolean micEnable) {
        isMicEnable = micEnable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigInteger getHandleId() {
        return handleId;
    }

    public void setHandleId(BigInteger handleId) {
        this.handleId = handleId;
    }
}

