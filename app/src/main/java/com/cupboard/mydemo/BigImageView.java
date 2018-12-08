package com.cupboard.mydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/*
 * Android中优雅的加载大图片和长图片
 * https://mp.weixin.qq.com/s/94tCsHdB5yNlXEkBm1nU-A
 */

public class BigImageView extends View {
    private static final String TAG = "zhoumao";

    private BitmapRegionDecoder mRegionDecoder;
    private int mImageWidth, mImageHeight;
    private Matrix mMatrix;
    private Rect mRect = new Rect();
    private static BitmapFactory.Options sOptions = new BitmapFactory.Options();

    {
        sOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sOptions.inScaled = true;
    }

    public BigImageView(Context context) {
        this(context, null);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrix = new Matrix();
    }

    public void setBitmap(String path) {
        FileInputStream inputStreamRegion = null;
        try {
            inputStreamRegion = new FileInputStream(path);
            setRegion(inputStreamRegion);
            requestLayout();
            invalidate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamRegion != null) {
                    inputStreamRegion.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRegion(InputStream inputStream) {
        try {
            mRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
            mImageHeight = mRegionDecoder.getHeight();
            mImageWidth = mRegionDecoder.getWidth();
            float scaleWidth =ViewUtils.getScreenWidth(getContext())/(float)(mImageWidth);
            mMatrix.postScale(scaleWidth, scaleWidth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int downX = 0;
    int downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int curY = (int) event.getY();

                int moveX = curX - downX;
                int moveY = curY - downY;

                onMove(moveX, moveY);

                Log.d(TAG, " moveX = " + moveX + " curX = " + curX + " downX = " + downX);
                downX = curX;
                downY = curY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void onMove(int moveX, int moveY) {
        if (mImageWidth > getWidth()) {
            mRect.offset(-moveX, 0);
            checkWidth();
            invalidate();
        }

        if (mImageHeight > getHeight()) {
            mRect.offset(0, -moveY);
            checkHeight();
            invalidate();
        }

    }

    private void checkWidth() {
        Rect rect = mRect;
        if (rect.right > mImageWidth) {
            rect.right = mImageWidth;
            rect.left = mImageWidth - getWidth();
        }

        if (rect.left < 0) {
            rect.left = 0;
            rect.right = getWidth();
        }
    }

    private void checkHeight() {
        Rect rect = mRect;
        if (rect.bottom > mImageHeight) {
            rect.bottom = mImageHeight;
            rect.top = mImageHeight - getHeight();
        }

        if (rect.top < 0) {
            rect.top = 0;
            rect.bottom = getWidth();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = width;
        mRect.bottom = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRegionDecoder != null) {
            Bitmap bitmap = mRegionDecoder.decodeRegion(mRect, sOptions);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }
}
