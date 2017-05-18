package com.ydx.test1.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UrlReaderDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UrlReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " TIMESTAMP";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UrlInstance.UrlEntry.TABLE_NAME + " (" +
                    UrlInstance.UrlEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    UrlInstance.UrlEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
                    UrlInstance.UrlEntry.COLUMN_NAME_TIMESTAMP + INTEGER_TYPE  + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UrlInstance.UrlEntry.TABLE_NAME;

    public UrlReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
