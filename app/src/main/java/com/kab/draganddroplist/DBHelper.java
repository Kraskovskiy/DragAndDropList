package com.kab.draganddroplist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final int NUMBER_OF_VERSION_DB = 1;
    private static final String AUTHORITY = "com.kab.draganddroplist";
    private static final String CONTENT_PATH = "dataTable";

    public static final String DATABASE_NAME = "listDB";
    public static final String TABLE_NAME = "dataTable";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NEXT = "next";
    public static final String COLUMN_PREV = "prev";

    public static final Uri MAIN_CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/" + CONTENT_PATH);
    public static final Uri URI_TABLE_NAME = Uri.parse("content://com.kab.draganddroplist/" + TABLE_NAME);

    public static final String DB_CREATE_STRING = "create table "+ TABLE_NAME+ " ("
            + COLUMN_ID +" integer primary key autoincrement,"
            + COLUMN_TEXT +" text,"
            + COLUMN_DATE +" text,"
            + COLUMN_NEXT +" text,"
            + COLUMN_PREV +" text"
            +");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, NUMBER_OF_VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
