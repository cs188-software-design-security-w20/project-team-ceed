package com.ceed.tripster;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ItineraryAdapter extends FirebaseRecyclerAdapter<Stop, TripViewActivity.StopsViewHolder> {
    private DatabaseReference _tripStopsDatabaseReference;


    public interface OnItemClickedCallBack {
        void onStopDeleted(String placeId);
        void onItemClicked(String placeId);
    }

    private OnItemClickedCallBack _onItemClickedCallBack;




    public ItineraryAdapter(FirebaseRecyclerOptions<Stop> options,
                            DatabaseReference tripStopsDatabaseReference,
                            OnItemClickedCallBack onItemClickedCallBack){
        super(options);

        this._tripStopsDatabaseReference = tripStopsDatabaseReference;
        _onItemClickedCallBack = onItemClickedCallBack;
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
                new TripViewActivity.StopsViewHolder(view, _onItemClickedCallBack);
        return holder;
    }


}
