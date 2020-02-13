package com.ceed.tripster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder>{
    private static final String TAG = "TripListAdapter";

    private Context mContext;
    private ArrayList<String> mTripNames = new ArrayList<>();
    private NavController mNavController;

    public TripListAdapter(Context context, ArrayList<String> tripNames, NavController navController){
        this.mContext = context;
        this.mTripNames = tripNames;
        this.mNavController = navController;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripListAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHoler called");
        holder.tripName.setText(mTripNames.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavController.navigate(R.id.action_tripListFragment2_to_tripView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTripNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tripName;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.trip_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
