package com.example.shanu.mode;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.shanu.mode.data.InventoryDbHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class listFragment extends Fragment {

    private final static String LOG_TAG = ListActivity.class.getCanonicalName();
    InventoryDbHelper dbHelper;
    StockCursorAdapter adapter;
    int lastVisibleItem = 0;
    public listFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        dbHelper = new InventoryDbHelper(getActivity());
//
//
//
//        final ListView listView = (ListView) getView().findViewById(R.id.list_view);
//        View emptyView = getView().findViewById(R.id.empty_view);
//        listView.setEmptyView(emptyView);
//
//        Cursor cursor = dbHelper.readStock();
//
//        adapter = new StockCursorAdapter(getActivity(), cursor);
//        listView.setAdapter(adapter);
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if(scrollState == 0) return;
//                final int currentFirstVisibleItem = view.getFirstVisiblePosition();
//                lastVisibleItem = currentFirstVisibleItem;
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);

    }

}
