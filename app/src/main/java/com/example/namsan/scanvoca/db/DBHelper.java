package com.example.namsan.scanvoca.db;

import android.database.Cursor;

/**
 * This class contain all DB schema information in static constant.
 */
public class DBHelper {

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
    public static final String COL_MEMORY = "memory";
    public static final String COL_STRENGTH = "strength";
    public static final String COL_UPDATE = "update";


    public static final String[] FOLDER_COLUMNS = {COL_ID, COL_NAME, COL_COUNT};
    public static final String[] UNIT_COLUMNS = {COL_ID, COL_NAME, COL_FOLDER, COL_COUNT};
    public static final String[] WORD_COLUMNS = {COL_ID, COL_FOLDER, COL_UNIT, COL_WORD, COL_MEAN};
    public static final String[] WORD_QUERY_COLUMNS = {COL_ID, COL_WORD, COL_MEAN};

    public static long getId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(COL_ID));
    }

    public static String getName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(COL_NAME));
    }

    public static String getWord(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(COL_WORD));
    }

    public static String getMean(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(COL_MEAN));
    }

    public static int getMemory(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(COL_MEMORY));
    }

    public static long getCount(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(COL_COUNT));
    }
}
