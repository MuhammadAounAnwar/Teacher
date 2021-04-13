package com.ono.cas.teacher.janusclientapi;

import com.ono.cas.teacher.MyLifecycleHandler;
import com.ono.cas.teacher.webRtc.WebRTCEngine;

public class SingletonWebRtc {

    private static WebRTCEngine webRTCEngine;

    public synchronized static WebRTCEngine getWebRTCEngine() {
        if (null == webRTCEngine) {
            webRTCEngine = new WebRTCEngine(MyLifecycleHandler.activity);
        }
        return webRTCEngine;
    }

    public static void releaseInstance() {
        webRTCEngine = null;
    }

}
