package chloe.todolist.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Chloe on 24/11/2014.
 *
 * This class allows the same constants to be used across all the other classes.
 * This lets for example a column name be changed easily as it will propagate throughout
 *
 */

public final class TaskContract {

    //empty constructor to prevent accidentally instantiating the contract class
    public TaskContract(){}

    //all constant variables
    public static final String DB_NAME = "chloe.todolist";
    public static final int DB_VERSION = 2; //if db schema is changed, must increment
    public static final String DB_TABLE = "tasks";

    public static final String AUTHORITY = "chloe.todolist";
    //used to access data in the table
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TABLE);
    public static final int TASKS_LIST = 1;
    public static final int TASKS_ITEM = 2;
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/example.tasksDB/"+ DB_TABLE;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/example/tasksDB" + DB_TABLE;


    public class Columns {
        public static final String TASK = "task";
        public static final String _ID = BaseColumns._ID;
        public static final String TIME = "time";

    }

}