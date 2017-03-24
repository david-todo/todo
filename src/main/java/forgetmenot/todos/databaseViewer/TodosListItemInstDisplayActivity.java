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
import forgetmenot.todos.database.ListItemInstTable;

public class TodosListItemInstDisplayActivity extends ListActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    //private Cursor
    private SimpleCursorAdapter adapter;
    private Uri itemInstUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_dbviewer_listitem_inst);
        this.getListView().setDividerHeight(2);

        //check from saved Instance
        itemInstUri = (savedInstanceState == null) ? null :
                (Uri)savedInstanceState.getParcelable(MyTodoContentProvider.CONTENT_LISTITEMINST_ITEM_TYPE);

        Bundle extras = getIntent().getExtras();
        //or passed from other activity
        if (extras != null) {
            itemInstUri = extras.getParcelable(MyTodoContentProvider.CONTENT_LISTITEMINST_ITEM_TYPE);
            fillData();
        }else {
            fillData();
        }
    }

    private void fillData() {
    //Use projection for all columns for from parameter
    //label integers for to parameter of cursoradapter
        int[] to = new int[] {
                R.id.listiteminst_dbid,
                R.id.listiteminst_headerid,
                R.id.listiteminst_listid,
                R.id.listiteminst_itemno,
                R.id.listiteminst_itemdesc,
                R.id.listiteminst_confirm,
                R.id.listiteminst_notiftype,
                R.id.listiteminst_distlist,
                R.id.listiteminst_status,
                R.id.listiteminst_compdatetime
        };

        getLoaderManager().initLoader(0,null,this);
        adapter = new SimpleCursorAdapter(this, R.layout.dbview_listiteminst_row, null,
                ListItemInstTable.PROJECTION,to,0);
        setListAdapter(adapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this,
                MyTodoContentProvider.LISTITEMINST_URI, ListItemInstTable.PROJECTION, null, null, null);
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
