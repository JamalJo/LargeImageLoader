package com.cupboard.largeimage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhoumao on 2018/12/23.
 * Description:
 */

public class LargeImageView extends View {
    public LargeImageView(Context context) {
        this(context, null);
    }

    public LargeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LargeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
    }
}
