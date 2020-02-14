package com.tech.playinapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private Button mInit;
    private Button mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        mInit = findViewById(R.id.init);
        mPlay = findViewById(R.id.play);

        mInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    checkAvailable(Constants.ADID);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            checkAvailable(Constants.ADID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkAvailable(String adid) throws IOException {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            URL url = new URL("https://api.playinads.com/webview/available");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            OutputStream os = connection.getOutputStream();
            os.write(new JSONObject().putOpt("ad_id", adid).toString().getBytes());
            os.close();
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                final StringBuilder sb = new StringBuilder();
                String readLine;
                while((readLine = br.readLine()) != null){
                    sb.append(readLine);
                }
                JSONObject result = new JSONObject(sb.toString());
                if(result.optInt("code") == 0) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            if (null != br) {
                br.close();
            }
            if (null != is) {
                is.close();
            }
        }
    }

    // 检查是否有设备可用
    private void checkAvailable1(String adid) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("ad_id", adid);
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, obj.toString());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.HOST)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showMessage(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject resultObj = new JSONObject(response.body().string());
                    Log.e(Constants.TAG, "onFailure: " + resultObj.toString());
                    if (resultObj.optInt("code") == 0) {
                        showPlayView();
                    } else {
                        showMessage(resultObj.optString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 显示Play按钮
    private void showPlayView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPlay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
