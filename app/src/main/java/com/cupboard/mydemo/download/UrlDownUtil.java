package com.cupboard.mydemo.download;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zhoumao on 2018/12/7.
 * Description:
 */

public class UrlDownUtil {
    private static final String TAG = "zhoumao";

    private static class MyRunable implements Runnable {

        @Override
        public void run() {
            try {
                //下载路径，如果路径无效了，可换成你的下载路径
                String url =
                        "https://alissl.ucdl.pp.uc"
                                + ".cn/fs08/2018/12/05/3/110_b4895c887c02b0c768f4c4f6ddd0d999.apk";
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                final long startTime = System.currentTimeMillis();
                Log.i(TAG, "startTime=" + startTime);
                //下载函数
                String filename = url.substring(url.lastIndexOf("/") + 1);
                //获取文件名
                URL myURL = new URL(url);
                URLConnection conn = myURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                int fileSize = conn.getContentLength();//根据响应获取文件大小
                if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                if (is == null) throw new RuntimeException("stream is null");
                File file1 = new File(path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                //把数据存入路径+文件名
                FileOutputStream fos = new FileOutputStream(path + "/" + filename);
                byte buf[] = new byte[1024];
                int downLoadFileSize = 0;
                do {
                    //循环读取
                    int numread = is.read(buf);
                    if (numread == -1) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                    downLoadFileSize += numread;
                    //更新进度条
                } while (true);

                Log.i(TAG, "download success");
                Log.i(TAG, "totalTime=" + (System.currentTimeMillis() - startTime));

                is.close();
            } catch (Exception ex) {
                Log.e(TAG, "error: " + ex.getMessage(), ex);
            }
        }
    }

    public static void downloadFile() {
        new Thread(new MyRunable()).start();
    }
}
