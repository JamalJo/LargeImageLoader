package com.cupboard.mydemo;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by zhoumao on 2018/12/7.
 * Description:
 */

public class ViewUtils {

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return width;
    }
}
