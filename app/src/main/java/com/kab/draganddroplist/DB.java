package com.kab.draganddroplist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class DB {
    private final Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context context) {
        mContext = context;
    }

    public void open() {
        mDBHelper = new DBHelper(mContext);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    public Cursor getAllData() {
        Cursor cursor = mDB.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        cursor.setNotificationUri(mContext.getContentResolver(),DBHelper.URI_TABLE_NAME);
        return cursor;
    }

    public Cursor getAllData(String table, String[] projection, String selection,
                             String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return mDB.query(table, projection, selection, selectionArgs, groupBy, having, sortOrder);
    }

    public void append(String message, String date) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_TEXT, message);
        cv.put(DBHelper.COLUMN_DATE, date);
    }

    public long insert(String table, ContentValues values) {
        long i =  mDB.insert(DBHelper.TABLE_NAME, null, values);
        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
        return  i;
    }

    public void delete(long id) {
        mDB.delete(DBHelper.TABLE_NAME, DBHelper.COLUMN_ID + " = " + id, null);
        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
    }
    public int delete(String table,String whereClause, String[] whereArgs) {
        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
        return mDB.delete(table, whereClause, whereArgs);
    }

    public int update(String table,ContentValues values, String whereClause, String[] whereArgs) {
       return mDB.update(table, values, whereClause, whereArgs);
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + DBHelper.TABLE_NAME;
        Cursor cursor = mDB.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void dbTrunc()
    {
        mDB.execSQL("DROP TABLE " + DBHelper.TABLE_NAME);
        mDB.execSQL(DBHelper.DB_CREATE_STRING);
        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
    }

}


