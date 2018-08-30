package com.example.asifsabir.rideshareapp;

/**
 * Created by asifsabir on 11/9/17.
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    double distance;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

//           int notifyParameter = checkSettingsData();

            if (1==1) {   //check notify settings here

                Log.d("error", "login ok! showing notification");

                //do location distance check
                showNotification(remoteMessage.getData().get("key"), remoteMessage.getData().get("distance"),
                        remoteMessage.getData().get("fare"));
            }
            //else do nothing ! simple

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }
    }


    private void showNotification(String key, String distance,String fare) {
        Intent intent = new Intent(this, ShowDriverRequest.class);

        intent.putExtra("reqKey", key);



        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, createID() /* Request code 0*/, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Ride request")
                .setSmallIcon(R.drawable.distance)
                .setContentText("Distance: " + distance+"Km || " + "Fare: " + fare+" Tk.")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(createID() /* ID of notification 0*/, notificationBuilder.build());


    }

    //creating ID using date stamp
    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    //sending the radius data
    public int checkSettingsData() {
        //viewing saved data in settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean enableNotification = prefs.getBoolean("enable_notification", true);
        String radiusRange = prefs.getString("notification_range", "50");
        if (enableNotification == true) {
            return Integer.valueOf(50);
        } else {
            return 0;
        }
    }
}