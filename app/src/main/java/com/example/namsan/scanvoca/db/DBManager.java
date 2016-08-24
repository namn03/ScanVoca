package com.example.namsan.scanvoca.db;

import android.content.Context;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.example.namsan.scanvoca.MainActivity;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLInput;
import java.util.Arrays;
import java.util.Calendar;


public class DBManager extends SQLiteOpenHelper{
    private static final String TAG = "DBManager";
    public static final int VERSION = 1;

    private static DBManager mInstance = null;
    private static SQLiteDatabase mDB;


    /**
     * Private constructor for singleton
     * */
    private DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    private DBManager(Context context) {
        super(context, MainActivity.APP_NAME, null, VERSION);
    }

    /*---------------------------------------------------*/
    /*------- implementing SQLiteOpenHelper START -------*/
    /** create new table if db is not exist */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        Log.i(TAG, "create DB tables");

        db.execSQL("CREATE TABLE " + FOLDER_TABLE +
                "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_COUNT + " INTEGER);");

        db.execSQL("CREATE TABLE " + UNIT_TABLE +
                   "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_FOLDER + " INTEGER, " +
                    COL_COUNT + " INTEGER);");

        db.execSQL("CREATE TABLE " + WORD_TABLE +
                "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FOLDER + " INTEGER, " +
                COL_UNIT + " INTEGER, " +
                COL_WORD + " TEXT, " +
                COL_MEAN + " TEXT);");
        */
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);  // TODO : version control
    }
    /*-------- implementing SQLiteOpenHelper END --------*/
    /*---------------------------------------------------*/

    /**
     * Get DBManager instance.
     * Create new instance only if no instance exists.
     * @param context application context
     * @return singleton DBManager instance
     */
    public static DBManager open(Context context) {
        Log.i(TAG, "DB open");
        if(mInstance == null) {
            Log.i(TAG, "new DB instance");
            mInstance = new DBManager(context);
            mDB = mInstance.getWritableDatabase();
        }

        // TODO : add it to test!
        /*
        Cursor dbgCursor = mDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Log.d("DBManager", "dbgcursor" + dbgCursor.getCount());
        */

        return mInstance;
    }

    /*---------------------------------------------------*/
    /*---------------- Insert part START ----------------*/

    public long createFolder(String name) {
        ContentValues content = new ContentValues();
        content.put(DBHelper.COL_NAME, name);
        content.put(DBHelper.COL_COUNT, 0);

        return mDB.insert(DBHelper.FOLDER_TABLE, null, content);
    }


    public long createUnit(long folder_id, String unit_name) {
        ContentValues content = new ContentValues();
        content.put(DBHelper.COL_NAME, unit_name);
        content.put(DBHelper.COL_FOLDER, folder_id);
        content.put(DBHelper.COL_COUNT, 0);

        return mDB.insert(DBHelper.UNIT_TABLE, null, content);
    }


    public long createWord(long unit_id, String word, String mean) {
        ContentValues content = new ContentValues();
        content.put(DBHelper.COL_UNIT, unit_id);
        content.put(DBHelper.COL_WORD, word);
        content.put(DBHelper.COL_MEAN, mean);

        // +1 unit count
        mDB.execSQL("UPDATE " + DBHelper.UNIT_TABLE +
                    " SET " + DBHelper.COL_COUNT + "=" + DBHelper.COL_COUNT + "+1" +
                    " WHERE " + DBHelper.COL_ID + "=" + unit_id);


        /* get parent folder id */
        String[] folderColumn = {DBHelper.COL_FOLDER};
        Cursor unit = mDB.query(DBHelper.UNIT_TABLE, folderColumn,
                                DBHelper.COL_ID + "=" + unit_id, null, null, null, null);
        unit.moveToFirst();
        long folder_id = DBHelper.getId(unit);

        /* +1 folder count */
        mDB.execSQL("UPDATE " + DBHelper.FOLDER_TABLE +
                " SET " + DBHelper.COL_COUNT + "=" + DBHelper.COL_COUNT + "+1" +
                " WHERE " + DBHelper.COL_ID + "=" + folder_id);

        unit.close();
        return mDB.insert(DBHelper.WORD_TABLE, null, content);
    }

    /*----------------- Insert part END -----------------*/
    /*---------------------------------------------------*/

    /*---------------------------------------------------*/
    /*---------------- Query part START ----------------*/
    public String getFolderNameById(long folder_id) {
        String[] column = new String[] {DBHelper.COL_NAME};

        Cursor cursor = mDB.query(DBHelper.FOLDER_TABLE, column,
                                  DBHelper.COL_ID + "=" + folder_id, null, null, null, null);
        // always one reslut for query
        cursor.moveToFirst();
        String name = DBHelper.getName(cursor);
        cursor.close();

        return name;
    }

    public String getUnitNameById(long unit_id) {
        String[] column = new String[] {DBHelper.COL_NAME};

        Cursor cursor = mDB.query(DBHelper.UNIT_TABLE, column,
                                  DBHelper.COL_ID + "=" + unit_id, null, null, null, null);

        // always one reslut for query
        cursor.moveToFirst();
        String name = DBHelper.getName(cursor);
        cursor.close();

        return name;
    }

    public Cursor getAllFolders() {
        return mDB.query(DBHelper.FOLDER_TABLE, DBHelper.FOLDER_COLUMNS, null, null, null, null, null);
    }


    public Cursor getAllUnits(long folder_id) {
        return mDB.query(DBHelper.UNIT_TABLE, DBHelper.UNIT_COLUMNS,
                DBHelper.COL_FOLDER + "=" + folder_id, null, null, null, null);
    }

    public Cursor getAllWords() {
        return mDB.query(DBHelper.WORD_TABLE, DBHelper.WORD_COLUMNS, null, null, null, null, null);
    }

    public Cursor getAllWords(long unit_id) {
        return mDB.rawQuery("SELECT * " +
                " FROM " + DBHelper.WORD_TABLE +
                " WHERE " + DBHelper.COL_UNIT + "=" + unit_id
                , null);
    }

    /*------------------ Query part END ------------------*/
    /*---------------------------------------------------*/
    //TODO : check [] is possible
    public void deleteFolders(long[] folder_ids) {
        String queryIds = java.util.Arrays.toString(folder_ids).replace('[', '(').replace(']', ')');

        mDB.execSQL("DELETE FROM " + DBHelper.WORD_TABLE +
                " WHERE " + DBHelper.COL_FOLDER + " IN " + queryIds);

        mDB.execSQL("DELETE FROM " + DBHelper.UNIT_TABLE +
                    " WHERE " + DBHelper.COL_FOLDER + " IN " + queryIds);

        mDB.execSQL("DELETE FROM " + DBHelper.FOLDER_TABLE +
                    " WHERE " + DBHelper.COL_ID + " IN " + queryIds);
    }


    public void deleteUnits(long folder_id, long[] unit_ids) {
        String queryIds = java.util.Arrays.toString(unit_ids).replace('[', '(').replace(']', ')');

        Cursor childWords = mDB.rawQuery("SELECT " + DBHelper.COL_ID +
                " FROM " + DBHelper.WORD_TABLE +
                " WHERE " + DBHelper.COL_UNIT + " IN " + queryIds, null);

        // update folder count
        mDB.execSQL("UPDATE " + DBHelper.FOLDER_TABLE +
                    " SET " + DBHelper.COL_COUNT + "=" + DBHelper.COL_COUNT + "-" + childWords.getCount() +
                    " WHERE " + DBHelper.COL_ID + "=" + folder_id);

        childWords.close();

        // delete units
        mDB.execSQL("DELETE FROM " + DBHelper.UNIT_TABLE +
                " WHERE " + DBHelper.COL_ID + " IN " + queryIds);

        // delete words
        mDB.execSQL("DELETE FROM " + DBHelper.WORD_TABLE +
                    " WHERE " + DBHelper.COL_UNIT + " IN " + queryIds);
    }


    public void deleteWords(long folder_id, long unit_id, long[] word_ids) {
        String queryIds = java.util.Arrays.toString(word_ids).replace('[', '(').replace(']', ')');

        // Update folder count
        mDB.execSQL("UPDATE " + DBHelper.FOLDER_TABLE +
                " SET " + DBHelper.COL_COUNT + "=" +
                DBHelper.COL_COUNT + "-" + String.valueOf(word_ids.length) +
                " WHERE " + DBHelper.COL_ID + "=" + folder_id);

        // update unit count
        mDB.execSQL("UPDATE " + DBHelper.UNIT_TABLE +
                    " SET " + DBHelper.COL_COUNT + "=" +
                DBHelper.COL_COUNT + "-" + String.valueOf(word_ids.length) +
                    " WHERE " + DBHelper.COL_ID + "=" + unit_id);

        // Delete words
        mDB.execSQL("DELETE FROM " + DBHelper.WORD_TABLE +
                " WHERE " + DBHelper.COL_ID + " IN " + queryIds);
    }

    public int getMemory(long word_id) {
        Cursor cursor = mDB.query(DBHelper.WORD_TABLE, DBHelper.MEMORY_COLUMNS,
                DBHelper.COL_ID + "=" + word_id, null, null, null, null);

        cursor.moveToFirst();
        int memory = DBHelper.getMemory(cursor);
        cursor.close();

        return memory;
    }


    public void setStrength(long word_id, double new_strength) {
        long currentDate = System.currentTimeMillis() / (1000 * 60);

        mDB.execSQL("UPDATE " + DBHelper.WORD_TABLE +
                    " SET " + DBHelper.COL_STRENGTH + "=" + new_strength + ", " +
                              DBHelper.COL_DATE + "=" + currentDate +
                    " WHERE " + DBHelper.COL_ID + "=" + word_id);
    }
}
