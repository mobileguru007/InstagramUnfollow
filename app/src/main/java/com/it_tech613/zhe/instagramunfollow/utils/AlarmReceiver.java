package com.it_tech613.zhe.instagramunfollow.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static final int hourOfDay=8;
    private static final int minuteOfHour=0;
    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";
    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        NavigationActivity inst = NavigationActivity.instance();
        Toast.makeText(inst,"Your Free Credit recovered as 100! Unfollow more people.",Toast.LENGTH_SHORT).show();
        AlarmService.enqueueWork(context,intent);
        Log.e("Alarmreceiver","received");
    }

    public static void cancelAlarm() {
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        /* cancel any pending alarm */
        alarm.cancel(getPendingIntent());
    }

    public static void setCtx(Context context){
        ctx=context;
    }
    public static void setAlarm(boolean force) {
        boolean alarmUp = (PendingIntent.getBroadcast(ctx, 0,
                new Intent(CUSTOM_INTENT),
                PendingIntent.FLAG_CANCEL_CURRENT) != null);

        if (alarmUp) {
            Log.e("AlarmReceiver", "Alarm is already active");
            return;
        }
        cancelAlarm();
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        // EVERY X MINUTES
//        long delay = (1000 * 60 * 60 * 24);
//        long when = System.currentTimeMillis();
//        if (!force) {
//            when += delay;
//        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minuteOfHour);
        long when=calendar.getTimeInMillis();
        /* fire the broadcast */
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT < Build.VERSION_CODES.KITKAT)
            alarm.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent());
        else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M)
            alarm.setExact(AlarmManager.RTC_WAKEUP, when, getPendingIntent());
        else if (SDK_INT >= Build.VERSION_CODES.M)
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, getPendingIntent());

//        alarm.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis() , getPendingIntent());
    }
    private static PendingIntent getPendingIntent() {
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(ctx, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}