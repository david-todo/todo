package forgetmenot.todos.databaseViewer;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import forgetmenot.todos.R;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderTable;

public class TodosListDisplayActivity extends ListActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    //private Cursor
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_dbviewer_listhead);
        this.getListView().setDividerHeight(2);
        fillData();
    }

    private void fillData() {
    //Use projection for all columns for from parameter
    //label integers for to parameter of cursoradapter
        int[] to = new int[] {
                R.id.listiteminst_dbid,
                R.id.listheadinst_userid,
                R.id.listiteminst_headerid,
                R.id.cons_listhead_listname,
                R.id.listiteminst_itemno,
                R.id.listiteminst_itemdesc,
                R.id.cons_listhead_alarm,
                R.id.listiteminst_notiftype,
                R.id.listiteminst_distlist,
                R.id.listiteminst_status,
                R.id.cons_listhead_wed,
                R.id.cons_listhead_thu,
                R.id.cons_listhead_fri,
                R.id.cons_listhead_sat
        };

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.dbview_listhead_row_cons, null,
                ListHeaderTable.PROJECTION,to,0);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
        Intent i = new Intent(this, TodosEditList.class);
        Uri listheadUri = Uri.parse(MyTodoContentProvider.LISTHEADER_URI + "/" + id);
        i.putExtra (MyTodoContentProvider.CONTENT_LISTHEAD_ITEM_TYPE, listheadUri);

        startActivity(i);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this,
                MyTodoContentProvider.LISTHEADER_URI, ListHeaderTable.PROJECTION, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
