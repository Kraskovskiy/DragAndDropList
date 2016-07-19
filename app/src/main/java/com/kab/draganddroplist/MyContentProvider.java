package com.kab.draganddroplist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class MyContentProvider  extends ContentProvider {
    private DB mDB;
    private static final UriMatcher uriMatcher;
    private static final String AUTHORITY = "com.kab.draganddroplist";
    private static final String CONTENT_PATH = "dataTable";

    public static final Uri MAIN_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + CONTENT_PATH);
    static final String MAIN_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + CONTENT_PATH;
    static final String MAIN_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + CONTENT_PATH;

    static final int URI_CONTENT = 14285714;
    static final int URI_CONTENT_ID = 14166667;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_CONTENT);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH + "/#", URI_CONTENT_ID);
    }

    @Override
    public boolean onCreate() {
        mDB = new DB(getContext());
        mDB.open();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) != URI_CONTENT) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        Cursor cursor = mDB.getAllData(DBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), MAIN_CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_CONTENT:
                return MAIN_CONTENT_TYPE;
            case URI_CONTENT_ID:
                return MAIN_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_CONTENT) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        long rowID = mDB.insert(DBHelper.TABLE_NAME, values);
        Uri resultUri = ContentUris.withAppendedId(MAIN_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != URI_CONTENT) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        int cnt = mDB.delete(DBHelper.TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != URI_CONTENT) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        int cnt = mDB.update(DBHelper.TABLE_NAME, values, selection, selectionArgs);
        return cnt;
    }

}