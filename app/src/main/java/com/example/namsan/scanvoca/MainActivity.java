package com.example.namsan.scanvoca;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;

import com.example.namsan.scanvoca.list.activity.FolderListActivity;
import com.example.namsan.scanvoca.study.StudyActivity;
import com.example.namsan.scanvoca.tab.BrowseFragment;
import com.example.namsan.scanvoca.tab.HomeFragment;
import com.example.namsan.scanvoca.tab.SettingFragment;
import com.example.namsan.scanvoca.tab.StudyFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class MainActivity extends FragmentActivity {
    public final static String APP_NAME = "ScanVoca";
    private final static String TAG = "MainActivity";

    private enum Tabs {
        HOME,
        VOCA,
        STUDY,
        SETTING
    }
    private Tabs mTabState;
    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private BrowseFragment mBrowseFragment;
    private StudyFragment mStudyFragment;
    private SettingFragment mSettingFragment;

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_home:
                    changeTab(Tabs.HOME);
                    break;
                case R.id.btn_voca:
                    changeTab(Tabs.VOCA);
                    break;
                case R.id.btn_study:
                    changeTab(Tabs.STUDY);
                    break;
                case R.id.btn_setting :
                    changeTab(Tabs.SETTING);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load initial db only first time
        loadDBFile();

        /* Tab buttons */
        ImageButton goHome = (ImageButton) findViewById(R.id.btn_home);
        goHome.setOnClickListener(mTabClickListener);
        ImageButton goVocaList = (ImageButton) findViewById(R.id.btn_voca);
        goVocaList.setOnClickListener(mTabClickListener);
        ImageButton goStudy = (ImageButton) findViewById(R.id.btn_study);
        goStudy.setOnClickListener(mTabClickListener);
        ImageButton goSetting = (ImageButton) findViewById(R.id.btn_setting);
        goSetting.setOnClickListener(mTabClickListener);

        /* Ready fragment manager and fragments for tab menu */
        mFragmentManager = getFragmentManager();
        mHomeFragment = new HomeFragment();
        mBrowseFragment = new BrowseFragment();
        mStudyFragment = new StudyFragment();
        mSettingFragment = new SettingFragment();

        /* Add all 4 fragment */
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.fragment, mHomeFragment);
        transaction.add(R.id.fragment, mBrowseFragment);
        transaction.add(R.id.fragment, mStudyFragment);
        transaction.add(R.id.fragment, mSettingFragment);

        /* Initial tab : HomeFragment */
        transaction.show(mHomeFragment);
        transaction.commit();
        mTabState = Tabs.HOME;
    }


    private void loadDBFile() {
        /* load default DB file to proper position */
        String path = new String(APP_NAME);
        File dbPath = getDatabasePath(path);

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

    private void changeTab(Tabs to) {
        // Ignore select current tab
        if(mTabState != to) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            /* Hide current Fragment */
            switch(mTabState) {
                case HOME:
                    transaction.hide(mHomeFragment);
                case VOCA:
                    transaction.hide(mBrowseFragment);
                case STUDY:
                    transaction.hide(mStudyFragment);
                case SETTING:
                    transaction.hide(mSettingFragment);
            }
            /* Show selected Fragment */
            switch(to) {
                case HOME:
                    transaction.show(mHomeFragment);
                case VOCA:
                    transaction.show(mBrowseFragment);
                case STUDY:
                    transaction.show(mStudyFragment);
                case SETTING:
                    transaction.show(mSettingFragment);
            }

            transaction.commit();
            mTabState = to;
        }
    }
}
