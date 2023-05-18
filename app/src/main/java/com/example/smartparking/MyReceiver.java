package com.example.smartparking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "test");
        builder.setContentTitle("RAKNII");
        builder.setContentText("You Should Pay Now");
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 250, 250, 250};
        builder.setSmallIcon(R.drawable.blue_bg);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}