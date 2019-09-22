package com.tejo.android.bustracker;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tejo.android.bustracker.models.BusStop;
import com.tejo.android.bustracker.utils.Utils;

import java.util.ArrayList;


public class BusInfoFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "busType";
    public static final String TAG = "BusInfoFragment";
    private ArrayList<BusStop> mBusStops;


    //Bus type 1 is from Campus to Secunderabad
    //Bus type 2 is from Secunderabad to Campus
    private int mBusType;

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusType = getArguments().getInt(ARG_PARAM1);
        }
        Log.d(TAG, "mBusType Received: "+ mBusType);
        mBusStops = Utils.addBusStops(mBusType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bus_info, container, false);
        setTextViews(rootView);
        RecyclerView recyclerView = rootView.findViewById(R.id.bus_stop_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new StopsAdapter(mBusStops));
        recyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        return rootView;
    }

    private void setTextViews(View view) {
        TextView from = view.findViewById(R.id.from);
        TextView to = view.findViewById(R.id.to);
        if(mBusType==0) {
            from.setText(R.string.campus);
            to.setText(R.string.secunderabad);
        } else {
            from.setText(R.string.secunderabad);
            to.setText(R.string.campus);
        }
    }

}
