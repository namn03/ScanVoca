package com.example.namsan.scanvoca.list.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.list.adapter.BaseListAdapter;

/**
 * Created by namsan on 2016. 7. 13..
 */
public abstract class BaseListActivity extends Activity{
    private static final String TAG = "BaseListActivity";

    private boolean mEditEnabled;
    private DBManager mDBManager;
    private BaseListAdapter mAdapter;
    private ListView mListView;

    /*----------------------------------*/
    /* --------- abstrct START ---------*/

    abstract String makeTitle();

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

    /* --------- abstrct END  ----------*/
    /*----------------------------------*/

    public boolean getEditState() {
        return mEditEnabled;
    }

    public ListView getListView() {
        return mListView;
    }

    protected DBManager getDBManager() {
        return mDBManager;
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
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mEditEnabled) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);
                    checkBox.toggle();
                } else {
                    onEntryClick(parent, view, position, id);
                }
            }
        });
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

        /* register cursor to list */
        mDBManager = DBManager.open(getApplicationContext());
        mAdapter = makeAdapter();
        mListView.setAdapter(mAdapter);

        /* Set Title : it should be done after loading DB (it may use DB query) */
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(makeTitle());

        /* set dialog for adding folder */
        Button add_or_delete = (Button) findViewById(R.id.btn_add_or_delete);
        Button edit = (Button) findViewById(R.id.btn_edit);
        add_or_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditEnabled) {
                    onDeleteClick();
                } else {
                    onAddClick();
                }
            }
        });

        edit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClick();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }


    @Override
    public void onBackPressed() {
        if(mEditEnabled) {
            // same effect with exiting edit state
            onEditClick();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.getCursor().close();
    }


    protected void onEditClick() {
        Button add_or_delete = (Button) findViewById(R.id.btn_add_or_delete);

        if(mEditEnabled) {
            resetCheck();
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
        final long[] checkedItems = mListView.getCheckedItemIds();

        // do nothing when no item checked
        if(checkedItems.length != 0) {
            /* Confirm delete with new alert dialog */
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("삭제하시겠습니까?");
            dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteData(mDBManager, checkedItems);

                    Button add_or_delete = (Button) findViewById(R.id.btn_add_or_delete);
                    add_or_delete.setText("Add");

                    mEditEnabled = false;
                    // reflect both data changing and uncheck, hide check boxes
                    loadList();
                }
            });
            dialog.setNegativeButton("취소", null);

            dialog.show();
        }
    }

    /**
     *  reset checked status of mListView when exit edit state
     */
    private void resetCheck() {
        int visibleCount = mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition();
        for(int i = 0; i < visibleCount; i++) {
            // reset list view
            mListView.setItemChecked(i, false);
            CheckBox checkBox = (CheckBox) mListView.getChildAt(i).findViewById(R.id.check_box);
            checkBox.setChecked(false);
        }
    }
}