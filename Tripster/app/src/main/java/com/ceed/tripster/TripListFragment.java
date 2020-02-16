package com.ceed.tripster;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripListFragment extends Fragment{

    private Context _context;
    private NavController _navController;
    private TabLayout _tabLayout;
    private View _tripListView;
    private RecyclerView _myTripList;

    private DatabaseReference _tripsDatabaseReference;
    private DatabaseReference _myTripsDatabaseReference;

    public TripListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        _tripListView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        _myTripList = _tripListView.findViewById(R.id.recyclerTripList);
        _myTripList.setLayoutManager(new LinearLayoutManager(_context));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        _tripsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
        _myTripsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User Trips").child(userId);

        return _tripListView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        _navController = Navigation.findNavController(view);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _navController.navigate(R.id.action_tripListFragment2_to_newTripFragment);
            }
        });

        //View rootView = inflater.inflate(R.layout.tabbar_layout, container, false);
        _tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab == _tabLayout.getTabAt(0)){
                    Log.d("TRIPLIST FRAGMENT", "currTripTab clicked");
                } else if (tab == _tabLayout.getTabAt(1)) {
                    Log.d("TRIPLIST FRAGMENT", "pastTripTab clicked");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
        _tabLayout.addOnTabSelectedListener(onTabSelectedListener);

       // New shit
        FirebaseRecyclerOptions<UserTrip> options =
                new FirebaseRecyclerOptions.Builder<UserTrip>()
                .setQuery(_myTripsDatabaseReference, UserTrip.class)
                .build();

        FirebaseRecyclerAdapter<UserTrip, TripsViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserTrip, TripsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final TripsViewHolder holder, int position, @NonNull UserTrip model) {
                        final String listTripId = getRef(position).getKey();
                        Log.d("Firebase", "ID: "+ listTripId);
                        // TODO: This is where the logic goes to check the state of each trip
                        DatabaseReference getStateRef = getRef(position).child("state").getRef();
                        getStateRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();
                                    if (TextUtils.equals(type, "active")) {
                                        _tripsDatabaseReference.child(listTripId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                final String tripName = dataSnapshot.child("name").getValue().toString();
                                                final String start = dataSnapshot.child("start").getValue().toString();
                                                final String destination = dataSnapshot.child("destination").getValue().toString();

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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        holder._layoutTripListItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                _navController.navigate(R.id.action_tripListFragment2_to_tripView);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public TripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_item, parent, false);
                        TripsViewHolder holder = new TripsViewHolder(view);

                        return holder;
                    }
                };

        _myTripList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder {

        TextView _textViewTripName;
        TextView _textViewStartLocation;
        TextView _textViewEndLocation;
        RelativeLayout _layoutTripListItem;

        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            _textViewTripName = itemView.findViewById(R.id.textViewTripName);
            _textViewStartLocation= itemView.findViewById(R.id.textViewStartLocation);
            _textViewEndLocation = itemView.findViewById(R.id.textViewEndLocation);
            _layoutTripListItem= itemView.findViewById(R.id.layoutTripListItem);
        }
    }
}
