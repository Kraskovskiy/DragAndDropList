package com.kab.draganddroplist;

import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * Created by Kraskovskiy on 13.07.2016.
 */
public class CursorObserver extends ContentObserver {
    private Loader<Cursor> mLoader;

    public CursorObserver(Handler handler, Loader<Cursor> loader) {
        super(handler);
        mLoader = loader;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        if (null != mLoader) {
            mLoader.onContentChanged();
        }
        super.onChange(selfChange);
    }
}