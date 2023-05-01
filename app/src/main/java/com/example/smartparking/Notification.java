package com.example.smartparking;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.core.Tag;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Notification extends BroadcastReceiver {

    final Integer notificationId=1;
    final String channelId="channel1";
    final String titleExtra="titleExtra";
    final String messagingExtra="messageExtra";
    @Override
    public void onReceive(Context context, Intent intent) {
        android.app.Notification notification= new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.backg)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContent(getRemoteView(intent.getStringExtra(titleExtra),intent.getStringExtra(messagingExtra)))
                .setContentText(intent.getStringExtra(messagingExtra))
                .build();


        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(notificationId,notification);


    }


//   final static String channel_id = "NOTIFICATION_CHANNEL";
//
//    @Override
//    public void onNewToken(@NonNull String token)
//    {
//        Log.d("Taghh", "Refreshed token: " + token);
//    }
//    // Override onMessageReceived() method to extract the
//    // title and
//    // body from the message passed in FCM
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage message) {
//
//        if(message!=null){
//            generateNotification(message.getNotification().getTitle(),message.getNotification().getBody());
//        }
//
//    }
//
//    public void generateNotification(String title, String message) {
//
//        Intent intent=new Intent(this,CounterBookingActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(),channel_id)
//                .setSmallIcon(R.drawable.backg)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
//                .setVibrate(new long[] { 1000, 1000, 1000,
//                        1000, 1000 })
//                .setContentIntent(pendingIntent);
//
//        builder=builder.setContent(getRemoteView(title,message));
//
//        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel =new NotificationChannel(channel_id,"WEB_APP",NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(notificationChannel);
//
//        }
//
//        notificationManager.notify(0,builder.build());
//    }
//
    @SuppressLint("RemoteViewLayout")
    public RemoteViews getRemoteView(String title, String message){
        RemoteViews remoteViews= new RemoteViews("com.example.smartparking",R.layout.activity_counter_booking);
        return remoteViews;
    }



}
