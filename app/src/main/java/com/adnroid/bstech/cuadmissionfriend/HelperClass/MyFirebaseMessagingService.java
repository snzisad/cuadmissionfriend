package com.adnroid.bstech.cuadmissionfriend.HelperClass;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.adnroid.bstech.cuadmissionfriend.MainActivity;
import com.adnroid.bstech.cuadmissionfriend.PostView;
import com.adnroid.bstech.cuadmissionfriend.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by snzisad on 9/7/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if(remoteMessage.getNotification() != null){
//            sendNotification(remoteMessage.getNotification().toString());
//            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getClickAction());
//        }

        if(remoteMessage.getData().size() > 0){
            pushNotification(remoteMessage.getData().get("id"), remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }
    }

    private void sendNotification(String title, String body, String click_action) {
        String id="";

        IDCollection.postID = id;
        IDCollection.title = title;

        Intent intent = new Intent(click_action);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(body);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher));
        notificationBuilder.setAutoCancel(true);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

    private void pushNotification(String id, String title, String body) {

        IDCollection.postID = id;
        IDCollection.title = title;

        Intent intent = new Intent(getApplicationContext(), PostView.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText("লিখেছেনঃ "+body);

        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher));
        notificationBuilder.setAutoCancel(true);


        //notification sound
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
        r.play();

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }


}
