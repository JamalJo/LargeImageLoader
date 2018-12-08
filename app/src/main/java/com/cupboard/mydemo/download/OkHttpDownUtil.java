package com.cupboard.mydemo.download;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by zhoumao on 2018/12/7.
 * Description:
 */

public class OkHttpDownUtil {
    private static final String TAG = "zhoumao";

    public static void downloadFile() {
        //下载路径，如果路径无效了，可换成你的下载路径
        final String url =
                "https://alissl.ucdl.pp.uc"
                        + ".cn/fs08/2018/12/05/3/110_b4895c887c02b0c768f4c4f6ddd0d999.apk";
        final long startTime = System.currentTimeMillis();
        Log.i(TAG, "startTime=" + startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i(TAG, "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath =
                            Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dest = new File(mSDCardPath, url.substring(url.lastIndexOf("/") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i(TAG, "download success");
                    Log.i(TAG, "totalTime=" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "download failed");
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }

                }
            }
        });
    }
}
