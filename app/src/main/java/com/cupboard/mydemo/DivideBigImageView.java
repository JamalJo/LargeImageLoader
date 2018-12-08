package com.cupboard.mydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoumao on 2018/12/7.
 * Description:
 */

public class DivideBigImageView extends android.support.v7.widget.AppCompatImageView {
    public DivideBigImageView(Context context) {
        this(context, null);
    }

    public DivideBigImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DivideBigImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBitmap(String path) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            setBitmapToImg(BitmapFactory.decodeStream(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String TAG = "zhoumao";


    private void setBitmapToImg(Bitmap resource) {
        Rect mRect = new Rect();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resource.compress(Bitmap.CompressFormat.PNG, 100, baos);

            InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

            //BitmapRegionDecoder newInstance(InputStream is, boolean isShareable)
            //用于创建BitmapRegionDecoder，isBm表示输入流，只有jpeg和png图片才支持这种方式，
            // isShareable如果为true，那BitmapRegionDecoder会对输入流保持一个表面的引用，
            // 如果为false，那么它将会创建一个输入流的复制，并且一直使用它。即使为true，程序也有可能会创建一个输入流的深度复制。
            // 如果图片是逐步解码的，那么为true会降低图片的解码速度。如果路径下的图片不是支持的格式，那就会抛出异常
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(isBm, true);
            float scaleWidth = ViewUtils.getScreenWidth(getContext()) / (float) decoder.getWidth();

            final int imgWidth = ViewUtils.getScreenWidth(getContext());
            final int imgHeight = decoder.getHeight();

            Matrix matrix = new Matrix();
            Log.d(TAG, "setBitmapToImg: " + scaleWidth);
            matrix.postScale(scaleWidth, scaleWidth);

            BitmapFactory.Options opts = new BitmapFactory.Options();

            //计算图片要被切分成几个整块，
            // 如果sum=0 说明图片的长度不足3000px，不进行切分 直接添加
            // 如果sum>0 先添加整图，再添加多余的部分，否则多余的部分不足3000时底部会有空白
            int sum = imgHeight / 3000;

            int redundant = imgHeight % 3000;

            List<Bitmap> bitmapList = new ArrayList<>();

            //说明图片的长度 < 3000
            if (sum == 0) {
                //直接加载
                bitmapList.add(resource);
            } else {
                //说明需要切分图片
                for (int i = 0; i < sum; i++) {
                    //需要注意：mRect.set(left, top, right, bottom)的第四个参数，
                    //也就是图片的高不能大于这里的4096
                    mRect.set(0, i * 3000, imgWidth, (i + 1) * 3000);
                    Bitmap bm = decoder.decodeRegion(mRect, opts);
                    bitmapList.add(bm);
                }

                //将多余的不足3000的部分作为尾部拼接
                if (redundant > 0) {
                    mRect.set(0, sum * 3000, imgWidth, imgHeight);
                    Bitmap bm = decoder.decodeRegion(mRect, opts);
                    bitmapList.add(bm);
                }

            }

            Bitmap bigbitmap = Bitmap.createBitmap(ViewUtils.getScreenWidth(getContext()),
                    (int)(imgHeight * scaleWidth), Bitmap.Config.ARGB_8888);
            Canvas bigcanvas = new Canvas(bigbitmap);
            bigcanvas.setMatrix(matrix);

            Paint paint = new Paint();
            int iHeight = 0;

            //将之前的bitmap取出来拼接成一个bitmap
            for (int i = 0; i < bitmapList.size(); i++) {
                Bitmap bmp = bitmapList.get(i);
                bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
                iHeight += bmp.getHeight();

                bmp.recycle();
                bmp = null;
            }

            this.setImageBitmap(bigbitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
