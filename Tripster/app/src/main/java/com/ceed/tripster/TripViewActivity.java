package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TripViewActivity extends FragmentActivity
        implements OnMapReadyCallback, ItineraryAdapter.DeleteStopCallBack {

    private GoogleMap _map;
    private BottomSheetBehavior _bottomSheetBehavior;
    private ItineraryAdapter _adapter;
    private DatabaseReference _tripDatabaseReference;
    private DatabaseReference _userTripDatabaseReference;
    private DatabaseReference _tripStopsDatabaseReference;
    private String _tripId;

    private Stop _startStop;
    private Stop _endStop;
    private String _endStopPlaceId;
    private HashMap<String, com.google.maps.model.LatLng> _wayPoints;
    private GeoApiContext _geoApiContext;
    private Polyline _routePolyline;
    private HashMap<String, Marker> _markers = new HashMap<>();

    private TextView _textViewTripName;
    private TextView _textViewStartLocation;
    private TextView _textViewEndLocation;
    private TextView _textViewTripStatus;
    private RecyclerView _itineraryStops;
    private FirebaseAuth _firebaseAuth;

    private DatabaseReference _databaseRoot;
    String _userId;

    private Trip _trip;


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
        _textViewTripStatus = findViewById(R.id.itineraryTextViewTripStatus);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        _itineraryStops.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(_itineraryStops.getContext(),
                layoutManager.getOrientation());
        _itineraryStops.addItemDecoration(dividerItemDecoration);


        // Initialize the tripId
        _tripId = TripViewActivityArgs.fromBundle(getIntent().getExtras()).getTripID();


        // New Adapter
        FirebaseRecyclerOptions<Stop> options =
                new FirebaseRecyclerOptions.Builder<Stop>()
                        .setQuery(_tripStopsDatabaseReference.orderByChild("index"), Stop.class)
                        .build();

        _adapter = new ItineraryAdapter(options, _tripId, _tripStopsDatabaseReference, this);

        _itineraryStops.setAdapter(_adapter);
        _adapter.startListening();

        _bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.itinerary));


        // Set the callback to read from the trip
        readTripFromFirebase(new FirebaseCallback<Trip>() {
            @Override
            public void onCallback(Trip dataItem) {
                // TODO: Do stuff with the trip
                Log.d("Trip info", dataItem.getName());
                Log.d("Trip info", dataItem.getStart());
                Log.d("Trip info", dataItem.getDestination());
                Log.d("Trip info", dataItem.getStops().toString());
                Log.d("Trip info", dataItem.getMemberIds().toString());

                _wayPoints = setStops(dataItem.getStops());
                createRoute();


                _textViewTripName.setText(dataItem.getName());
                _textViewStartLocation.setText(dataItem.getStart());
                _textViewEndLocation.setText(dataItem.getDestination());

                _trip = dataItem;
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk");
        }


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Add stop");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // Waypoints.size() + 2 to get to end of list
                Stop stop = new Stop(place.getName(), "stop", place.getAddress(),
                        place.getLatLng().latitude, place.getLatLng().longitude, _wayPoints.size() + 2);

                _endStop.setIndex(_endStop.getIndex() + 1);
                _trip.getStops().put(_endStopPlaceId, _endStop);
                writeStopToDatabase(place.getId(), stop);

                _wayPoints.put(place.getId(), new com.google.maps.model.LatLng(place.getLatLng().latitude,
                        place.getLatLng().longitude));
                createRoute();
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

       if (_geoApiContext == null) {
           _geoApiContext =
                   new GeoApiContext.Builder().apiKey("AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk").build();
       }

        _databaseRoot = FirebaseDatabase.getInstance().getReference();


        FloatingActionButton addfab = findViewById(R.id.addfab);
        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                addPersonFragment p_fragment = new addPersonFragment();
                p_fragment.show(manager, "addPersonFragment");
            }
        });

        Button endTripButton = findViewById(R.id.endTripButton);
        endTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _userTripDatabaseReference.child(_tripId).child("state").setValue("inactive");
                _tripDatabaseReference.child("memberIds").child(_userId).setValue("inactive");
            }
        });


        final FloatingActionButton acceptfab = findViewById(R.id.acceptfab);
        final FloatingActionButton rejectfab = findViewById(R.id.rejectfab);


        readUserTripsFromFirebase(new FirebaseCallback<HashMap<String, HashMap<String, String>>>() {
            @Override
            public void onCallback(final HashMap<String, HashMap<String, String>> dataItem) {
                if (dataItem != null && dataItem.get(_tripId) != null) {
                    String tripState = dataItem.get(_tripId).get("state");
                    if(TextUtils.equals(tripState, "inactive")){
                        _textViewTripStatus.setText("Ended");
                    } else if(TextUtils.equals(tripState, "active")){
                        _textViewTripStatus.setText("In Progress");
                    }

                    if(TextUtils.equals(tripState, "pending")) {
                        _textViewTripStatus.setText("Pending");
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
                    } else {
                        acceptfab.setVisibility(View.GONE);
                        rejectfab.setVisibility(View.GONE);
                    }
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


    private HashMap<String, com.google.maps.model.LatLng> setStops(HashMap<String, Stop> stops) {

        HashMap<String, com.google.maps.model.LatLng> wayPoints = new HashMap<>();

        for (Map.Entry mapElement : stops.entrySet()) {
            String key = (String) mapElement.getKey();

            if (((Stop) mapElement.getValue()).getType().equals("start")) {
                _startStop = (Stop) mapElement.getValue();

                LatLng start = new LatLng(_startStop.getLatitude(), _startStop.getLongitude());
                _markers.put(key, _map.addMarker(new MarkerOptions().position(start).title(_startStop.getName())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Start")));
                _map.moveCamera(CameraUpdateFactory.newLatLng(start));
            } else if (((Stop) mapElement.getValue()).getType().equals("end")) {
                _endStop = (Stop) mapElement.getValue();
                _endStopPlaceId = (String) mapElement.getKey();
                LatLng end = new LatLng(_endStop.getLatitude(), _endStop.getLongitude());
                _markers.put(key, _map.addMarker(new MarkerOptions().position(end).title(_endStop.getName())
                        .snippet("Destination")));
            } else {
                Stop stop = ((Stop) mapElement.getValue());
                LatLng stopLatLng = new LatLng(stop.getLatitude(),
                        stop.getLongitude());
                wayPoints.put((String)mapElement.getKey(), new com.google.maps.model.LatLng(stopLatLng.latitude, stopLatLng.longitude));

                _markers.put(key, _map.addMarker(new MarkerOptions().position(stopLatLng).title(stop.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));

            }


        }

        return wayPoints;
    }

    private void createRoute() {

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                _endStop.getLatitude(),
                _endStop.getLongitude()
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(_geoApiContext);

        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        _startStop.getLatitude(),
                        _startStop.getLongitude()
                )
        );


        if (_wayPoints.size() != 0) {
            directions.waypoints(_wayPoints.values().toArray(new com.google.maps.model.LatLng[0]));
        }

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult result) {
                new Handler(Looper.getMainLooper()).post(() -> {

                    DirectionsRoute route = result.routes[0];
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());


                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    if (_routePolyline != null) {
                        _routePolyline.remove();
                    }
                    _routePolyline = _map.addPolyline(
                            new PolylineOptions().addAll(newDecodedPath).color(Color.argb(200,82, 136, 242)));
                    _routePolyline.setClickable(true);


                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("boo", "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }

    public void addUserToTrip(String email) {
        Log.d("TRIPVIEWACTIVITY", email);
        String adjustedEmail = email.replaceAll("\\.", ",");

        ValueEventListener listener = _databaseRoot.child("User Email to UID").child(adjustedEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue().toString();
                _databaseRoot.child("Trips").child(_tripId).child("memberIds").child(user).setValue("pending");
                _databaseRoot.child("User Trips").child(user).child(_tripId).child("state").setValue("pending");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void writeStopToDatabase(String placeId, Stop stop) {
        HashMap<String, Stop> stops= _trip.getStops();
        stops.put(placeId, stop);

        _trip.setStops(stops);

        _databaseRoot.child("Trips").child(_tripId).setValue(_trip);
    }

    @Override
    public void onStopDeleted(String placeId) {
        HashMap<String, Stop> stops= _trip.getStops();
        stops.remove(placeId);

        _trip.setStops(stops);

        _databaseRoot.child("Trips").child(_tripId).setValue(_trip);

        _markers.get(placeId).remove();
        _markers.remove(placeId);

        _wayPoints.remove(placeId);
        createRoute();
    }


    static public class StopsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView _textViewStopName;
        TextView _textViewStopAddress;
        TextView _textViewStopType;
        ImageButton _imageButton;
        String _placeId;
        ItineraryAdapter.DeleteStopCallBack _onDeleteStopCallBack;



        public StopsViewHolder(@NonNull View itemView,
                               ItineraryAdapter.DeleteStopCallBack deleteStopCallBack) {
            super(itemView);
            _textViewStopName = itemView.findViewById(R.id.textViewStopName);
            _textViewStopAddress = itemView.findViewById(R.id.textViewStopAddress);
            _textViewStopType = itemView.findViewById(R.id.textViewStopType);
            _imageButton = itemView.findViewById(R.id.delete_stop_button);
            _imageButton.setOnClickListener(this);
            _onDeleteStopCallBack = deleteStopCallBack;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.delete_stop_button:
                    _onDeleteStopCallBack.onStopDeleted(_placeId);
                    break;
                default:
                    break;
            }
        }
    }


}

