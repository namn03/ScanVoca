package com.example.namsan.scanvoca.db;

import android.database.Cursor;
import android.util.Log;

import com.example.namsan.scanvoca.MainActivity;

/**
 * This class contain all DB schema information in static constant.
 */
public class DBHelper {
    private static final String TAG = "DBHelper";

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
    public static final String COL_DATE = "date";


    public static final String[] FOLDER_COLUMNS = {COL_ID, COL_NAME, COL_COUNT};
    public static final String[] UNIT_COLUMNS = {COL_ID, COL_NAME, COL_FOLDER, COL_COUNT};
    public static final String[] WORD_COLUMNS = {COL_ID, COL_FOLDER, COL_UNIT,
                                                 COL_WORD, COL_MEAN, COL_STRENGTH, COL_DATE};
    public static final String[] WORD_QUERY_COLUMNS = {COL_ID, COL_WORD, COL_MEAN};
    public static final String[] MEMORY_COLUMNS = {COL_STRENGTH, COL_DATE};

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

    public static long getCount(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(COL_COUNT));
    }

    public static int getDate(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(COL_DATE));
    }

    public static double getStrength(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndex(COL_STRENGTH));
    }

    public static int getMemory(Cursor cursor) {
        int memory = 0;
        long lastMin = DBHelper.getDate(cursor);

        // If lastMin is 0, it means it have never been studied
        if(lastMin != 0) {
            long currentMin = System.currentTimeMillis() / (1000 * 60);
            double strength = getStrength(cursor);

            double dayInterval = (currentMin - lastMin) / (60.0 * 24);
            Log.d(TAG, "str" + strength);

            memory = (int) (100 * Math.exp(- dayInterval / strength));
            Log.d(TAG, "me" + Math.exp(- dayInterval / strength));
        }

        return memory;
    }
}
