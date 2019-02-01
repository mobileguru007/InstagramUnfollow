package com.it_tech613.zhe.instagramunfollow.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;

public class WeeklyService extends JobIntentService {
    private NotificationManager alarmNotificationManager;
    private static final String CHANNEL_ID = "InstUnfollow_notification_channel";
    /* Give the Job a Unique Id */
    private static final int JOB_ID = 1000;

    public static WindowManager windowManager;
    ViewGroup view;
    Button btn_ok;
    TextView warning_text,header;
    Button btn_cancel;
    static Context context;
    public static WindowManager.LayoutParams params;

    public static void enqueueWork(Context ctx, Intent intent) {
        enqueueWork(ctx, WeeklyService.class, JOB_ID, intent);
        context=ctx;
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {
        Log.e("WeeklyService","get intent");
        if (PreferenceManager.getUserName().equals("")) return;
        sendNotification(getString(R.string.rate_us_title),getString(R.string.rate_us_body));
//        showPopup();
        stopSelf();
    }

    private void showPopup() {
        Log.e("WeeklyService","created");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = (ViewGroup) inflater.inflate(R.layout.dialog_confirm_cancel, null);
        btn_ok = (Button)view.findViewById(R.id.btn_ok);
        btn_cancel= (Button)view.findViewById(R.id.btn_skip);
        warning_text=(TextView)view.findViewById(R.id.warning_text);
        header=(TextView)view.findViewById(R.id.header);
        warning_text.setText(R.string.rateus_body);
        header.setText(R.string.rateus_title);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                stopSelf();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        Touch();
    }

    private Intent getIntent(){
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (getPackageManager().getPackageInfo("com.android.vending", 0) != null) {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow"));
                intent.setPackage("com.android.vending");
            }
        } catch (PackageManager.NameNotFoundException ignored) {}
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_tech613.zhe.instagramunfollow"));
        return intent;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("RtlHardcoded")
    public void Touch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 100;
        params.alpha = 0.75f;
        windowManager.addView(view, params);
    }

    private void sendNotification(String title,String msg) {
        Log.e("WeeklyService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                getIntent(), 0);
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
        Log.e("WeeklyService", "Notification sent.");
        PreferenceManager.is_rateus_noti_showed=true;
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