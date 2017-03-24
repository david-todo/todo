package forgetmenot.todos.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.app.LauncherActivity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;

import forgetmenot.todos.database.ListHeaderInstTable;
import forgetmenot.todos.database.ListHeaderTable;
import forgetmenot.todos.database.ListItemInstTable;
import forgetmenot.todos.database.ListItemTable;
import forgetmenot.todos.database.TodoDatabaseHelper;
import forgetmenot.todos.database.UserTable;


/**
 * Created by SherrelD on 2/20/2017.
 */

public class MyTodoContentProvider extends ContentProvider {

    // database
    private TodoDatabaseHelper dbHelper;

    //used for uriMatcher
    private static final int USERS = 10;
    private static final int USER_ID = 20;
    private static final int LISTHEADERS = 30;
    private static final int LISTHEADER_ID = 40;
    private static final int LISTITEMS = 50;
    private static final int LISTITEM_ID = 60;
    private static final int LISTHEADERINSTS = 70;
    private static final int LISTHEADERINST_ID = 80;
    private static final int LISTITEMINSTS = 90;
    private static final int LISTITEMINST_ID = 100;

    //general path strings
    private static final String AUTHORITY = "forgetmenot.todos.contentprovider";
    private static final String BASE_PATH = "forgetmenot.todos";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //individual table URIs
    public static final Uri USERS_URI = Uri.parse(CONTENT_URI + "/" + UserTable.TABLE_NAME);
    public static final Uri LISTHEADER_URI = Uri.parse(CONTENT_URI + "/" +
            ListHeaderTable.TABLE_NAME);
    public static final Uri LISTHEADERINST_URI = Uri.parse(CONTENT_URI + "/" +
            ListHeaderInstTable.TABLE_NAME);
    public static final Uri LISTITEM_URI = Uri.parse(CONTENT_URI + "/" +
            ListItemTable.TABLE_NAME);
    public static final Uri LISTITEMINST_URI = Uri.parse(CONTENT_URI + "/" +
            ListItemInstTable.TABLE_NAME);

    public static final String CONTENT_USER_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
            UserTable.TABLE_NAME;
    public static final String CONTENT_USER_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
            UserTable.TABLE_NAME;
    public static final String CONTENT_LISTHEAD_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
            ListHeaderTable.TABLE_NAME;
    public static final String CONTENT_LISTHEAD_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
            ListHeaderTable.TABLE_NAME;
    public static final String CONTENT_LISTITEM_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/ " +
            ListItemTable.TABLE_NAME;
    public static final String CONTENT_LISTITEM_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
            ListItemTable.TABLE_NAME;
    public static final String CONTENT_LISTHEADINST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
            ListHeaderInstTable.TABLE_NAME;
    public static final String CONTENT_LISTHEADINST_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
            ListHeaderInstTable.TABLE_NAME;
    public static final String CONTENT_LISTITEMINST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
            ListItemInstTable.TABLE_NAME;
    public static final String CONTENT_LISTITEMINST_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
            ListItemInstTable.TABLE_NAME;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, UserTable.TABLE_NAME, USERS);
        sURIMatcher.addURI(AUTHORITY, UserTable.TABLE_NAME + "/#", USER_ID);
        sURIMatcher.addURI(AUTHORITY, ListHeaderTable.TABLE_NAME, LISTHEADERS);
        sURIMatcher.addURI(AUTHORITY, ListHeaderTable.TABLE_NAME + "/#", LISTHEADER_ID);
        sURIMatcher.addURI(AUTHORITY, ListItemTable.TABLE_NAME, LISTITEMS);
        sURIMatcher.addURI(AUTHORITY, ListItemTable.TABLE_NAME + "/#", LISTITEM_ID);
        sURIMatcher.addURI(AUTHORITY, ListHeaderInstTable.TABLE_NAME, LISTHEADERINSTS);
        sURIMatcher.addURI(AUTHORITY, ListHeaderInstTable.TABLE_NAME + "/#", LISTHEADERINST_ID);
        sURIMatcher.addURI(AUTHORITY, ListItemInstTable.TABLE_NAME, LISTITEMINSTS);
        sURIMatcher.addURI(AUTHORITY, ListItemInstTable.TABLE_NAME + "/#", LISTITEMINST_ID);
    }


    @Override
    public boolean onCreate() {
        dbHelper = new TodoDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //Check if caller has requested a column which does not exist
          checkColumns(uri, projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case USERS:
                queryBuilder.setTables(UserTable.TABLE_NAME);
                break;
            case USER_ID:
                queryBuilder.setTables(UserTable.TABLE_NAME);
                queryBuilder.appendWhere(UserTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case LISTHEADERS:
                queryBuilder.setTables(ListHeaderTable.TABLE_NAME);
                break;
            case LISTHEADER_ID:
                queryBuilder.setTables(ListHeaderTable.TABLE_NAME);
                queryBuilder.appendWhere(ListHeaderTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case LISTITEMS:
                queryBuilder.setTables(ListItemTable.TABLE_NAME);
                break;
            case LISTITEM_ID:
                queryBuilder.setTables(ListItemTable.TABLE_NAME);
                queryBuilder.appendWhere(ListItemTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case LISTHEADERINSTS:
                queryBuilder.setTables(ListHeaderInstTable.TABLE_NAME);
                break;
            case LISTHEADERINST_ID:
                queryBuilder.setTables(ListHeaderInstTable.TABLE_NAME);
                queryBuilder.appendWhere(ListHeaderInstTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case LISTITEMINSTS:
                queryBuilder.setTables(ListItemInstTable.TABLE_NAME);
                break;
            case LISTITEMINST_ID:
                queryBuilder.setTables(ListItemInstTable.TABLE_NAME);
                queryBuilder.appendWhere(ListItemInstTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;

    }
    private void checkColumns(Uri uri, String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String> (Arrays.asList(projection));

            int uriType = sURIMatcher.match(uri);
            HashSet<String> availableColumns;
            switch (uriType) {
                case USERS:
                    availableColumns = new HashSet<String>(Arrays.asList(UserTable.PROJECTION));
                    break;
                case USER_ID:
                    availableColumns = new HashSet<String>(Arrays.asList(UserTable.PROJECTION));
                    break;
                case LISTHEADERS:
                    availableColumns = new HashSet<String>(Arrays.asList(ListHeaderTable.PROJECTION));
                    break;
                case LISTHEADER_ID:
                    availableColumns = new HashSet<String>(Arrays.asList(ListHeaderTable.PROJECTION));
                    break;
                case LISTITEMS:
                    availableColumns = new HashSet<String>(Arrays.asList(ListItemTable.PROJECTION));
                    break;
                case LISTITEM_ID:
                    availableColumns = new HashSet<String>(Arrays.asList(ListItemTable.PROJECTION));
                    break;
                case LISTHEADERINST_ID:
                    availableColumns = new HashSet<String>(Arrays.asList(ListHeaderInstTable.PROJECTION));
                    break;
                case LISTHEADERINSTS:
                    availableColumns = new HashSet<String>(Arrays.asList(ListHeaderInstTable.PROJECTION));
                    break;
                case LISTITEMINSTS:
                    availableColumns = new HashSet<String>(Arrays.asList(ListItemInstTable.PROJECTION));
                    break;
                case LISTITEMINST_ID:
                    availableColumns = new HashSet<String>(Arrays.asList(ListItemInstTable.PROJECTION));
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }


            //check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columsn in projection");
            }
        }
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        switch (uriType) {
            case USERS:
                id = db.insert(UserTable.TABLE_NAME, null, values);
                break;
            case LISTHEADERS:
                id = db.insert(ListHeaderTable.TABLE_NAME, null, values);
                break;
            case LISTITEMS:
                id = db.insert(ListItemTable.TABLE_NAME, null, values);
                break;
            case LISTHEADERINSTS:
                id = db.insert(ListHeaderInstTable.TABLE_NAME, null, values);
                break;
            case LISTITEMINSTS:
                id = db.insert(ListItemInstTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String id;
        switch(uriType) {
            case USERS:
                rowsDeleted = db.delete(UserTable.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(UserTable.TABLE_NAME, UserTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsDeleted = db.delete(UserTable.TABLE_NAME, UserTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTHEADERS:
                rowsDeleted = db.delete(ListHeaderTable.TABLE_NAME, selection, selectionArgs);
                break;
            case LISTHEADER_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ListHeaderTable.TABLE_NAME, ListHeaderTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsDeleted = db.delete(ListHeaderTable.TABLE_NAME, ListHeaderTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTITEMS:
                rowsDeleted = db.delete(ListItemTable.TABLE_NAME, selection, selectionArgs);
                break;
            case LISTITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ListItemTable.TABLE_NAME, ListItemTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsDeleted = db.delete(ListItemTable.TABLE_NAME, ListItemTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTHEADERINSTS:
                rowsDeleted = db.delete(ListHeaderInstTable.TABLE_NAME, selection, selectionArgs);
                break;
            case LISTHEADERINST_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ListHeaderInstTable.TABLE_NAME, ListHeaderInstTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsDeleted = db.delete(ListHeaderInstTable.TABLE_NAME, ListHeaderInstTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTITEMINSTS:
                rowsDeleted = db.delete(ListItemInstTable.TABLE_NAME, selection, selectionArgs);
                break;
            case LISTITEMINST_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ListItemInstTable.TABLE_NAME, ListItemInstTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsDeleted = db.delete(ListItemInstTable.TABLE_NAME, ListItemInstTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;
        String id;

        switch (uriType) {
            case USERS:
                rowsUpdated = db.update(UserTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_ID:
                id  = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(UserTable.TABLE_NAME, values, ListHeaderTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsUpdated = db.update(UserTable.TABLE_NAME, values, ListHeaderTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTHEADERS:
                rowsUpdated = db.update(ListHeaderTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LISTHEADER_ID:
                id  = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ListHeaderTable.TABLE_NAME, values, ListHeaderTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsUpdated = db.update(ListHeaderTable.TABLE_NAME, values, ListHeaderTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTITEMS:
                rowsUpdated = db.update(ListItemTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LISTITEM_ID:
                id  = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ListItemTable.TABLE_NAME, values, ListItemTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsUpdated = db.update(ListItemTable.TABLE_NAME, values, ListItemTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTHEADERINSTS:
                rowsUpdated = db.update(ListHeaderInstTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LISTHEADERINST_ID:
                id  = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ListHeaderInstTable.TABLE_NAME, values, ListHeaderInstTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsUpdated = db.update(ListHeaderInstTable.TABLE_NAME, values, ListHeaderInstTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            case LISTITEMINSTS:
                rowsUpdated = db.update(ListItemInstTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LISTITEMINST_ID:
                id  = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ListItemInstTable.TABLE_NAME, values, ListItemInstTable.COLUMN_ID + " = "
                            + id, null);
                }else {
                    rowsUpdated = db.update(ListItemInstTable.TABLE_NAME, values, ListItemInstTable.COLUMN_ID + " = "
                            + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int uritype = sURIMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numInserted = 0;

        switch(uritype) {
            case LISTITEMINSTS:
                db.beginTransaction();
                try{
                    SQLiteStatement insert =
                            db.compileStatement("insert into " + ListItemInstTable.TABLE_NAME
                            + "(" + ListItemInstTable.COLUMN_HEADINST_ID + ","
                            + ListItemInstTable.COLUMN_LISTID + ","
                            + ListItemInstTable.COLUMN_ITEMNO + ","
                            + ListItemInstTable.COLUMN_ITEMDESC + ","
                            + ListItemInstTable.COLUMN_CONFIRM + ","
                            + ListItemInstTable.COLUMN_NOTIF_TYPE + ","
                            + ListItemInstTable.COLUMN_DIST_LIST + ","
                            + ListItemInstTable.COLUMN_STATUS + ","
                            + ListItemInstTable.COLUMN_COMPDATETIME + ")"
                            + " values " + "(?,?,?,?,?,?,?,?,?)");

                    for (ContentValues value : values) {
                        insert.bindString(1, value.getAsString(ListItemInstTable.COLUMN_HEADINST_ID));
                        insert.bindString(2, value.getAsString(ListItemInstTable.COLUMN_LISTID));
                        insert.bindString(3, value.getAsString(ListItemInstTable.COLUMN_ITEMNO));
                        insert.bindString(4, value.getAsString(ListItemInstTable.COLUMN_ITEMDESC));
                        insert.bindString(5, value.getAsString(ListItemInstTable.COLUMN_CONFIRM));
                        insert.bindString(6, value.getAsString(ListItemInstTable.COLUMN_NOTIF_TYPE));
                        insert.bindString(7, value.getAsString(ListItemInstTable.COLUMN_DIST_LIST));
                        insert.bindString(8, value.getAsString(ListItemInstTable.COLUMN_STATUS));
                        insert.bindString(9, value.getAsString(ListItemInstTable.COLUMN_COMPDATETIME));

                        insert.execute();
                    }
                    db.setTransactionSuccessful();
                    numInserted = values.length;


                }finally{
                    db.endTransaction();
                }
                return numInserted;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }
}
