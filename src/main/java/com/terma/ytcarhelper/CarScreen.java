package com.terma.ytcarhelper;

import static androidx.media.MediaBrowserServiceCompat.BrowserRoot.EXTRA_SUGGESTED;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.OnClickListener;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class CarScreen extends Screen implements DefaultLifecycleObserver {

    private MediaControllerCompat mController;
    private MediaBrowserCompat mBrowser;

    private String mediaDescription = "Loading...";

    public CarScreen(@NonNull CarContext carContext) {
        super(carContext);
        getLifecycle().addObserver(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        setupMedia();
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        getLifecycle().removeObserver(this);
    }
    @NonNull
    @Override
    public Template onGetTemplate() {
        Row row = new Row.Builder()
                .setTitle("YT Car Helper")
                .addText(mediaDescription)
                .build();

        return new PaneTemplate.Builder(new Pane.Builder().addRow(row).build())
                .setHeaderAction(androidx.car.app.model.Action.APP_ICON)
                .setActionStrip(new ActionStrip.Builder().addAction(new androidx.car.app.model.Action.Builder()
                        .setTitle("Play")
                        .setOnClickListener(() -> {
                            Log.i("Video", "Checking session");
                            if (mController != null) {
                                mController.getTransportControls().play();
                            }
                            Log.i("Video", "Video Playing....");
                        })
                        .build()).build())
                .build();
    }

    private void setupMedia() {
        Log.d("CarScreen", "setupMedia()");
        final ComponentName cn = new ComponentName("com.google.android.youtube", "com.google.android.apps.youtube.app.extensions.mediabrowser.impl.MainAppMediaBrowserService");

        mBrowser = new MediaBrowserCompat(getCarContext(), cn,
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        setupMediaController();
                    }

                    @Override
                    public void onConnectionSuspended() {
                    }

                    @Override
                    public void onConnectionFailed() {
//                        showToastAndFinish(getString(
//                                R.string.connection_failed_msg, "YT"));
                    }

                }, null);
        mBrowser.connect();

        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_SUGGESTED, true);

//        } else if (mMediaAppDetails.sessionToken != null) {
//            setupMediaController();
//        } else {
//            showToastAndFinish(getString(R.string.connection_failed_msg, mMediaAppDetails.appName));
//        }
    }

    private void setupMediaController() {
        Log.d("CarScreen", "setupMediaController()");
        try {
            MediaSessionCompat.Token token = null;// mMediaAppDetails.sessionToken;
            if (token == null) {
                token = mBrowser.getSessionToken();
            }
            mController = new MediaControllerCompat(this.getCarContext(), token);

            Log.d("CarScreen", "setupMedialController().play");

            mediaDescription = "" + mController.getMetadata().getDescription().getDescription();
            // trigger template refresh
            invalidate();
        } catch (RemoteException remoteException) {
//            Log.e(TAG, "Failed to create MediaController from session token", remoteException);
//            showToastAndFinish(getString(R.string.media_controller_failed_msg));
        }
    }

}
