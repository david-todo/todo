package forgetmenot.todos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import forgetmenot.todos.Receivers.ObserveAlarmReceiver;
import forgetmenot.todos.database.ListHeaderInstTable;
import forgetmenot.todos.database.ListHeaderTable;
import forgetmenot.todos.databaseViewer.TodosDatabaseViewer;

public class TodosMainActivity extends FragmentActivity
    implements TodosMainNoActiveListFragment.OnFragmentInteractionListener,
    TodosMainActiveListFragment.OnFragmentInteractionListener, Observer {

    //private cursor
    private SimpleCursorAdapter adapter=null;
    private static TextView mNextListId;
    private static TextView mNextListStartTime;
    private static TextView mNextListStartDay;
    private static TextView mNextListName;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos_main);

        ObserveAlarmReceiver.getInstance().addObserver(this);

        getViews();


        //check that activty is using the version with the fragment container layout
        if (findViewById(R.id.todos_main_fragmentContainer) != null) {

            //check if we are being restored from previous state so we do not end up
            //with overlapping fragments
            if (savedInstanceState != null) {
                mNextListId.setText(savedInstanceState.getString("next" +
                        ListHeaderTable.COLUMN_LISTID));
                mNextListStartDay.setText(savedInstanceState.getString("nextstartday"));
                mNextListStartTime.setText(savedInstanceState.getString("next" +
                        ListHeaderTable.COLUMN_STARTTIME));
                mNextListName.setText(savedInstanceState.getString("next" +
                        ListHeaderTable.COLUMN_LISTNAME));
                return;
            }
            //only reset Alarms on first create, not if we are being restored.
            //reset Alarms with background service
            Intent serviceIntent = new Intent(this, setListAlarm.class);
            startService(serviceIntent);

        }

    }
    //Saved Instnace state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("next" + ListHeaderTable.COLUMN_LISTID,mNextListId.getText().toString());
        savedInstanceState.putString("next" + ListHeaderTable.COLUMN_STARTTIME,mNextListStartTime.getText().toString());
        savedInstanceState.putString("nextstartday",mNextListStartDay.getText().toString());
        savedInstanceState.putString("next" + ListHeaderTable.COLUMN_LISTNAME,mNextListName.getText().toString());

    }
    //Create menu
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //reaction to menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
              //  createTodo();
                return true;
            case R.id.db_view:
                open_db_view();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void open_db_view() {
        Intent i = new Intent(this, TodosDatabaseViewer.class);
        startActivity(i);
    }

    private void getViews(){
        mNextListId = (TextView)findViewById(R.id.todos_main_next_listid);
        mNextListStartTime = (TextView)findViewById(R.id.todos_main_next_time_val);
        mNextListStartDay = (TextView)findViewById(R.id.todos_main_next_day_val);
        mNextListName = (TextView)findViewById(R.id.todos_main_next_listname);
    }

    @Override
    public void onNoListFragmentInteraction(String s) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void dispActiveListFrag(int headinstid, int listid, String listname) {
        //Clear next list information
        mNextListId.setText(null);
        mNextListName.setText(null);
        mNextListStartDay.setText(null);
        mNextListStartTime.setText(null);

        //Set list information
        TodosMainActiveListFragment activelist =
                new TodosMainActiveListFragment().newInstance(headinstid, listid, listname);
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.todos_main_fragmentContainer,activelist);
        if (!isFinishing()) {
            transaction.commit();
        }
    }

    private void dispNoListFrag(ListInfo list) {
        //Set Next List
        if (list!=null) {
            mNextListId.setText(String.valueOf(list.listid));
            mNextListName.setText(list.listName);
            mNextListStartTime.setText(list.startTime);
            mNextListStartDay.setText(list.startDay);
        }

        TodosMainNoActiveListFragment nolist = new TodosMainNoActiveListFragment();

        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.replace(R.id.todos_main_fragmentContainer, nolist);
        if (!isFinishing()) {
            transaction.commit();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Intent i = (Intent) arg;
        String intentType = i.getExtras().getString("intenttype");
        int headerInstId = i.getExtras().getInt(ListHeaderInstTable.COLUMN_ID);

        if (intentType.equals("activelist")) {
            int listid = i.getExtras().getInt(ListHeaderTable.COLUMN_LISTID);
            String listname = i.getExtras().getString(ListHeaderTable.COLUMN_LISTNAME);
            dispActiveListFrag(headerInstId, listid, listname);
        }else { //no active list
            int nextListId = i.getExtras().getInt(ListHeaderTable.COLUMN_LISTID);
            if (nextListId == 0) {
                dispNoListFrag(null);
            }else {
                //build ListInfo
                ListInfo next = new ListInfo();
                next.listid = nextListId;
                next.listName = i.getExtras().getString(ListHeaderTable.COLUMN_LISTNAME);
                next.startTime = i.getExtras().getString(ListHeaderTable.COLUMN_STARTTIME);
                next.startDay = i.getExtras().getString("startday");
                dispNoListFrag(next);
            }
        }

    }
}
