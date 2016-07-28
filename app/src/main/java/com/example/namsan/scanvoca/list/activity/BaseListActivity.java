package com.example.namsan.scanvoca.list.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.list.adapter.BaseListAdapter;

/**
 * Created by namsan on 2016. 7. 13..
 */
public abstract class BaseListActivity extends Activity{

    private boolean mEditEnabled;
    private DBManager mDBManager;
    private BaseListAdapter mAdapter;
    private ListView mListView;


    /**
     * Provide proper Cursor which contain necessary data.
     * e.g return getAllWords();
     * */
    abstract Cursor makeData(DBManager dbManager);


    abstract void deleteData(DBManager dbManager, long[] checkedItems);


    abstract BaseListAdapter makeAdapter();

    /**
     * Event for add button click.
     * */
    abstract void onAddClick();

    /**
     * Event for list item click.
     * */
    abstract void onEntryClick(AdapterView<?> parent, View view, int position, long id);


    public boolean getEditState() {
        return mEditEnabled;
    }


    protected DBManager getDBManager() {
        return mDBManager;
    }

    protected ListView getListView() {
        return mListView;
    }


    /**
     *
     * @return new cursor
     */
    protected Cursor loadList() {
        Cursor newCursor = makeData(mDBManager);
        // it contain notifyDataSetChanged
        Cursor oldCursor = mAdapter.swapCursor(newCursor);
        if(mDBManager == null) {
            oldCursor.close();
        }

        return newCursor;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        /* load list view */
        mListView = (ListView) findViewById(R.id.item_list);
        mListView.setOnItemClickListener(new OnItemClickListener());
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        mDBManager = DBManager.open(getApplicationContext());
        mAdapter = makeAdapter();
        mListView.setAdapter(mAdapter);

        /* set dialog for adding folder */
        Button add_or_delete = (Button) findViewById(R.id.btn_add_or_delete);
        Button edit = (Button) findViewById(R.id.btn_edit);
        OnClickListener onClickListener = new OnClickListener();
        add_or_delete.setOnClickListener(onClickListener);
        edit.setOnClickListener(onClickListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.getCursor().close();
    }


    protected void onEditClick() {
        Button add_or_delete = (Button) this.findViewById(R.id.btn_add_or_delete);

        if(mEditEnabled) {
            /* reset checked status of mListView when exit edit state*/
            for(int i = 0; i < mListView.getCount(); i++) {
                mListView.setItemChecked(i, false);
            }

            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            add_or_delete.setText("Add");
        } else {
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            add_or_delete.setText("Delete");
        }

        mEditEnabled = !mEditEnabled;
        // for toggling checkbox visibility
        mAdapter.notifyDataSetChanged();
    }

    protected void onDeleteClick() {
        long[] checkedItems = mListView.getCheckedItemIds();

        // prevent unnecessary query
        if(checkedItems.length != 0) {
            deleteData(mDBManager, checkedItems);
        }

        mEditEnabled = false;
        // reflect both data changing and uncheck, hide check boxes
        loadList();
    }


    private class OnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_add_or_delete:
                    if(mEditEnabled) {
                        onDeleteClick();
                    } else {
                        onAddClick();
                    }
                    break;
                case R.id.btn_edit:
                    onEditClick();
                    break;
            }
        }
    }


    private class OnItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mEditEnabled) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
                checkBox.toggle();
            } else {
                onEntryClick(parent, view, position, id);
            }
        }
    }
}
