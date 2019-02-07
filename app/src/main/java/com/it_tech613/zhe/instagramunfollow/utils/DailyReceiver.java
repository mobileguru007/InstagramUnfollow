package com.it_tech613.zhe.instagramunfollow.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.activity.NavigationActivity;

import java.util.Calendar;

public class DailyReceiver extends BroadcastReceiver {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static final int hourOfDay=8;
    private static final int minuteOfHour=0;
    public static final String CUSTOM_INTENT = "com.test.intent.action.DAILYALARM";
    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        NavigationActivity inst = NavigationActivity.instance();
        Log.e("DailyReceiver","received");
        if (!PreferenceManager.getUserName().equals("") && PreferenceManager.getFreeLimit()!=100 && !PreferenceManager.is_credit_noti_showed ) {
//            Toast.makeText(inst,context.getString(R.string.free_credit_charge_alert),Toast.LENGTH_SHORT).show();
            DailyService.enqueueWork(context,intent);
        }
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
//        boolean alarmUp = (PendingIntent.getBroadcast(ctx, 0,
//                new Intent(CUSTOM_INTENT),
//                PendingIntent.FLAG_CANCEL_CURRENT) != null);
//
//        if (alarmUp) {
//            Log.e("DailyReceiver", "Alarm is already active");
//            return;
//        }
        cancelAlarm();
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minuteOfHour);
        long when=calendar.getTimeInMillis();
        /* fire the broadcast */
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, when,AlarmManager.INTERVAL_DAY, getPendingIntent());
        Log.e("DailyReceiver", "Alarm is set successfully");
    }
    private static PendingIntent getPendingIntent() {
        Intent alarmIntent = new Intent(ctx, DailyReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(ctx, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}