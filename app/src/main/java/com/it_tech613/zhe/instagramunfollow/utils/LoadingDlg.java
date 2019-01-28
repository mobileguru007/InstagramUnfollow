package com.it_tech613.zhe.instagramunfollow.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.it_tech613.zhe.instagramunfollow.R;


public class LoadingDlg extends Dialog {
    AdView adView;
    TextView msg_1,msg_2;
    Context context;
    public LoadingDlg(@NonNull final Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_loading);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        adView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//        adView.loadAd(adRequest);
        msg_1=findViewById(R.id.msg1);
        msg_2=findViewById(R.id.msg2);
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
//            }45r5
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

    public void setMsg_1(String msg_1) {
        this.msg_1.setText(msg_1);
        this.msg_2.setVisibility(View.GONE);
        this.msg_1.setVisibility(View.VISIBLE);
    }

    public void setMsg_2(String msg_2) {
        this.msg_2.setVisibility(View.VISIBLE);
        this.msg_2.setText(msg_2);
    }

}