package com.example.namsan.scanvoca.list.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.list.adapter.BaseListAdapter;
import com.example.namsan.scanvoca.list.adapter.FolderListAdapter;


public class FolderListActivity extends BaseListActivity {
    @Override
    Cursor makeData(DBManager dbManager) {
        return dbManager.getAllFolders();
    }


    @Override
    void deleteData(DBManager dbManager, long[] checkedItems) {
        dbManager.deleteFolders(checkedItems);
    }


    @Override
    BaseListAdapter makeAdapter() {
        return new FolderListAdapter(FolderListActivity.this, null);
    }


    @Override
    void onAddClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("폴더 추가");
        dialog.setMessage("추가할 폴더 이름을 입력하세요");

        final EditText editText = new EditText(this);
        dialog.setView(editText);

        dialog.setPositiveButton("만들기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog_interface, int btn_idx) {
                String input = editText.getText().toString();

                if(input.length() > 0) {
                    getDBManager().createFolder(input);
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


    @Override
    void onEntryClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(FolderListActivity.this, UnitListActivity.class);
        intent.putExtra("folder_id", id);

        startActivity(intent);
    }
}
