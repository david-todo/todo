package forgetmenot.todos.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by SherrelD on 2/20/2017.
 */

public class ListHeaderInstTable {
    //Databsase Table
    public static final String TABLE_NAME = "listheaderinst";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERID = "userid";
    public static final String COLUMN_LISTID = "listid";
    public static final String COLUMN_STARTDATETIME = "start_datetime";
    public static final String COLUMN_STARTDAY = "start_day";
    public static final String COLUMN_OVERALLSTAT = "overall_stat";

    //all columns projection
    public static final String[] PROJECTION = {COLUMN_ID, COLUMN_USERID,
            COLUMN_LISTID, COLUMN_STARTDATETIME, COLUMN_STARTDAY, COLUMN_OVERALLSTAT};

    //Database creation SQL Statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USERID + " text not null, "
            + COLUMN_LISTID + " int not null, "
            + COLUMN_STARTDATETIME + " text not null,"
            + COLUMN_STARTDAY + " text not null,"
            + COLUMN_OVERALLSTAT + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ListHeaderInstTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
