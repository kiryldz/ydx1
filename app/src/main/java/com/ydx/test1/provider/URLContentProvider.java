package com.ydx.test1.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ydx.test1.utils.UrlInstance;
import com.ydx.test1.utils.UrlReaderDbHelper;

public class URLContentProvider extends ContentProvider {
    // // Uri
    // authority
    static final String AUTHORITY = "com.ydx.test1.urls";

    // path
    static final String URL_PATH = "URLS";

    // Общий Uri
    public static final Uri URL_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + URL_PATH + "/common");

    // Типы данных
    // набор строк
    static final String URL_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + URL_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_URLS = 1;
    // для доступа ко всему списку Uri
    static final int URI_URLS_GET_ALL = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, URL_PATH+ "/common", URI_URLS);
        uriMatcher.addURI(AUTHORITY, URL_PATH + "/read_all", URI_URLS_GET_ALL);
    }

    UrlReaderDbHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        dbHelper = new UrlReaderDbHelper(getContext());
        return true;
    }

    // чтение
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_URLS: // общий Uri
                // если сортировка не указана, ставим свою - по времени desc
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = UrlInstance.UrlEntry.COLUMN_NAME_TIMESTAMP + " DESC";
                }
                break;
            case URI_URLS_GET_ALL: // общий Uri
                // если сортировка не указана, ставим свою - по времени desc
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = UrlInstance.UrlEntry.COLUMN_NAME_TIMESTAMP + " DESC";
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(UrlInstance.UrlEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        // просим ContentResolver уведомлять этот курсор
        // об изменениях данных в URL_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(), URL_CONTENT_URI);
        return cursor;
    }

    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_URLS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(UrlInstance.UrlEntry.TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(URL_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_URLS:
                return URL_CONTENT_TYPE;
            case URI_URLS_GET_ALL:
                return URL_CONTENT_TYPE;
        }
        return null;
    }
}
