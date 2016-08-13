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
import android.widget.ListView;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.list.CheckableLinearLayout;
import com.example.namsan.scanvoca.list.activity.BaseListActivity;

import java.util.Arrays;

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

        /*
        *  When entry is clicked, activity toggle the box directly.
        *  However, recheck is necessary because list view recycle the entry layout.
        *  If we don't set again in bindView, checkbox may follow status before recycling!
        *  */
        ListView listView = mHostActivity.getListView();
        long[] checkedItems = listView.getCheckedItemIds();
        long cursorId = cursor.getLong(cursor.getColumnIndex(DBManager.COL_ID));
        boolean checked = Arrays.binarySearch(checkedItems, cursorId) >= 0;

        if(mHostActivity.getEditState()) {
            checkBox.setChecked(checked);
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
    }
}
