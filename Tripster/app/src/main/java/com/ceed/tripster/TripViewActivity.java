package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TripViewActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap _map;
    private BottomSheetBehavior _bottomSheetBehavior;
    private ItineraryAdapter _adapter;
    private DatabaseReference _tripDatabaseReference;
    private DatabaseReference _userTripDatabaseReference;
    private DatabaseReference _tripStopsDatabaseReference;
    private String _tripId;
    private TextView _textViewTripName;
    private TextView _textViewStartLocation;
    private TextView _textViewEndLocation;
    private RecyclerView _itineraryStops;
    private FirebaseAuth _firebaseAuth;

    private DatabaseReference _databaseRoot;
    String _userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the tripId
        _tripId = TripViewActivityArgs.fromBundle(getIntent().getExtras()).getTripID();

        _firebaseAuth = FirebaseAuth.getInstance();
        _databaseRoot = FirebaseDatabase.getInstance().getReference();

        _userId = _firebaseAuth.getCurrentUser().getUid();

        // Initialize the database references
        // _userTripDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User Trips").child(_userId).child(_tripId).child("state");
        _userTripDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User Trips").child(_userId);
        _tripDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trips").child(_tripId);
        _tripStopsDatabaseReference = _tripDatabaseReference.child("stops");


        setContentView(R.layout.activity_trip_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        _itineraryStops = findViewById(R.id.itineraryRecyclerView);
        _textViewTripName = findViewById(R.id.itineraryTextViewTripName);
        _textViewStartLocation = findViewById(R.id.itineraryTextViewStartLocation);
        _textViewEndLocation = findViewById(R.id.itineraryTextViewEndLocation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        _itineraryStops.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(_itineraryStops.getContext(),
                layoutManager.getOrientation());
        _itineraryStops.addItemDecoration(dividerItemDecoration);

        /*
        String[] dummy = {"Hello", "World", "Goodbye"};
        _adapter = new ItineraryAdapter(this, Arrays.asList(dummy));
        _adapter.setClickListener(this);
        recyclerView.setAdapter(_adapter);
        */




        // New Adapter
        FirebaseRecyclerOptions<Stop> options =
                new FirebaseRecyclerOptions.Builder<Stop>()
                        .setQuery(_tripStopsDatabaseReference, Stop.class)
                        .build();

        _adapter = new ItineraryAdapter(options, _tripStopsDatabaseReference);

        _itineraryStops.setAdapter(_adapter);
        _adapter.startListening();
        //

        _bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.itinerary));


        // Set the callback to read from the trip
        readTripFromFirebase(new FirebaseCallback<Trip>() {
            @Override
            public void onCallback(Trip dataItem) {
                // TODO: Do stuff with the trip
                _textViewTripName.setText(dataItem.getName());
                _textViewStartLocation.setText(dataItem.getStart());
                _textViewEndLocation.setText(dataItem.getDestination());
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk");
        }


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
                // Add a marker in Sydney and move the camera
                _map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                _map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        View searchCard = findViewById(R.id.search_card_view);
        searchCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Log.d("tag", "Close");
                _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });



        FloatingActionButton addfab = findViewById(R.id.addfab);
        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                addPersonFragment p_fragment = new addPersonFragment();
                p_fragment.show(manager, "addPersonFragment");
            }
        });
        final FloatingActionButton acceptfab = findViewById(R.id.acceptfab);
        final FloatingActionButton rejectfab = findViewById(R.id.rejectfab);


        readUserTripsFromFirebase(new FirebaseCallback<HashMap<String, HashMap<String, String>>>() {
            @Override
            public void onCallback(final HashMap<String, HashMap<String, String>> dataItem) {
                String tripState = dataItem.get(_tripId).get("state");
                if(TextUtils.equals(tripState, "pending")) {
                    acceptfab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            _userTripDatabaseReference.child(_tripId).child("state").setValue("active");
                            _tripDatabaseReference.child("memberIds").child(_userId).setValue("active");
                        }
                    });
                    rejectfab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            readTripFromFirebase(new FirebaseCallback<Trip>() {
                                @Override
                                public void onCallback(Trip dataItem) {
                                    // Removing user from trip
                                    HashMap<String, String> memberIds = dataItem.getMemberIds();
                                    memberIds.remove(_firebaseAuth.getCurrentUser().getUid());
                                    _tripDatabaseReference.setValue(dataItem);
                                }
                            });
                            readUserTripsFromFirebase(new FirebaseCallback<HashMap<String, HashMap<String, String>>>() {
                                @Override
                                public void onCallback(HashMap<String, HashMap<String, String>> dataItem) {
                                    // Removing the trip from user
                                    dataItem.remove(_tripId);
                                    _userTripDatabaseReference.setValue(dataItem);
                                }
                            });
                            finish();
                        }
                    });
                } else{
                    acceptfab.setVisibility(View.GONE);
                    rejectfab.setVisibility(View.GONE);
                }
            }
        });




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        _map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        _map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /*
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + _adapter.getItem(position) +
                " on row number " + position, Toast.LENGTH_SHORT).show();
    }
    */

    private void readTripFromFirebase(final FirebaseCallback<Trip> readTripCallback) {
        ValueEventListener tripEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip newTrip = dataSnapshot.getValue(Trip.class);
                readTripCallback.onCallback(newTrip);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", databaseError.getMessage());
            }
        };

        _tripDatabaseReference.addValueEventListener(tripEventListener);
    }

    private void readUserTripsFromFirebase(final FirebaseCallback<HashMap<String, HashMap<String, String>>> readUserTripsCallback) {
        ValueEventListener tripEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, HashMap<String, String>> tripState = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                    readUserTripsCallback.onCallback(tripState);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", databaseError.getMessage());
            }
        };

        _userTripDatabaseReference.addValueEventListener(tripEventListener);
    }

    public void addUserToTrip(String email){
        Log.d("TRIPVIEWACTIVITY", email);
        String adjustedEmail = email.replaceAll("\\.", ",");

        ValueEventListener  listener = _databaseRoot.child("User Email to UID").child(adjustedEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue().toString();
                _databaseRoot.child("Trips").child(_tripId).child("memberIds");
                _databaseRoot.child("User Trips").child(user).child(_tripId).child("state").setValue("pending");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class StopsViewHolder extends RecyclerView.ViewHolder {

        TextView _textViewStopName;
        TextView _textViewStopAddress;
        TextView _textViewStopType;

        public StopsViewHolder(@NonNull View itemView) {
            super(itemView);
            _textViewStopName = itemView.findViewById(R.id.textViewStopName);
            _textViewStopAddress = itemView.findViewById(R.id.textViewStopAddress);
            _textViewStopType = itemView.findViewById(R.id.textViewStopType);
        }
    }
}
