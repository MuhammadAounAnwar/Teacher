package com.ono.cas.teacher.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import java.lang.reflect.Method;

public class App extends Application {

    public static final String TAG = "ApplicationTAG";
    public static Context CurrentContext;
    private static App app;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private String username = "";

    public static App getInstance() {
        return app;
    }

    public static Context getContext() {
        if (context == null) {
            context = getContext();
        }
        return context;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = getApplicationContext();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
    }
}
