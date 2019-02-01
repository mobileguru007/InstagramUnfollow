package com.it_tech613.zhe.instagramunfollow.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;

public class FaqActivity extends AppCompatActivity {
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_faq);
        ImageView back=(ImageView) findViewById(R.id.imageleft);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        webView = (WebView)findViewById(R.id.webview);
        PreferenceManager.webviewUri="file:///android_asset/html/faq.html";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(PreferenceManager.webviewUri);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
