package com.example.namsan.scanvoca.list.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.namsan.scanvoca.db.DBHelper;
import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.R;

public class WordListAdapter extends BaseListAdapter {
    /** TODO : auto requery = true check */
    public WordListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView textName = (TextView) view.findViewById(R.id.text_name);
        final TextView textCount = (TextView) view.findViewById(R.id.text_word);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_memory);

        textName.setText(DBHelper.getWord(cursor));
        textCount.setText(DBHelper.getMean(cursor));
        progressBar.setProgress(DBHelper.getMemory(cursor));

        super.bindView(view, context, cursor);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        return inflater.inflate(R.layout.item_word_entry, parent, false);
    }
}
