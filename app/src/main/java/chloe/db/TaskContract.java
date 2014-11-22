package chloe.db;

import android.provider.BaseColumns;

/**
 * Created by Chloe on 22/11/2014.
 */

//all static variables (constants)
public class TaskContract {
    public static final String DB_NAME = "chloe.TodoList";
    public static final int DB_VERSION = 1;
    public static final String DB_TABLE = "tasks";

    public class Columns{
        public static final String TASK = "task";
        public static final String _ID = BaseColumns._ID;

    }

}