package forgetmenot.todos.databaseViewer;


import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.CursorLoader;

import forgetmenot.todos.R;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListItemTable;

public class TodosItemActivity extends ListActivity
   implements LoaderManager.LoaderCallbacks<Cursor>  {

    private TextView mListid;
    private TextView mListname;
    private Button msaveBtn;
    private EditText mItemno;
    private EditText mItemdesc;
    private CheckBox mConfirm;
    private Spinner mNotif;
    private EditText mDist;
    private int listkey;
    private TextView mDbid;

    //private cursor adapter
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbview_list_item);

        mDbid = (TextView)findViewById(R.id.listitem_dbid);
        mListid = (TextView)findViewById(R.id.listid_val);
        mListname = (TextView)findViewById(R.id.listname_val);
        msaveBtn = (Button)findViewById(R.id.saveBtn);
        mItemno = (EditText)findViewById(R.id.item_edit_itemno);
        mItemdesc = (EditText)findViewById(R.id.item_desc_val);
        mConfirm = (CheckBox) findViewById(R.id.confirm_Chkbx);
        mNotif = (Spinner) findViewById(R.id.notif_type_spinner);
        mDist = (EditText)findViewById(R.id.dist_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String listid = extras.getString("listid");
            String listname = extras.getString("listname");
            mListid.setText(listid);
            mListname.setText(listname);
            listkey = Integer.parseInt(listid);
            fillListData();

        }

        msaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mItemno.getText().toString()) ||
                        TextUtils.isEmpty(mItemdesc.getText().toString())) {
                    makeToast();
                }else {
                    saveListItem();
                }
            }
        });

    }

    private void saveListItem() {
        String itemdesc = mItemdesc.getText().toString();
        String itemno = mItemno.getText().toString();
        String confirm;

        if (mConfirm.isChecked()) {
            confirm = "1";
        }else{
            confirm = "0";
        }

        String notif = mNotif.getSelectedItem().toString();
        String dist = mDist.getText().toString();
        //only save if itemno and itemdesc have value
        if (itemdesc.length() == 0 || itemno.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ListItemTable.COLUMN_LISTID, listkey);
        values.put(ListItemTable.COLUMN_ITEMNO, itemno);
        values.put(ListItemTable.COLUMN_ITEMDESC, itemdesc);
        values.put(ListItemTable.COLUMN_CONFIRM, confirm);
        values.put(ListItemTable.COLUMN_NOTIF_TYPE, notif);
        values.put(ListItemTable.COLUMN_DIST_LIST, dist);
        getContentResolver().insert(MyTodoContentProvider.LISTITEM_URI, values);
    }

    private void fillListData() {

        int[] to = new int[]{
                R.id.listiteminst_dbid,
                R.id.listheadinst_userid,
                R.id.listitem_itemno,
                R.id.listiteminst_headerid,
                R.id.listiteminst_itemno,
                R.id.listiteminst_itemdesc,
                R.id.listitem_dist
        };

        getLoaderManager().initLoader(0,null,this);
        adapter = new SimpleCursorAdapter(this, R.layout.dbview_listitem_row, null,
                ListItemTable.PROJECTION,to, 0);
        setListAdapter(adapter);
    }

    private void fillItemData() {

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        String whereclause = "listid=" + listkey;
        cursorLoader = new CursorLoader(this, MyTodoContentProvider.LISTITEM_URI,
                ListItemTable.PROJECTION, whereclause, null, null);
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

    private void makeToast() {
        Toast.makeText(TodosItemActivity.this,"Please maintain all required fields",
                Toast.LENGTH_LONG).show();
    }
}
