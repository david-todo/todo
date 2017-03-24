package forgetmenot.todos.databaseViewer;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.content.CursorLoader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import forgetmenot.todos.R;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.UserTable;

public class TodosDatabaseViewer extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    // private Cursor
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_dbviewer_user_list);
        this.getListView().setDividerHeight(2);
        fillUserData();

    }

    //Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.db_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Reaction to menu selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.AddUser:
                createUser();
                return true;
            case R.id.AddList:
                createList();
                return true;
            case R.id.DisplayList:
                openListHead();
                return true;
            case R.id.DispListHeadInst:
                openListHeadInst();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillUserData() {
        //Use projection for all columns for from paramater
        //label integers for to paramater of cursor adapter
        int[] to = new int[] {
                R.id.user_id,
                R.id.user_userid,
                R.id.user_name
        };

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.dbview_user_row, null,
                UserTable.PROJECTION, to, 0);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position, id);
        Intent i = new Intent(this,TodosEditUser.class);
        Uri userUri = Uri.parse(MyTodoContentProvider.USERS_URI + "/" + id);
        i.putExtra(MyTodoContentProvider.CONTENT_USER_ITEM_TYPE, userUri);

        startActivity(i);
    }
    private void createUser() {
        Intent i = new Intent(this, TodosEditUser.class);
        startActivity(i);
    }

    private void openListHead() {
        Intent i = new Intent(this, TodosListDisplayActivity.class);
        startActivity(i);
    }

    private void openListHeadInst() {
        Intent i = new Intent (this, TodosListInstDisplayActivity.class);
        startActivity(i);
    }

    private void createList() {
        Intent i = new Intent(this, TodosEditList.class);
        startActivity(i);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        CursorLoader cursorLoader = null;

        cursorLoader = new CursorLoader(this,
                        MyTodoContentProvider.USERS_URI, UserTable.PROJECTION, null, null, null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    //data is not available anymore, delete reference
        adapter.swapCursor(null);
    }
}
