package forgetmenot.todos.Receivers;

import android.content.Intent;

import java.util.Observable;

/**
 * Created by SherrelD on 3/21/2017.
 */

public class ObserveAlarmReceiver extends Observable {
    private static ObserveAlarmReceiver instance = new ObserveAlarmReceiver();

    public static ObserveAlarmReceiver getInstance() {
        return instance;
    }

    private ObserveAlarmReceiver() {
    }

    public void triggerObservers(Intent intent) {
        setChanged();
        notifyObservers(intent);
    }

}
