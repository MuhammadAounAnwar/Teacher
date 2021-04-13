package com.ono.cas.teacher;

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
