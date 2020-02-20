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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import static java.lang.String.format;

public class TripViewActivity extends FragmentActivity
        implements OnMapReadyCallback, ItineraryAdapter.CommunicationCallbacks {

    private GoogleMap _map;
    private BottomSheetBehavior _bottomSheetBehavior;
    private ItineraryAdapter _adapter;
    private DatabaseReference _tripDatabaseReference;
    private DatabaseReference _userTripDatabaseReference;
    private DatabaseReference _tripStopsDatabaseReference;
    private String _tripId;
    private boolean _tripActive = false;
    private PlacesClient _placesClient;

    private Stop _startStop;
    private Stop _endStop;
    private String _endStopPlaceId;
    private GeoApiContext _geoApiContext;
    private Polyline _routePolyline;
    private HashMap<String, StopInfo> _stopInfos = new HashMap<>();


    private TextView _textViewTripName;
    private TextView _textViewStartLocation;
    private TextView _textViewEndLocation;
    private TextView _textViewTripStatus;
    private RecyclerView _itineraryStops;
    private FirebaseAuth _firebaseAuth;

    private DatabaseReference _databaseRoot;
    String _userId;

    private Trip _trip;

    private class StopInfo {
        public Marker _marker;
        public com.google.maps.model.LatLng _wayPoint;
        public Double _rating;
        public String _type;
        public String _duration;

        public StopInfo() {

        }

        public StopInfo(Marker marker, Double rating, String type) {
            this._marker = marker;
            this._rating = rating;
            _type = type;
        }

        public StopInfo(Marker _marker, com.google.maps.model.LatLng wayPoint, Double rating, String type, String duration) {
            this._marker = _marker;
            this._wayPoint = wayPoint;
            this._rating = rating;
            this._type = type;
            this._duration = duration;
        }
    }


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


                _map.clear();
                setStops(dataItem.getStops());
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

        _placesClient = Places.createClient(this);



        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Add stop");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (_stopInfos.size() <= 25) {
                    // Waypoints.size() + 1 to get to end of list.
                    Stop stop = new Stop(place.getName(), "stop", place.getAddress(),
                            place.getLatLng().latitude, place.getLatLng().longitude, _stopInfos.size() - 1);

                    _endStop.setIndex(_endStop.getIndex() + 1);
                    _trip.getStops().put(_endStopPlaceId, _endStop);
                    writeStopToDatabase(place.getId(), stop);

                    StopInfo info = new StopInfo();
                    info._wayPoint = new com.google.maps.model.LatLng(place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    info._type = "stop";
                    _stopInfos.put(place.getId(), info);

                    // This call isn't entirely necessary since createRoute will automatically get called
                    // when a database change is detected but ill do it just in case
                    createRoute();

                    // The marker will get created in setStops() which is called in readTripFromFirebase
                    _map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 4f));
                } else {
                    Toast.makeText(TripViewActivity.this,
                            "Could not add stop; already at max waypoints.", Toast.LENGTH_SHORT).show();
                }
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
                addPersonFragment p_fragment = new addPersonFragment(_tripId);
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
                    acceptfab.setVisibility(View.VISIBLE);
                    rejectfab.setVisibility(View.VISIBLE);
                    searchCard.setVisibility(View.GONE);
                    addfab.setVisibility(View.GONE);
                    _tripActive = false;

                    String tripState = dataItem.get(_tripId).get("state");
                    if (TextUtils.equals(tripState, "inactive")) {
                        _textViewTripStatus.setText("Ended");
                    } else if (TextUtils.equals(tripState, "active")) {
                        _textViewTripStatus.setText("In Progress");
                    }

                    if (TextUtils.equals(tripState, "pending")) {
                        _textViewTripStatus.setText("Pending");
                        acceptfab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                _userTripDatabaseReference.child(_tripId).child("state").setValue("active");
                                _tripDatabaseReference.child("memberIds").child(_userId).setValue("active");
                                _tripActive = true;
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
                        searchCard.setVisibility(View.VISIBLE);
                        _tripActive = true;
                        addfab.setVisibility(View.VISIBLE);


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


    private void setStops(HashMap<String, Stop> stops) {
        HashMap<String, com.google.maps.model.LatLng> wayPoints = new HashMap<>();

        for (Map.Entry mapElement : stops.entrySet()) {
            String key = (String) mapElement.getKey();

            if (((Stop) mapElement.getValue()).getType().equals("start")) {
                _startStop = (Stop) mapElement.getValue();

                LatLng start = new LatLng(_startStop.getLatitude(), _startStop.getLongitude());
                Marker marker = _map.addMarker(new MarkerOptions().position(start).title(_startStop.getName())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Start"));

                if (_stopInfos.get(key) == null) {
                    StopInfo info = new StopInfo();
                    info._type = "start";
                    info._wayPoint = new com.google.maps.model.LatLng(start.latitude, start.longitude);
                    _stopInfos.put(key, info);

                }

                _stopInfos.get(key)._marker = marker;


                // Only zoom in on start marker if trip view was just opened.
                if (_stopInfos.size() <= 2) {
                    _map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 3.5f));
                }
            } else if (((Stop) mapElement.getValue()).getType().equals("end")) {
                _endStop = (Stop) mapElement.getValue();
                _endStopPlaceId = (String) mapElement.getKey();
                LatLng end = new LatLng(_endStop.getLatitude(), _endStop.getLongitude());

                Marker marker = _map.addMarker(new MarkerOptions().position(end).title(_endStop.getName())
                        .snippet("Destination"));

                if (_stopInfos.get(key) == null) {
                    StopInfo info = new StopInfo();
                    info._type = "end";
                    info._wayPoint = new com.google.maps.model.LatLng(end.latitude, end.longitude);
                    _stopInfos.put(key, info);
                }

                _stopInfos.get(key)._marker = marker;
            } else {
                Stop stop = ((Stop) mapElement.getValue());
                LatLng stopLatLng = new LatLng(stop.getLatitude(),
                        stop.getLongitude());
                wayPoints.put((String) mapElement.getKey(), new com.google.maps.model.LatLng(stopLatLng.latitude, stopLatLng.longitude));


                Marker marker = _map.addMarker(new MarkerOptions().position(stopLatLng).title(stop.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                if (_stopInfos.get(key) == null) {
                    StopInfo info = new StopInfo();
                    info._type = "stop";
                    info._wayPoint = new com.google.maps.model.LatLng(stopLatLng.latitude, stopLatLng.longitude);
                    _stopInfos.put(key, info);
                }

                _stopInfos.get(key)._marker = marker;




            }




        }

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


        if (_stopInfos.size() > 2) {
            ArrayList<com.google.maps.model.LatLng> wayPoints = new ArrayList<>();
            for (String key : _stopInfos.keySet()) {
                StopInfo info = _stopInfos.get(key);
                Log.v("info", info._type + ", " + info._wayPoint);
                if (info._type.equals("stop")) {
                    wayPoints.add(info._wayPoint);
                }
            }
            directions.waypoints(wayPoints.toArray(new com.google.maps.model.LatLng[0]));
        }

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (result.routes.length > 0) {

                        DirectionsRoute route = result.routes[0];

                        TreeMap<Integer, String> indexToPlaceId = new TreeMap<>();
                        for (String key : _trip.getStops().keySet()){
                            Stop stop = _trip.getStops().get(key);
                            if(!stop.getType().equals("start")){
                                indexToPlaceId.put(stop.getIndex(), key);
                            }

                        }

                        for (Map.Entry mapElement : indexToPlaceId.entrySet()) {
                            String placeId = (String) mapElement.getValue();
                            int index = (int) mapElement.getKey();
                            _stopInfos.get(placeId)._duration = route.legs[index - 1].duration.humanReadable;
                        }


                        // We make the adapter here cus
                        if(_adapter == null) {
                            // New Adapter
                            FirebaseRecyclerOptions<Stop> options =
                                    new FirebaseRecyclerOptions.Builder<Stop>()
                                            .setQuery(_tripStopsDatabaseReference.orderByChild("index"), Stop.class)
                                            .build();

                            _adapter = new ItineraryAdapter(options, _tripStopsDatabaseReference, _placesClient, TripViewActivity.this);

                            _itineraryStops.setAdapter(_adapter);
                            _adapter.startListening();
                        }


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
                                new PolylineOptions().addAll(newDecodedPath).color(Color.argb(200, 82, 136, 242)));
                        _routePolyline.setClickable(true);

                    }
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
        HashMap<String, Stop> stops = _trip.getStops();
        stops.put(placeId, stop);

        _trip.setStops(stops);

        _databaseRoot.child("Trips").child(_tripId).setValue(_trip);
    }

    @Override
    public void onStopDeleted(String placeId) {
        if (_tripActive) {
            HashMap<String, Stop> stops = _trip.getStops();

            for (String key : stops.keySet()) {
                int index = stops.get(key).getIndex();
                if (index > stops.get(placeId).getIndex()) {
                    stops.get(key).setIndex(index - 1);
                }
            }


            stops.remove(placeId);

            _trip.setStops(stops);

            _databaseRoot.child("Trips").child(_tripId).setValue(_trip);

            _stopInfos.get(placeId)._marker.remove();
            _stopInfos.remove(placeId);

            createRoute();
        }
    }

    @Override
    public void onItemClicked(String placeId) {
        Marker marker = _stopInfos.get(placeId)._marker;
        marker.showInfoWindow();
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5));

    }


    @Override
    public boolean isTripActive() {
        return _tripActive;
    }

    @Override
    public void setRating(String placeId, Double rating) {
        _stopInfos.get(placeId)._rating = rating;
        String type = _stopInfos.get(placeId)._type;
        if (type.equals("stop") ) {
            _stopInfos.get(placeId)._marker.setSnippet("Rating: " + rating);
        } else {
            _stopInfos.get(placeId)._marker.setSnippet(
                    format("%s Rating: " + rating+ "/5",
                            type.equals("start") ? "Start," : "Destination,"));
        }
    }

    @Override
    public String getDuration(String placeId) {
        return "Duration: " + _stopInfos.get(placeId)._duration;
    }


    static public class StopsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView _textViewStopName;
        TextView _textViewStopAddress;
        TextView _textViewStopType;
        TextView _textViewRating;
        TextView _textViewDuration;
        ImageButton _imageButton;
        RelativeLayout _listItem;
        String _placeId;
        ItineraryAdapter.CommunicationCallbacks _communicationCallbacks;


        public StopsViewHolder(@NonNull View itemView,
                               ItineraryAdapter.CommunicationCallbacks communicationCallbacks) {
            super(itemView);
            _textViewStopName = itemView.findViewById(R.id.textViewStopName);
            _textViewStopAddress = itemView.findViewById(R.id.textViewStopAddress);
            _textViewStopType = itemView.findViewById(R.id.textViewStopType);
            _textViewRating = itemView.findViewById(R.id.textViewRating);
            _textViewDuration = itemView.findViewById(R.id.textViewDuration);
            _imageButton = itemView.findViewById(R.id.delete_stop_button);
            _imageButton.setOnClickListener(this);
            _listItem = itemView.findViewById(R.id.layoutItineraryItem);
            _listItem.setOnClickListener(this);
            _communicationCallbacks = communicationCallbacks;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.delete_stop_button) {
                _communicationCallbacks.onStopDeleted(_placeId);
            } else {
                _communicationCallbacks.onItemClicked(_placeId);
            }
        }



    }


}

