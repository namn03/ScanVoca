package com.example.namsan.scanvoca;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;

import com.example.namsan.scanvoca.list.activity.FolderListActivity;

public class MainActivity extends AppCompatActivity {

    /** inner class for on click listener */
    class OnClickListener implements ImageButton.OnClickListener {
        @Override
        public void onClick (View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.btn_go_study :
                    break;
                case R.id.btn_go_folderlist :
                    intent=new Intent(MainActivity.this, FolderListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_go_test :
                    break;
                case R.id.btn_go_setting :
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* set click listeners */
        OnClickListener onClickListener = new OnClickListener();

        ImageButton goStudy = (ImageButton) findViewById(R.id.btn_go_study);
        goStudy.setOnClickListener(onClickListener);
        ImageButton goVocaList = (ImageButton) findViewById(R.id.btn_go_folderlist);
        goVocaList.setOnClickListener(onClickListener);
        ImageButton goTest = (ImageButton) findViewById(R.id.btn_go_test);
        goTest.setOnClickListener(onClickListener);
        ImageButton goSetting = (ImageButton) findViewById(R.id.btn_go_setting);
        goSetting.setOnClickListener(onClickListener);
    }
}
