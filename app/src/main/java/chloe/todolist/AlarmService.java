package chloe.todolist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Chloe on 30/11/2014.
 * A service to allow for notifications to run when the app is running in the background
 *
 */
public class AlarmService extends Service {

//    public static String TAG = AlarmService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Caller: ", intent.getStringExtra("taskName"));
        // AlarmManagerHelper.setAlarms(this);

        Intent resultIntent = new Intent(this, MainActivity.class);
        NotificationManager notifications = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity( this.getApplicationContext(),0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle("To-do List")
                .setContentText(intent.getStringExtra("taskName") + " is due!")
                .setSmallIcon(R.drawable.ic_launcher);
        notifications.notify(intent.getIntExtra("id", 0), builder.build());

        Log.d("Notification fired: ", "fired");

        return super.onStartCommand(intent, flags, startId);
    }
}
