package com.it_tech613.zhe.instagramunfollow.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;

public class DailyService extends JobIntentService {
    private NotificationManager alarmNotificationManager;
    private static final String CHANNEL_ID = "InstUnfollow_notification_channel";

    /* Give the Job a Unique Id */
    private static final int JOB_ID = 1000;
    public static void enqueueWork(Context ctx, Intent intent) {
        enqueueWork(ctx, DailyService.class, JOB_ID, intent);
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        Log.e("DailyService","get intent");
        if (PreferenceManager.getUserName().equals("")) return;
        sendNotification(getString(R.string.free_credit_title),getString(R.string.free_credit_body));
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();
        if (!PreferenceManager.getUserName().equals("")) PreferenceManager.restoreFreeCredit();

        stopSelf();
    }

    private void sendNotification(String title,String msg) {
        Log.e("DailyService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NavigationActivity.class), 0);
        NotificationCompat.Builder alamNotificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            alamNotificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID);
        }else {
            alamNotificationBuilder = new NotificationCompat.Builder(this);
        }
        alamNotificationBuilder.setSmallIcon(R.drawable.noti_icon)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX);


        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.e("DailyService", "Notification sent.");
        PreferenceManager.is_credit_noti_showed=true;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            alarmNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}