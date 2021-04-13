package com.ono.cas.teacher.janusclientapi;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.PeerConnection;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class JanusServer implements Runnable, IJanusMessageObserver, IJanusSessionCreationCallbacks, IJanusAttachPluginCallbacks {

    @Override
    public void run() {

    }

    private class RandomString {
        final String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final Random rnd = new Random();

        public String randomString(Integer length) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(str.charAt(rnd.nextInt(str.length())));
            }
            return sb.toString();
        }
    }

    private final RandomString stringGenerator = new RandomString();
    public ConcurrentHashMap<BigInteger, JanusPluginHandle> attachedPlugins = new ConcurrentHashMap<>();
    public ConcurrentHashMap<BigInteger, JanusPluginHandle> feedPlugins = new ConcurrentHashMap<>();
    private Object attachedPluginsLock = new Object();
    private ConcurrentHashMap<String, ITransactionCallbacks> transactions = new ConcurrentHashMap<String, ITransactionCallbacks>();
    private Object transactionsLock = new Object();
    public String serverUri;
    public IJanusGatewayCallbacks gatewayObserver;
    public List<PeerConnection.IceServer> iceServers;
    public Boolean ipv6Support;
    public Integer maxPollEvents;
    private BigInteger sessionId;
    private Boolean connected;
    private IJanusMessenger serverConnection;
    private volatile Thread keep_alive;
    private Boolean peerConnectionFactoryInitialized = false;
    public static final String TAG = "JanusServer";

    private class AsyncAttach extends AsyncTask<IJanusPluginCallbacks, Void, Void> {
        protected Void doInBackground(IJanusPluginCallbacks... cbs) {
            IJanusPluginCallbacks cb = cbs[0];
            try {
                JSONObject obj = new JSONObject();
                obj.put("janus", JanusMessageType.attach);
                obj.put("plugin", cb.getPlugin());
                obj.put("session_id", sessionId);
                ITransactionCallbacks tcb = JanusTransactionCallbackFactory.createNewTransactionCallback(JanusServer.this, TransactionType.attach, cb.getPlugin(), cb);
                String transaction = putNewTransaction(tcb);
                obj.put("transaction", transaction);
                serverConnection.sendMessage(obj.toString(), sessionId);
            } catch (JSONException ex) {
                onCallbackError(ex.getMessage());
            }
            return null;
        }
    }

    public JanusServer() {

    }

    public JanusServer(IJanusGatewayCallbacks gatewayCallbacks) {
        gatewayObserver = gatewayCallbacks;
//        System.setProperty("java.net.preferIPv6Addresses", "false");
//        System.setProperty("java.net.preferIPv4Stack", "true");
        serverUri = gatewayObserver.getServerUri();
        iceServers = gatewayObserver.getIceServers();
        ipv6Support = gatewayObserver.getIpv6Support();
        maxPollEvents = gatewayObserver.getMaxPollEvents();
        connected = false;
        sessionId = new BigInteger("-1");
        serverConnection = JanusMessagerFactory.createMessager(serverUri, this);
    }

    private String putNewTransaction(ITransactionCallbacks transactionCallbacks) {
        String transaction = stringGenerator.randomString(12);
        synchronized (transactionsLock) {
            while (transactions.containsKey(transaction))
                transaction = stringGenerator.randomString(12);
            transactions.put(transaction, transactionCallbacks);
        }
        return transaction;
    }

    private void createSession() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("janus", JanusMessageType.create);
            ITransactionCallbacks cb = JanusTransactionCallbackFactory.createNewTransactionCallback(this, TransactionType.create);
            String transaction = putNewTransaction(cb);
            obj.put("transaction", transaction);
            serverConnection.sendMessage(obj.toString());
        } catch (JSONException ex) {
            onCallbackError(ex.getMessage());
        }
    }

//    public void run() {
//        Thread thisThread = Thread.currentThread();
//        while (keep_alive == thisThread) {
//            try {
//                thisThread.sleep(25000);
//            } catch (InterruptedException ex) {
//            }
//            if (serverConnection != null) {
//                JSONObject obj = new JSONObject();
//                try {
//                    obj.put("janus", JanusMessageType.keepalive.toString());
//                    obj.put("session_id", sessionId);
//                    obj.put("transaction", stringGenerator.randomString(12));
//                    serverConnection.sendMessage(obj.toString(), sessionId);
//                } catch (JSONException ex) {
//                    gatewayObserver.onCallbackError("Keep alive failed is Janus online?" + ex.getMessage());
//                    connected = false;
//                    return;
//                }
//            }
//        }
//    }

    public Boolean isConnected() {
        return connected;
    }

    public BigInteger getSessionId() {
        return sessionId;
    }

    public void Attach(IJanusPluginCallbacks callbacks) {
        new AsyncAttach().execute(callbacks);
    }

    public void Destroy() {
        keep_alive = null;
        connected = false;
        gatewayObserver.onDestroy();

        for (ConcurrentHashMap.Entry<BigInteger, JanusPluginHandle> handle : attachedPlugins.entrySet()) {
            handle.getValue().detach();
        }
        synchronized (transactionsLock) {
            for (Object trans : transactions.entrySet())
                transactions.remove(trans);
        }

        serverConnection.disconnect();
        serverConnection = null;
        stopKeepAliveTimer();
    }

    public void Connect() {
        serverConnection.connect();
    }

    public void newMessageForPlugin(String message, BigInteger plugin_id) {
        JanusPluginHandle handle = null;
        synchronized (attachedPluginsLock) {
            handle = attachedPlugins.get(plugin_id);
        }
        if (handle != null) {
            handle.onMessage(message);
        }
    }

    @Override
    public void onCallbackError(String msg) {
        gatewayObserver.onCallbackError(msg);
    }

    public void sendMessage(JSONObject msg, JanusMessageType type, BigInteger handle) {
        try {
            msg.put("janus", type.toString());
            msg.put("session_id", sessionId);
            msg.put("handle_id", handle);
            msg.put("transaction", stringGenerator.randomString(12));

            serverConnection.sendMessage(msg.toString(), sessionId, handle);
            if (type == JanusMessageType.detach) {
                synchronized (attachedPluginsLock) {
                    if (attachedPlugins.containsKey(handle)) {
                        JanusPluginHandle janusPluginHandle = attachedPlugins.get(handle);
                        if (feedPlugins != null &&
                                janusPluginHandle.janusUserDetail != null &&
                                janusPluginHandle.janusUserDetail.getFeedId() != null &&
                                feedPlugins.containsKey(janusPluginHandle.janusUserDetail.getFeedId())) {
                            feedPlugins.remove(janusPluginHandle.janusUserDetail.getFeedId());
                        }
                        attachedPlugins.remove(handle);
                    }
                }
            }
        } catch (JSONException ex) {
            gatewayObserver.onCallbackError(ex.getMessage());
        }
    }

    //TODO not sure if the send message functions should be Asynchronous

    public void sendMessage(TransactionType type, BigInteger handle, IPluginHandleSendMessageCallbacks callbacks, JanusSupportedPluginPackages plugin) {
        JSONObject msg = callbacks.getMessage();
        if (msg != null) {
            try {
                JSONObject newMessage = new JSONObject();

                newMessage.put("janus", JanusMessageType.message.toString());
                newMessage.put("session_id", sessionId);
                newMessage.put("handle_id", handle);

                ITransactionCallbacks cb = JanusTransactionCallbackFactory.createNewTransactionCallback(this, TransactionType.plugin_handle_message, plugin, callbacks);
                String transaction = putNewTransaction(cb);
                newMessage.put("transaction", transaction);
                if (msg.has("message"))
                    newMessage.put("body", msg.getJSONObject("message"));
                if (msg.has("jsep"))
                    newMessage.put("jsep", msg.getJSONObject("jsep"));
                serverConnection.sendMessage(newMessage.toString(), sessionId, handle);
            } catch (JSONException ex) {
                callbacks.onCallbackError(ex.getMessage());
            }
        }
    }

    public void sendMessage(TransactionType type, BigInteger handle, IPluginHandleWebRTCCallbacks callbacks, JanusSupportedPluginPackages plugin) {
        try {
            JSONObject msg = new JSONObject();

            msg.put("janus", JanusMessageType.message.toString());
            msg.put("session_id", sessionId);
            msg.put("handle_id", handle);

            ITransactionCallbacks cb = JanusTransactionCallbackFactory.createNewTransactionCallback(this, TransactionType.plugin_handle_webrtc_message, plugin, callbacks);
            String transaction = putNewTransaction(cb);
            msg.put("transaction", transaction);
            if (callbacks.getJsep() != null) {
                msg.put("jsep", callbacks.getJsep());
            }
            serverConnection.sendMessage(msg.toString(), sessionId, handle);
        } catch (JSONException ex) {
            callbacks.onCallbackError(ex.getMessage());
        }
    }

    private void handleRoomCreationResponse(JanusPluginHandle janusPluginHandle, JSONObject jsonObject) {
        try {
            if (jsonObject.has("plugindata")) {
                JSONObject plugindata = jsonObject.getJSONObject("plugindata");
                if (plugindata.has("data")) {
                    JSONObject data = plugindata.getJSONObject("data");
                    if (data.has("videoroom")) {
                        String videoroom = data.getString("videoroom");
                        if (videoroom.equals("created")) {
                            if (janusPluginHandle != null) {
                                janusPluginHandle.onMessage(data.toString());
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //region MessageObserver
    @Override
    public void receivedNewMessage(JSONObject obj) {
        try {
            Log.d(TAG, "JANUSCLIENT: Sent: ==> receivedNewMessage: " + obj.toString());
            JanusMessageType type = JanusMessageType.fromString(obj.getString("janus"));
            String transaction = null;
            BigInteger sender = null;
            if (obj.has("transaction")) {
                transaction = obj.getString("transaction");
            }
            if (obj.has("sender"))
                sender = new BigInteger(obj.getString("sender"));
            JanusPluginHandle handle = null;
            if (sender != null) {
                synchronized (attachedPluginsLock) {
                    handle = attachedPlugins.get(sender);
                }
            }
            switch (type) {
                case keepalive:
                    break;
                case ack:
                case success:
                case error: {
                    if (transaction != null) {
                        ITransactionCallbacks cb = null;
                        synchronized (transactionsLock) {
                            cb = transactions.get(transaction);
                            if (cb != null)
                                transactions.remove(transaction);
                        }
                        if (cb != null) {
                            cb.reportSuccess(obj);
                            transactions.remove(transaction);
                        }
                        handleRoomCreationResponse(handle, obj);
                    }
                    break;
                }
                case hangup: {
                    if (handle != null) {
                        handle.hangUp();
                    }
                    break;
                }
                case detached: {
                    if (handle != null) {
                        handle.onDetached();
                        handle.detach();
                    }
                    break;
                }
                case event: {
                    if (handle != null) {
                        JSONObject plugin_data = null;
                        if (obj.has("plugindata"))
                            plugin_data = obj.getJSONObject("plugindata");
                        if (plugin_data != null) {
                            JSONObject data = null;
                            JSONObject jsep = null;
                            if (plugin_data.has("data"))
                                data = plugin_data.getJSONObject("data");
                            if (obj.has("jsep"))
                                jsep = obj.getJSONObject("jsep");
                            handle.onMessage(data, jsep);
                        }
                    }
                }
            }
        } catch (JSONException ex) {
            gatewayObserver.onCallbackError(ex.getMessage());
        }
    }

    @Override
    public void onOpen() {
        startKeepAliveTimer();
        createSession();
    }

    @Override
    public void onClose() {
        connected = false;
        gatewayObserver.onCallbackError("Connection to janus server is closed");
    }

    @Override
    public void onError(Exception ex) {
        gatewayObserver.onCallbackError("Error connected to Janus gateway. Exception: " + ex.getMessage());
    }
    //endregion

    //region SessionCreationCallbacks
    @Override
    public void onSessionCreationSuccess(JSONObject obj) {
        try {
            sessionId = new BigInteger(obj.getJSONObject("data").getString("id"));
            keep_alive = new Thread(this, "KeepAlive");
            keep_alive.start();
            connected = true;
            //TODO do we want to keep track of multiple sessions and servers?
            gatewayObserver.onSuccess();
        } catch (JSONException ex) {
            gatewayObserver.onCallbackError(ex.getMessage());
        }
    }

    //endregion

    //region AttachPluginCallbacks

    @Override
    public void attachPluginSuccess(JSONObject obj, JanusSupportedPluginPackages plugin, IJanusPluginCallbacks pluginCallbacks) {
        try {
            BigInteger handle = new BigInteger(obj.getJSONObject("data").getString("id"));
            JanusPluginHandle pluginHandle = new JanusPluginHandle(this, plugin, handle, pluginCallbacks);
            pluginHandle.janusUserDetail.setHandleId(handle);
            synchronized (attachedPluginsLock) {
                attachedPlugins.put(handle, pluginHandle);
            }
            pluginCallbacks.success(pluginHandle);
        } catch (JSONException ex) {
            gatewayObserver.onCallbackError(ex.getMessage());
        }
    }
    //endregion

    private volatile boolean isKeepAliveRunning;
    private Thread keepAliveThread;

    public void startKeepAliveTimer() {
        isKeepAliveRunning = true;
        keepAliveThread = new Thread(() -> {
            while (isKeepAliveRunning) {
                try {
                    Thread.sleep(25000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (serverConnection != null) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("janus", JanusMessageType.keepalive.toString());
                        obj.put("session_id", sessionId);
                        obj.put("transaction", stringGenerator.randomString(12));
                        serverConnection.sendMessage(obj.toString(), sessionId);
                    } catch (JSONException ex) {
                        gatewayObserver.onCallbackError("Keep alive failed is Janus online?" + ex.getMessage());
                        connected = false;
                        return;
                    }
                } else {
//                    Log.e(TAG, "keepAlive failed websocket is null or not connected");
                }
            }
//            Log.d(TAG, "keepAlive thread stopped");
        }, "KeepAlive");
        keepAliveThread.start();
    }

    public void stopKeepAliveTimer() {
        isKeepAliveRunning = false;
        if (keepAliveThread != null) {
            keepAliveThread.interrupt();
        }
    }

}
