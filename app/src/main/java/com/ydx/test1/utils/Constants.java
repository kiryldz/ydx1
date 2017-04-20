package com.ydx.test1.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import com.ydx.test1.R;

final public class Constants {
    static final public int NUM_WELCOME_PAGES = 3;
    static final public String EMPTY_ICON = "EMPTY_ICON";
    static final public int[] ICON_LIST_SIZE_STANDART = {4,6};
    static final public int[] ICON_LIST_SIZE_LARGE = {5,7};

    static public int MIN(int a, int b) {
        return a > b ? b : a;
    }

    static public int fetchAccentColor(Context ctx) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = ctx.obtainStyledAttributes(typedValue.data
                , new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    static public final int[] welcomeScreenPictures =
            {
                    R.mipmap.ic_launcher
                    ,R.drawable.welcome2
                    ,R.drawable.welcome3
            };
    static public final int[] welcomeScreenText =
            {
                    R.string.welcome_text_hello_to_user
                    ,R.string.welcome_text_hello_to_user_2
                    ,R.string.welcome_text_hello_to_user_3
            };
}
