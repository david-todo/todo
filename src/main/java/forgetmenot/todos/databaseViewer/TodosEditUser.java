package forgetmenot.todos.databaseViewer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import forgetmenot.todos.R;
import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.UserTable;

public class TodosEditUser extends Activity {

    private EditText mUserid;
    private EditText mName;
    private TextView id;
    private Uri todoUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dbview_user_edit);

        mUserid = (EditText)findViewById(R.id.user_edit_userid);
        mName = (EditText) findViewById(R.id.user_edit_name);
        id = (TextView)findViewById(R.id.user_edit_id);

        Button confirmButton = (Button) findViewById(R.id.user_edit_button);

        Bundle extras = getIntent().getExtras();

        //check from saved Instance
        todoUri = (bundle==null) ? null:
                (Uri)bundle.getParcelable(MyTodoContentProvider.CONTENT_USER_ITEM_TYPE);

        //or passed from other activity
        if (extras!=null) {
            todoUri = extras.getParcelable(MyTodoContentProvider.CONTENT_USER_ITEM_TYPE);
            fillData(todoUri);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mUserid.getText().toString())) {
                    makeToast();
                }else{
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
    private void fillData(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, UserTable.PROJECTION, null, null, null);

        if (cursor!=null) {
            cursor.moveToFirst();
            mUserid.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_USERID)));
            mName.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME)));
            id.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_ID)));
        }
        cursor.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String name = (String)mName.getText().toString();
        String userid = (String)mUserid.getText().toString();

        //only save if userid and name have values
        if (name.length() == 0 || userid.length() == 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_NAME, name);
        values.put(UserTable.COLUMN_USERID, userid);

        if (todoUri == null) {
            //new user
            todoUri = getContentResolver().insert(MyTodoContentProvider.USERS_URI, values);
        }else {
            //Update user
            getContentResolver().update(todoUri, values, null, null);
        }
    }
    private void makeToast() {
        Toast.makeText(TodosEditUser.this,"Please maintain all required fields",
                Toast.LENGTH_LONG).show();
    }
}
