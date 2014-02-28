package com.comfymobile.saadat.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Nixy on 28.02.14.
 */
public class MyListView extends ListView {


    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch(IllegalArgumentException iae) {
            // Workaround for http://code.google.com/p/android/issues/detail?id=22751
        }
    }
}