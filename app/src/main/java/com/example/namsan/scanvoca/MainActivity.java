a
package com.example.namsan.scanvoca;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;

import com.example.namsan.scanvoca.list.activity.FolderListActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public final static String APP_NAME = "ScanVoca";
    private final static String TAG = "MainActivity";

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

        loadDBFile();

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


    private void loadDBFile() {
        /* load default DB file to proper position */
        File dbPath = getDatabasePath(new String(APP_NAME));

        if(dbPath.exists()) {
            return;
        } else {
            try {
                dbPath.getParentFile().mkdirs();
                dbPath.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "fail to create DB file in internal path");
                e.printStackTrace();
            }
        }

        Log.d(TAG, "no DB detected : copying default DB");
        AssetManager assetManager = getAssets();

        InputStream inputStream;
        try {
            inputStream = assetManager.open(APP_NAME + ".db", AssetManager.ACCESS_BUFFER);

            //transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            FileOutputStream outputStream = new FileOutputStream(dbPath.getPath());

            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "fail to load default DB in assets");
            e.printStackTrace();
        }
    }
}
