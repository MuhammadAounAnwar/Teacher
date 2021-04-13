package com.ono.cas.teacher.janusclientapi; /**
 * Created by ben.trent on 5/7/2015.
 */

import org.json.JSONObject;
import org.webrtc.MediaStream;

public interface IJanusPluginCallbacks extends IJanusCallbacks {
    void success(JanusPluginHandle handle);

    void onMessage(JSONObject msg, JSONObject jsep);

    void onLocalStream(MediaStream stream);

    void onRemoteStream(MediaStream stream);

    void updateStream(MediaStream mediaStream);

    void onRemoveStream(MediaStream mediaStream);

    void onDataOpen(Object data);

    void onData(Object data);

    void onCleanup();

    void onDetached();

    JanusSupportedPluginPackages getPlugin();
}
