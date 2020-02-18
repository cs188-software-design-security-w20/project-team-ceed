package com.ceed.tripster;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItineraryAdapter extends FirebaseRecyclerAdapter<Stop, TripViewActivity.StopsViewHolder> {
    private DatabaseReference _tripStopsDatabaseReference;
    private String _tripId;

    public interface DeleteStopCallBack {
        void onStopDeleted(String placeId);
    }

    private DeleteStopCallBack _deleteStopCallBack;




    public ItineraryAdapter(FirebaseRecyclerOptions<Stop> options, String tripId,
                            DatabaseReference tripStopsDatabaseReference,
                            DeleteStopCallBack deleteStopCallBack){
        super(options);

        this._tripStopsDatabaseReference = tripStopsDatabaseReference;
        _tripId = tripId;
        _deleteStopCallBack = deleteStopCallBack;
    }

    @Override
    protected void onBindViewHolder(@NonNull final TripViewActivity.StopsViewHolder holder, int position, @NonNull Stop model) {
        final String listStopId = getRef(position).getKey();
        Log.d("Firebase", "ID: "+ listStopId);
        _tripStopsDatabaseReference.orderByChild("index").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child(listStopId).child("name").getValue() != null) {
                    final String stopName = dataSnapshot.child(listStopId).child("name").getValue().toString();
                    final String type = dataSnapshot.child(listStopId).child("type").getValue().toString();
                    final String stopAddress = dataSnapshot.child(listStopId).child("address").getValue().toString();

                    holder._textViewStopName.setText(stopName);
                    holder._textViewStopAddress.setText(stopAddress);
                    holder._placeId = listStopId;

                    if (TextUtils.equals(type, "start")) {
                        holder._textViewStopType.setText("Start Stop");
                        holder._textViewStopType.setVisibility(View.VISIBLE);
                        holder._imageButton.setVisibility(View.GONE);
                    }
                    else if (TextUtils.equals(type, "end")) {
                        holder._textViewStopType.setText("End Stop");
                        holder._textViewStopType.setVisibility(View.VISIBLE);
                        holder._imageButton.setVisibility(View.GONE);
                    } else {
                        holder._textViewStopType.setText("");
                        holder._textViewStopType.setVisibility(View.GONE);
                        holder._imageButton.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", databaseError.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public TripViewActivity.StopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_item, parent, false);

        TripViewActivity.StopsViewHolder holder =
                new TripViewActivity.StopsViewHolder(view, _deleteStopCallBack);

        return holder;
    }

    /*
    private List<String> _data;
    private LayoutInflater _inflater;
    private ItemClickListener _clickListener;

    // data is passed into the constructor
    ItineraryAdapter(Context context, List<String> data) {
        this._inflater = LayoutInflater.from(context);
        this._data = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = _inflater.inflate(R.layout.itinerary_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text = _data.get(position);
        holder.myTextView.setText(text);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return _data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.layoutItineraryItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (_clickListener != null) {
                _clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return _data.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this._clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    */
}
