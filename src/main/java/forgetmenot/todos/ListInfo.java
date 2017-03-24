package forgetmenot.todos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SherrelD on 3/7/2017.
 */

public class ListInfo {
    public int listid;
    public String listName;
    public String startTime;
    public String startDay;
    public String deadlineTime;
    public ListInfo() {
        startTime = null;
        startDay = null;
        listid = 0;
        listName = null;
        deadlineTime = null;
    };
    public ListInfo(int id, String name, String startday, String time, String deadline) {
        startTime = time;
        startDay = startday;
        listid = id;
        listName = name;
        deadlineTime = deadline;
    }
}
