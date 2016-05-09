package com.example.sysucjl.familytelephonedirectory.fragment;

/**
 * Created by sysucjl on 16-3-31.
 */

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.sysucjl.familytelephonedirectory.R;
import com.example.sysucjl.familytelephonedirectory.adapter.RecordExpandAdapter;
import com.example.sysucjl.familytelephonedirectory.tools.ContactOptionManager;


public class RecordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * The fragment's ListView/GridView.
     */
    private android.support.v7.widget.RecyclerView mRecyclerView;
    private ExpandableListView expandableListView;
    //private RecyclerView.Adapter mAdapter;
    private RecordExpandAdapter mExpandAdapter;
    private int mLastGroupPosition = 0;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */

    // TODO: Rename and change types of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        // Set the adapter
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
//        mRecyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setAdapter(mAdapter);

        final View view = inflater.inflate(R.layout.fragment_record, container, false);
        expandableListView = (ExpandableListView) view.findViewById(R.id.explv_record);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            expandableListView.setNestedScrollingEnabled(true);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ContactOptionManager tool = new ContactOptionManager();
        mExpandAdapter = new RecordExpandAdapter(getContext(), tool.getCallLog(getContext()));
        expandableListView.setAdapter(mExpandAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (mLastGroupPosition != groupPosition) {
                    expandableListView.collapseGroup(mLastGroupPosition);
                }
                mLastGroupPosition = groupPosition;
            }
        });
        mExpandAdapter.setOnRecordAdapterListener(new RecordExpandAdapter.RecordAdapterListener() {
            @Override
            public void collapseGroup(int groupPosition) {
                expandableListView.collapseGroup(groupPosition);
            }

            @Override
            public void expandGroup(int groupPosition){
                expandableListView.expandGroup(groupPosition);
            }

            @Override
            public void myNotifyDataSetChanged(int groupPosition) {
                mExpandAdapter.notifyDataSetChanged();
                expandableListView.expandGroup(groupPosition);
                expandableListView.collapseGroup(groupPosition);
            }
        });
    }
}