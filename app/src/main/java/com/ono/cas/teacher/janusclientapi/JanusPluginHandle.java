package com.ono.cas.teacher.janusclientapi;

import android.os.AsyncTask;
import android.util.Log;

import com.ono.cas.teacher.utils.MyLifecycleHandler;
import com.ono.cas.teacher.webRtc.WebRTCEngine;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.math.BigInteger;

public class JanusPluginHandle {

    private boolean started = false;
    public MediaStream myStream = null;
    public MediaStream remoteStream = null;
    public SessionDescription mySdp = null;
    private PeerConnection pc = null;
    private DataChannel dataChannel = null;
    private boolean trickle = true;
    private boolean iceDone = false;
    public boolean sdpSent = false;
    public static final String TAG = "JanusPluginHandle";

    @Override
    public String toString() {
        return "JanusPluginHandle{" +
                "started=" + started +
                ", myStream=" + myStream +
                ", remoteStream=" + remoteStream +
                ", mySdp=" + mySdp +
                ", pc=" + pc +
                ", dataChannel=" + dataChannel +
                ", trickle=" + trickle +
                ", iceDone=" + iceDone +
                ", sdpSent=" + sdpSent +
                ", webRTCEngine=" + webRTCEngine +
                ", sessionFactory=" + sessionFactory +
                ", server=" + server +
                ", plugin=" + plugin +
                ", id=" + id +
                ", callbacks=" + callbacks +
                ", janusUserDetail=" + janusUserDetail +
                '}';
    }

    private class WebRtcObserver implements SdpObserver, PeerConnection.Observer {
        private final IPluginHandleWebRTCCallbacks webRtcCallbacks;

        public WebRtcObserver(IPluginHandleWebRTCCallbacks callbacks) {
            this.webRtcCallbacks = callbacks;
        }

        @Override
        public void onSetSuccess() {
            Log.d("JANUSCLIENT", "On Set Success");
            if (mySdp == null) {
                createSdpInternal(webRtcCallbacks, false);
            }
        }

        @Override
        public void onSetFailure(String error) {
            Log.d("JANUSCLIENT", "On set Failure");
            //todo JS api does not account for this
            webRtcCallbacks.onCallbackError(error);
        }

        @Override
        public void onCreateSuccess(SessionDescription sdp) {
            Log.d("JANUSCLIENT", "Create success");
            onLocalSdp(sdp, webRtcCallbacks);
        }

        @Override
        public void onCreateFailure(String error) {
            Log.d("JANUSCLIENT", "Create failure");
            webRtcCallbacks.onCallbackError(error);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState state) {
            Log.d("JANUSCLIENT", "Signal change " + state.toString());
            switch (state) {
                case STABLE:
                    break;
                case HAVE_LOCAL_OFFER:
                    break;
                case HAVE_LOCAL_PRANSWER:
                    break;
                case HAVE_REMOTE_OFFER:
                    break;
                case HAVE_REMOTE_PRANSWER:
                    break;
                case CLOSED:
                    break;
            }
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState state) {
            Log.d("JANUSCLIENT", "Ice Connection change " + state.toString());
            switch (state) {
                case DISCONNECTED:
                    break;
                case FAILED:
                    webRtcCallbacks.disconnectedUser(id);
                    break;
                case CONNECTED:
                    break;
                case NEW:
                    break;
                case CHECKING:
                    break;
                case CLOSED:
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState state) {
            switch (state) {
                case NEW:
                    break;
                case GATHERING:
                    break;
                case COMPLETE:
                    if (!trickle) {
                        mySdp = pc.getLocalDescription();
                        sendSdp(webRtcCallbacks);
                    } else {
                        sendTrickleCandidate(null);
                    }
                    break;
                default:
                    break;
            }
            Log.d("JANUSCLIENT", "Ice Gathering " + state.toString());
        }

        @Override
        public void onIceCandidate(IceCandidate candidate) {
            if (trickle) {
                sendTrickleCandidate(candidate);
            }
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] candidates) {

        }

        @Override
        public void onAddStream(MediaStream stream) {
            Log.d("JANUSCLIENT1 Video", "RemoteStream onAddStream " + stream.getId());
//            pc.addTransceiver(stream.videoTracks.get(0));
//            pc.addTransceiver(stream.audioTracks.get(0));
            remoteStream = stream;
            onRemoteStream(stream);
        }

        @Override
        public void onRemoveStream(MediaStream stream) {
            Log.d("JANUSCLIENT1 Video", "RemoteStream onRemoveStream");
            remoteStream = stream;
            onRemoveStream(stream);
        }

        @Override
        public void onDataChannel(DataChannel channel) {
            Log.d("JANUSCLIENT1", "onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.d("JANUSCLIENT1", "Renegotiation needed");
        }

        @Override
        public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
            Log.d("JANUSCLIENT1 Video", "RemoteStream onAddTrack (1): " + mediaStreams.length);
            if (1 <= mediaStreams.length) {
                if (mediaStreams[0].videoTracks.size() > 0) {
                    Log.d(TAG,
                            "RemoteStream onAddTrack (2): " +
                                    " VideoTracks:==>" + String.valueOf(mediaStreams[0].videoTracks.size()));
                }
                if (mediaStreams[0].audioTracks.size() > 0) {
                    Log.d(TAG,
                            "RemoteStream onAddTrack (3): " +
                                    " AudioTracks:==>" + String.valueOf(mediaStreams[0].videoTracks.size()));
                }

                if (remoteStream != null) {
                    remoteStream = mediaStreams[0];
                    Log.d(TAG,
                            "RemoteStream onAddTrack (4): " +
                                    " AudioTracks:==>" + String.valueOf(remoteStream.audioTracks.size()) +
                                    " VideoTracks:==>" + String.valueOf(remoteStream.videoTracks.size()));
                    updateStream(remoteStream);
                }
//                else {
//                    remoteStream = mediaStreams[0];
//                    onRemoteStream(remoteStream);
//                }
            }
        }

        @Override
        public void onConnectionChange(PeerConnection.PeerConnectionState newState) {
            Log.d(TAG, "onConnectionChange: " + newState.name());
        }

        @Override
        public void onTrack(RtpTransceiver transceiver) {
            Log.d(TAG, "onTrackEdit: " + transceiver.isStopped());
        }
    }

    private WebRTCEngine webRTCEngine;
    private PeerConnectionFactory sessionFactory;
    private JanusServer server;
    public JanusSupportedPluginPackages plugin;
    public BigInteger id;
    private IJanusPluginCallbacks callbacks;
    public JanusUserDetail janusUserDetail;

    private class AsyncPrepareWebRtc extends AsyncTask<IPluginHandleWebRTCCallbacks, Void, Void> {

        @Override
        protected Void doInBackground(IPluginHandleWebRTCCallbacks... params) {
            IPluginHandleWebRTCCallbacks cb = params[0];
            prepareWebRtc(cb);
            return null;
        }
    }

    private class AsyncHandleRemoteJsep extends AsyncTask<IPluginHandleWebRTCCallbacks, Void, Void> {
        @Override
        protected Void doInBackground(IPluginHandleWebRTCCallbacks... params) {
            IPluginHandleWebRTCCallbacks webrtcCallbacks = params[0];
            if (sessionFactory == null) {
                webrtcCallbacks.onCallbackError("WebRtc PeerFactory is not initialized. Please call initializeMediaContext");
                return null;
            }
            JSONObject jsep = webrtcCallbacks.getJsep();
            if (jsep != null) {
                if (pc == null) {
                    Log.d("JANUSCLIENT", "could not set remote offer");
                    callbacks.onCallbackError("No peerconnection created, if this is an answer please use createAnswer");
                    return null;
                }
                try {

                    String sdpString = jsep.getString("sdp");
                    Log.d("JANUSCLIENT", sdpString);
                    SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(jsep.getString("type"));
                    SessionDescription sdp = new SessionDescription(type, sdpString);
                    pc.setRemoteDescription(new WebRtcObserver(webrtcCallbacks), sdp);
                } catch (JSONException ex) {
                    Log.d("JANUSCLIENT", ex.getMessage());
                    webrtcCallbacks.onCallbackError(ex.getMessage());
                }
            }
            return null;
        }
    }

    public JanusPluginHandle() {
    }

    public JanusPluginHandle(JanusServer server, JanusSupportedPluginPackages plugin, BigInteger handle_id, IJanusPluginCallbacks callbacks) {
        this.server = server;
        this.plugin = plugin;
        id = handle_id;
        this.callbacks = callbacks;
        webRTCEngine = new WebRTCEngine();
        sessionFactory = webRTCEngine.createConnectionFactory(MyLifecycleHandler.activity, SingletonWebRtc.getWebRTCEngine().mRootEglBase);
        janusUserDetail = new JanusUserDetail();
    }

    public void onMessage(String msg) {
        try {
            JSONObject obj = new JSONObject(msg);
            callbacks.onMessage(obj, null);
        } catch (JSONException ex) {
            Log.d(TAG, "onMessage: " + ex.toString());
            //TODO do we want to notify the GatewayHandler?
        }
    }

    public void onMessage(JSONObject msg, JSONObject jsep) {
        callbacks.onMessage(msg, jsep);
    }

    private void onLocalStream(MediaStream stream) {
        callbacks.onLocalStream(stream);
    }

    private void onRemoteStream(MediaStream stream) {
        callbacks.onRemoteStream(stream);
    }

    private void updateStream(MediaStream mediaStream) {
        Log.d(TAG, "RemoteStream updateStream: ");
        callbacks.updateStream(mediaStream);
    }

    private void onRemoveStream(MediaStream stream) {
        callbacks.onRemoveStream(stream);
    }

    public void onDataOpen(Object data) {
        callbacks.onDataOpen(data);
    }

    public void onData(Object data) {
        callbacks.onData(data);
    }

    public void onCleanup() {
        callbacks.onCleanup();
    }

    public void onDetached() {
        callbacks.onDetached();
    }

    public void sendMessage(IPluginHandleSendMessageCallbacks obj) {
        server.sendMessage(TransactionType.plugin_handle_message, id, obj, plugin);
    }

    private void streamsDone(IPluginHandleWebRTCCallbacks webRTCCallbacks) {
        MediaConstraints pc_cons = new MediaConstraints();
        pc_cons.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        if (webRTCCallbacks.getMedia().getRecvAudio())
            pc_cons.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        if (webRTCCallbacks.getMedia().getRecvVideo())
            pc_cons.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
//        pc = sessionFactory.createPeerConnection(server.iceServers, pc_cons, new WebRtcObserver(webRTCCallbacks));
        pc = sessionFactory.createPeerConnection(server.iceServers, new WebRtcObserver(webRTCCallbacks));
        if (myStream != null)
            pc.addStream(myStream);
        if (webRTCCallbacks.getJsep() == null) {
            createSdpInternal(webRTCCallbacks, true);
        } else {
            try {
                JSONObject obj = webRTCCallbacks.getJsep();
                String sdp = obj.getString("sdp");
                SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(obj.getString("type"));
                SessionDescription sessionDescription = new SessionDescription(type, sdp);
                pc.setRemoteDescription(new WebRtcObserver(webRTCCallbacks), sessionDescription);
            } catch (Exception ex) {
                webRTCCallbacks.onCallbackError(ex.getMessage());
            }
        }
    }

    public void createOffer(IPluginHandleWebRTCCallbacks webrtcCallbacks) {
        new AsyncPrepareWebRtc().execute(webrtcCallbacks);
    }

    public void createAnswer(IPluginHandleWebRTCCallbacks webrtcCallbacks) {
        new AsyncPrepareWebRtc().execute(webrtcCallbacks);
    }

    private void prepareWebRtc(IPluginHandleWebRTCCallbacks callbacks) {
        if (pc != null) {
            if (callbacks.getJsep() == null) {
                createSdpInternal(callbacks, true);
            } else {
                try {
                    JSONObject jsep = callbacks.getJsep();
                    String sdpString = jsep.getString("sdp");
                    SessionDescription.Type type = SessionDescription.Type.fromCanonicalForm(jsep.getString("type"));
                    SessionDescription sdp = new SessionDescription(type, sdpString);
                    pc.setRemoteDescription(new WebRtcObserver(callbacks), sdp);
                } catch (JSONException ex) {
                    Log.d(TAG, "prepareWebRtc: " + ex.toString());
                }
            }
        } else {
            trickle = callbacks.getTrickle() != null ? callbacks.getTrickle() : false;
            myStream = SingletonWebRtc.getWebRTCEngine().getLocalMediaStream();
            if (myStream != null)
                onLocalStream(myStream);
            streamsDone(callbacks);
        }
    }

    private void createSdpInternal(IPluginHandleWebRTCCallbacks callbacks, Boolean isOffer) {
        MediaConstraints pc_cons = new MediaConstraints();
        pc_cons.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        if (callbacks.getMedia().getRecvAudio()) {
            pc_cons.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        }
        if (callbacks.getMedia().getRecvVideo()) {
            Log.d("VIDEO_ROOM", "Receiving video");
        }
        if (isOffer) {
            pc.createOffer(new WebRtcObserver(callbacks), pc_cons);
        } else {
            pc.createAnswer(new WebRtcObserver(callbacks), pc_cons);
        }
    }

    public void handleRemoteJsep(IPluginHandleWebRTCCallbacks webrtcCallbacks) {
        new AsyncHandleRemoteJsep().execute(webrtcCallbacks);
    }

    public void hangUp() {
        if (remoteStream != null) {
            remoteStream.dispose();
            remoteStream = null;
        }
//        if (myStream != null) {
//            myStream.dispose();
//            myStream = null;
//        }
        if (pc != null && pc.signalingState() != PeerConnection.SignalingState.CLOSED)
            pc.close();
        pc = null;
        started = false;
        mySdp = null;
        if (dataChannel != null)
            dataChannel.close();
        dataChannel = null;
        trickle = true;
        iceDone = false;
        sdpSent = false;
    }

    public void detach() {
        hangUp();
        JSONObject obj = new JSONObject();
        server.sendMessage(obj, JanusMessageType.detach, id);
    }

    private void onLocalSdp(SessionDescription sdp, IPluginHandleWebRTCCallbacks callbacks) {
        if (pc != null) {
            if (mySdp == null) {
                mySdp = sdp;
                pc.setLocalDescription(new WebRtcObserver(callbacks), sdp);
            }
            if (!iceDone && !trickle)
                return;
            if (sdpSent)
                return;
            try {
                sdpSent = true;
                JSONObject obj = new JSONObject();
                obj.put("sdp", mySdp.description);
                obj.put("type", mySdp.type.canonicalForm());
                callbacks.onSuccess(obj);
            } catch (JSONException ex) {
                callbacks.onCallbackError(ex.getMessage());
            }
        }
    }

    private void sendTrickleCandidate(IceCandidate candidate) {
        try {
            JSONObject message = new JSONObject();
            JSONObject cand = new JSONObject();
            if (candidate == null)
                cand.put("completed", true);
            else {
                cand.put("candidate", candidate.sdp);
                cand.put("sdpMid", candidate.sdpMid);
                cand.put("sdpMLineIndex", candidate.sdpMLineIndex);
            }
            message.put("candidate", cand);

            server.sendMessage(message, JanusMessageType.trickle, id);
        } catch (JSONException ex) {
            Log.d(TAG, "sendTrickleCandidate: " + ex.toString());
        }
    }

    private void sendSdp(IPluginHandleWebRTCCallbacks callbacks) {
        if (mySdp != null) {
            mySdp = pc.getLocalDescription();
            if (!sdpSent) {
                sdpSent = true;
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("sdp", mySdp.description);
                    obj.put("type", mySdp.type.canonicalForm());
                    callbacks.onSuccess(obj);
                } catch (JSONException ex) {
                    callbacks.onCallbackError(ex.getMessage());
                }
            }
        }
    }
}
