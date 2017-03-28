package forgetmenot.todos.Receivers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import forgetmenot.todos.CustomUtils;
import forgetmenot.todos.ListInfo;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderTable;
import forgetmenot.todos.setListAlarm;

/**
 * Created by SherrelD on 3/6/2017.
 */

public class DeadlineAlarmReceiver extends WakefulBroadcastReceiver {
    String userId = getUserID();
    String selection = ListHeaderTable.COLUMN_USERID + "=?";
    String[] selectionArgs = {userId};

    @Override
    public void onReceive(Context context, Intent intent) {
        //finalize list header and list item instances

        //calculate and send notifications

        Cursor c = context.getContentResolver().query(MyTodoContentProvider.LISTHEADER_URI,
                ListHeaderTable.PROJECTION, selection, selectionArgs, null, null);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEE");
        String currDay = sdfDay.format(cal.getTime());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HHmm");
        String nowTime = sdfTime.format(cal.getTime());

        ListInfo nextList = CustomUtils.getNextList(c,currDay,nowTime);
        c.close();

        if (nextList != null) {
            intent.putExtra(ListHeaderTable.COLUMN_LISTID, nextList.listid);
            intent.putExtra(ListHeaderTable.COLUMN_LISTNAME, nextList.listName);
            intent.putExtra(ListHeaderTable.COLUMN_STARTTIME, nextList.startTime);
            intent.putExtra("startday",nextList.startDay);
            intent.putExtra("intenttype","nextlist");
        }

        //change fragment
        ObserveAlarmReceiver.getInstance().triggerObservers(intent);

        Intent serviceIntent = new Intent(context, setListAlarm.class);
        context.startService(serviceIntent);

    }
    private String getUserID() {
        return "sherreld";
    }
}
