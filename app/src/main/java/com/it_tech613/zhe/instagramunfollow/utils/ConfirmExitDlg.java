package com.it_tech613.zhe.instagramunfollow.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.it_tech613.zhe.instagramunfollow.R;


public class ConfirmExitDlg extends Dialog {

    Context context;
    DialogNumberListener listener;
    Button btn_ok;
    TextView warning_text,header;
    Button btn_cancel;
    WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    public ConfirmExitDlg(@NonNull Context context, final DialogNumberListener listener, String strheader, String body, boolean is_webview) {
        super(context);
        this.context = context;
        this.listener = listener;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_cancel);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_cancel= (Button)findViewById(R.id.btn_skip);
        warning_text=(TextView)findViewById(R.id.warning_text);
        header=(TextView)findViewById(R.id.header);
        header.setText(strheader);
        webView=findViewById(R.id.webview);
        if (body!=null)warning_text.setText(body);
        if (is_webview) {
            warning_text.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.GONE);
            webView.getSettings().setJavaScriptEnabled(true);
            PreferenceManager.webviewUri="file:///android_asset/html/faq.html";
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
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnYesClick(ConfirmExitDlg.this);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnCancelClick(ConfirmExitDlg.this);
            }
        });
    }
    public interface DialogNumberListener {
        public void OnYesClick(Dialog dialog);
        public void OnCancelClick(Dialog dialog);
    }
}