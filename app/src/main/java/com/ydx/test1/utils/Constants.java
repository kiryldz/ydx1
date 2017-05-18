package com.ydx.test1.utils;

import com.ydx.test1.R;

final public class Constants {
    static final public int NUM_WELCOME_PAGES = 3;
    static final public String EMPTY_ICON = "EMPTY_ICON";
    static final public int[] ICON_LIST_SIZE_STANDART = {4,6};
    static final public int[] ICON_LIST_SIZE_LARGE = {5,7};

    static public int MIN(Integer a, Integer b) {
        if (a == null && b == null) {
            return 0;
        } else if (a != null && b == null) {
            return a;
        } else if (a == null){
            return b;
        } else {
            return a > b ? b : a;
        }
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
