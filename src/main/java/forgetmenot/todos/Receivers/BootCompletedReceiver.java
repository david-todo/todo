package forgetmenot.todos.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import forgetmenot.todos.setListAlarm;

/**
 * Created by SherrelD on 3/7/2017.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, setListAlarm.class);
        context.startService(serviceIntent);
    }
}
