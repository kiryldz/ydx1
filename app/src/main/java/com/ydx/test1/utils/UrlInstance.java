package com.ydx.test1.utils;


import android.provider.BaseColumns;

public final class UrlInstance {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public UrlReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class UrlEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }
}
