package com.terma.ytcarhelper;

import static android.media.MediaMetadata.METADATA_KEY_TITLE;
import static androidx.media.MediaBrowserServiceCompat.BrowserRoot.EXTRA_SUGGESTED;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.Template;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class CarScreen extends Screen implements DefaultLifecycleObserver {

    private MediaControllerCompat mController;
    private MediaBrowserCompat mBrowser;

    private String mediaTitle = null;
    private String mediaDescription = null;
    private boolean loaded = false;

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
        final String desc = mediaDescription;

        if (!loaded) {
            final MessageTemplate.Builder builder = new MessageTemplate.Builder("Checking...");
            builder.setTitle("YT Car Helper");
            builder.setLoading(true);
            return builder.build();
        }

        if (desc == null) {
            final MessageTemplate.Builder builder = new MessageTemplate.Builder("Nothing to play");
            builder.setTitle("YT Car Helper");
            return builder.build();
        }

        final MessageTemplate.Builder builder = new MessageTemplate.Builder(
                mediaTitle + " " + mediaDescription);
        builder.setTitle("YT Car Helper");
        builder.setActionStrip(new ActionStrip.Builder().addAction(new androidx.car.app.model.Action.Builder()
                .setTitle("Play")
                .setOnClickListener(() -> {
                    Log.i("Video", "Checking session");
                    if (mController != null) {
                        mController.getTransportControls().play();
                    }
                    Log.i("Video", "Video Playing....");
                })
                .build()).build());
        return builder.build();
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

            final MediaMetadataCompat metadata = mController.getMetadata();
            loaded = true;
            if (metadata != null) {
                mediaTitle = metadata.getString(METADATA_KEY_TITLE);
                mediaDescription = "" + metadata.getDescription().getDescription();
            }
            // trigger template refresh
            invalidate();
        } catch (RemoteException remoteException) {
//            Log.e(TAG, "Failed to create MediaController from session token", remoteException);
//            showToastAndFinish(getString(R.string.media_controller_failed_msg));
        }
    }

}
