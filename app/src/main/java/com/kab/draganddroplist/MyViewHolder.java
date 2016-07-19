package com.kab.draganddroplist;

import android.view.View;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class MyViewHolder extends AbstractDraggableItemViewHolder {
    TextView textView;
    TextView textViewDate;

    public MyViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
        textViewDate = (TextView) itemView.findViewById(android.R.id.text2);
    }
}
