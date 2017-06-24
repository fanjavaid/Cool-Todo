package com.fanjavaid.android.cooltodo.data;

import android.provider.BaseColumns;

/**
 * Created by fanjavaid on 6/23/17.
 */

public class TodoContract {
    private TodoContract() { }

    public static final class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo_tasks";

        public static final String COLUMN_NAME = "task_name";
        public static final String COLUMN_DESCRIPTION = "task_description";
        public static final String COLUMN_CREATED_DATE = "task_created_date";
    }
}
