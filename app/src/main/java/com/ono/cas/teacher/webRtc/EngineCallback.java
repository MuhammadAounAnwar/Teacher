package com.ono.cas.teacher.webRtc;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

public interface EngineCallback {

    void joinRoomSucc();

    void exitRoom();

    void onSendIceCandidate(String userId, IceCandidate candidate);

    void onSendOffer(String userId, SessionDescription description);

    void onSendAnswer(String userId, SessionDescription description);

    void onRemoteStream(String userId);

    void onDataChanel(DataChannel dataChannel);

    void manageCallState(String userId);

    void manageCallState(String userId, String connectionState);

    void manageCallState(String userId, String connectionState, boolean isShouldCreateOffer);
}
