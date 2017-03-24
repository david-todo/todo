package forgetmenot.todos.databaseViewer;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import forgetmenot.todos.R;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderTable;

public class TodosEditList extends Activity {

    private TextView mdbid;
    private EditText mListid;
    private EditText mUserid;
    private EditText mListname;
    private EditText mDeadline;
    private EditText mStarttime;
    private Spinner mAlarm_type;
    private CheckBox mDue_sun;
    private CheckBox mDue_mon;
    private CheckBox mDue_tue;
    private CheckBox mDue_wed;
    private CheckBox mDue_fri;
    private CheckBox mDue_sat;
    private CheckBox mDue_thu;
    private Uri listHeadUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dbview_list_edit);

        mdbid = (TextView) findViewById(R.id.dbid_txt);
        mListid = (EditText) findViewById(R.id.list_edit_listid);
        mUserid = (EditText)findViewById(R.id.list_edit_userid_val);
        mListname = (EditText) findViewById(R.id.list_edit_listname);
        mDeadline = (EditText) findViewById(R.id.deadline_edit);
        mStarttime = (EditText) findViewById(R.id.start_time_edit);
        mAlarm_type = (Spinner) findViewById(R.id.alarm_type_spinner);
        mDue_sun = (CheckBox) findViewById(R.id.due_sun_chbx);
        mDue_mon = (CheckBox) findViewById(R.id.due_mon_chbx);
        mDue_tue = (CheckBox) findViewById(R.id.due_tue_chbx);
        mDue_wed = (CheckBox) findViewById(R.id.due_wed_chbx);
        mDue_thu = (CheckBox) findViewById(R.id.due_thu_chbx);
        mDue_fri = (CheckBox) findViewById(R.id.due_fri_chbx);
        mDue_sat = (CheckBox) findViewById(R.id.due_sat_chbx);

        Button saveBtn = (Button) findViewById(R.id.save_list_btn);
        Button itemBtn = (Button) findViewById(R.id.new_item_btn);

        Bundle extras = getIntent().getExtras();

        //check from saved Instance
        listHeadUri = (bundle == null) ? null :
                (Uri) bundle.getParcelable(MyTodoContentProvider.CONTENT_LISTHEAD_ITEM_TYPE);

        //or passed from other activity
        if (extras != null) {
            listHeadUri = extras.getParcelable(MyTodoContentProvider.CONTENT_LISTHEAD_ITEM_TYPE);
            fillData(listHeadUri);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mListid.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String listid_lbl = "listid";
                 String listname_lbl = "listname";
                 Activity myactivity = (Activity) v.getContext();
                 Intent i = new Intent(myactivity, TodosItemActivity.class);
                 i.putExtra(listid_lbl, mListid.getText().toString());
                 i.putExtra(listname_lbl, mListname.getText().toString());
                 startActivity(i);
                 }
            }
        );
    }
    private void fillData(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, ListHeaderTable.PROJECTION, null, null, null);

        if (cursor!=null) {
            cursor.moveToFirst();
            mdbid.setText(cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_ID)));
            mUserid.setText(cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_USERID)));
            mListid.setText(cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_LISTID)));
            mListname.setText(cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_LISTNAME)));
            mDeadline.setText(cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DEADLINE)));
            mStarttime.setText(cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_STARTTIME)));
            mAlarm_type = (Spinner) findViewById(R.id.alarm_type_spinner);
            String spinnerValue = cursor.getString(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_ALARMTYPE));
            mAlarm_type.setSelection(getIndex(mAlarm_type, spinnerValue));
            mDue_sun = (CheckBox) findViewById(R.id.due_sun_chbx);
            setCheckVal(mDue_sun, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_SUN)));
            mDue_mon = (CheckBox) findViewById(R.id.due_mon_chbx);
            setCheckVal(mDue_mon, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_MON)));
            mDue_tue = (CheckBox) findViewById(R.id.due_tue_chbx);
            setCheckVal(mDue_tue, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_TUE)));
            mDue_wed = (CheckBox) findViewById(R.id.due_wed_chbx);
            setCheckVal(mDue_wed, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_WED)));
            mDue_thu = (CheckBox) findViewById(R.id.due_thu_chbx);
            setCheckVal(mDue_thu, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_THU)));
            mDue_fri = (CheckBox) findViewById(R.id.due_fri_chbx);
            setCheckVal(mDue_fri, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_FRI)));
            mDue_sat = (CheckBox) findViewById(R.id.due_sat_chbx);
            setCheckVal(mDue_sat, cursor.getInt(cursor.getColumnIndexOrThrow(ListHeaderTable.COLUMN_DUE_SAT)));

            cursor.close();
        }
    }

    private void setCheckVal(CheckBox c, int i){
        if (i==1) {
            c.setChecked(true);
        }else {
            c.setChecked(false);
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyTodoContentProvider.CONTENT_LISTHEAD_ITEM_TYPE, listHeadUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String name = mListname.getText().toString();
        int listid;
        if (mListid.getText().toString() == null) {
            listid = 0;
        }else{
            listid = Integer.parseInt(mListid.getText().toString());
        }
        String userid = mUserid.getText().toString();
        String start = mStarttime.getText().toString();
        String deadline = mDeadline.getText().toString();
        String alarm = mAlarm_type.getSelectedItem().toString();
        int sun = readCheckbox(mDue_sun);
        int mon = readCheckbox(mDue_mon);
        int tue = readCheckbox(mDue_tue);
        int wed = readCheckbox(mDue_wed);
        int thu = readCheckbox(mDue_thu);
        int fri = readCheckbox(mDue_fri);
        int sat = readCheckbox(mDue_sat);

        //only save if listid and list name have value
        if (name.length() == 0 || listid == 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(ListHeaderTable.COLUMN_LISTNAME, name);
        values.put(ListHeaderTable.COLUMN_LISTID, listid);
        values.put(ListHeaderTable.COLUMN_USERID, userid);
        values.put(ListHeaderTable.COLUMN_STARTTIME, start);
        values.put(ListHeaderTable.COLUMN_DEADLINE,deadline);
        values.put(ListHeaderTable.COLUMN_ALARMTYPE, alarm);
        values.put(ListHeaderTable.COLUMN_DUE_SUN, sun);
        values.put(ListHeaderTable.COLUMN_DUE_MON, mon);
        values.put(ListHeaderTable.COLUMN_DUE_TUE, tue);
        values.put(ListHeaderTable.COLUMN_DUE_WED, wed);
        values.put(ListHeaderTable.COLUMN_DUE_THU, thu);
        values.put(ListHeaderTable.COLUMN_DUE_FRI, fri);
        values.put(ListHeaderTable.COLUMN_DUE_SAT, sat);

//* Add all the other values here

        if (listHeadUri == null) {
            //new list
            listHeadUri = getContentResolver().insert(MyTodoContentProvider.LISTHEADER_URI, values);
        }else {
            //Update list
            getContentResolver().update(listHeadUri, values, null, null);
        }
    }

    private int readCheckbox(CheckBox c) {
        if (c.isChecked()) {
            return 1;
        }else{
            return 0;
        }
    }

    private void makeToast() {
        Toast.makeText(TodosEditList.this,"Please maintain all required fields",
                Toast.LENGTH_LONG).show();
    }
}

