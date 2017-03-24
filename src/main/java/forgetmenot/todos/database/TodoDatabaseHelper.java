package forgetmenot.todos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SherrelD on 2/20/2017.
 */

public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todos.db";
    private static final int DATABASE_VERSION = 8;

    public TodoDatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Method is called during creation of database
    @Override
    public void onCreate(SQLiteDatabase db) {
        UserTable.onCreate(db);
        ListHeaderTable.onCreate(db);
        ListItemTable.onCreate(db);
        ListHeaderInstTable.onCreate(db);
        ListItemInstTable.onCreate(db);
    }

    //Method is called during an upgrade of the database
    //e.g. you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        UserTable.onUpgrade(db,oldVersion,newVersion);
        ListHeaderTable.onUpgrade(db,oldVersion,newVersion);
        ListItemTable.onUpgrade(db,oldVersion,newVersion);
        ListHeaderInstTable.onUpgrade(db,oldVersion,newVersion);
        ListItemInstTable.onUpgrade(db,oldVersion,newVersion);
    }
}
