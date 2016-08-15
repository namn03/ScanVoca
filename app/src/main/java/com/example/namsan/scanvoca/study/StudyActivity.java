package com.example.namsan.scanvoca.study;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.db.DBManager;

public class StudyActivity extends Activity {
    Cursor mCursor;
    TextView mWord;
    TextView mMean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        // Test data set
        // TODO : start with indent data
        DBManager db = DBManager.open(getApplicationContext());
        mCursor = db.getAllWords();
        mCursor.moveToFirst();

        Button pass = (Button) findViewById(R.id.btn_pass);
        Button skip = (Button) findViewById(R.id.btn_skip);
        Button fail = (Button) findViewById(R.id.btn_fail);

        mWord = (TextView) findViewById(R.id.text_word);
        mMean = (TextView) findViewById(R.id.text_mean);
        mWord.setText(mCursor.getString(mCursor.getColumnIndex(DBManager.COL_WORD)));
        mMean.setText(mCursor.getString(mCursor.getColumnIndex(DBManager.COL_MEAN)));

        pass.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });

        skip.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });

        fail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });
    }

    private void nextWord() {
        mCursor.moveToNext();
        mWord.setText(mCursor.getString(mCursor.getColumnIndex(DBManager.COL_WORD)));
        mMean.setText(mCursor.getString(mCursor.getColumnIndex(DBManager.COL_MEAN)));
    }

    private void previousWord() {
        mCursor.moveToPrevious();
        mWord.setText(mCursor.getString(mCursor.getColumnIndex(DBManager.COL_WORD)));
        mMean.setText(mCursor.getString(mCursor.getColumnIndex(DBManager.COL_MEAN)));
    }
}
