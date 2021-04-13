package com.ono.cas.teacher.webRtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebRTCEngine implements IEngine, Peer.IPeerEvent {
    private static final String TAG = "WebRTCEngine";
    private PeerConnectionFactory _factory;
    public EglBase mRootEglBase;
    public MediaStream _localStream;
    public VideoSource videoSource;
    public AudioSource audioSource;
    public AudioTrack _localAudioTrack;
    private VideoCapturer captureAndroid;
    private SurfaceTextureHelper surfaceTextureHelper;

    private ProxyVideoSink localSink;
    private SurfaceViewRenderer localRenderer;

    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    public static final String VIDEO_CODEC_H264 = "H264";
    private static final int VIDEO_RESOLUTION_WIDTH = 320;
    private static final int VIDEO_RESOLUTION_HEIGHT = 240;
    private static final int FPS = 15;

    public ConcurrentHashMap<String, Peer> peers = new ConcurrentHashMap<>();
    private List<PeerConnection.IceServer> iceServers = new ArrayList<>();

    private EngineCallback mCallback;

    public boolean mIsAudioOnly;
    private Context mContext;
    private AudioManager audioManager;

    public static boolean TURN_CREDENTIALS_SUCCESS = false;
    public static String USERNAME = "";
    public static String PASSWORD = "";
    public static String STUN_URL = "";
    public static String TURN_URL = "";

    private Handler handler = new Handler(Looper.getMainLooper());
    private static boolean isCallRequest;

    public WebRTCEngine() {
    }

    public WebRTCEngine(Context context) {
        mContext = context;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        initSteps();
    }

    public void initSteps() {
        if (mRootEglBase == null) {
            mRootEglBase = EglBase.create();
        }
        if (_factory == null) {
            _factory = createConnectionFactory();
        }
        if (_localStream == null) {
            createLocalStream();
        }
    }

    public PeerConnectionFactory get_factory() {
        return _factory;
    }

    public MediaStream getLocalMediaStream() {
        return _localStream;
    }

    public WebRTCEngine(boolean mIsAudioOnly, Context mContext, boolean isCallRequest) {
        this.mIsAudioOnly = mIsAudioOnly;
        this.mContext = mContext;
        WebRTCEngine.isCallRequest = isCallRequest;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    // -----------------------------------对外方法------------------------------------------
    @Override
    public void init(EngineCallback callback) {
        mCallback = callback;

        if (mRootEglBase == null) {
            mRootEglBase = EglBase.create();
        }
        if (_factory == null) {
            _factory = createConnectionFactory();
        }
        if (_localStream == null && isCallRequest) {
            createLocalStream();
        }
    }

//    @Override
//    public void joinRoom(String userId) {
//
//        Peer peer = new Peer(_factory, iceServers, userId, this);
//        peer.setOffer(false);
//        // add localStream
//        peer.addLocalStream(_localStream);
//        // 添加列表
//        peers.put(userId, peer);
//
//        printHashMap();
//
//        if (mCallback != null) {
//            mCallback.joinRoomSucc();
//        }
//        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//
//    }

    @Override
    public void joinRoom(String userId, String userName, String password, String turn, String stun) {
        Peer peer = new Peer(_factory, iceServers, userId, this);
        peer.setOffer(false);
        peer.addLocalStream(_localStream);
        peers.put(userId, peer);
        Log.d(TAG, "joinRoom: IceServers Size:" + iceServers.size() + " Ice servers: " + iceServers.toString());
        if (mCallback != null) {
            mCallback.joinRoomSucc();
        }
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    @Override
    public DataChannel createPeerConnection(String userId, Boolean offer) {

        DataChannel dataChannel = null;
        Peer peer = new Peer(_factory, iceServers, userId, this);
        peer.setOffer(offer);
        peers.put(userId, peer);
        Log.d(TAG, "createPeerConnection: " + iceServers.toString());

        if (offer) {
            dataChannel = peer.createDataChannel();
            peer.createForFileOffer();
        }

        return dataChannel;


    }

    @Override
    public void userIn(String userId, Boolean shouldCreateOffer, String userName, String password, String turn, String stun) {
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder(stun).createIceServer());
        if (!turn.isEmpty()) {
            iceServers.add(PeerConnection.IceServer.builder(turn).setUsername(userName).setPassword(password).createIceServer());
        }

        Peer peer = new Peer(_factory, iceServers, userId, this);
        peer.setOffer(true);
        peer.shouldCreateOffer(shouldCreateOffer);
        peer.addLocalStream(_localStream);
        peers.put(userId, peer);

        if (shouldCreateOffer) {
            peer.createOffer();
        }
    }

//    @Override
//    public void userIn(String userId, Boolean shouldCreateOffer) {
//        // create Peer
//        Peer peer = new Peer(_factory, iceServers, userId, this);
//        peer.setOffer(true);
//        // add localStream
//        peer.addLocalStream(_localStream);
//        // 添加列表
//        peers.put(userId, peer);
//        // createOffer
//
//        if (shouldCreateOffer) {
//            peer.createOffer();
//        }
//    }

    @Override
    public void userReject(String userId) {

    }

    @Override
    public void receiveOffer(String userId, String description) {
        Peer peer = peers.get(userId);
        if (peer != null) {
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER, description);
            peer.setOffer(false);
            handler.post(() -> {
                peer.setRemoteDescription(sdp);
                peer.createAnswer();
            });
        }
    }

    @Override
    public void receiveOfferForFile(String userId, String description) {
        Peer peer = peers.get(userId);
        if (peer != null) {
            SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER, description);
            peer.setOffer(false);

            handler.post(() -> {
                peer.setRemoteDescription(sdp);
                peer.createForFileAnswer();
            });


        }
    }

    @Override
    public void receiveAnswer(String userId, String sdp) {
        Peer peer = peers.get(userId);
        if (peer != null) {
            SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, sdp);
            peer.setRemoteDescription(sessionDescription);
        }
    }

    @Override
    public void receiveIceCandidate(String userId, String id, int label, String candidate) {
        Peer peer = peers.get(userId);
        if (peer != null) {
            IceCandidate iceCandidate = new IceCandidate(id, label, candidate);
            peer.addRemoteIceCandidate(iceCandidate);
        }
    }

    @Override
    public void leaveRoom(String userId) {
        Peer peer = peers.get(userId);
        if (peer != null) {
            peer.close();
            peers.remove(userId);
        }
        if (peers.size() == 0) {
            if (mCallback != null) {
                mCallback.exitRoom();
            }
        }
    }

    @Override
    public View startPreview(boolean isOverlay) {
        return startLocalStream(isOverlay);
    }

    public View startLocalStream(boolean isOverLay) {
        if (null == mRootEglBase) {
            return null;
        }
        localRenderer = new SurfaceViewRenderer(mContext);
        localRenderer.init(mRootEglBase.getEglBaseContext(), null);
        localRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localRenderer.setMirror(true);
        localRenderer.setZOrderMediaOverlay(isOverLay);

        localSink = new ProxyVideoSink();
        localSink.setTarget(localRenderer);
        if (_localStream.videoTracks.size() > 0) {
//            _localStream.audioTracks.get(0).setVolume(0);
            _localStream.videoTracks.get(0).addSink(localSink);
        }
        return localRenderer;
    }

    public View getStreamPreview(boolean isOverLay, MediaStream stream) {
        localRenderer = new SurfaceViewRenderer(mContext);
        localRenderer.init(mRootEglBase.getEglBaseContext(), null);
        localRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        localRenderer.setMirror(true);
        localRenderer.setZOrderMediaOverlay(isOverLay);

        localSink = new ProxyVideoSink();
        localSink.setTarget(localRenderer);
        if (stream.videoTracks.size() > 0) {
            stream.videoTracks.get(0).addSink(localSink);
        }
        return localRenderer;
    }

    public View convertStreamIntoView(MediaStream mediaStream, boolean isOverLay, Context context) {
        SurfaceViewRenderer surfaceViewRenderer = new SurfaceViewRenderer(context);
        surfaceViewRenderer.init(mRootEglBase.getEglBaseContext(), null);
        surfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        surfaceViewRenderer.setMirror(false);
        surfaceViewRenderer.setZOrderMediaOverlay(isOverLay);

        ProxyVideoSink proxyVideoSink = new ProxyVideoSink();
        proxyVideoSink.setTarget(surfaceViewRenderer);
        if (mediaStream.videoTracks.size() > 0) {
            mediaStream.videoTracks.get(0).addSink(proxyVideoSink);
        }
        return surfaceViewRenderer;
    }

    public View removeVideoTrackFromStream(MediaStream mediaStream, boolean isOverLay, Context context) {
        SurfaceViewRenderer surfaceViewRenderer = new SurfaceViewRenderer(context);
        surfaceViewRenderer.init(mRootEglBase.getEglBaseContext(), null);
        surfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        surfaceViewRenderer.setMirror(true);
        surfaceViewRenderer.setZOrderMediaOverlay(isOverLay);

        ProxyVideoSink proxyVideoSink = new ProxyVideoSink();
        proxyVideoSink.setTarget(surfaceViewRenderer);
        if (mediaStream.videoTracks.size() > 0) {
            mediaStream.videoTracks.get(0).addSink(proxyVideoSink);
        }
        return surfaceViewRenderer;
    }


    @Override
    public void stopPreview() {
        if (localSink != null) {
            localSink.setTarget(null);
            localSink = null;
        }
        if (audioSource != null) {
            audioSource.dispose();
            audioSource = null;
        }
        // 释放摄像头
        if (captureAndroid != null) {
            try {
                captureAndroid.stopCapture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (captureAndroid != null) {
                captureAndroid.dispose();
                captureAndroid = null;
            }

        }
        // 释放画布
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            surfaceTextureHelper = null;
        }

        if (videoSource != null) {
            videoSource.dispose();
            videoSource = null;
        }
        if (_localStream != null) {
            _localStream = null;
        }
        if (localRenderer != null) {
            localRenderer.release();
        }


    }

    @Override
    public void startStream() {

    }

    @Override
    public void stopStream(Boolean stop) {
        _localStream.videoTracks.get(0).setEnabled(stop);
    }


    @Override
    public View setupRemoteVideo(String userId, boolean isO) {
        if (TextUtils.isEmpty(userId)) {
            Log.e(TAG, "setupRemoteVideo userId is null ");
            return null;
        }
        Peer peer = peers.get(userId);
        if (peer == null) return null;

        if (peer.renderer == null) {
            peer.createRender(mRootEglBase, mContext, isO);
        }
        return peer.renderer;
    }

    @Override
    public void stopRemoteVideo() {

    }

    private boolean isSwitch = false; // 是否正在切换摄像头

    @Override
    public void switchCamera() {
        if (isSwitch) return;
        isSwitch = true;
        if (captureAndroid == null) return;
        if (captureAndroid instanceof CameraVideoCapturer) {
            CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) captureAndroid;
            try {
                cameraVideoCapturer.switchCamera(new CameraVideoCapturer.CameraSwitchHandler() {
                    @Override
                    public void onCameraSwitchDone(boolean isFrontCamera) {
                        if (isFrontCamera) {
                            localRenderer.setMirror(true);
                        } else {
                            localRenderer.setMirror(false);
                        }
                        isSwitch = false;
                    }

                    @Override
                    public void onCameraSwitchError(String errorDescription) {
                        isSwitch = false;
                    }
                });
            } catch (Exception e) {
                isSwitch = false;
            }
        } else {
        }
    }

    @Override
    public boolean muteAudio(boolean enable) {
        if (_localAudioTrack != null) {
            _localAudioTrack.setEnabled(enable);
            return true;
        }
        return false;
    }

    @Override
    public boolean toggleSpeaker(boolean enable) {
        if (audioManager != null) {
            if (enable) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
            } else {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.STREAM_VOICE_CALL);
            }

            return true;
        }
        return false;
    }

    @Override
    public void release() {
        try {
            if (audioManager != null) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
            // 清空peer
            if (peers != null) {
                for (Peer peer : peers.values()) {
                    peer.close();
                }
                peers.clear();
            }

            stopPreview();

            if (_factory != null) {
                _factory.dispose();
                _factory = null;
            }

            if (mRootEglBase != null) {
                mRootEglBase.release();
                mRootEglBase = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "release: ", e);
        }
    }

    // -----------------------------其他方法--------------------------------

    public void initIceServer() {
        Log.d(TAG, "initIceServer: Turn_Url:" + TURN_URL);
        Log.d(TAG, "initIceServer: Stun_Url:" + STUN_URL);

        iceServers.add(getGoogleServer());

//        if (TURN_CREDENTIALS_SUCCESS) {
//            iceServers.add(getStunUdpServer());
//            iceServers.add(getTurnUdpServer());
//            iceServers.add(getTurnTcpServer());
//        } else {
//            iceServers.add(getStunUdpServer());
//        }

        iceServers.add(getStunUdpServer());
        iceServers.add(getTurnTcpServer());
        iceServers.add(getStunUdpServer());
    }

    private PeerConnection.IceServer getGoogleServer() {
        return PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                .createIceServer();
    }

    private PeerConnection.IceServer getStunUdpServer() {
        return PeerConnection.IceServer.builder(STUN_URL)
                .createIceServer();
    }

    private PeerConnection.IceServer getTurnTcpServer() {
        return PeerConnection.IceServer.builder(TURN_URL)
                .setUsername(USERNAME)
                .setPassword(PASSWORD)
                .createIceServer();
    }

//    private PeerConnection.IceServer getStunUdpServer() {
//        return PeerConnection.IceServer.builder("stun:a94c32b6630a295cf.awsglobalaccelerator.com:3478")
//                .createIceServer();
//    }
//
//    private PeerConnection.IceServer getTurnUdpServer() {
//        return PeerConnection.IceServer.builder("turn:a94c32b6630a295cf.awsglobalaccelerator.com:3478?transport=tcp")
//                .setUsername(USERNAME)
//                .setPassword(PASSWORD)
//                .createIceServer();
//    }
//
//    private PeerConnection.IceServer getTurnTcpServer() {
//        return PeerConnection.IceServer.builder("turn:a94c32b6630a295cf.awsglobalaccelerator.com?transport=tcp")
//                .setUsername(USERNAME)
//                .setPassword(PASSWORD)
//                .createIceServer();
//    }


    /**
     * 构造PeerConnectionFactory
     *
     * @return PeerConnectionFactory
     */
    public PeerConnectionFactory createConnectionFactory() {

        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions
                .builder(mContext)
                .createInitializationOptions());

        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;

        encoderFactory = new DefaultVideoEncoderFactory(mRootEglBase.getEglBaseContext(), true, true);
        decoderFactory = new DefaultVideoDecoderFactory(mRootEglBase.getEglBaseContext());

        AudioDeviceModule audioDeviceModule = JavaAudioDeviceModule.builder(mContext).createAudioDeviceModule();
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        return PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioDeviceModule)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    public PeerConnectionFactory createConnectionFactory(Context context, EglBase eglBase) {

        // 1. 初始化的方法，必须在开始之前调用
        PeerConnectionFactory.initialize(PeerConnectionFactory
                .InitializationOptions
                .builder(context)
                .createInitializationOptions());

        // 2. 设置编解码方式：默认方法
        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;

        encoderFactory = new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
        decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());

        // 构造Factory
        AudioDeviceModule audioDeviceModule = JavaAudioDeviceModule.builder(context).createAudioDeviceModule();
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        return PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioDeviceModule)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    /**
     * 创建本地流
     */
    public void createLocalStream() {
        _localStream = get_localStream();
    }

    public MediaStream get_localStream() {
        _localStream = _factory.createLocalMediaStream("ARDAMS");
        // 音频
        audioSource = _factory.createAudioSource(createAudioConstraints());
        _localAudioTrack = _factory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        _localStream.addTrack(_localAudioTrack);

        // 视频
        if (!mIsAudioOnly) {
            captureAndroid = createVideoCapture();
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", mRootEglBase.getEglBaseContext());
            videoSource = _factory.createVideoSource(captureAndroid.isScreencast());

            captureAndroid.initialize(surfaceTextureHelper, mContext, videoSource.getCapturerObserver());
            captureAndroid.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);


            VideoTrack _localVideoTrack = _factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
            _localStream.addTrack(_localVideoTrack);
        }
        return _localStream;
    }

    private boolean screencaptureEnabled = false;

    /**
     * 创建媒体方式
     *
     * @return VideoCapturer
     */
    public VideoCapturer createVideoCapture() {
        VideoCapturer videoCapturer;

        if (screencaptureEnabled) {
            return createScreenCapturer();
        }

        if (Camera2Enumerator.isSupported(mContext)) {
            videoCapturer = createCameraCapture(new Camera2Enumerator(mContext));
        } else {
            videoCapturer = createCameraCapture(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    /**
     * 创建相机媒体流
     *
     * @param enumerator
     * @return VideoCapturer
     */
    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }


    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;

    @TargetApi(21)
    private VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                Log.e(TAG, "User revoked permission to capture the screen.");
            }
        });
    }

    //**************************************各种约束******************************************/
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";

    // 配置音频参数
    private MediaConstraints createAudioConstraints() {
        MediaConstraints audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
        audioConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));
        return audioConstraints;
    }

    //------------------------------------回调---------------------------------------------
    @Override
    public void onSendIceCandidate(String userId, IceCandidate candidate) {
        if (mCallback != null) {
            mCallback.onSendIceCandidate(userId, candidate);
        }

    }

    @Override
    public void onSendOffer(String userId, SessionDescription description) {
        if (mCallback != null) {
            mCallback.onSendOffer(userId, description);
        }
    }

    @Override
    public void onSendAnswer(String userId, SessionDescription description) {
        if (mCallback != null) {
            mCallback.onSendAnswer(userId, description);
        }
    }

    @Override
    public void onRemoteStream(String userId, MediaStream stream) {
        if (mCallback != null) {
            mCallback.onRemoteStream(userId);
        }
    }

    @Override
    public void onRemoveStream(String userId, MediaStream stream) {
        leaveRoom(userId);
    }

    @Override
    public void onDataChanel(DataChannel dataChannel) {
        if (mCallback != null) {
            mCallback.onDataChanel(dataChannel);
        }
    }

    @Override
    public void manageCallState(String userId) {
        if (mCallback != null) {
            mCallback.manageCallState(userId);
        }
    }

    @Override
    public void manageCallState(String userId, String connectionStatus) {
        if (mCallback != null) {
            Peer peer = peers.get(userId);
            mCallback.manageCallState(userId, connectionStatus, peer.getShouldCreateOffer());
        }
    }

    @Override
    public void manageCallState(String userId, String connectionStatus, boolean isShouldCreateOffer) {
//        if (mCallback != null) {
//            Peer peer = peers.get(userId);
//            mCallback.manageCallState(userId, connectionStatus);
//        }
    }

    public void removePeersRenderer() {
        Set<Map.Entry<String, Peer>> entrySet = peers.entrySet();
        Iterator<Map.Entry<String, Peer>> itr = entrySet.iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Peer> entry = itr.next();
            String key = entry.getKey();
            Objects.requireNonNull(peers.get(key)).renderer = null;
        }

    }

    @Override
    public void release(String userId) {
        if (peers != null && peers.containsKey(userId)) {
            Peer peer = peers.get(userId);
            peer.close();
            peers.remove(peer);
        }
//        printHashMap();
    }

    @Override
    public void toggleVideo(boolean enable) {
        if (_localStream != null && _localStream.videoTracks.size() > 0) {
            _localStream.videoTracks.get(0).setEnabled(enable);
        }
    }

    private void printHashMap() {
        Set<Map.Entry<String, Peer>> entrySet = peers.entrySet();
        Iterator<Map.Entry<String, Peer>> itr = entrySet.iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Peer> entry = itr.next();
            String key = entry.getKey();
            Peer peer = peers.get(key);
            Log.d(TAG, "printHashMap: HashMap Length: " + peers.size());
            Log.d(TAG, "printHashMap: " + "key: " + key + " value: " + peer);
//            System.out.println("key: " + key + " value: " + peer);
        }
    }


}
