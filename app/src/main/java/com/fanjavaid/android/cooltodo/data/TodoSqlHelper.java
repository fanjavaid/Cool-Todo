package com.fanjavaid.android.cooltodo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fanjavaid on 6/23/17.
 */

public class TodoSqlHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "todoapp.db";

    public static final int DATABASE_VERSION = 1;

    public TodoSqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE ").append(TodoContract.TodoEntry.TABLE_NAME)
                .append("(")
                .append(TodoContract.TodoEntry._ID)
                .append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ")
                .append(TodoContract.TodoEntry.COLUMN_NAME).append(" TEXT NOT NULL, ")
                .append(TodoContract.TodoEntry.COLUMN_DESCRIPTION).append(" TEXT NOT NULL, ")
                .append(TodoContract.TodoEntry.COLUMN_CREATED_DATE)
                .append(" TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
                .append(");");

        String query = queryBuilder.toString();
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TodoContract.TodoEntry.TABLE_NAME);
        onCreate(db);
    }
}
