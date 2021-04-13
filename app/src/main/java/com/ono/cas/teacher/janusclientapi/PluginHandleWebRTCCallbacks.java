package com.ono.cas.teacher.janusclientapi;

import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by ben.trent on 8/13/2015.
 */
public class PluginHandleWebRTCCallbacks implements IPluginHandleWebRTCCallbacks {

    private final JanusMediaConstraints constraints;
    private final JSONObject jsep;
    private final boolean trickle;
    private BigInteger handleId;

    public BigInteger getHandleId() {
        return this.handleId;
    }

    public void setHandleId(final BigInteger handleId) {
        this.handleId = handleId;
    }

    public PluginHandleWebRTCCallbacks(JanusMediaConstraints constraints, JSONObject jsep, boolean trickle) {
        this.constraints = constraints;
        this.jsep = jsep;
        this.trickle = trickle;
    }

    public PluginHandleWebRTCCallbacks(JanusMediaConstraints constraints, JSONObject jsep, boolean trickle, BigInteger handleId) {
        this.constraints = constraints;
        this.jsep = jsep;
        this.trickle = trickle;
        this.handleId = handleId;
    }

    @Override
    public void onSuccess(JSONObject obj) {

    }

    @Override
    public JSONObject getJsep() {
        return jsep;
    }

    @Override
    public JanusMediaConstraints getMedia() {
        return constraints;
    }

    @Override
    public Boolean getTrickle() {
        return trickle;
    }

    @Override
    public void disconnectedUser(BigInteger handleId) {

    }

    @Override
    public void onCallbackError(String error) {

    }
}
