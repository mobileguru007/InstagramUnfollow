package com.it_tech613.zhe.instagramunfollow.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.it_tech613.zhe.instagramunfollow.R;


public class UnfollowingDlg extends Dialog {
    AdView adView;
    TextView unfollow_name,progress,unfollow_status;
    ProgressBar progressBar;
    Context context;
    public UnfollowingDlg(@NonNull final Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_unfollowing);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);
        progressBar=findViewById(R.id.progress_bar);
        unfollow_name=findViewById(R.id.unfollow_name);
        unfollow_status=findViewById(R.id.unfollow_status);
        progress=findViewById(R.id.progress);
//        adView.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
////                Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
////                Toast.makeText(context, "onAdOpened()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
////                Toast.makeText(context, "onAdClosed()", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                Toast.makeText(context, "onAdFailedToLoad()"+i, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
////                Toast.makeText(context, "onAdLeftApplication()", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @SuppressLint("SetTextI18n")
    public void setProgress(String name, int unfollowed_number){
        unfollow_name.setText(String.format(context.getResources().getString(R.string.unfollow_name),name));
        int progress_status=(int) unfollowed_number*100/50;
        progressBar.setProgress(unfollowed_number);
        progress.setText(progress_status+"%");
        unfollow_status.setText(String.format(context.getResources().getString(R.string.d_50),unfollowed_number));
    }
}