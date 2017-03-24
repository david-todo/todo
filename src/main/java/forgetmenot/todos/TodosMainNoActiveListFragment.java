package forgetmenot.todos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TodosMainNoActiveListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TodosMainNoActiveListFragment() {
        // Required empty public constructor
    }

    public static TodosMainNoActiveListFragment newInstance() {
        TodosMainNoActiveListFragment fragment = new TodosMainNoActiveListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragview = inflater.inflate(R.layout.fragment_todos_main_no_active_list, container, false);
        return fragview;
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

    public interface OnFragmentInteractionListener {
        void onNoListFragmentInteraction(String s);
    }
}
