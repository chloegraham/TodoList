package chloe.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Chloe on 22/11/2014.


 This class takes care of opening the database if it exists, creating it if it does not, and upgrading it as necessary.
 Extends SQLiteOpenHelper to manage database creation and version management
 */
public class TaskDBHelper extends SQLiteOpenHelper {

    public TaskDBHelper(Context context){

        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s LONG)", TaskContract.DB_TABLE, TaskContract.Columns.TASK, TaskContract.Columns.TIME);
//        Log.d("TaskDBHelper", "Query to form table: " + sqlQuery);
        db.execSQL(sqlQuery);
    }

    @Override //incrementing version
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // just discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.DB_TABLE);
        onCreate(db);

    }
}
