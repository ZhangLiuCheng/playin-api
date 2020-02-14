package com.tech.playinapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().hide();
        mWebView = findViewById(R.id.webview);
        setupWebView(mWebView);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private void setupWebView(WebView webView) {
        webView.addJavascriptInterface(new PlayInterface(), "playin");
        webView.getSettings().setJavaScriptEnabled(true);
        String url = String.format("https://webview.playinads.com/v1/index.html#/play/%s",
                Constants.ADID);
        webView.loadUrl(url);
    }

    private class PlayInterface {
        @JavascriptInterface
        public void postMessage(String msg) {
            try {
                JSONObject obj = new JSONObject(msg);
                String type = obj.optString("type");
                String body = obj.getString("body");
                if ("playInCloseAction".equals(type)) {

                } else if ("playInInstallAction".equals(type)) {

                } else if ("playInTerminate".equals(type)) {

                } else if ("playInError".equals(type)) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 游戏结束
    private void gameover() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
}
