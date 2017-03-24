package forgetmenot.todos.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by SherrelD on 2/20/2017.
 */

public class ListHeaderTable {
    //Database Table
    public static final String TABLE_NAME = "listheader";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERID = "userid";
    public static final String COLUMN_LISTID = "listid";
    public static final String COLUMN_LISTNAME = "listname";
    public static final String COLUMN_STARTTIME = "start_time";
    public static final String COLUMN_DEADLINE = "deadline_time";
    public static final String COLUMN_ALARMTYPE = "alarm_type";
    public static final String COLUMN_DUE_MON = "due_mon";
    public static final String COLUMN_DUE_TUE = "due_tue";
    public static final String COLUMN_DUE_WED = "due_wed";
    public static final String COLUMN_DUE_THU = "due_thu";
    public static final String COLUMN_DUE_FRI = "due_fri";
    public static final String COLUMN_DUE_SAT = "due_sat";
    public static final String COLUMN_DUE_SUN = "due_sun";


    //all columns projection
    public static final String[] PROJECTION = {COLUMN_ID, COLUMN_USERID, COLUMN_LISTID,
            COLUMN_LISTNAME, COLUMN_STARTTIME, COLUMN_DEADLINE, COLUMN_ALARMTYPE,
            COLUMN_DUE_SUN, COLUMN_DUE_MON, COLUMN_DUE_TUE, COLUMN_DUE_WED,
            COLUMN_DUE_THU, COLUMN_DUE_FRI, COLUMN_DUE_SAT
    };

    //Database creation SQL Statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USERID + " text,"
            + COLUMN_LISTID + " int not null unique, "
            + COLUMN_LISTNAME + " text, "
            + COLUMN_STARTTIME + " text, "
            + COLUMN_DEADLINE + " text, "
            + COLUMN_ALARMTYPE + " text, "
            + COLUMN_DUE_SUN + " int, "
            + COLUMN_DUE_MON + " int, "
            + COLUMN_DUE_TUE + " int, "
            + COLUMN_DUE_WED + " int, "
            + COLUMN_DUE_THU + " int, "
            + COLUMN_DUE_FRI + " int, "
            + COLUMN_DUE_SAT + " int, "
            + "FOREIGN KEY(" + COLUMN_USERID + ") REFERENCES " + UserTable.TABLE_NAME + "(" +
            UserTable.COLUMN_USERID + ")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ListHeaderTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
