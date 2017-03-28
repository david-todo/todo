package forgetmenot.todos;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import forgetmenot.todos.Receivers.DeadlineAlarmReceiver;
import forgetmenot.todos.Receivers.ObserveAlarmReceiver;
import forgetmenot.todos.Receivers.StartAlarmReceiver;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderInstTable;
import forgetmenot.todos.database.ListHeaderTable;

import static forgetmenot.todos.CustomUtils.getNextList;
import static forgetmenot.todos.CustomUtils.getActiveList;

/**
 * Created by SherrelD on 3/7/2017.
 */


public class setListAlarm extends IntentService {
    private static final String TAG = "setInitialAlarmService";
    private PendingIntent pendingIntent;
    AlarmManager alarmManager;

    public setListAlarm() {
        super("set-alarm-service");
    }
    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //get all ListHeaders for user ID in db
        String userId = getUserID();
        String selection = ListHeaderTable.COLUMN_USERID + "=?";
        String[] selectionArgs = {userId};
        Cursor c = getContentResolver().query(MyTodoContentProvider.LISTHEADER_URI,
                ListHeaderTable.PROJECTION, selection, selectionArgs, null, null);

        if (c.getCount()!=0) {
            //get current day of week and time
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEE");
            String currDay = sdfDay.format(cal.getTime());
            SimpleDateFormat sdfTime = new SimpleDateFormat("HHmm");
            String nowTime = sdfTime.format(cal.getTime());

            ListInfo activeList = getActiveList(c,currDay,nowTime);
            if (activeList == null) { //no list currently active
                ListInfo nextList = getNextList(c,currDay,nowTime);

                if (nextList !=null) {
                    //set intent for nextlist fragment display
                    Intent nextListIntent = new Intent(this,StartAlarmReceiver.class);
                    nextListIntent.putExtra(ListHeaderTable.COLUMN_LISTID, nextList.listid);
                    nextListIntent.putExtra(ListHeaderTable.COLUMN_LISTNAME,nextList.listName);
                    nextListIntent.putExtra(ListHeaderTable.COLUMN_STARTTIME,nextList.startTime);
                    nextListIntent.putExtra(ListHeaderTable.COLUMN_DEADLINE,nextList.deadlineTime);
                    nextListIntent.putExtra("startday",nextList.startDay);
                    nextListIntent.putExtra("intenttype","nextlist");
                    ObserveAlarmReceiver.getInstance().triggerObservers(nextListIntent);
                    //set alarm intent

                    //only set alarm if next list is today
                    if (nextList.startDay.equals(currDay)) {
                        //set nextlist alarm
                        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        pendingIntent = PendingIntent.getBroadcast(this, 1001, nextListIntent, 0);

                        int alarmHr = CustomUtils.getHrIntfrom4charTime(nextList.startTime);
                        int alarmMin = CustomUtils.getMinIntfrom4charTime(nextList.startTime);

                        Calendar alarmcal = Calendar.getInstance();
                        alarmcal.set(Calendar.HOUR_OF_DAY, alarmHr);
                        alarmcal.set(Calendar.MINUTE, alarmMin);
                        alarmcal.set(Calendar.SECOND, 0);
                        alarmcal.set(Calendar.MILLISECOND, 0);

                        //setexact only works on API 19 and up
                        if (Build.VERSION.SDK_INT >= 19) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmcal.getTimeInMillis(),
                                    pendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmcal.getTimeInMillis(),
                                    pendingIntent);
                        }
                    }
                }

            }else { //list is currently active
                //assume the setListAlarm class would not have been triggered if the
                //active list already had a listheader and list item instance created.

                Uri headInstUri = CustomUtils.addListInstance(this, activeList.listid);

                if (headInstUri != null) {
                    //successful creation of list instance


                    //set activelist fragment
                    c.moveToFirst();
                    Intent activelistIntent = new Intent(this, DeadlineAlarmReceiver.class);
                    activelistIntent.putExtra(ListHeaderTable.COLUMN_LISTID, activeList.listid);
                    activelistIntent.putExtra(ListHeaderInstTable.COLUMN_ID,
                            CustomUtils.getIdfromUri(headInstUri));
                    activelistIntent.putExtra(ListHeaderTable.COLUMN_LISTNAME,
                            c.getString(c.getColumnIndexOrThrow(ListHeaderTable.COLUMN_LISTNAME)));
                    activelistIntent.putExtra("intenttype", "activelist");
                    ObserveAlarmReceiver.getInstance().triggerObservers(activelistIntent);

                    //set deadline alarm
                    int deadlineHr = CustomUtils.getHrIntfrom4charTime(activeList.deadlineTime);
                    int deadlineMin = CustomUtils.getMinIntfrom4charTime(activeList.deadlineTime);

                    Calendar deadlineCal = Calendar.getInstance();
                    deadlineCal.set(Calendar.HOUR_OF_DAY, deadlineHr);
                    deadlineCal.set(Calendar.MINUTE, deadlineMin);
                    deadlineCal.set(Calendar.SECOND, 0);
                    deadlineCal.set(Calendar.MILLISECOND, 0);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent activeListIntent = new Intent(this, DeadlineAlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(this, 1002, activeListIntent, 0);

                    if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, deadlineCal.getTimeInMillis(),
                                pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, deadlineCal.getTimeInMillis(),
                                pendingIntent);
                    }
                }
            }
            c.close();
        }
    }


    private String getUserID() {
    //figure out how to store currently valid userid on device
        return "sherreld";
    }
}
