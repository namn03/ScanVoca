package com.example.namsan.scanvoca.list.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.R;

public class UnitListAdapter extends BaseListAdapter {
    /** TODO : auto requery = true check */
    public UnitListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView textName = (TextView) view.findViewById(R.id.text_name);
        final TextView textCount = (TextView) view.findViewById(R.id.text_count);

        Log.d("debugg", cursor.getString(cursor.getColumnIndex(DBManager.COL_NAME)));
        textName.setText(cursor.getString(cursor.getColumnIndex(DBManager.COL_NAME)));
        textCount.setText(String.valueOf(cursor.getLong(cursor.getColumnIndex(DBManager.COL_COUNT))));

        super.bindView(view, context, cursor);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_unit_entry, parent, false);

        return v;
    }
}
