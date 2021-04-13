package com.ono.cas.teacher.janusclientapi;

import android.util.Log;

import org.json.JSONObject;

import java.math.BigInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;

/**
 * Created by ben.trent on 5/7/2015.
 */
public class JanusWebsocketMessenger implements IJanusMessenger {

    private final String uri;
    private final IJanusMessageObserver handler;
    private final JanusMessengerType type = JanusMessengerType.websocket;
    private okhttp3.WebSocket webSocket;
    public static final String TAG = "JanusWebsocketMessenger";

    public JanusWebsocketMessenger(String uri, IJanusMessageObserver handler) {
        this.uri = uri;
        this.handler = handler;
    }

    @Override
    public JanusMessengerType getMessengerType() {
        return type;
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Sec-WebSocket-Protocol", "janus-protocol")
                .url(uri)
                .build();
        webSocket = client.newWebSocket(request, new WebSocketHandler());
    }

    public class WebSocketHandler extends WebSocketListener {
        @Override
        public void onOpen(okhttp3.WebSocket webSocket, Response response) {
            handler.onOpen();
        }

        @Override
        public void onMessage(okhttp3.WebSocket webSocket, String text) {
            if (handler != null) {
                try {
                    JSONObject obj = new JSONObject(text);
//                    handler.receivedNewMessage(obj);
                    onNewMessage(text);
                } catch (Exception ex) {
                    handler.onError(ex);
                }
            }
        }

        @Override
        public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "onClosed: ");
            handler.onClose();
        }

        @Override
        public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
            Log.e(TAG, "onFailure: ", t);
        }
    }

    private void onMessageOrigianl(String message) {
        Log.d("JANUSCLIENT", "Recv: \n\t" + message);
        receivedMessage(message);
    }

    private void onNewMessage(String message) {
        Log.d("JANUSCLIENT", "Recv: \n\t" + message);
        receivedMessage(message);
    }

    private void onClose(int code, String reason, boolean remote) {
        handler.onClose();
    }

    private void onError(Exception ex) {
        handler.onError(ex);
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect: WebSocket Disconnected");
        webSocket.cancel();
    }

    @Override
    public void sendMessage(String message) {
        Log.d("JANUSCLIENT", "Sent: \n\t" + message);
//        client.send(message); //original
        webSocket.send(message);
    }

    @Override
    public void sendMessage(String message, BigInteger session_id) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(String message, BigInteger session_id, BigInteger handle_id) {
        sendMessage(message);
    }

    @Override
    public void receivedMessage(String msg) {
        Log.d(TAG, "JANUSCLIENT1 receivedMessage: " + msg);
        try {
            JSONObject obj = new JSONObject(msg);
            handler.receivedNewMessage(obj);
        } catch (Exception ex) {
            handler.onError(ex);
        }
    }
}
