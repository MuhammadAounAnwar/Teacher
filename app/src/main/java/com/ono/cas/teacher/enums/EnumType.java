package com.ono.cas.teacher.enums;

public class EnumType {

    public enum CallState {
        IDLE,
        OUTGOING,
        INCOMING,
        CONNECTING,
        CONNECTED,
        RE_CONNECTING,
        RINGING,
        CALL_IN_PROCESS,
        INCOMING_CALL_IN_PROCESS;

        CallState() {
        }
    }

    public enum CallEndReason {
        Busy,
        SignalError,
        Hangup,
        MediaError,
        RemoteHangup,
        OpenCameraFailure,
        Timeout,
        AcceptByOtherClient;

        CallEndReason() {
        }
    }

    public enum RefuseType {
        Busy,
        Hangup,
    }

    public enum CallType {
        NA,
        INCOMING,
        OUTGOING,
        INVITE,
        DEEP_LINK;

        CallType() {

        }
    }

    public enum CallStream {
        NA,
        AUDIO,
        VIDEO,
        GROUP;

        CallStream() {

        }
    }


}
