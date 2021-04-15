package com.ono.cas.teacher.interfaces;

public interface Messages {

    interface FILE_OPTIONS_CODE {
        int CAMERA_CODE = 01;
        int IMAGE_CODE = 02;
        int VIDEO_CODE = 03;
        int FILE_CODE = 04;
        int AUDIO_CODE = 05;
    }

    interface ERROR {
        String INVALID_NAME = "Invalid Name";
        String INVALID_EMAIL = "Invalid Email";
        String INVALID_PASSWORD = "Invalid Password";
        String INVALID_CONFIRM_PASSWORD = "Invalid Password";
        String MISS_MATCH_PASSWORD = "Password Mis-match";
    }

    interface DATABASE_BOOK {
        String USER = "USER_BOOK";
        String DEVICE = "DEVICE_BOOK";
        String TEMP = "TEMP_BOOK";
    }

    interface DATABASE_KEYS {

    }

    interface Actions {
        String INCOMING_CALL = "INCOMING_CALL";
    }

    interface ToastMessages {
        String SOCKET_CONNECTION_ISSUE = "Couldn't place call. Make sure your phone has an internet connection and try again.";
        String CANNOT_MAKE_ANOTHER_CALL = "Right now, You cannot make another call.";
    }

    interface NetworkSocketConstant {
        //        String SOCKET_CONNECTION_URL = "https://prjcomm.trango.io";
        String SOCKET_CONNECTION_URL = "https://prjcomm.blst.site";

        /**
         * Connection
         */
        String SERVER_CONNECTED = "SERVER_CONNECTED";
        String SERVER_DISCONNECTED = "SERVER_DISCONNECTED";
        String SERVER_CONNECTION_ERROR = "SERVER_CONNECTION_ERROR";
        String SERVER_CONNECTION_TIMEOUT = "SERVER_CONNECTION_TIMEOUT";
        String TESTING_LISTENER = "TESTING_LISTENER";
        String RECONNECT = "reconnect";

        /**
         * listener
         */
        String SERVER_TYPE_LISTENER = "SERVER_TYPE_LISTENER";
        String RECONNECT_LISTENER = "RECONNECT_LISTENER";
        String RECONNECT_FAILED_LISTENER = "RECONNECT_FAILED_LISTENER";
        String RECONNECT_ERROR_LISTENER = "RECONNECT_ERROR_LISTENER";
        String RECONNECTING_LISTENER = "RECONNECTING_LISTENER";
        String LOGIN_LISTENER = "LOGIN_LISTENER";
        String REGISTER_DEVICE_LISTENER = "REGISTER_DEVICE_LISTENER";
        String SAME_NETWORK_LISTENER = "SAME_NETWORK_LISTENER";
        String ADD_PEER_LISTENER = "ADD_PEER_LISTENER";
        String DELETE_PEER_LISTENER = "DELETE_PEER_LISTENER";
        String ROOM_JOIN_SUCCESS = "ROOM_JOIN_SUCCESS";
        String ROOM_JOIN_FAILURE = "ROOM_JOIN_FAILURE";
        String CALL_RESPONSE_LISTENER = "CALL_RESPONSE_LISTENER";
        String CALL_REJECTION_RESPONSE_LISTENER = "CALL_REJECTION_RESPONSE_LISTENER";
        String CALL_ID_LISTENER = "CALL_ID_LISTENER";
        String OFFER_LISTENER = "OFFER_LISTENER";
        String ANSWER_LISTENER = "ANSWER_LISTENER";
        String CANDIDATE_LISTENER = "CANDIDATE_LISTENER";
        String PART_ROOM_LISTENER = "PART_ROOM_LISTENER";
        String PEER_PARTED_LISTENER = "PEER_PARTED_LISTENER";
        String VIDEO_LISTENER = "VIDEO_LISTENER";
        String DEEP_LINK_REGISTER_LISTENER = "DEEP_LINK_REGISTER_LISTENER";
        String ROOM_CREATION_OUTGOING = "ROOM_CREATION_OUTGOING";
        String ROOM_JOINING_INCOMING = "ROOM_JOINING_INCOMING";
        String ROOM_CREATION_IDLE = "ROOM_CREATION_IDLE";
        String RECONNECT_CALL_LISTENER = "RECONNECT_CALL_LISTENER";
        String NAME_CHANGED_LISTENER = "NAME_CHANGED_LISTENER";
        String PEER_JOINED_LISTENER = "PEER_JOINED_LISTENER";
        String FILE_REQUEST_LISTENER = "FILE_REQUEST_LISTENER";
        String FILE_RESPONSE_LISTENER = "FILE_RESPONSE_LISTENER";
        String FILE_COMPLETE_LISTENER = "FILE_COMPLETE_LISTENER";
        String FILE_CANCELLED_LISTENER = "FILE_CANCELLED_LISTENER";
        String CALL_DISMISSED_BY_RECIPIENT_LISTENER = "CALL_DISMISSED_BY_RECIPIENT_LISTENER";
        String CONNECTING_LISTENER = "CONNECTING_LISTENER";
        String ERROR_LISTENER = "ERROR_LISTENER";
        String CHAT_LISTENER = "CHAT_LISTENER";
        String BROADCAST_LISTENER = "BROADCAST_LISTENER";

        /**
         * Common
         */
        String SERVER_TYPE = "servertype";
        String LOGIN = "login";
        String REGISTER = "register";
        String SAME_NETWORK = "samenetwork";
        String ADD_PEER = "addpeer";
        String DELETE_PEER = "delpeer";

        /**
         * Call
         */
        String AUDIO = "audio";
        String VIDEO = "video";
        String CALL_RESPONSE = "callresponse";
        String CALL_REQUEST = "callrequest";
        String CHANGE_NAME = "changename";
        String NAME_CHANGED = "namechanged";

        String OFFER = "offer";
        String ANSWER = "answer";
        String CANDIDATE = "candidate";
        String PING = "ping";
        String PEER_JOINED = "peerjoined";
        String PEERS = "__peers";
        String CREATE_JOIN_ROOM = "joinroom";
        String PEER_PARTED = "peerparted";
        String LEAVE = "__leave";
        String PART_ROOM = "partroom";
        String CALL_ID = "callid";

        /**
         * File
         */
        String FILE_REQUEST = "filerequest";
        String FILE_RESPONSE = "fileresponse";
        String FILE_COMPLETE = "completed";
        String FILE_CANCELLED = "completed";

        /**
         * Janus Handlers
         */
        String JANUS_ROOM_JOIN = "janusroomjoin";
        String JANUS_CONFIGURE = "janusconfigure";
        String JANUS_PUBLISHER_JOINED = "januspubjoined";
        String JANUS_VIDEO = "janusvideo";
        String JANUS_AUDIO = "janusaudio";
        String JANUS_SCREEN = "janusscreen";
        String JANUS_CHAT = "mediaserverchat";
        String JANUS_PEER_PARTED = "januspeerparted";

        /**
         * Janus Handlers Listeners
         */
        String JANUS_ROOM_JOIN_LISTENER = "JANUS_ROOM_JOIN_LISTENER";
        String JANUS_CONFIGURE_LISTENER = "JANUS_CONFIGURE_LISTENER";
        String JANUS_PUBLISHER_JOINED_LISTENER = "JANUS_PUBLISHER_JOINED_LISTENER";
        String JANUS_VIDEO_LISTENER = "JANUS_VIDEO_LISTENER";
        String JANUS_AUDIO_LISTENER = "JANUS_AUDIO_LISTENER";
        String JANUS_SCREEN_LISTENER = "JANUS_SCREEN_LISTENER";
        String JANUS_CHAT_LISTENER = "JANUS_CHAT_LISTENER";
        String JANUS_PEER_PARTED_LISTENER = "JANUS_PEER_PARTED_LISTENER";

        /*
         * Chat
         * */
        String CHAT = "chat";
        String BROADCAST = "broadcast";
    }

    interface RocketChat {
        interface MethodsId {
            String LOGIN_RESUME_ID = "00";
            String GET_ROOMS_ID = "01";
            String GET_SUBSCRIPTIONS_ID = "02";
            String GET_CHAT_HISTORY_ID = "03";
            String SEND_TEXT_MESSAGE_ID = "04";
            String SEND_READ_MESSAGE_ID = "05";
            String CREATE_DIRECT_MESSAGE_ID = "06";
            String SUBSCRIBE_ROOM_MESSAGES_ID = "07";
            String SUBSCRIBE_SPECIFIC_ROOM_MESSAGES_ID = "08";
        }

        interface MethodsName {
            String LOGIN_RESUME_NAME = "login";
            String GET_ROOMS_NAME = "rooms/get";
            String GET_SUBSCRIPTIONS_NAME = "subscriptions/get";
            String GET_CHAT_HISTORY_NAME = "loadHistory";
            String SEND_TEXT_MESSAGE_NAME = "sendMessage";
            String SEND_READ_MESSAGE_NAME = "readMessages";
            String CREATE_DIRECT_MESSAGE_NAME = "createDirectMessage";
            String SUBSCRIBE_ROOM_MESSAGES_NAME = "stream-room-messages";
            String SUBSCRIBE_SPECIFIC_ROOM_MESSAGES_NAME = "stream-room-messages";
        }

        interface MethodsTag {
            String LOGIN_RESUME_TAG = "resumeLogin";
            String GET_ROOMS_TAG = "getRooms";
            String GET_SUBSCRIPTIONS_TAG = "getSubscriptions";
            String GET_CHAT_HISTORY_TAG = "getChatMessages";
            String SEND_TEXT_MESSAGE_TAG = "sendTextMessage";
            String SEND_READ_MESSAGE_TAG = "sendReadMessagesRequest";
            String CREATE_DIRECT_MESSAGE_TAG = "createDirectMessage";
            String SUBSCRIBE_ROOM_MESSAGES_TAG = "subscribeRoomMessages";
            String SUBSCRIBE_SPECIFIC_ROOM_MESSAGES_TAG = "subscribeToSpecificRoomMessages";
        }

    }

}
