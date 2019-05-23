package com.it_tech613.zhe.instagramunfollow.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;


public class FaqFragment extends Fragment {
  public static FaqFragment newInstance() {
    FaqFragment fragment = new FaqFragment();
    return fragment;
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view= inflater.inflate(R.layout.fragment_privacy_policy, container, false);
    initWidget(view);
    return view;
  }
  @SuppressLint("SetJavaScriptEnabled")
  private void initWidget(View view){
    WebView webView = (WebView)view.findViewById(R.id.webview);
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
}
