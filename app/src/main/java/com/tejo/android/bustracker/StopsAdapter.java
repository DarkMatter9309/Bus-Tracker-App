package com.tejo.android.bustracker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tejo.android.bustracker.models.BusStop;

import java.util.ArrayList;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.StopsHolder> {
    private ArrayList<BusStop> mBusStops;

    public StopsAdapter(ArrayList<BusStop> busStops){
        mBusStops = busStops;
    }

    @NonNull
    @Override
    public StopsAdapter.StopsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bus_stop_item,
                viewGroup, false);
        StopsHolder stopsHolder = new StopsHolder(v);
        return stopsHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StopsAdapter.StopsHolder stopsHolder, int i) {
        stopsHolder.mTextView.setText(mBusStops.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return mBusStops.size();
    }

    public static class StopsHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public StopsHolder(View view){
            super(view);
            mTextView = view.findViewById(R.id.bus_stop_name);
        }
    }
}
