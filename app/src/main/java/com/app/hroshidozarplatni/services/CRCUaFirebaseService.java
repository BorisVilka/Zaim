package com.app.hroshidozarplatni.services;
//

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.hroshidozarplatni.R;
import com.app.hroshidozarplatni.screens.CRCUaPropositionsScreen;
import com.app.hroshidozarplatni.utils.Const;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CRCUaFirebaseService extends FirebaseMessagingService {

    private final static String TAG = CRCUaFirebaseService.class.getSimpleName();
    private final static String CHANNEL_ID = "1";
    private final static int PUSH_ID = 76123;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null) {
            if (remoteMessage.getData() != null
                    && remoteMessage.getData().size() > 0
                    && remoteMessage.getData().containsKey("url")) {
                RemoteMessage.Notification notification = remoteMessage.getNotification();

                createNotification(
                        notification != null ? notification.getTitle() : null,
                        notification != null ? notification.getBody() : null,
                        remoteMessage.getData().get("url"));
            } else {
                super.onMessageReceived(remoteMessage);
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FB_token", s);
    }

    private void createNotification(String title, String text, String link) {
        if (TextUtils.isEmpty(text)) {
            text = getString(R.string.notifiction_body);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentText(text)
                        .setPriority(Notification.PRIORITY_MAX);

        if (!TextUtils.isEmpty(title)) {
            notificationBuilder.setContentTitle(title);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String redirectUrl = Const.getRedirectUrl(link);
        String url = !TextUtils.isEmpty(redirectUrl) ? redirectUrl : link;

        Intent resultIntent = new Intent(this, CRCUaPropositionsScreen.class);
        resultIntent.putExtra("url", url);

        PendingIntent pending = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pending);

        // using the same tag and Id causes the new notification to replace an existing one
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(CHANNEL_ID);
            }

            mNotificationManager.notify(PUSH_ID, notificationBuilder.build());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}