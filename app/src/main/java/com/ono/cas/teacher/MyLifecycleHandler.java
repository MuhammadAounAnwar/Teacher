package com.ono.cas.teacher;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    public static boolean isAppVisible = false;
    public static Activity activity;
    private static final String TAG = "MyLifecycleHandler";

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        MyLifecycleHandler.activity = activity;
        isAppVisible = true;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        MyLifecycleHandler.activity = activity;
        isAppVisible = true;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        isAppVisible = true;
        MyLifecycleHandler.activity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        isAppVisible = false;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isAppVisible = false;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        isAppVisible = false;
    }


}
