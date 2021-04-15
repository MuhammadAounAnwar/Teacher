package com.ono.cas.teacher.janusclientapi;

import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by ben.trent on 6/25/2015.
 */
public interface IPluginHandleWebRTCCallbacks extends IJanusCallbacks {
    void onSuccess(JSONObject obj);

    JSONObject getJsep();

    JanusMediaConstraints getMedia();

    Boolean getTrickle();

    void disconnectedUser(BigInteger handleId);
}
