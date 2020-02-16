package com.ceed.tripster;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class TripListAdapter extends FirebaseRecyclerAdapter<UserTrip, TripListFragment.TripsViewHolder> {

    private String _tabState;
    private DatabaseReference _tripsDatabaseReference;
    private NavController _navController;

    public TripListAdapter(FirebaseRecyclerOptions<UserTrip> options, String tabState, DatabaseReference tripsDatabaseReference, NavController navController){
        super(options);

        this._tabState = tabState;
        this._tripsDatabaseReference = tripsDatabaseReference;
        this._navController = navController;
    }

    @Override
    protected void onBindViewHolder(@NonNull final TripListFragment.TripsViewHolder holder, int position, @NonNull UserTrip model) {
        final String listTripId = getRef(position).getKey();
        Log.d("Firebase", "ID: "+ listTripId);
        // TODO: This is where the logic goes to check the state of each trip
        DatabaseReference getStateRef = getRef(position).child("state").getRef();
        getStateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder._layoutTripListItem.setVisibility(View.VISIBLE);
                    holder._layoutTripListItem.getChildAt(0).setVisibility(View.VISIBLE);
                    String type = dataSnapshot.getValue().toString();
                    Log.d("Firebase", "snapshot type: " + type);
                    Log.d("Firebase", "tab state: " + _tabState);
                    if (TextUtils.equals(type, _tabState)) {
                        Log.d("Firebase", "tab state == snapshot type");
                        _tripsDatabaseReference.child(listTripId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String tripName = dataSnapshot.child("name").getValue().toString();
                                final String start = dataSnapshot.child("start").getValue().toString();
                                final String destination = dataSnapshot.child("destination").getValue().toString();

                                holder._tripID = listTripId;
                                holder._textViewTripName.setText(tripName);
                                holder._textViewStartLocation.setText(start);
                                holder._textViewEndLocation.setText(destination);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("Firebase", "Error: " + databaseError.getMessage());
                            }
                        });
                    }
                    else {
                        Log.d("Debug", "Making the holder gone");
                        holder._layoutTripListItem.setVisibility(View.GONE);
                        holder._layoutTripListItem.getChildAt(0).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder._layoutTripListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TRIPLIST FRAGMENT", "onClick: " + holder._tripID);
                TripListFragmentDirections.ActionTripListFragment2ToTripView action = TripListFragmentDirections.actionTripListFragment2ToTripView(holder._tripID);
                //action.setMessage(holder._tripID);
                _navController.navigate(action);
            }
        });

    }

    @NonNull
    @Override
    public TripListFragment.TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
        TripListFragment.TripsViewHolder holder = new TripListFragment.TripsViewHolder(view);

        return holder;
    }

    public String get_tabState() {
        return _tabState;
    }

    public void set_tabState(String _tabState) {
        this._tabState = _tabState;
    }

}
