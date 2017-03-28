package forgetmenot.todos.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import forgetmenot.todos.CustomUtils;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderInstTable;
import forgetmenot.todos.database.ListHeaderTable;
import forgetmenot.todos.database.ListItemInstTable;
import forgetmenot.todos.database.ListItemTable;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by SherrelD on 3/6/2017.
 */

public class StartAlarmReceiver extends WakefulBroadcastReceiver {
    private PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private Uri headInstUri;
    private int itemCount;

    @Override
    public void onReceive(Context context, Intent intent) {

        int list = intent.getExtras().getInt(ListHeaderTable.COLUMN_LISTID);
        String listname = intent.getExtras().getString(ListHeaderTable.COLUMN_LISTNAME);
        String status = "open";

        //create list header and item instances
        Uri headInstUri = CustomUtils.addListInstance(context,list);

        if (headInstUri != null) {
            int headInstId = CustomUtils.getIdfromUri(headInstUri);
            if (headInstId != 0) {
                intent.putExtra(ListHeaderInstTable.COLUMN_ID, headInstId);
                intent.putExtra(ListHeaderInstTable.COLUMN_LISTID, list);
                intent.putExtra(ListHeaderTable.COLUMN_LISTNAME, listname);
                intent.putExtra("intenttype", "activelist");
            }
        }

        /*change fragment */
        ObserveAlarmReceiver.getInstance().triggerObservers(intent);

        /* add deadline alarm */
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent deadlineIntent = new Intent(context, DeadlineAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,1002,deadlineIntent,0);

        String deadline = intent.getExtras().getString(ListHeaderTable.COLUMN_DEADLINE);
        int alarmHr = CustomUtils.getHrIntfrom4charTime(deadline);
        int alarmMin = CustomUtils.getMinIntfrom4charTime(deadline);

        Calendar alarmcal = Calendar.getInstance();
        alarmcal.set(Calendar.HOUR_OF_DAY, alarmHr);
        alarmcal.set(Calendar.MINUTE, alarmMin);
        alarmcal.set(Calendar.SECOND, 0);
        alarmcal.set(Calendar.MILLISECOND,0);

        //setexact only works on API 19 and up
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmcal.getTimeInMillis(),
                    pendingIntent);
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmcal.getTimeInMillis(),
                    pendingIntent);
        }
    }
}
