package com.example.namsan.scanvoca.list.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CursorAdapter;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.list.CheckableLinearLayout;
import com.example.namsan.scanvoca.list.activity.BaseListActivity;

/**
 * Created by namsan on 2016. 7. 21..
 */
public class BaseListAdapter extends CursorAdapter {

    private BaseListActivity mHostActivity;

    public BaseListAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
        mHostActivity = (BaseListActivity) context;
    }

    public void toggleCheckBox() {
        notifyDataSetChanged();
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);

        if(mHostActivity.getEditState()) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
    }
}
