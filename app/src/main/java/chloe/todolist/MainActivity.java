package chloe.todolist;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import chloe.todolist.db.TaskContract;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "updateUI");
        updateUI();
    }

/*

        SQLiteDatabase sql = new TaskDBHelper(this).getWritableDatabase();

        //cursor is where your queries (question to db) will be run
        //The results of the query are returned to you in a Cursor object.
        Cursor cursor = sql.query(TaskContract.DB_TABLE, new String[]{TaskContract.Columns.TASK}, null, null, null, null, null);

        cursor.moveToFirst(); //makes sure we start at the first entry in the results
        while(cursor.moveToNext()) {

            //getting column "task"
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK));
            Log.d("MainActivity cursor", taskName);
        }


*/


    private void updateUI(){

//        get the entire table as a query
        Uri uri = TaskContract.CONTENT_URI;
        Cursor cursor = this.getContentResolver().query(uri,null,null,null,null);

//        R (res)
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.task_view, cursor, new String[]{TaskContract.Columns.TASK, TaskContract.Columns.TIME},new int[]{R.id.taskTextView, R.id.expiresView}, 0);
        listAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (columnIndex == cursor.getColumnIndex(TaskContract.Columns.TIME)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(columnIndex));

                    TextView textView = (TextView) view;

                    SimpleDateFormat format = new SimpleDateFormat("h:mm a 'on' d/MM/yy");

                    textView.setText(format.format(calendar.getTime()));
                    return true;
                }

                return false;
            }
        });

//       generate the list of tasks in GUI
        this.setListAdapter(listAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
//        if it's an add task then log it and return true, if it's an action settings return true also else refer to the super
        switch(id){
            case R.id.action_add_task:
                Log.d("MainActivity", "Add a new task");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("");
                builder.setMessage("What would you like to do?");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputField = new EditText(this);
                inputField.setHint("Create Task");
                layout.addView(inputField);

                final TextView timeField = new TextView(this);
                timeField.setText("Click to set time of alarm");
                layout.addView(timeField);

                final Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 00);

                final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hour, int minute) {
                            timeField.setText(hour + ":" + minute);
//                            calendar is final so can reference in nested block
                            calendar.set(Calendar.HOUR, hour);
                            calendar.set(Calendar.MINUTE, minute);
                        }
                    }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);

                timeField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timePickerDialog.show();
                    }
                });

                builder.setView(layout);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmManagerHelper.cancelAlarms(MainActivity.this);
                        String task = inputField.getText().toString();
                        Log.d("MainActivity", task);

//                        TaskDBHelper helper = new TaskDBHelper(MainActivity.this);
//                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(TaskContract.Columns.TASK,task);
                        values.put(TaskContract.Columns.TIME,calendar.getTimeInMillis());

                        Uri uri = TaskContract.CONTENT_URI;
                        getApplicationContext().getContentResolver().insert(uri, values);

//                        now pushed, the row has now been inserted into the db
                        updateUI();

                        AlarmManagerHelper.setAlarms(MainActivity.this);
                    }

                });

                builder.setNegativeButton("Cancel", null);
//                AlertDialog alert = builder.create().show();
//                alert means must click either options, not outside of dialog box, back is cancel

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

    /* Respond to done button being selected */
    public void onDoneButtonClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        String task = taskTextView.getText().toString();

        Uri uri = TaskContract.CONTENT_URI;
        this.getContentResolver().delete(uri, TaskContract.Columns.TASK + "=?", new String[]{task});
        updateUI();
    }
}
