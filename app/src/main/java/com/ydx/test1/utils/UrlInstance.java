package com.ydx.test1.utils;


import android.provider.BaseColumns;

public final class UrlInstance {
    public UrlInstance() {}

    public static abstract class UrlEntry implements BaseColumns {
        public static final String TABLE_NAME = "URLS";
        public static final String COLUMN_NAME_URL = "URL_NAME";
        public static final String COLUMN_NAME_TIMESTAMP = "URL_TIMESTAMP";
    }
}
