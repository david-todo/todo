package forgetmenot.todos;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import forgetmenot.todos.contentprovider.MyTodoContentProvider;
import forgetmenot.todos.database.ListHeaderTable;
import forgetmenot.todos.database.ListItemInstTable;


public class TodosMainActiveListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_HEADERID = "headerid";
    private static TextView mListName;
    private static ListView mListItem;
    private SimpleCursorAdapter adapter;

    private int headerInstId;
    private int listid;
    private String listname;

    private OnFragmentInteractionListener mListener;

    public TodosMainActiveListFragment() {
        // Required empty public constructor
    }

    public static TodosMainActiveListFragment newInstance(int headerid, int listid, String listname) {
        TodosMainActiveListFragment fragment = new TodosMainActiveListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HEADERID, headerid);
        args.putInt(ListHeaderTable.COLUMN_LISTID, listid);
        args.putString(ListHeaderTable.COLUMN_LISTNAME, listname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            headerInstId = getArguments().getInt(ARG_HEADERID);
            listid = getArguments().getInt(ListHeaderTable.COLUMN_LISTID);
            listname = getArguments().getString(ListHeaderTable.COLUMN_LISTNAME);

        }
    }
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(ListHeaderTable.COLUMN_LISTID, listid);
        savedInstanceState.putString(ListHeaderTable.COLUMN_LISTNAME,listname);
        savedInstanceState.putInt(ARG_HEADERID, headerInstId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        View fragview = inflater.inflate(R.layout.fragment_todos_main_active_list, container, false);
        mListName = (TextView) fragview.findViewById(R.id.todos_main_activelist_listname_val);
        mListItem = (ListView) fragview.findViewById(R.id.todos_main_activelist_list1);
        mListName.setText(listname);

        getLoaderManager().initLoader(0, getArguments(), this);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.todos_main_listiteminst_row, null,
                ListItemInstTable.PROJECTION,to,0);
        mListItem.setAdapter(adapter);

        mListItem.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String selection = ListItemInstTable.COLUMN_ID + "=?";
                        final String[] selectionArgs = new String[] {String.valueOf(id)};

                        String itemStatus = adapter.getCursor().getString(
                                adapter.getCursor().getColumnIndexOrThrow(
                                        ListItemInstTable.COLUMN_STATUS));

                        String itemConfirm = adapter.getCursor().getString(adapter.getCursor().getColumnIndexOrThrow(
                                ListItemInstTable.COLUMN_CONFIRM));

                        if (itemStatus.equals("Complete")) {
                            AlertDialog.Builder resetStatDialog = new AlertDialog.Builder(
                                    getActivity());
                            resetStatDialog.setTitle("Reset Item to Open");
                            resetStatDialog.setMessage("Item Already Marked Complete. " +
                                    "Reset to Open?");
                            resetStatDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ContentValues values = new ContentValues();
                                    values.put(ListItemInstTable.COLUMN_STATUS,"Open");
                                    values.put(ListItemInstTable.COLUMN_COMPDATETIME, "00000000 00:00");
                                    int rowsupdatedOpen = getActivity().getContentResolver().update(
                                            MyTodoContentProvider.LISTITEMINST_URI,values,selection,selectionArgs);

                                    if (rowsupdatedOpen != 0) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            resetStatDialog.show();
                        }else { //Item status is open
                            if (itemConfirm != null) {
                                if (itemConfirm.equals("1")) {
                                    //Confirm Dialog
                                    AlertDialog.Builder confirmDialog = new AlertDialog.Builder(
                                            getActivity());
                                    confirmDialog.setTitle("Confirm Item Completion");

                                    confirmDialog.setMessage("Complete Item XXX?");

                                    confirmDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    confirmDialog.show();
                                }
                            }
                            ContentValues values = new ContentValues();
                            values.put(ListItemInstTable.COLUMN_STATUS,"Complete");

                            Calendar complCal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm");
                            String nowTime = sdf.format(complCal.getTime());

                            values.put(ListItemInstTable.COLUMN_COMPDATETIME,nowTime);
                            int rowsupdatedComp = getActivity().getContentResolver().update(
                                    MyTodoContentProvider.LISTITEMINST_URI,values,selection,selectionArgs);

                            if (rowsupdatedComp != 0) {
                                adapter.notifyDataSetChanged();
                            }

                        }

                    }
                }
        );
        return fragview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String whereclause =  ListItemInstTable.COLUMN_HEADINST_ID + "=" + headerInstId;
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                MyTodoContentProvider.LISTITEMINST_URI, ListItemInstTable.PROJECTION, whereclause, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
