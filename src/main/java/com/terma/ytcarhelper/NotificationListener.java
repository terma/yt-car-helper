package com.terma.ytcarhelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.service.notification.NotificationListenerService;

import androidx.core.app.NotificationManagerCompat;

@TargetApi(VERSION_CODES.LOLLIPOP)
public class NotificationListener extends NotificationListenerService {
}
