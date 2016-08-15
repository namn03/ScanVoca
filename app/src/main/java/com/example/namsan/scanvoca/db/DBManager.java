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


public class DBManager extends SQLiteOpenHelper{
    private static final String TAG = "DBManager";

    // TODO : check access range
    public static final String FOLDER_TABLE = "FOLDER";
    public static final String UNIT_TABLE = "UNIT";
    public static final String WORD_TABLE = "VOCA";

    /** unique id of raw */
    public static final String COL_ID = "_id";
    public static final String COL_FOLDER = "folder";
    public static final String COL_UNIT = "unit";

    /** folder name */
    public static final String COL_NAME = "name";
    public static final String COL_COUNT = "count";


    public static final String COL_WORD = "word";
    public static final String COL_MEAN = "mean";

    public static final String[] FOLDER_COLUMNS = {COL_ID, COL_NAME, COL_COUNT};
    public static final String[] UNIT_COLUMNS = {COL_ID, COL_NAME, COL_FOLDER, COL_COUNT};
    public static final String[] WORD_COLUMNS = {COL_ID, COL_FOLDER, COL_UNIT, COL_WORD, COL_MEAN};
    public static final String[] WORD_QUERY_COLUMNS = {COL_ID, COL_WORD, COL_MEAN};

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
        return;
    }
    /*-------- implementing SQLiteOpenHelper END --------*/
    /*---------------------------------------------------*/

    /**
     * Get DBManager instance.
     * Create new instance only if no instance exists.
     * @param context
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
        content.put(COL_NAME, name);
        content.put(COL_COUNT, 0);

        return mDB.insert(FOLDER_TABLE, null, content);
    }


    public long createUnit(long folder_id, String unit_name) {
        ContentValues content = new ContentValues();
        content.put(COL_NAME, unit_name);
        content.put(COL_FOLDER, folder_id);
        content.put(COL_COUNT, 0);

        return mDB.insert(UNIT_TABLE, null, content);
    }


    public long createWord(long unit_id, String word, String mean) {
        ContentValues content = new ContentValues();
        content.put(COL_UNIT, unit_id);
        content.put(COL_WORD, word);
        content.put(COL_MEAN, mean);

        // +1 unit count
        mDB.execSQL("UPDATE " + UNIT_TABLE +
                    " SET " + COL_COUNT + "=" + COL_COUNT + "+1" +
                    " WHERE " + COL_ID + "=" + unit_id);


        /* get parent folder id */
        String[] folderColumn = {COL_FOLDER};
        Cursor unit = mDB.query(UNIT_TABLE, folderColumn, COL_ID + "=" + unit_id, null, null, null, null);
        unit.moveToFirst();
        long folder_id = unit.getLong(unit.getColumnIndex(COL_FOLDER));

        /* +1 folder count */
        mDB.execSQL("UPDATE " + FOLDER_TABLE +
                    " SET " + COL_COUNT + "=" + COL_COUNT + "+1" +
                    " WHERE " + COL_ID + "=" + folder_id);

        unit.close();
        return mDB.insert(WORD_TABLE, null, content);
    }

    /*----------------- Insert part END -----------------*/
    /*---------------------------------------------------*/

    /*---------------------------------------------------*/
    /*---------------- Query part START ----------------*/
    public String getFolderNameById(long folder_id) {
        String[] column = new String[] {COL_NAME};

        Cursor cursor = mDB.query(FOLDER_TABLE, column, COL_ID + "=" + folder_id, null, null, null, null);
        // always one reslut for query
        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex(COL_NAME));
    }

    public String getUnitNameById(long unit_id) {
        String[] column = new String[] {COL_NAME};

        Cursor cursor = mDB.query(UNIT_TABLE, column, COL_ID + "=" + unit_id, null, null, null, null);
        // always one reslut for query
        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex(COL_NAME));
    }

    public Cursor getAllFolders() {
        return mDB.query(FOLDER_TABLE, FOLDER_COLUMNS, null, null, null, null, null);
    }


    public Cursor getAllUnits(long folder_id) {
        return mDB.query(UNIT_TABLE, UNIT_COLUMNS, COL_FOLDER + "=" + folder_id, null, null, null, null);
    }

    public Cursor getAllWords() {
        return mDB.query(WORD_TABLE, WORD_COLUMNS, null, null, null, null, null);
    }

    public Cursor getAllWords(long unit_id) {
        return mDB.rawQuery("SELECT * " +
                            " FROM " + WORD_TABLE +
                            " WHERE " + COL_UNIT + "=" + unit_id
                , null);
    }

    /*------------------ Query part END ------------------*/
    /*---------------------------------------------------*/

    //TODO : check [] is possible
    public void deleteFolders(long[] folder_ids) {
        String queryIds = java.util.Arrays.toString(folder_ids).replace('[', '(').replace(']', ')');

        mDB.execSQL("DELETE FROM " + WORD_TABLE +
                    " WHERE " + COL_FOLDER + " IN " + queryIds);

        mDB.execSQL("DELETE FROM " + UNIT_TABLE +
                    " WHERE " + COL_FOLDER + " IN " + queryIds);

        mDB.execSQL("DELETE FROM " + FOLDER_TABLE +
                    " WHERE " + COL_ID + " IN " + queryIds);
    }


    public void deleteUnits(long folder_id, long[] unit_ids) {
        String queryIds = java.util.Arrays.toString(unit_ids).replace('[', '(').replace(']', ')');

        Cursor childWords = mDB.rawQuery("SELECT " + COL_ID +
                                        " FROM " + WORD_TABLE +
                                        " WHERE " + COL_UNIT + " IN " + queryIds, null);

        // update folder count
        mDB.execSQL("UPDATE " + FOLDER_TABLE +
                    " SET " + COL_COUNT + "=" + COL_COUNT + "-" + childWords.getCount() +
                    " WHERE " + COL_ID + "=" + folder_id);

        // delete units
        mDB.execSQL("DELETE FROM " + UNIT_TABLE +
                " WHERE " + COL_ID + " IN " + queryIds);

        // delete words
        mDB.execSQL("DELETE FROM " + WORD_TABLE +
                    " WHERE " + COL_UNIT + " IN " + queryIds);
    }


    public void deleteWords(long folder_id, long unit_id, long[] word_ids) {
        String queryIds = java.util.Arrays.toString(word_ids).replace('[', '(').replace(']', ')');

        // Update folder count
        mDB.execSQL("UPDATE " + FOLDER_TABLE +
                " SET " + COL_COUNT + "=" + COL_COUNT + "-" + String.valueOf(word_ids.length) +
                " WHERE " + COL_ID + "=" + folder_id);

        // update unit count
        mDB.execSQL("UPDATE " + UNIT_TABLE +
                    " SET " + COL_COUNT + "=" + COL_COUNT + "-" + String.valueOf(word_ids.length) +
                    " WHERE " + COL_ID + "=" + unit_id);

        // Delete words
        mDB.execSQL("DELETE FROM " + WORD_TABLE +
                " WHERE " + COL_ID + " IN " + queryIds);

    }
}
