package com.kab.draganddroplist;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private LinkedList<MyItem> mItems;
    private LinkedList<MyItem> mItemsSorted;
    private RecyclerView mRecyclerView;
    private DB mDB;
    private MyAsyncQueryHandler myAsyncQueryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mItems = new LinkedList<>();
        mItemsSorted = new LinkedList<>();

        myAsyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 addItem();
            }
        });

        dbConnection();
        createRecyclerView();
    }

    public void addItem() {
        MyItem m = new MyItem((mRecyclerView.getAdapter().getItemCount()), "Item " + (mRecyclerView.getAdapter().getItemCount()),
                (mRecyclerView.getAdapter().getItemCount() + 1), mRecyclerView.getAdapter().getItemCount());

        ContentValues cvAdd = new ContentValues();
        cvAdd.put(DBHelper.COLUMN_ID, m.getId());
        cvAdd.put(DBHelper.COLUMN_TEXT, m.getText());
        cvAdd.put(DBHelper.COLUMN_DATE, m.getDate());
        cvAdd.putNull(DBHelper.COLUMN_NEXT);
        cvAdd.put(DBHelper.COLUMN_PREV, getPrev());

        ContentValues cvLinks = new ContentValues();
        cvLinks.put(DBHelper.COLUMN_NEXT, getNext());

        myAsyncQueryHandler.startInsert(1, null, DBHelper.MAIN_CONTENT_URI, cvAdd);
        myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvLinks, updatePrev(), null);

        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
        mRecyclerView.getAdapter().notifyDataSetChanged();

    }


    public String getPrev() {
        if (mRecyclerView.getAdapter().getItemCount() > 0) {
            return mItems.getLast().getId() + "";
        } else {
            return null;
        }
    }

    public String getNext() {
        if (mRecyclerView.getAdapter().getItemCount() < 1) {
            return null;
        } else {
            return (mRecyclerView.getAdapter().getItemCount()) + "";
        }
    }

    public String updatePrev() {
        if (mRecyclerView.getAdapter().getItemCount() > 0) {
            return "_id = " + mItems.getLast().getId();
        } else {
            return null;
        }
    }

    public void createRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Setup D&D feature and RecyclerView
        RecyclerViewDragDropManager mDragMgr = new RecyclerViewDragDropManager();

        mDragMgr.setInitiateOnMove(false);
        mDragMgr.setInitiateOnLongPress(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(mDragMgr.createWrappedAdapter(new MyRecyclerViewAdapter(mItems,getApplicationContext())));

        mDragMgr.attachRecyclerView(mRecyclerView);
    }

    public void dbConnection() {
        mDB = new DB(this);
        mDB.open();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionBarItem1:
                clearAllList();
                Toast.makeText(this, R.string.textClearAll, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    public void clearAllList() {
        mDB.dbTrunc();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        getLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return new MyListLoader(this, mDB);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        readCursor(cursor);
        CursorObserver mObserver = new CursorObserver(new Handler(), loader);
        cursor.registerContentObserver(mObserver);
        cursor.setNotificationUri(getContentResolver(), DBHelper.URI_TABLE_NAME);
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    public void readCursor(Cursor cursor){
        mItems.clear();
        Log.e("readCursor", "___________" );
        if (cursor.moveToFirst()){
            do{
                mItems.add(new MyItem(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TEXT)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEXT)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PREV))
                ));

                Log.e("readCursor", " "+cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID))+
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TEXT))+
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE))+" "+
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NEXT))+" "+
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PREV))
                );
            }while(cursor.moveToNext());
        }

        Log.e("readCursor", "___________" );
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
        sortedList();
    }

    public void sortedList() {
        mItemsSorted.clear();
        Boolean isLastElement = false;
        int next = 0;

        if (mItems.size() > 1) {
            for (MyItem m : mItems) {
                if (m.getPrev().equals("null")) {
                    mItemsSorted.add(m);
                    if (!m.getNext().equals("null")) {
                        next = Integer.decode(m.getNext());
                    } else {
                        isLastElement = true;
                    }
                }
            }

            if (!isLastElement) {
                for (int i = 0; i < mItems.size(); i++) {
                    mItemsSorted.add(mItems.get(next));
                    try {
                        next = Integer.decode(mItems.get(next).getNext());
                    } catch (NumberFormatException e) {
                        break;
                    }
                }
            }
            mItems.clear();
            mItems.addAll(mItemsSorted);
        }
    }


}
