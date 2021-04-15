package com.ono.cas.teacher;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ono.cas.teacher.adapter.DummyJanusStreamsAdapter;
import com.ono.cas.teacher.adapter.JanusStreamsAdapter;
import com.ono.cas.teacher.bottomSheet.Attendees_BottomSheet;
import com.ono.cas.teacher.dto.DtoJanusCallState;
import com.ono.cas.teacher.enums.EnumType;
import com.ono.cas.teacher.interfaces.ISendData;
import com.ono.cas.teacher.interfaces.OnClickAttendee;
import com.ono.cas.teacher.janusclientapi.IJanusGatewayCallbacks;
import com.ono.cas.teacher.janusclientapi.IJanusPluginCallbacks;
import com.ono.cas.teacher.janusclientapi.IPluginHandleWebRTCCallbacks;
import com.ono.cas.teacher.janusclientapi.JanusMediaConstraints;
import com.ono.cas.teacher.janusclientapi.JanusPluginHandle;
import com.ono.cas.teacher.janusclientapi.JanusServer;
import com.ono.cas.teacher.janusclientapi.JanusSupportedPluginPackages;
import com.ono.cas.teacher.janusclientapi.PluginHandleSendMessageCallbacks;
import com.ono.cas.teacher.janusclientapi.PluginHandleWebRTCCallbacks;
import com.ono.cas.teacher.janusclientapi.SingletonWebRtc;
import com.ono.cas.teacher.utils.GlobalData;
import com.ono.cas.teacher.utils.MyLifecycleHandler;
import com.ono.cas.teacher.utils.UtilityFunctions;
import com.ono.cas.teacher.webRtc.WebRTCEngine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ono.cas.teacher.enums.EnumType.CallState.CALL_IN_PROCESS;
import static com.ono.cas.teacher.enums.EnumType.CallState.CONNECTED;
import static com.ono.cas.teacher.enums.EnumType.CallState.CONNECTING;
import static com.ono.cas.teacher.enums.EnumType.CallState.IDLE;
import static com.ono.cas.teacher.enums.EnumType.CallState.RE_CONNECTING;
import static com.ono.cas.teacher.utils.GlobalData.isShareApp;

public class DummyActivity extends AppCompatActivity implements View.OnClickListener, ISendData, OnClickAttendee {

    private ImageButton imageButton_Mic, imageButton_Video, imageButton_HangUp;

    private ConstraintLayout constraintLayout_CallControls;
    private RecyclerView recyclerView_SurfaceRenderers;

    private boolean isVoiceEnable = true;
    private boolean isVideoEnable = true;
    private boolean isSpeakerEnable = true;
    private boolean isControlsEnable = false;

    private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    public static final String TAG = "DummyActivity";
    boolean ROOM_CREATION;

    private JanusServer janusServer;
    private WebRTCEngine webRTCEngine;
    LinearLayout linearLayout_UserInitials;

    public static final String REQUEST = "request";
    public static final String MESSAGE = "message";
    public static final String PUBLISHERS = "publishers";
    DummyJanusStreamsAdapter janusStreamsAdapter;
    List<JanusPluginHandle> janusPluginHandles = new ArrayList<>();
    JanusPublisherPluginCallbacks janusPublisherPluginCallbacks;
    GridLayoutManager gridLayoutManager;

    private JanusPluginHandle handle;
    private BigInteger myid;
    private BigInteger selectedUserHandleId;
    private BigInteger selectedUserFeedId;
    private Attendees_BottomSheet attendees_bottomSheet;

    public static final String JANUS_URI = "wss://web.trango.io/janus";
    public static final String DEEP_LINK_URL = "https://web.trango.io";

    private BigInteger roomid = new BigInteger("11223344");
    private String user_name = "TeacherName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        Start();
    }

    public void Start() {
//        ROOM_CREATION = true;

        initComponent();
        initListener();
        GlobalData.currentCall.setCallState(CONNECTING);
        webRTCEngine = SingletonWebRtc.getWebRTCEngine();
        janusServer = new JanusServer(new JanusGlobalCallbacks());
        janusServer.Connect();

        recyclerView_SurfaceRenderers.setVisibility(View.VISIBLE);
        initJanusStreamsRecycler();
    }

    /**
     * Store Activity State
     */
    private void setActivityState() {
        DtoJanusCallState janusCallState = new DtoJanusCallState();
        janusCallState.setLoudSpeaker(isSpeakerEnable);
        janusCallState.setVideoEnable(isVideoEnable);
        janusCallState.setMicEnable(isVoiceEnable);
        janusCallState.setAttachedPlugins(janusServer.attachedPlugins);
        janusCallState.setFeedPlugins(janusServer.feedPlugins);
        janusCallState.setJanusPluginHandles(janusPluginHandles);
        janusCallState.setJanusServer(janusServer);
        janusCallState.setSelectedUserHandleId(selectedUserHandleId);
        janusCallState.setSelectedUserFeedId(selectedUserFeedId);
        janusCallState.setSelfFeedId(myid);
        janusCallState.setSelfJanusPluginHandle(handle);
        janusCallState.setSelfHandleId(handle.id);

        GlobalData.setJanusCallState(janusCallState);
    }

    private boolean isActivityStateValid() {
        return janusServer != null &&
                janusServer.feedPlugins != null &&
                janusServer.attachedPlugins != null &&
                janusPluginHandles != null &&
                selectedUserHandleId != null &&
                selectedUserFeedId != null && myid != null && handle != null;
    }

    /**
     * Set on going call
     */
    private void setOnGoingCall() {
//        Log.d(TAG, "setOnGoingCall: Inside Ongoing call Function");
        if (CALL_IN_PROCESS == GlobalData.currentCall.getState() || CONNECTED == GlobalData.currentCall.getState()) {
            initComponent();
            initListener();

            if (null != GlobalData.getJanusCallState()) {
                DtoJanusCallState janusCallState = GlobalData.getJanusCallState();
                isSpeakerEnable = janusCallState.isLoudSpeaker();
                isVideoEnable = janusCallState.isVideoEnable();
                isVoiceEnable = janusCallState.isMicEnable();
                janusServer = janusCallState.getJanusServer();
                janusServer.feedPlugins = janusCallState.getFeedPlugins();
                janusServer.attachedPlugins = janusCallState.getAttachedPlugins();
                janusPluginHandles = janusCallState.getJanusPluginHandles();
                selectedUserHandleId = janusCallState.getSelectedUserHandleId();
                selectedUserFeedId = janusCallState.getSelectedUserFeedId();
                handle = janusCallState.getSelfJanusPluginHandle();
                myid = janusCallState.getSelfFeedId();
                webRTCEngine = SingletonWebRtc.getWebRTCEngine();
                initJanusStreamsRecycler();

            }
        }
    }

    /**
     * Initialize activity components
     */
    private void initComponent() {
        /*
         * Call Controls initialization
         * */
        imageButton_HangUp = findViewById(R.id.imageButton_HangUp);
        imageButton_Video = findViewById(R.id.imageButton_Video);
        imageButton_Mic = findViewById(R.id.imageButton_Mic);

        /*
         * Call Views
         * */
        recyclerView_SurfaceRenderers = findViewById(R.id.recyclerView_SurfaceRenderers);

        /*
         * Controls Layout
         * */
        constraintLayout_CallControls = findViewById(R.id.constraintLayout_CallControls);
    }

    /**
     * Initialize Activity components listeners
     */
    private void initListener() {
        imageButton_HangUp.setOnClickListener(this);
        imageButton_Video.setOnClickListener(this);
        imageButton_Mic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(imageButton_Mic)) {
            isVoiceEnable = !isVoiceEnable;
            String micState = isVoiceEnable ? "un-muted" : "muted";
            showToast(MyLifecycleHandler.activity, "mic is " + micState);
//            imageButton_Mic.setAlpha(isVoiceEnable ? 1.0f : 0.3f);
            configureMic(isVoiceEnable);
            micControl(isVoiceEnable);
        } else if (v.equals(imageButton_Video)) {
            isVideoEnable = !isVideoEnable;
        } else if (v.equals(imageButton_HangUp)) {
            GlobalData.currentCall.setCallState(IDLE);
            disconnectCall();
            janusServer = null;
            SingletonWebRtc.releaseInstance();
            finishAffinity();
        }
    }

    public void disconnectCall() {
        runOnUiThread(() -> {
            if (!janusServer.attachedPlugins.isEmpty()) {
                janusServer.Destroy();
            }

            if (null != webRTCEngine) {
                webRTCEngine.release();
            }
        });
    }

    /**
     * Send Socket request to notify other remote peers about current audio state.
     * Enable/Disable current audio Stream
     * update audio status in current handle
     * update the handle in attached plugins(HashMap)
     *
     * @param isAudioEnable
     */
    public void micControl(final boolean isAudioEnable) {
        isVoiceEnable = isAudioEnable;
        handle.myStream.audioTracks.get(0).setEnabled(isAudioEnable);
        webRTCEngine.muteAudio(isAudioEnable);

        //todo update attached plugins and recycler adapter list
        handle.janusUserDetail.setMicEnable(isAudioEnable);
        janusServer.attachedPlugins.put(handle.id, handle);
        janusServer.feedPlugins.put(myid, handle);
    }

    /**
     * Janus Util Methods
     */
    private List<JanusPluginHandle> getJanusPluginHandles() {
        List<JanusPluginHandle> janusPluginHandles = new ArrayList<>();
        for (BigInteger handleId : janusServer.attachedPlugins.keySet()) {
            JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(handleId);
            janusPluginHandles.add(janusPluginHandle);
        }
        return janusPluginHandles;
    }

    public void subscriberOnLeaving(JSONObject jsonObject) {
        /*
         * Get user if from json response using keyword
         * */
        if (jsonObject.has("leaving")) {
            String id = null;
            try {
                id = jsonObject.getString("leaving");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BigInteger feedId = new BigInteger(id);

            /*
             * if un-published user is in main renderer.
             * then, remove that subscriber and show self user in main renderer.
             * */
            if (selectedUserFeedId.equals(feedId)) {

                selectedUserFeedId = myid;
                selectedUserHandleId = handle.id;
                JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(handle.id);
                janusPluginHandle.janusUserDetail.setSelected(true);

                /*
                 * update plugins
                 * */
                janusServer.attachedPlugins.put(handle.id, janusPluginHandle);
                janusServer.feedPlugins.put(myid, janusPluginHandle);

                /*
                 * update main renderer
                 * update renderers list at index "0"
                 * */
                runOnUiThread(() -> {
                    janusStreamsAdapter.updateRenderer(0, janusPluginHandle);
                });
            }

            /*
             * Remove Subscriber from adapter and release its renderer.
             * */
            else {
                int index = janusStreamsAdapter.getHandlerIndexFromAdapter(feedId);
                if (index != -1) {
                    MyLifecycleHandler.activity.runOnUiThread(() -> janusStreamsAdapter.removeRendererByIndex(index));

                    /*
                     * Remove subscriber plugin from HashMap
                     * */
                    JanusPluginHandle janusPluginHandle = janusServer.feedPlugins.get(feedId);
                    if (janusPluginHandle != null) {
                        BigInteger handleId = janusPluginHandle.janusUserDetail.getHandleId();
                        showToast(MyLifecycleHandler.activity, janusPluginHandle.janusUserDetail.getUserName() + " left the meeting.");
                        janusServer.feedPlugins.remove(feedId);
                        janusServer.attachedPlugins.remove(handleId);
                    }
                }
            }
        }
    }

    public void subscriberOnUnPublish(JSONObject jsonObject) {
        /*
         * Get user if from json response using keyword
         * */
        if (jsonObject.has("unpublished")) {
            String id = null;
            try {
                id = jsonObject.getString("unpublished");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            BigInteger feedId = new BigInteger(id);

            /*
             * if un-published user is in main renderer.
             * then, remove that subscriber and show self user in main renderer.
             * */
            if (selectedUserFeedId.equals(feedId)) {
                selectedUserFeedId = myid;
                selectedUserHandleId = handle.id;
                JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(handle.id);
                janusPluginHandle.janusUserDetail.setSelected(true);

                /*
                 * update plugins
                 * */
                janusServer.attachedPlugins.put(handle.id, janusPluginHandle);
                janusServer.feedPlugins.put(myid, janusPluginHandle);

                /*
                 * update main renderer
                 * update renderers list at index "0"
                 * */
                runOnUiThread(() -> {
                    janusStreamsAdapter.updateRenderer(0, janusPluginHandle);
                });
            }
            /*
             * Remove Subscriber from adapter and release its renderer.
             * */
            else {
                int index = janusStreamsAdapter.getHandlerIndexFromAdapter(feedId);
                if (-1 != index) {
                    MyLifecycleHandler.activity.runOnUiThread(() -> janusStreamsAdapter.removeRendererByIndex(index));

                    /*
                     * Remove subscriber plugin from HashMap
                     * */
                    JanusPluginHandle janusPluginHandle = janusServer.feedPlugins.get(feedId);
                    janusPluginHandle.detach();
                }
            }
        }
    }//[onSubscriberUnPublish]

    public void updateVideoInPluginHandle(BigInteger pubHandleId, boolean isVideoEnable) {
        JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(pubHandleId);
        if (janusPluginHandle != null) {
            janusPluginHandle.janusUserDetail.setVideoEnable(isVideoEnable);
            janusServer.attachedPlugins.put(pubHandleId, janusPluginHandle);
            janusServer.feedPlugins.put(janusPluginHandle.janusUserDetail.getFeedId(), janusPluginHandle);
        }
    }

    public void updateAudioInPluginHandle(BigInteger pubHandleId, boolean isAudioEnable) {
        JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(pubHandleId);
        if (janusPluginHandle != null) {
            janusPluginHandle.janusUserDetail.setMicEnable(isAudioEnable);
            janusServer.attachedPlugins.put(pubHandleId, janusPluginHandle);
            janusServer.feedPlugins.put(janusPluginHandle.janusUserDetail.getFeedId(), janusPluginHandle);
        }
    }

    /**
     * Set recycler adapter for janus remote streams
     */
    private void initJanusStreamsRecycler() {
        janusStreamsAdapter = new DummyJanusStreamsAdapter(this, janusPluginHandles, this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView_SurfaceRenderers.setLayoutManager(gridLayoutManager);
        recyclerView_SurfaceRenderers.setAdapter(janusStreamsAdapter);
    }

    /**
     * Get current selected user index from adapter.
     * get newly selected user index from adapter.
     * update handles and update those handles into adapter
     * update hash maps for plugin handles
     *
     * @param index
     * @param object
     */
    private void swapUsers(int index, Object object) {
        if (null != object) {
            if (object instanceof JanusPluginHandle) {
                //get index for current selected handle.
                int currentSelectedHandleIndex = janusStreamsAdapter.getHandlerIndexFromAdapter(selectedUserFeedId);
                JanusPluginHandle currentSelectedHandle = janusServer.attachedPlugins.get(selectedUserHandleId);

                /*
                 * if Current user is self user
                 * */
                if (selectedUserHandleId.equals(handle.id)) {
                    currentSelectedHandle.janusUserDetail.setVideoEnable(isVideoEnable);
                    currentSelectedHandle.janusUserDetail.setMicEnable(isVoiceEnable);
                }
                currentSelectedHandle.janusUserDetail.setSelected(false);

                /*
                 * Get newly selected handle and update its values
                 * */
                JanusPluginHandle newSelectedHandle = (JanusPluginHandle) object;
                selectedUserHandleId = newSelectedHandle.id;
                selectedUserFeedId = newSelectedHandle.janusUserDetail.getFeedId();
                newSelectedHandle.janusUserDetail.setSelected(true);

                /*
                 * If current selected handle is equal to self then update "0" index for self user.
                 * and if current selected handle is not equal to self user then update at its own index.
                 * */
                if (currentSelectedHandle.id.equals(handle.id)) {
                    janusStreamsAdapter.updateRenderer(0, currentSelectedHandle);
                } else {
                    janusStreamsAdapter.updateRenderer(currentSelectedHandleIndex, currentSelectedHandle);
                }
                janusStreamsAdapter.updateRenderer(index, newSelectedHandle);

                /*
                 * update Hashmaps for plugin handles.
                 * */
                janusServer.attachedPlugins.put(currentSelectedHandle.id, currentSelectedHandle);
                janusServer.feedPlugins.put(newSelectedHandle.janusUserDetail.getFeedId(), newSelectedHandle);
            }
        }
    }

    @Override
    public void sendData(Object object) {
    }

    @Override
    public void sendData(final int index, final Object object) {
        swapUsers(index, object);
    }

    @Override
    public void onAttendeeSelected(Object object, int index) {
        swapUsers(index, object);
    }

    @Override
    public void onMicClick(Object object, int index) {
        if (object instanceof JanusPluginHandle) {
            JanusPluginHandle janusPluginHandle = (JanusPluginHandle) object;
            janusServer.attachedPlugins.put(janusPluginHandle.id, janusPluginHandle);
            janusServer.feedPlugins.put(janusPluginHandle.janusUserDetail.getFeedId(), janusPluginHandle);
        }
    }

    class ListenerAttachCallbacks implements IJanusPluginCallbacks {
        private final BigInteger feedId;
        private final String displayName;
        private JanusPluginHandle listener_handle;

        public ListenerAttachCallbacks(BigInteger id, String displayName) {
            this.feedId = id;
            this.displayName = displayName;
        }

        public void success(JanusPluginHandle handle) {
            listener_handle = handle;
            listener_handle.janusUserDetail.setVideoEnable(true);
            listener_handle.janusUserDetail.setMicEnable(true);
            listener_handle.janusUserDetail.setFeedId(feedId);
            janusServer.attachedPlugins.put(listener_handle.id, listener_handle);
            janusServer.feedPlugins.put(feedId, listener_handle);
            try {
                JSONObject body = new JSONObject();
                JSONObject msg = new JSONObject();
                body.put(REQUEST, "join");
                body.put("room", roomid);
                body.put("ptype", "subscriber");
                body.put("feed", feedId);
                msg.put(MESSAGE, body);
                handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
            } catch (Exception ex) {
//                Log.d(TAG, "success: Subscriber" + ex);
            }
        }

        @Override
        public void onMessage(JSONObject msg, JSONObject jsep) {
            try {
//                Log.d(TAG, "onMessage: Subscriber==> " + msg);
                String event = msg.getString("videoroom");
                if (event.equals("attached") && jsep != null) {
                    final JSONObject remoteJsep = jsep;
                    listener_handle.createAnswer(new IPluginHandleWebRTCCallbacks() {
                        @Override
                        public void onSuccess(JSONObject obj) {
                            try {
                                JSONObject mymsg = new JSONObject();
                                JSONObject body = new JSONObject();
                                body.put(REQUEST, "start");
                                body.put("room", roomid);
                                mymsg.put(MESSAGE, body);
                                mymsg.put("jsep", obj);
                                listener_handle.sendMessage(new PluginHandleSendMessageCallbacks(mymsg));
                            } catch (Exception ex) {
//                                Log.d(TAG, "onSuccess: " + ex);
                            }
                        }

                        @Override
                        public JSONObject getJsep() {
                            return remoteJsep;
                        }

                        @Override
                        public JanusMediaConstraints getMedia() {
                            JanusMediaConstraints cons = new JanusMediaConstraints();
                            cons.setVideo(null);
                            cons.setRecvAudio(true);
                            cons.setRecvVideo(true);
                            cons.setSendAudio(false);
                            return cons;
                        }

                        @Override
                        public Boolean getTrickle() {
                            return true;
                        }

                        @Override
                        public void disconnectedUser(BigInteger handleId) {
                            showToast(MyLifecycleHandler.activity, "Someother user got disconnected.");

                            /*
                             * Check if user exist in adapter and hashmaps
                             * then remove it from the hashmap and adapter
                             * */

                            runOnUiThread(() -> removeUserOnWebRtcError(handleId));

                        }

                        @Override
                        public void onCallbackError(String error) {
//                            Log.d(TAG, "onCallbackError: " + error);
                        }
                    });
                }
                /*
                 * if user again enable/disable video
                 * */
                else if (event.equals("event") && msg.getString("configured").equals("ok") && jsep != null) {
//                    Log.d(TAG, "ListenerAttachCallbacks ==> onMessage: " + jsep.toString());
                    JSONObject remoteJsep = jsep;
                    listener_handle.mySdp = null;
                    listener_handle.createAnswer(new IPluginHandleWebRTCCallbacks() {
                        @Override
                        public void onSuccess(JSONObject obj) {
                            try {
                                JSONObject mymsg = new JSONObject();
                                JSONObject body = new JSONObject();
                                body.put(REQUEST, "start");
                                body.put("room", roomid);
                                mymsg.put(MESSAGE, body);
                                mymsg.put("jsep", obj);
                                listener_handle.sendMessage(new PluginHandleSendMessageCallbacks(mymsg));
                            } catch (Exception ex) {
//                                Log.d(TAG, "onSuccess: CreateAnswer" + ex.toString());
                            }
                        }

                        @Override
                        public JSONObject getJsep() {
                            return remoteJsep;
                        }

                        @Override
                        public JanusMediaConstraints getMedia() {
                            JanusMediaConstraints cons = new JanusMediaConstraints();
                            cons.setVideo(null);
                            cons.setRecvAudio(true);
                            cons.setRecvVideo(true);
                            cons.setSendAudio(false);
                            return cons;
                        }

                        @Override
                        public Boolean getTrickle() {
                            return true;
                        }

                        @Override
                        public void disconnectedUser(BigInteger handleId) {
                        }

                        @Override
                        public void onCallbackError(String error) {
//                            Log.d(TAG, "onCallbackError: " + error);
                        }
                    });
                }
            } catch (Exception ex) {
//                Log.d(TAG, "onMessage: " + ex);
            }
        }

        @Override
        public void onLocalStream(MediaStream stream) {

        }

        @Override
        public void onRemoteStream(MediaStream stream) {
            MyLifecycleHandler.activity.runOnUiThread(() -> {
                View view = webRTCEngine.convertStreamIntoView(stream, true, MyLifecycleHandler.activity);
                JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(listener_handle.janusUserDetail.getHandleId());
                assert janusPluginHandle != null;
                janusPluginHandle.janusUserDetail.setUserName(displayName);
                janusPluginHandle.janusUserDetail.setView(view);
                janusPluginHandle.janusUserDetail.setSelected(false);
                janusPluginHandle.janusUserDetail.setVideoEnable(janusPluginHandle.janusUserDetail.isVideoEnable());
                janusPluginHandle.janusUserDetail.setMicEnable(janusPluginHandle.janusUserDetail.isMicEnable());

                Log.d(TAG, "onRemoteStream: " + janusPluginHandle.toString());

                janusServer.attachedPlugins.put(listener_handle.janusUserDetail.getHandleId(), janusPluginHandle);
                janusServer.feedPlugins.put(janusPluginHandle.janusUserDetail.getFeedId(), janusPluginHandle);
                janusStreamsAdapter.addRenderer(janusPluginHandle);

                if (CALL_IN_PROCESS != GlobalData.currentCall.getState()) {
                    GlobalData.currentCall.setCallState(CONNECTED);
                }

                if (CONNECTED == GlobalData.currentCall.getState()) {
                }
            });
        }

        @Override
        public void updateStream(MediaStream mediaStream) {
            MyLifecycleHandler.activity.runOnUiThread(() -> {
                View view = webRTCEngine.convertStreamIntoView(mediaStream, true, MyLifecycleHandler.activity);
                JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(listener_handle.janusUserDetail.getHandleId());
                assert janusPluginHandle != null;
                janusPluginHandle.janusUserDetail.setUserName(displayName);
                janusPluginHandle.janusUserDetail.setView(view);
                if (selectedUserFeedId.equals(janusPluginHandle.janusUserDetail.getFeedId())) {
                    janusPluginHandle.janusUserDetail.setSelected(true);
                } else {
                    janusPluginHandle.janusUserDetail.setSelected(false);
                }

                janusPluginHandle.janusUserDetail.setVideoEnable(janusPluginHandle.janusUserDetail.isVideoEnable());
                janusPluginHandle.janusUserDetail.setMicEnable(janusPluginHandle.janusUserDetail.isMicEnable());

                janusServer.attachedPlugins.put(listener_handle.janusUserDetail.getHandleId(), janusPluginHandle);
                janusServer.feedPlugins.put(janusPluginHandle.janusUserDetail.getFeedId(), janusPluginHandle);

                if (selectedUserFeedId.equals(janusPluginHandle.janusUserDetail.getFeedId())) {
                    if (view != null) {
                        if (view.getParent() != null) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                    }
                } else {
                    int index = janusStreamsAdapter.getHandlerIndexFromAdapter(listener_handle.janusUserDetail.getFeedId());
                    if (-1 != index) {
                        janusStreamsAdapter.updateRenderer(index, janusPluginHandle);
                    }
                }

                if (CALL_IN_PROCESS != GlobalData.currentCall.getState()) {
                    GlobalData.currentCall.setCallState(CONNECTED);
                }

                if (CONNECTED == GlobalData.currentCall.getState()) {
                }
            });
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataOpen(Object data) {

        }

        @Override
        public void onData(Object data) {

        }

        @Override
        public void onCleanup() {

        }

        @Override
        public void onDetached() {
//            Log.d(TAG, "onDetached: Subscriber");
        }

        @Override
        public JanusSupportedPluginPackages getPlugin() {
            return JanusSupportedPluginPackages.JANUS_VIDEO_ROOM;
        }

        @Override
        public void onCallbackError(String error) {

        }
    }

    public class JanusGlobalCallbacks implements IJanusGatewayCallbacks {
        public void onSuccess() {
//            janusPublisherPluginCallbacks = new JanusPublisherPluginCallbacks();
//            janusServer.Attach(janusPublisherPluginCallbacks);
            janusServer.Attach(new JanusPublisherPluginCallbacks());
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public String getServerUri() {
            return JANUS_URI;
        }

        @Override
        public List<PeerConnection.IceServer> getIceServers() {
            return new ArrayList<>();
        }

        @Override
        public Boolean getIpv6Support() {
            return Boolean.FALSE;
        }

        @Override
        public Integer getMaxPollEvents() {
            return 0;
        }

        @Override
        public void onCallbackError(String error) {

        }
    }

    public class JanusPublisherPluginCallbacks implements IJanusPluginCallbacks {

        public void publishOwnFeed(boolean isVideoEnable, boolean isAudioEnable) {
            if (null != handle) {
                handle.createOffer(new IPluginHandleWebRTCCallbacks() {
                    @Override
                    public void onSuccess(final JSONObject obj) {
                        try {
                            final JSONObject msg = new JSONObject();
                            final JSONObject body = new JSONObject();
                            body.put(REQUEST, "configure");
                            body.put("audio", true);
                            body.put("video", true);
                            body.put("videocodec", "vp8");
                            msg.put(MESSAGE, body);
                            msg.put("jsep", obj);
                            handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
                        } catch (Exception ex) {
                            Log.e(TAG, "onSuccess: ", ex);
                        }
                    }

                    @Override
                    public JSONObject getJsep() {
                        return null;
                    }

                    @Override
                    public JanusMediaConstraints getMedia() {
                        final JanusMediaConstraints cons = new JanusMediaConstraints();
                        cons.setRecvAudio(false);
                        cons.setRecvVideo(false);
                        cons.setSendAudio(true);
                        return cons;
                    }

                    @Override
                    public Boolean getTrickle() {
                        return true;
                    }

                    @Override
                    public void disconnectedUser(BigInteger handleId) {
                        showToast(MyLifecycleHandler.activity, "Self user got disconnected.");

                        /*
                         * Todo
                         *  show reconnecting screen
                         *  clear adapter
                         *  clear all hash maps
                         *  call janus start
                         *  make socket connection.
                         * */
                        if (MyLifecycleHandler.activity.getClass().getSimpleName().equals("VideoRoomActivity")) {
                            runOnUiThread(() -> {
                                GlobalData.currentCall.setCallState(RE_CONNECTING);
                                janusPluginHandles.clear();
                                janusStreamsAdapter.notifyDataSetChanged();
                                janusServer.attachedPlugins.clear();
                                janusServer.feedPlugins.clear();
                                reconnectionTimer();//for reconnection
                            });

                        } else {
                            runOnUiThread(() -> {
                                handleCallDisconnectionScenarios();
                            });
                        }
                    }

                    @Override
                    public void onCallbackError(final String error) {

                    }
                });
            }
        }

        private void createRoom() {
            final JSONObject body = new JSONObject();
            final JSONObject msg = new JSONObject();
            try {
                body.put(REQUEST, "create");
                body.put("room", roomid);
                body.put("permanent", false);
                body.put("description", "Android_Room");
                body.put("is_private", false);
                body.put("publishers", 25);
                body.put("videocodec", "vp8");
                body.put("bitrate", 30000);

                msg.put(MESSAGE, body);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
        }

        private void registerUsername() {
            if (handle != null) {
                final JSONObject obj = new JSONObject();
                final JSONObject msg = new JSONObject();
                try {
                    obj.put(REQUEST, "join");
                    obj.put("room", roomid);
                    obj.put("ptype", "publisher");
                    obj.put("display", user_name);
                    msg.put(MESSAGE, obj);
                } catch (final Exception ex) {
                    Log.e(TAG, "registerUsername: ", ex);
                }
                handle.sendMessage(new PluginHandleSendMessageCallbacks(msg));
            }
        }

        private void newRemoteFeed(final BigInteger id, final String displayName) { //todo attach the plugin as a listener
            janusServer.Attach(new ListenerAttachCallbacks(id, displayName));
        }

        @Override
        public void success(final JanusPluginHandle pluginHandle) {
            handle = pluginHandle;
            janusStreamsAdapter.setPubHandleId(handle.janusUserDetail.getHandleId());
            if (ROOM_CREATION) {
                this.createRoom();
            } else {
                this.registerUsername();
            }
        }

        @Override
        public void onMessage(final JSONObject msg, final JSONObject jsepLocal) {
            try {
                final String event = msg.getString("videoroom");
                if (event.equals("joined")) {
                    GlobalData.currentCall.setCallState(CONNECTING);
                    myid = new BigInteger(msg.getString("id"));
                    handle.janusUserDetail.setVideoEnable(isVideoEnable);
                    handle.janusUserDetail.setMicEnable(isVoiceEnable);
                    handle.janusUserDetail.setFeedId(myid);
                    janusServer.feedPlugins.put(myid, handle);
                    janusServer.attachedPlugins.put(handle.id, handle);

                    this.publishOwnFeed(isVideoEnable, isVoiceEnable);
                    if (msg.has(PUBLISHERS)) {
                        final JSONArray pubs = msg.getJSONArray(PUBLISHERS);
                        for (int i = 0; i < pubs.length(); i++) {
                            final JSONObject pub = pubs.getJSONObject(i);
                            final BigInteger tehId = new BigInteger(pub.getString("id"));
                            final String displayName = pub.getString("display");
                            this.newRemoteFeed(tehId, displayName);
                        }
                    }
                } else if (event.equals("destroyed")) {
//                    Log.d(VideoRoom.TAG, "onMessage: destroyed" + msg);
                } else if (event.equals("created")) {
                    this.registerUsername();
                } else if (event.equals("event")) {
                    if (msg.has(PUBLISHERS)) {
                        final JSONArray pubs = msg.getJSONArray(PUBLISHERS);
                        for (int i = 0; i < pubs.length(); i++) {
                            final JSONObject pub = pubs.getJSONObject(i);
                            final BigInteger tehId = new BigInteger(pub.getString("id"));
                            final String displayName = pub.getString("display");
                            this.newRemoteFeed(tehId, displayName);
                        }
                    } else if (msg.has("leaving")) {
                        subscriberOnLeaving(msg);
                    } else if (msg.has("unpublished")) {
                        subscriberOnUnPublish(msg);
                    } else {
                        //todo error
                        if (msg.has("error")) {
                            if (msg.has("error_code")) {
                                int errorCode = msg.getInt("error_code");
                                if (errorCode == 426) {
                                    runOnUiThread(() -> {
                                        showToast(MyLifecycleHandler.activity, "This room link has been already expired.");
                                        GlobalData.currentCall.setCallState(IDLE);
                                        disconnectCall();
                                        janusServer = null;
                                        SingletonWebRtc.releaseInstance();

                                        finishAffinity();
                                    });
                                }
                            }
                        }
                    }
                }
                if (jsepLocal != null) {
                    handle.handleRemoteJsep(new PluginHandleWebRTCCallbacks(null, jsepLocal, false));
                }
            } catch (final Exception ex) {
                Log.e(TAG, "onMessage: ", ex);
            }
        }

        @Override
        public void onLocalStream(final MediaStream stream) {
        }

        @Override
        public void onRemoteStream(final MediaStream stream) {

        }

        @Override
        public void updateStream(MediaStream mediaStream) {

        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataOpen(final Object data) {

        }

        @Override
        public void onData(final Object data) {

        }

        @Override
        public void onCleanup() {

        }

        @Override
        public JanusSupportedPluginPackages getPlugin() {
            return JanusSupportedPluginPackages.JANUS_VIDEO_ROOM;
        }

        @Override
        public void onCallbackError(final String error) {
//            Log.d(TAG, "onCallbackError: Publisher" + error);
        }

        @Override
        public void onDetached() {
//            Log.d(TAG, "onDetached: Publisher");
        }
    }

    private void changeIconsOpacity(boolean isEnable) {
        imageButton_Mic.setAlpha(isEnable ? 1.0f : 0.3f);
        imageButton_Video.setAlpha(isEnable ? 1.0f : 0.3f);
    }

    private void removeUserOnWebRtcError(BigInteger handleId) {
        if (selectedUserHandleId.equals(handleId)) {

            selectedUserFeedId = myid;
            selectedUserHandleId = handle.id;
            JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(handle.id);
            if (janusPluginHandle != null && janusPluginHandle.janusUserDetail != null) {
                janusPluginHandle.janusUserDetail.setSelected(true);

                /*
                 * update plugins
                 * */
                if (janusServer != null && janusServer.attachedPlugins != null && handle.id != null) {
                    janusServer.attachedPlugins.put(handle.id, janusPluginHandle);
                }
                if (janusServer != null && janusServer.feedPlugins != null && myid != null) {
                    janusServer.feedPlugins.put(myid, janusPluginHandle);
                }

                /*
                 * update main renderer
                 * update renderers list at index "0"
                 * */
                runOnUiThread(() -> {
                    janusStreamsAdapter.updateRenderer(0, janusPluginHandle);
                });
            }
        }
        /*
         * Remove Subscriber from adapter and release its renderer.
         * */
        else {
            if (janusServer.attachedPlugins.containsKey(handleId)) {
                JanusPluginHandle janusPluginHandle = janusServer.attachedPlugins.get(handleId);
                if (janusPluginHandle.janusUserDetail != null && janusPluginHandle.janusUserDetail.getFeedId() != null) {
                    BigInteger feedId = janusPluginHandle.janusUserDetail.getFeedId();
                    int index = janusStreamsAdapter.getHandlerIndexFromAdapter(feedId);
                    if (index != -1) {
                        MyLifecycleHandler.activity.runOnUiThread(() -> janusStreamsAdapter.removeRendererByIndex(index));

                        /*
                         * Remove subscriber plugin from HashMap
                         * */
                        if (janusPluginHandle != null) {
                            showToast(MyLifecycleHandler.activity, janusPluginHandle.janusUserDetail.getUserName() + " left the meeting.");
                            janusServer.feedPlugins.remove(feedId);
                            janusServer.attachedPlugins.remove(handleId);
                        }
                    }
                }
            }
        }
    }

    private void reconnectionTimer() {
    }

    private void handleCallDisconnectionScenarios() {
        disconnectCall();

        janusServer = null;
        SingletonWebRtc.releaseInstance();

        finishAffinity();
    }

    private void configureMic(boolean isMicEnable) {
        if (isMicEnable) {
            imageButton_Mic.setImageResource(R.mipmap.mic);
        } else {
            imageButton_Mic.setImageResource(R.mipmap.mic_disable);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        setControlsVisibility(isInPictureInPictureMode);
    }

    @Override
    protected void onUserLeaveHint() {
        if (supportsPiPMode() && isCallInProcess(GlobalData.currentCall.getState())) {
            enterPictureInPictureMode(new PictureInPictureParams.Builder().build());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        Log.d(TAG, "onNewIntent: ");
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (IDLE == GlobalData.currentCall.getState() && isShareApp) {
            isShareApp = false;
            return;
        }

        if (CALL_IN_PROCESS == GlobalData.currentCall.getState() || CONNECTED == GlobalData.currentCall.getState()) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EnumType.CallState callState = GlobalData.currentCall.getState();
        Log.d(TAG, "onPause: VideoCallActivity(0)" + callState.toString());

        if (attendees_bottomSheet != null) {
            attendees_bottomSheet.dismiss();
        }

        if (isShareApp && GlobalData.currentCall.getState() == IDLE || GlobalData.currentCall.getState() == CONNECTING) {
            return;
        }

        callState = GlobalData.currentCall.getState();
        Log.d(TAG, "onPause: VideoCallActivity(1)" + callState.toString());
        if (isActivityStateValid() && CALL_IN_PROCESS == callState || CONNECTING == callState || CONNECTED == callState) {
            Log.d(TAG, "onPause: VideoCallActivity(2)");
            GlobalData.currentCall.setCallState(CALL_IN_PROCESS);
            setActivityState();
            if (supportsPiPMode()) {
                enterPictureInPictureMode(new PictureInPictureParams.Builder().build());
            }
        } else {
            Log.d(TAG, "onPause: VideoCallActivity(2)");
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {

        EnumType.CallState callState = GlobalData.currentCall.getState();
        if (isActivityStateValid() && CALL_IN_PROCESS == callState || CONNECTING == callState || CONNECTED == callState) {
            GlobalData.currentCall.setCallState(CALL_IN_PROCESS);
            setActivityState();
            if (supportsPiPMode()) {
                enterPictureInPictureMode(new PictureInPictureParams.Builder().build());
            }
        } else {
//            RESET_GLOBAL_DATA();
//            removeChatMessages();
//            Intent intent = new Intent(this, FragmentActivity.class);
//            startActivity(intent);

            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!isInPictureInPictureMode()) {
            Log.d(TAG, "onRestart: InFullScreenMode");
        }
    }

    public boolean supportsPiPMode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }


    private boolean isCallInProcess(EnumType.CallState callState) {
        return (CALL_IN_PROCESS == callState || CONNECTED == callState || CONNECTING == callState);
    }

    private void setControlsVisibility(boolean isInPipMode) {
        if (isInPipMode) {
            recyclerView_SurfaceRenderers.setVisibility(View.GONE);
            constraintLayout_CallControls.setVisibility(View.GONE);
        } else {
            recyclerView_SurfaceRenderers.setVisibility(View.VISIBLE);
            constraintLayout_CallControls.setVisibility(View.VISIBLE);
        }
    }


    public static void showToast(Context context, String message) {
        MyLifecycleHandler.activity.runOnUiThread(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}