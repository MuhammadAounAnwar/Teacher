package com.ono.cas.teacher.utils;

import com.ono.cas.teacher.dto.DtoJanusCallState;
import com.ono.cas.teacher.dto.SessionCurrentCall;

public class GlobalData {

    public static boolean isShareApp;
    public static final String TAG = "GlobalData";
    public static SessionCurrentCall currentCall = new SessionCurrentCall();
    public static DtoJanusCallState janusCallState = new DtoJanusCallState();

    public static DtoJanusCallState getJanusCallState() {
        return janusCallState;
    }

    public static void setJanusCallState(DtoJanusCallState janusCallState) {
        GlobalData.janusCallState = janusCallState;
    }

    public static SessionCurrentCall getCurrentCall() {
        return currentCall;
    }

    public static void setCurrentCall(SessionCurrentCall currentCall) {
        GlobalData.currentCall = currentCall;
    }


}
