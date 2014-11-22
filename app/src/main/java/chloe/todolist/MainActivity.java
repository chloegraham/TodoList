package chloe.todolist;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import chloe.db.TaskContract;
import chloe.db.TaskDBHelper;


public class MainActivity extends ListActivity {

    private TaskDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUI();

/*
        //cursor is where your queries (question to db) will be run
        SQLiteDatabase sql = new TaskDBHelper(this).getWritableDatabase();
        Cursor cursor = sql.query(TaskContract.DB_TABLE, new String[]{TaskContract.Columns.TASK}, null, null, null, null, null);

        cursor.moveToFirst(); //makes sure we start at the top of the table always
        while(cursor.moveToNext()) {

            //getting column "task"
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK));
            Log.d("MainActivity cursor", taskName);
        }


*/
    }

    private void updateUI(){
        this.helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        Cursor cursor = sqlDB.query(TaskContract.DB_TABLE, new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK}, null, null, null, null, null);
        //R (res)
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.task_view, cursor, new String[]{TaskContract.Columns.TASK},new int[]{R.id.taskTextView}, 0);
        this.setListAdapter(listAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //if it's an add task then log it and return true, if it's an action settings return true also else refer to the super
        switch(id){
            case R.id.action_add_task:
                Log.d("MainActivity", "Add a new task");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add a task");
                builder.setMessage("What would you like to do?");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = inputField.getText().toString();

                        Log.d("MainActivity", task);

                        //if it was just this it would only refer to the dialog interface, not whole thing

                        TaskDBHelper helper = new TaskDBHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues(); //values is a MAP. (key, value)

                        values.clear();
                        values.put(TaskContract.Columns.TASK,task); //TASK is column NAME, task is value eg "Walk dog"
                        //hasn't pushed them to the db yet, just adding them to a pile or such
                        db.insertWithOnConflict(TaskContract.DB_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                        //now pushed
                        updateUI();
                    }

                });

                builder.setNegativeButton("Cancel", null);
                //get alert then show it //
               // AlertDialog alert = builder.create().show();
                //alert means must click either options, not outside of dialog box, back is cancel

                builder.create().show();
                return true;

            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
