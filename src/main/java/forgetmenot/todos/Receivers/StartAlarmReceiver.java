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

        String list = String.valueOf(intent.getExtras().getInt("next" + ListHeaderTable.COLUMN_LISTID));
        String selection = ListHeaderTable.COLUMN_LISTID + "=?";
        String[] selectionArgs = {list};
        String status = "open";


        //create list header and item instances
        //create list header instance
        Cursor c = context.getContentResolver().query(MyTodoContentProvider.LISTHEADER_URI,
                ListHeaderTable.PROJECTION, selection, selectionArgs, null, null);

        if (c != null) {
            ContentValues headinstvalues = new ContentValues();
            String user = getUser();
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEE");
            SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
            Calendar cal = Calendar.getInstance();
            String currDay = sdfDay.format(cal.getTime());
            String currDateTime = sdfDateTime.format(cal.getTime());


            headinstvalues.put(ListHeaderInstTable.COLUMN_USERID, user);
            headinstvalues.put(ListHeaderInstTable.COLUMN_LISTID, c.getColumnIndexOrThrow(
                    ListHeaderTable.COLUMN_LISTID));
            headinstvalues.put(ListHeaderInstTable.COLUMN_STARTDATETIME, currDateTime);
            headinstvalues.put(ListHeaderInstTable.COLUMN_STARTDAY, currDay);
            headinstvalues.put(ListHeaderInstTable.COLUMN_OVERALLSTAT, status);

            headInstUri = context.getContentResolver().insert(
                    MyTodoContentProvider.LISTHEADERINST_URI, headinstvalues);

            //create list item instance
            if (headInstUri != null) {
                int headInstId = CustomUtils.getIdfromUri(headInstUri);
                if (headInstId != 0) {
                    intent.putExtra(ListHeaderInstTable.COLUMN_ID,headInstId);
                }

                selection = ListItemTable.COLUMN_LISTID + "=?";
                Cursor citem = context.getContentResolver().query(MyTodoContentProvider.LISTITEM_URI,
                        ListItemTable.PROJECTION, selection, selectionArgs, null, null);

                if (citem != null) {
                    ContentValues[] iteminstvalues = new ContentValues[citem.getCount()];
                    citem.moveToFirst();
                    status = "open";
                    for (int i=0;i<citem.getCount();i++) {
                        ContentValues cv = new ContentValues();
                        cv.put(ListItemInstTable.COLUMN_HEADINST_ID,headInstId);
                        cv.put(ListItemInstTable.COLUMN_LISTID,list);
                        cv.put(ListItemInstTable.COLUMN_ITEMNO,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_ITEMNO)));
                        cv.put(ListItemInstTable.COLUMN_ITEMDESC,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_ITEMDESC)));
                        cv.put(ListItemInstTable.COLUMN_CONFIRM,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_CONFIRM)));
                        cv.put(ListItemInstTable.COLUMN_NOTIF_TYPE,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_NOTIF_TYPE)));
                        cv.put(ListItemInstTable.COLUMN_DIST_LIST,
                                citem.getString(citem.getColumnIndexOrThrow(ListItemTable.COLUMN_DIST_LIST)));
                        cv.put(ListItemInstTable.COLUMN_STATUS,status);
                        cv.put(ListItemInstTable.COLUMN_COMPDATETIME, "00000000 00:00:00");

                        iteminstvalues[i] = cv;
                        citem.moveToNext();
                    }

                    itemCount = context.getContentResolver().bulkInsert(
                            MyTodoContentProvider.LISTITEMINST_URI,iteminstvalues);

                }

            }
        }

        c.close();

        /*change fragment */
        ObserveAlarmReceiver.getInstance().triggerObservers(intent);

        /* add deadline alarm */
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent deadlineIntent = new Intent(context, DeadlineAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context,1002,deadlineIntent,0);

        String deadline = intent.getExtras().getString("next" + ListHeaderTable.COLUMN_DEADLINE);
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
    private String getUser() {
        return "sherreld";
    }
}
