package com.kab.draganddroplist;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by Kraskovskiy on 14.07.2016.
 */
public class MyAsyncQueryHandler extends AsyncQueryHandler {
    public MyAsyncQueryHandler(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
    }
}
