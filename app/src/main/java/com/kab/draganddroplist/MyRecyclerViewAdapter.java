package com.kab.draganddroplist;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;

import java.util.LinkedList;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> implements DraggableItemAdapter<MyViewHolder> {
    LinkedList<MyItem> mItems;
    private MyAsyncQueryHandler myAsyncQueryHandler;
    private Context mContext;

    public MyRecyclerViewAdapter(LinkedList<MyItem> items,Context context) {
        setHasStableIds(true); // this is required for D&D feature.
        mItems = items;
        myAsyncQueryHandler = new MyAsyncQueryHandler(context.getContentResolver());
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId(); // need to return stable (= not change even after reordered) value
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyItem item = mItems.get(position);
        holder.textView.setText(item.getText());
        holder.textViewDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        swapStroke(fromPosition,toPosition);
        MyItem movedItem = mItems.remove(fromPosition);
        mItems.add(toPosition, movedItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void swapStroke(int fromPosition, int toPosition) {
        ContentValues cvFrom = new ContentValues();
        if (fromPosition > toPosition) {
            cvFrom.put(DBHelper.COLUMN_NEXT, mItems.get(toPosition).getId());
            cvFrom.put(DBHelper.COLUMN_PREV, mItems.get(toPosition).getPrev());
        } else {
            cvFrom.put(DBHelper.COLUMN_NEXT, mItems.get(toPosition).getNext());
            cvFrom.put(DBHelper.COLUMN_PREV, mItems.get(toPosition).getId());
        }

        myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvFrom, "_id = " + mItems.get(fromPosition).getId(), null);

        ContentValues cvTo = new ContentValues();
        if (fromPosition > toPosition) {
            cvTo.put(DBHelper.COLUMN_PREV, mItems.get(fromPosition).getId());
        } else {
            cvTo.put(DBHelper.COLUMN_NEXT, mItems.get(fromPosition).getId());
        }

        myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvTo, "_id = " + mItems.get(toPosition).getId(), null);
        updateLinkedElements(fromPosition, toPosition);
    }

    public void updateLinkedElements(int fromPosition, int toPosition) {
        ContentValues cvPrev = new ContentValues();
        ContentValues cvNext = new ContentValues();

        if (mItems.getLast().getId() != mItems.get(fromPosition).getId() && mItems.getFirst().getId() != mItems.get(fromPosition).getId()) {
            cvNext.put(DBHelper.COLUMN_NEXT, mItems.get(fromPosition).getNext());
            cvPrev.put(DBHelper.COLUMN_PREV, mItems.get(fromPosition).getPrev());
            myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvPrev, "_id = " + mItems.get(fromPosition + 1).getId(), null);
            myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvNext, "_id = " + mItems.get(fromPosition - 1).getId(), null);
        } else {
            if (mItems.getLast().getId() == mItems.get(fromPosition).getId()) {
                cvNext.putNull(DBHelper.COLUMN_NEXT);
                myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvNext, "_id = " + mItems.get(fromPosition - 1).getId(), null);
            } else {
                cvPrev.putNull(DBHelper.COLUMN_PREV);
                myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvPrev, "_id = " + mItems.get(fromPosition + 1).getId(), null);
            }
        }

       cvPrev = new ContentValues();
       cvNext = new ContentValues();

        if (mItems.getLast().getId() != mItems.get(toPosition).getId() && mItems.getFirst().getId() != mItems.get(toPosition).getId()) {
            if (fromPosition > toPosition) {
                cvNext.put(DBHelper.COLUMN_NEXT, mItems.get(fromPosition).getId());
                myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvNext, "_id = " + mItems.get(toPosition - 1).getId(), null);
            } else {
                cvPrev.put(DBHelper.COLUMN_PREV, mItems.get(fromPosition).getId());
                myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvPrev, "_id = " + mItems.get(toPosition + 1).getId(), null);
            }
        } else {
            if (mItems.getLast().getId() == mItems.get(toPosition).getId()) {
                if (fromPosition > toPosition) {
                    cvNext.putNull(DBHelper.COLUMN_NEXT);
                    myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvNext, "_id = " + mItems.get(toPosition).getId(), null);
                } else {
                    cvNext.put(DBHelper.COLUMN_NEXT, mItems.get(fromPosition).getId());
                    myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvNext, "_id = " + mItems.get(toPosition).getId(), null);
                }
            }
            if (mItems.size() > 2) {
                if (mItems.getFirst().getId() == mItems.get(toPosition).getId()) {
                    cvPrev.put(DBHelper.COLUMN_PREV, mItems.get(fromPosition).getId());
                    myAsyncQueryHandler.startUpdate(1, null, DBHelper.MAIN_CONTENT_URI, cvPrev, "_id = " + mItems.get(toPosition).getId(), null);
                }
            }
        }

        mContext.getContentResolver().notifyChange(DBHelper.URI_TABLE_NAME, null);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }
}