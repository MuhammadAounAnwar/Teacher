package com.ono.cas.teacher.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

public class UtilityFunctions {
    public static String getUserNameInitials(String string) {
//        Log.d(TAG, "getUserNameInitials: " + string);
        if (string.matches(".*\\s.*")) {
            String[] splitStrings = string.split("\\s+");
            String name = "";
            if (!splitStrings[0].isEmpty()) {
                name = splitStrings[0].substring(0, 1);
            }
            if (splitStrings.length >= 2) {
                if (!splitStrings[1].isEmpty()) {
                    name += splitStrings[1].substring(0, 1);
                } else {
                    if (!splitStrings[0].isEmpty() && splitStrings[0].length() >= 2) {
                        name = splitStrings[0].substring(0, 2);
                    }
                }
            }
            return name;
        }
        if (string.length() == 1) {
            return string;
        }
        return string.substring(0, 2);
    }


    public static void shareInviteLink(String inviteURL) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Trango call invitation");
        sendIntent.putExtra(Intent.EXTRA_TEXT, inviteURL);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        MyLifecycleHandler.activity.startActivity(shareIntent);
    }

    public static void copyUrlToClipBoard(String invitationLink) {
        ClipboardManager clipboard = (ClipboardManager) MyLifecycleHandler.activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Trango : Invitation Link", invitationLink);
        clipboard.setPrimaryClip(clip);
        shareInviteLink(invitationLink);
    }


    public static String between(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.indexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }

}
