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
    private TripListAdapter _adapter;

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

       // New shit
        FirebaseRecyclerOptions<UserTrip> options =
                new FirebaseRecyclerOptions.Builder<UserTrip>()
                .setQuery(_myTripsDatabaseReference, UserTrip.class)
                .build();

        _adapter = new TripListAdapter(options, "active", _tripsDatabaseReference, _navController);

        _myTripList.setAdapter(_adapter);
        _adapter.startListening();

        // Tab stuff
        _tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab == _tabLayout.getTabAt(0)){
                    Log.d("TRIPLIST FRAGMENT", "currTripTab clicked");
                    _adapter.set_tabState("active");
                    _adapter.notifyDataSetChanged();

                } else if (tab == _tabLayout.getTabAt(1)) {
                    Log.d("TRIPLIST FRAGMENT", "pastTripTab clicked");
                    _adapter.notifyDataSetChanged();
                    _adapter.set_tabState("inactive");
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
    }

    public static class TripsViewHolder extends RecyclerView.ViewHolder {

        TextView _textViewTripName;
        TextView _textViewStartLocation;
        TextView _textViewEndLocation;
        RelativeLayout _layoutTripListItem;
        String _tripID;

        public TripsViewHolder(@NonNull View itemView) {
            super(itemView);
            _textViewTripName = itemView.findViewById(R.id.textViewTripName);
            _textViewStartLocation= itemView.findViewById(R.id.textViewStartLocation);
            _textViewEndLocation = itemView.findViewById(R.id.textViewEndLocation);
            _layoutTripListItem= itemView.findViewById(R.id.layoutTripListItem);

        }


    }
}
