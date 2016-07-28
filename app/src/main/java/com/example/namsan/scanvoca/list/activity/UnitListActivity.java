package com.example.namsan.scanvoca.list.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.namsan.scanvoca.db.DBManager;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.list.adapter.BaseListAdapter;
import com.example.namsan.scanvoca.list.adapter.UnitListAdapter;

public class UnitListActivity extends BaseListActivity {
    private long mFolderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* get intent from FolderListActivity */
        Intent intent = getIntent();
        mFolderId = intent.getLongExtra("folder_id", -1);

        super.onCreate(savedInstanceState);
    }


    @Override
    Cursor makeData(DBManager dbManager) {
        return dbManager.getAllUnits(mFolderId);
    }


    @Override
    void deleteData(DBManager dbManager, long[] checkedItems) {
        dbManager.deleteUnits(mFolderId, checkedItems);
    }


    @Override
    void onEntryClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(UnitListActivity.this, WordListActivity.class);
        intent.putExtra("folder_id", mFolderId);
        intent.putExtra("unit_id", id);

        startActivity(intent);
    }


    @Override
    BaseListAdapter makeAdapter() {
        return new UnitListAdapter(UnitListActivity.this, null);
    }

    //TODO : protected void onDestroy(); -> cursor close
    @Override
    void onAddClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("단어장 추가");
        dialog.setMessage("추가할 단어장의 이름을 입력하세요");

        final EditText editText = new EditText(this);
        dialog.setView(editText);

        dialog.setPositiveButton("만들기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog_interface, int btn_idx) {
                String input = editText.getText().toString();

                if(input.length() > 0) {
                    getDBManager().createUnit(mFolderId, input);
                }
                loadList();
            }
        });

        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog_interface, int btn_idx) {
            }
        });

        dialog.show();
    }
}
