package forgetmenot.todos.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by SherrelD on 2/20/2017.
 */

public class ListItemTable {
    //Database Table
    public static final String TABLE_NAME = "listitem";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LISTID = "listid";
    public static final String COLUMN_ITEMNO = "itemno";
    public static final String COLUMN_ITEMDESC = "itemdesc";
    public static final String COLUMN_CONFIRM = "confirm";
    public static final String COLUMN_NOTIF_TYPE = "notif_type";
    public static final String COLUMN_DIST_LIST = "dist_list";

    //all columns projection
    public static final String[] PROJECTION = {COLUMN_ID, COLUMN_LISTID,
            COLUMN_ITEMNO, COLUMN_ITEMDESC, COLUMN_CONFIRM, COLUMN_NOTIF_TYPE,
            COLUMN_DIST_LIST};

    //Database creation SQL Statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LISTID + " int not null, "
            + COLUMN_ITEMNO + " text not null, "
            + COLUMN_ITEMDESC + " text not null, "
            + COLUMN_CONFIRM + " text, "
            + COLUMN_NOTIF_TYPE + " text, "
            + COLUMN_DIST_LIST + " text, "
            + "FOREIGN KEY(" + COLUMN_LISTID + ") REFERENCES " + ListHeaderTable.TABLE_NAME +
            "(" + ListHeaderTable.COLUMN_LISTID + ")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ListItemTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
