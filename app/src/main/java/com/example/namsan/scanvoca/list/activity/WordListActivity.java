package com.example.namsan.scanvoca.list.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.db.DBManager;
import com.example.namsan.scanvoca.list.adapter.BaseListAdapter;
import com.example.namsan.scanvoca.list.adapter.WordListAdapter;

public class WordListActivity extends BaseListActivity {

    private long mFolderId;
    private long mUnitId;


    @Override
    String makeTitle() {
        String title = getDBManager().getUnitNameById(mUnitId);
        return title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* get intent from FolderListActivity */
        Intent intent = getIntent();
        mFolderId = intent.getLongExtra("folder_id", -1);
        mUnitId = intent.getLongExtra("unit_id", -1);

        Log.i("WordListActivity",
              "word list : folder id = " + String.valueOf(mFolderId) +
              ", unit id = " + mUnitId);

        super.onCreate(savedInstanceState);
    }


    @Override
    Cursor makeData(DBManager dbManager) {
        return dbManager.getAllWords(mUnitId);
    }


    @Override
    void deleteData(DBManager dbManager, long[] checkedItems) {
        dbManager.deleteWords(mFolderId, mUnitId, checkedItems);
    }


    @Override
    BaseListAdapter makeAdapter() {
        return new WordListAdapter(WordListActivity.this, null);
    }

    @Override
    void onAddClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("단어 추가");
        dialog.setMessage("추가할 단어의 이름을 입력하세요");

        final LinearLayout linearLayout =
                (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_word_dialog, null);
        final TextView wordText = (TextView) linearLayout.findViewById(R.id.text_word);
        final TextView meanText = (TextView) linearLayout.findViewById(R.id.text_mean);
        dialog.setView(linearLayout);

        dialog.setPositiveButton("만들기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog_interface, int btn_idx) {
                String inputWord = wordText.getText().toString();
                String inputMean = meanText.getText().toString();

                if(inputWord.length() > 0 && inputMean.length() > 0) {
                    getDBManager().createWord(mUnitId, inputWord, inputMean);
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

    }
}
