package chloe.todolist.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.sql.SQLException;

/**
 * Created by Chloe on 26/11/2014.
 *
 * Content Provider
 */

public class TaskProvider extends ContentProvider {


    private SQLiteDatabase db;
    private TaskDBHelper taskDBHelper;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.DB_TABLE,TaskContract.TASKS_LIST);
        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.DB_TABLE+"/#",TaskContract.TASKS_ITEM);

    }

   /* The TaskDBHelper class is used to create a helper object
    * which creates a database if it does not already exist.
    * This method (onCreate) would return false if the provider won’t be loaded because
    * the database is not accessible, otherwise would return true.
    */

    @Override
    public boolean onCreate() {
        boolean ret = true;
        taskDBHelper = new TaskDBHelper(getContext());
        db = taskDBHelper.getWritableDatabase();

        //return false
        if (db == null) {
            ret = false;
        }

        else if (db.isReadOnly()) {
            db.close();
            db = null;
            ret = false;
        }

        return ret;
    }

    // Retrieves data stored in the database and returns a Cursor instance - otherwise will throw
    // an exception if there is a problem with the query

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TaskContract.DB_TABLE);

        switch (uriMatcher.match(uri)) {
            case TaskContract.TASKS_LIST:
                break;

            //if the object requested is an item (not a table) then the querybuilder helps to add the WHERE clause to the query for the ID
            case TaskContract.TASKS_ITEM:
                qb.appendWhere(TaskContract.Columns._ID + " = "+ uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // a cursor instance is created by executing the query created by qb (the querybuilder)
        Cursor cursor = qb.query(db,projection,selection,selectionArgs,null,null,null);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case TaskContract.TASKS_LIST:
                return TaskContract.CONTENT_TYPE;

            case TaskContract.TASKS_ITEM:
                return TaskContract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Invalid URI: "+uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (uriMatcher.match(uri) != TaskContract.TASKS_LIST) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        long id = db.insert(TaskContract.DB_TABLE, null, contentValues);

        if (id > 0) {
            //returns a select query for the new row
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleted = 0;

        switch (uriMatcher.match(uri)) {
            case TaskContract.TASKS_LIST:
                // Should we allow this?
                db.delete(TaskContract.DB_TABLE,selection,selectionArgs);
                break;

            case TaskContract.TASKS_ITEM:
                String where = TaskContract.Columns._ID + " = " + uri.getLastPathSegment();
                if (!selection.isEmpty()) {
                    where += " AND "+selection;
                }

                deleted = db.delete(TaskContract.DB_TABLE,where,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Invalid URI: "+uri);
        }

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int updated = 0;

        switch (uriMatcher.match(uri)) {
            //for updating all rows
            case TaskContract.TASKS_LIST:
                db.update(TaskContract.DB_TABLE,contentValues,selection,selectionArgs);
                break;
            //for updating specific item
            case TaskContract.TASKS_ITEM:
                String where = TaskContract.Columns._ID + " = " + uri.getLastPathSegment();
                if (!selection.isEmpty()) {
                    where += " AND "+ selection;
                }
                updated = db.update(TaskContract.DB_TABLE,contentValues,where,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Invalid URI: "+uri);
        }

        return updated;
    }

}
