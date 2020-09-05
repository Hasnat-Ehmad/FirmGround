package com.firmground.evs.firmground.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firmground.evs.firmground.R;
import com.firmground.evs.firmground.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;



/**
 * Created by HP on 26/6/2018.
 * Hasnat Ahmad
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private  static  final String TAG = "MyFirebaseMsgService";
    public static NotificationCompat.Builder mBuilder ; public static NotificationManager notifManager;

    String id,online_status;
    String order_id;

    int mId = 0;

    String message ;   AudioAttributes attributes = null;
    Intent i;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG,"From: "+remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();
        String myCustomKey = data.get("my_key");

        Log.d(TAG,"data = "+data.toString());

        Log.d(TAG,"my_key = "+myCustomKey);

        Log.d(TAG,"Title: "+remoteMessage.getNotification().getTitle());
        Log.d(TAG,"Body: "+remoteMessage.getNotification().getBody());

        //check if the message contains data
        if(remoteMessage.getData().size()>0){
            Log.d(TAG,"Message Data: "+remoteMessage.getData().get("id"));
        }
        Log.d(TAG,"Message Data: "+remoteMessage.getData().get("id"));

        //check message contains notification
        if(remoteMessage.getNotification() != null){
            Log.d(TAG,"Message Body: "+remoteMessage.getNotification());
//                syncData_admin();

            sendNotification("FirmGround");

            Log.d(TAG,"Message title: "+remoteMessage.getTtl());
        }
    }

    public void sendNotification(String body) {

        notifManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/raw/siren_noise_kevangc_1337458893");

        //notifManager.cancelAll();

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        mBuilder = new NotificationCompat.Builder(this,channelId);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

            mChannel.setLightColor(Color.BLUE);

            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            mChannel.setDescription("My Channel");
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setSound(uri, attributes); // This is IMPORTANT

            notifManager.createNotificationChannel(mChannel);
        }

        //Create the intent that’ll fire when the user taps the notification//
        //String value = "1";
        Intent resultIntent = new Intent(this, MainActivity.class);

        resultIntent.putExtra("value",true);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); error with this line set flag with single top

        //resultIntent.setAction(Intent.ACTION_MAIN);
        //resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        //PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,0);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.androidauthority.com/"));
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //mBuilder.setContentIntent(pendingIntent);


        //mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setSound(uri);//this is for sound;

        mBuilder.setVibrate(new long[] { 400, 400}); //this for vibration

        //mBuilder.setPriority(Notification.PRIORITY_MAX);

        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        mBuilder.setAutoCancel(true);

        mBuilder.setSmallIcon(R.drawable.football);
        mBuilder.setContentTitle("New incoming order");
        mBuilder.setContentText ("New Incoming Job Order No "+body);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

}
