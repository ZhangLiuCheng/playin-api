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
//        String url = String.format("https://webview.playinads.com/w/index.html?a=%s&i=1", Constants.ADID);
        String url = String.format("https://webview.playinads.com/v1/index.html#/play/%s", Constants.ADID);

        webView.loadUrl(url);
    }

    private class PlayInterface {

        // 监听js回调事件
        @JavascriptInterface
        public void postMessage(String msg) {
            Log.e("TAG", msg);
            try {
                JSONObject obj = new JSONObject(msg);
                // type 和 body 具体含义见文档
                String type = obj.optString("type");
                String body = obj.getString("body");

                Log.e("TAG", "----------  " + type + "  ====   " + ("playInCloseAction".equals(type)));


                if ("playInCloseAction".equals(type)) {
                    Log.e("TAG", "----------  22222");

                    gameover();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 游戏结束
    private void gameover() {
        Log.e("TAG", "----------  gameover");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
}
