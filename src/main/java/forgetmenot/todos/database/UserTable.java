package forgetmenot.todos.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by SherrelD on 2/20/2017.
 */

public class UserTable {
    //Databsase Table
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERID = "userid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERPIC = "userpic";

    //all columns projection
    public static final String[] PROJECTION = {COLUMN_ID, COLUMN_USERID,
            COLUMN_NAME, COLUMN_USERPIC};

    //Database creation SQL Statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USERID + " text not null, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_USERPIC + " text"  //userpic value can be null
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(UserTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
