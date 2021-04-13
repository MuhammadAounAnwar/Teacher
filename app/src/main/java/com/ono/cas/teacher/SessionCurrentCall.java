package com.ono.cas.teacher;

public class SessionCurrentCall {


    private EnumType.CallState _callState = EnumType.CallState.IDLE;
    private EnumType.CallType callType = EnumType.CallType.NA;
    private EnumType.CallStream callStream = EnumType.CallStream.NA;

    @Override
    public String toString() {
        return "SessionCurrentCall{" +
                "_callState=" + _callState +
                ", callType=" + callType +
                ", callStream=" + callStream +
                '}';
    }

    public EnumType.CallState getState() {
        return _callState;
    }

    public void setCallState(EnumType.CallState callState) {
        this._callState = callState;
    }

    /*
     * Call Type
     * */

    public void setCallType(EnumType.CallType callType) {
        this.callType = callType;
    }

    public EnumType.CallType getCallType() {
        return callType;
    }

    /*
     * Call Stream
     * */

    public void setCallStream(EnumType.CallStream callStream) {
        this.callStream = callStream;
    }

    public EnumType.CallStream getCallStream() {
        return callStream;
    }


}
