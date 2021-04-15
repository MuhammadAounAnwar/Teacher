package com.ono.cas.teacher;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int RC_CAMERA_AND_MIC = 1001;

    private static final String[] CAMERA_AND_MIC =
            {android.Manifest.permission.CAMERA,
                    android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.CHANGE_WIFI_STATE,
                    android.Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        methodRequiresCameraAndMicPermissions();
    }


    @AfterPermissionGranted(RC_CAMERA_AND_MIC)
    private void methodRequiresCameraAndMicPermissions() {
        if (EasyPermissions.hasPermissions(this, CAMERA_AND_MIC)) {

            startActivity(new Intent(this, DummyActivity.class));
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.camera_and_mic),
                    RC_CAMERA_AND_MIC, CAMERA_AND_MIC);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}