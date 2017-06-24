package com.fanjavaid.android.cooltodo.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by fanjavaid on 6/23/17.
 */

public class TodoDummyUtil {
    public static void insertDummyData(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(TodoContract.TodoEntry.COLUMN_NAME, "Learn Background Task");
        cv.put(TodoContract.TodoEntry.COLUMN_DESCRIPTION, "Background task is Android component");

        db.insert(TodoContract.TodoEntry.TABLE_NAME, null, cv);
    }
}
