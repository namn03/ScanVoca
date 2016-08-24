package com.example.namsan.scanvoca.study;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.namsan.scanvoca.R;
import com.example.namsan.scanvoca.db.DBHelper;
import com.example.namsan.scanvoca.db.DBManager;

public class StudyActivity extends Activity {
    final private String TAG = "StudyActivity";

    private DBManager mDB;
    private Cursor mCursor;
    private TextView mWord;
    private TextView mMean;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        // Test data set
        // TODO : start with indent data
        // TODO : initial text setting optimization
        mDB = DBManager.open(getApplicationContext());
        mCursor = mDB.getAllWords();
        mCursor.moveToFirst();

        Button pass = (Button) findViewById(R.id.btn_pass);
        Button skip = (Button) findViewById(R.id.btn_skip);
        Button fail = (Button) findViewById(R.id.btn_fail);

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

        mWord = (TextView) findViewById(R.id.text_word);
        mMean = (TextView) findViewById(R.id.text_mean);
        mProgressBar = (ProgressBar) findViewById(R.id.memory_bar);

        mWord.setText(DBHelper.getWord(mCursor));
        mMean.setText(DBHelper.getMean(mCursor));
        mProgressBar.setProgress(mDB.getMemory(DBHelper.getId(mCursor)));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
    }

    private void nextWord() {
        long currentId = DBHelper.getId(mCursor);
        studySuccess(currentId);

        mCursor.moveToNext();
        mWord.setText(DBHelper.getWord(mCursor));
        mMean.setText(DBHelper.getMean(mCursor));
        mProgressBar.setProgress(mDB.getMemory(currentId));
    }

    private void previousWord() {
        mCursor.moveToPrevious();
        mWord.setText(DBHelper.getWord(mCursor));
        mMean.setText(DBHelper.getMean(mCursor));
    }


    private void studySuccess(long currentId) {
        mDB.setStrength(currentId, 0.01);
    }

    private void studyFail(long currentId) {

    }
}
