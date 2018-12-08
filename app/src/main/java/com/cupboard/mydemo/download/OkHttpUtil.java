package com.cupboard.mydemo.download;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {

    private static final String TAG = "zhoumao";
    private static final String URL = "https://www.baidu.com/";

    //同步
    public static void getDataSync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .proxy(new Proxy(Proxy.Type.HTTP,
                                new InetSocketAddress("10.130.16.111", 8888)))
                        .build();

                Request request = new Request.Builder()
                        .url(URL)
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Log.d(TAG, "getDataSync: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //异步
    public static void getDataAsync() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    Log.d(TAG, "onResponse: " + response.body().string());
                }
            }
        });
    }
}