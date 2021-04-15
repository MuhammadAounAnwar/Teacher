package com.ono.cas.teacher.webRtc;


import android.view.View;

import org.webrtc.DataChannel;

/**
 * rtc基类
 */
public interface IEngine {


    /**
     * 初始化
     */
    void init(EngineCallback callback);

//    void joinRoom(String userIds);

    void joinRoom(String userIds, String userName, String password, String turn, String stun);


    DataChannel createPeerConnection(String userId, Boolean offer);

//    void userIn(String userId, Boolean shouldCreateOffer);

    void userIn(String userId, Boolean shouldCreateOffer, String userName, String password, String turn, String stun);


    void userReject(String userId);


    /**
     * receive Offer
     */
    void receiveOffer(String userId, String description);

    void receiveOfferForFile(String userId, String description);

    /**
     * receive Answer
     */
    void receiveAnswer(String userId, String sdp);

    /**
     * receive IceCandidate
     */
    void receiveIceCandidate(String userId, String id, int label, String candidate);

    /**
     * 离开房间
     *
     * @param userId
     */
    void leaveRoom(String userId);

    /**
     * 开启本地预览
     */
    View startPreview(boolean isOverlay);

    /**
     * 关闭本地预览
     */
    void stopPreview();

    /**
     * 开始远端推流
     */
    void startStream();

    /**
     * 停止远端推流
     */
    void stopStream(Boolean stop);

    /**
     * 开始远端预览
     */
    View setupRemoteVideo(String userId, boolean isO);

    /**
     * 关闭远端预览
     */
    void stopRemoteVideo();

    /**
     * 切换摄像头
     */
    void switchCamera();

    /**
     * 设置静音
     */
    boolean muteAudio(boolean enable);

    /**
     * 开启扬声器
     */
    boolean toggleSpeaker(boolean enable);

    /**
     * 释放所有内容
     */
    void release();

    /*
     * Delete peers renderer
     * */
    void removePeersRenderer();

    /*
     * delete specific peer
     * */
    void release(String userId);

    /**
     * Toggle video
     *
     * @param enable
     */
    void toggleVideo(boolean enable);
}
