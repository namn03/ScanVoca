package com.example.namsan.scanvoca.db;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.sql.SQLInput;


public class DBManager extends SQLiteOpenHelper{
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
    /** words in folder OR words in unit (created by query temporary) */
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


    /** private for singleton */
    private DBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    private DBManager(Context context) {
        super(context, WORD_TABLE, null, VERSION);
    }


    /*** implements SQLiteOpenHelper ***/
    /** create new table if db is not exist */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("scanvoca.db.DBManager", "create DB tables");

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
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);  // TODO : version control
        return;
    }


    public static DBManager open(Context context) {
        Log.i("scanvoca.db.DBManager", "DB open");
        if(mInstance == null) {
            Log.i("scanvoca.db.DBManager", "new DB instance");
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


    public Cursor getAllFolders() {
        return mDB.query(FOLDER_TABLE, FOLDER_COLUMNS, null, null, null, null, null);
    }


    public Cursor getAllUnits(long folder_id) {
        return mDB.query(UNIT_TABLE, UNIT_COLUMNS, COL_FOLDER + "=" + folder_id, null, null, null, null);
    }


    public Cursor getAllWords(long unit_id) {
        return mDB.rawQuery("SELECT * " +
                            " FROM " + WORD_TABLE +
                            " WHERE " + COL_UNIT + "=" + unit_id
                , null);
    }

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
                    " WHERE " + COL_FOLDER + "=" + folder_id);

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
