package com.ono.cas.teacher.janusclientapi;

public interface WebSocketCallback {
    void onOpen();

    void onMessage(String text);

    void onClosed();
}
