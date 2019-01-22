package com.it_tech613.zhe.instagramunfollow.utils;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.it_tech613.zhe.instagramunfollow.BuildConfig;


public class CountdownTimerService extends Service {

    private final static String TAG = "CountdownTimerService";

    public static final String COUNTDOWN_BR = BuildConfig.APPLICATION_ID+".countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int endTime=intent.getIntExtra("endTime",0);
        Log.i(TAG, "Starting timer...");
        cdt = new CountDownTimer((endTime*60+1)*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                bi.putExtra("finished", true);
                sendBroadcast(bi);
            }
        };
        cdt.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}