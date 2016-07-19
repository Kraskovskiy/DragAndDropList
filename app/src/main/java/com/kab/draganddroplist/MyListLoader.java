package com.kab.draganddroplist;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class MyListLoader  extends CursorLoader {
    private DB mDB;

    public MyListLoader(Context context, DB db) {
        super(context);
        this.mDB = db;
    }

    @Override
    public Cursor loadInBackground() {
        return mDB.getAllData();
    }

}
