package com.ono.cas.teacher.dto;

import com.ono.cas.teacher.janusclientapi.JanusPluginHandle;
import com.ono.cas.teacher.janusclientapi.JanusServer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DtoJanusCallState {
    private boolean isMicEnable;
    private boolean isVideoEnable;
    private boolean isLoudSpeaker;
    private BigInteger selectedUserHandleId;
    private BigInteger selectedUserFeedId;
    private BigInteger selfFeedId;
    private BigInteger selfHandleId;

    private JanusPluginHandle selfJanusPluginHandle = new JanusPluginHandle();
    private JanusServer janusServer = new JanusServer();
    private List<JanusPluginHandle> janusPluginHandles = new ArrayList<>();
    public ConcurrentHashMap<BigInteger, JanusPluginHandle> attachedPlugins = new ConcurrentHashMap<>();
    public ConcurrentHashMap<BigInteger, JanusPluginHandle> feedPlugins = new ConcurrentHashMap<>();


    public BigInteger getSelfFeedId() {
        return this.selfFeedId;
    }

    public void setSelfFeedId(final BigInteger selfFeedId) {
        this.selfFeedId = selfFeedId;
    }

    public BigInteger getSelfHandleId() {
        return this.selfHandleId;
    }

    public void setSelfHandleId(final BigInteger selfHandleId) {
        this.selfHandleId = selfHandleId;
    }

    public JanusPluginHandle getSelfJanusPluginHandle() {
        return this.selfJanusPluginHandle;
    }

    public void setSelfJanusPluginHandle(final JanusPluginHandle selfJanusPluginHandle) {
        this.selfJanusPluginHandle = selfJanusPluginHandle;
    }

    public BigInteger getSelectedUserHandleId() {
        return this.selectedUserHandleId;
    }

    public void setSelectedUserHandleId(final BigInteger selectedUserHandleId) {
        this.selectedUserHandleId = selectedUserHandleId;
    }

    public BigInteger getSelectedUserFeedId() {
        return this.selectedUserFeedId;
    }

    public void setSelectedUserFeedId(final BigInteger selectedUserFeedId) {
        this.selectedUserFeedId = selectedUserFeedId;
    }

    public JanusServer getJanusServer() {
        return this.janusServer;
    }

    public void setJanusServer(final JanusServer janusServer) {
        this.janusServer = janusServer;
    }

    public List<JanusPluginHandle> getJanusPluginHandles() {
        return this.janusPluginHandles;
    }

    public void setJanusPluginHandles(final List<JanusPluginHandle> janusPluginHandles) {
        this.janusPluginHandles = janusPluginHandles;
    }

    public ConcurrentHashMap<BigInteger, JanusPluginHandle> getAttachedPlugins() {
        return this.attachedPlugins;
    }

    public void setAttachedPlugins(final ConcurrentHashMap<BigInteger, JanusPluginHandle> attachedPlugins) {
        this.attachedPlugins = attachedPlugins;
    }

    public ConcurrentHashMap<BigInteger, JanusPluginHandle> getFeedPlugins() {
        return this.feedPlugins;
    }

    public void setFeedPlugins(final ConcurrentHashMap<BigInteger, JanusPluginHandle> feedPlugins) {
        this.feedPlugins = feedPlugins;
    }

    public boolean isMicEnable() {
        return this.isMicEnable;
    }

    public void setMicEnable(final boolean micEnable) {
        this.isMicEnable = micEnable;
    }

    public boolean isVideoEnable() {
        return this.isVideoEnable;
    }

    public void setVideoEnable(final boolean videoEnable) {
        this.isVideoEnable = videoEnable;
    }

    public boolean isLoudSpeaker() {
        return this.isLoudSpeaker;
    }

    public void setLoudSpeaker(final boolean loudSpeaker) {
        this.isLoudSpeaker = loudSpeaker;
    }


    public DtoJanusCallState() {
    }
}
