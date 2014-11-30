package chloe.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import java.util.Calendar;

import chloe.todolist.db.TaskContract;

/**
 * Created by Chloe on 30/11/2014.
 * Manages alarm manager
 *
 */
public class AlarmManagerHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context) {
        // TaskDBHelper dbHelper = new TaskDBHelper(context);
        Uri uri = TaskContract.CONTENT_URI;
        //RETURN TABLE
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        //iterate through the table and create an intent with each id etc
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(TaskContract.Columns._ID));
            String name = cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK));
            long time = cursor.getLong(cursor.getColumnIndex(TaskContract.Columns.TIME));

            PendingIntent pIntent = createPendingIntent(context, id, name, time);

            Calendar calendar = Calendar.getInstance();
            //creating calendar sets time as current, set as stored time
            calendar.setTimeInMillis(time);
            //Log.d("Adding alert: ", "id: " + id + ", name: " + name + ", time: " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
            setAlarm(context, calendar, pIntent);
        }
        cursor.close();
    }

    /**
     * Attach alarm to Android Alarm manager.
     *
     * @param context current program state
     * @param calendar time for alarm to expire
     * @param pIntent action to occur after alarm
     */
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    public static void cancelAlarms(Context context) {

        Uri uri = TaskContract.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        //grab the system alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //iterate through the table and create an intent with each id, task and time
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex(TaskContract.Columns._ID));
            String name = cursor.getString(cursor.getColumnIndex(TaskContract.Columns.TASK));
            long time = cursor.getLong(cursor.getColumnIndex(TaskContract.Columns.TIME));

            PendingIntent pIntent = createPendingIntent(context, id, name, time);
            alarmManager.cancel(pIntent);
        }
        cursor.close();
    }

    private static PendingIntent createPendingIntent(Context context, int id, String taskName, long time) {
        //stores the caller and the target
        Intent intent = new Intent(context, AlarmService.class);
        //add on extra information to the intent
        intent.putExtra("id", id);
        intent.putExtra("taskName", taskName);
        intent.putExtra("time", time);
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}