package com.launchkey.android.authenticator.demo.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.launchkey.android.authenticator.demo.R;
import com.launchkey.android.authenticator.demo.ui.activity.ListDemoActivity;

public class Notifier {

    private static final int NOTIFICATION_ID = 100;

    @SuppressLint("StaticFieldLeak")
    private static volatile Notifier sInstance;

    public static Notifier getInstance(Context context) {

        if (sInstance == null) {
            synchronized (Notifier.class) {
                if (sInstance == null) {
                    sInstance = new Notifier(context.getApplicationContext());
                }
            }
        }

        return sInstance;
    }

    private Context mAppContext;
    private NotificationManager mNotifications;

    private Notifier(Context appContext) {

        mAppContext = appContext;
        mNotifications = (NotificationManager) appContext.
                getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notifyOfRequest(RemoteMessage.Notification optionalPushNotification) {

        Intent tapIntent = new Intent(mAppContext, ListDemoActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        tapIntent.putExtra(ListDemoActivity.EXTRA_SHOW_REQUEST, true);

        PendingIntent tapPendingIntent = PendingIntent.getActivity(
                mAppContext, 1, tapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = createAndGetNotificationChannel();

        // Check if a push notification has defined a notification and use its title and body instead
        String title = null, body = null;

        if (optionalPushNotification != null) {

            title = optionalPushNotification.getTitle();
            body = optionalPushNotification.getBody();
        }

        if (title == null) {

            title = mAppContext.getString(R.string.app_name);
        }

        if (body == null) {

            body = mAppContext.getString(R.string.demo_notification_message);
        }

        Notification notification = new NotificationCompat.Builder(mAppContext, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setColor(ContextCompat.getColor(mAppContext, R.color.demo_primary))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(tapPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        mNotifications.notify(NOTIFICATION_ID, notification);
    }

    private String createAndGetNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            final String id = mAppContext.getString(R.string.demo_notification_channel_authrequests_id);
            final CharSequence name = mAppContext.getString(R.string.demo_notification_channel_authrequests_name);
            final String desc = mAppContext.getString(R.string.demo_notification_channel_authrequests_desc);

            final NotificationChannel channel =
                    new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(desc);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);

            mNotifications.createNotificationChannel(channel);

            return id;
        }

        return "none";
    }

    public void cancelRequestNotification() {

        if (mNotifications != null) {
            mNotifications.cancel(NOTIFICATION_ID);
        }
    }
}
