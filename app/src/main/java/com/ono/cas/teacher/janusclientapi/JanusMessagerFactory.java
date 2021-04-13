package com.ono.cas.teacher.janusclientapi;

/**
 * Created by ben.trent on 6/25/2015.
 */
class JanusMessagerFactory {
    public static IJanusMessenger createMessager(String uri, IJanusMessageObserver handler) {

        return new JanusWebsocketMessenger(uri, handler);
//        if (uri.indexOf("wss") == 0) {
//            return new JanusWebsocketMessenger(uri, handler);
//        } else {
//            return new JanusRestMessenger(uri, handler);
//        }
    }
}
