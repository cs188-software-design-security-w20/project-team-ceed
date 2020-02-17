package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TripViewActivity extends FragmentActivity
        implements OnMapReadyCallback, ItineraryAdapter.ItemClickListener {

    private GoogleMap _map;
    private BottomSheetBehavior _bottomSheetBehavior;
    private ItineraryAdapter _adapter;
    private DatabaseReference _tripDatabaseReference;
    private String _tripId;
    private Trip _trip;
    private HashMap<String, Stop> _stops;
    private Stop _startStop;
    private Stop _endStop;
    private GeoApiContext _geoApiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RecyclerView recyclerView = findViewById(R.id.itineraryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        String[] dummy = {"Hello", "World", "Goodbye"};
        _adapter = new ItineraryAdapter(this, Arrays.asList(dummy));
        _adapter.setClickListener(this);
        recyclerView.setAdapter(_adapter);


        _bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.itinerary));

        // Initialize the tripId
        _tripId = TripViewActivityArgs.fromBundle(getIntent().getExtras()).getTripID();

        // Initialize the database reference
        _tripDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trips").child(_tripId);


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
                setTrip(dataItem);
                setStops(dataItem.getStops());

            }
        });


        // getStops();

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

       if (_geoApiContext == null) {
           _geoApiContext =
                   new GeoApiContext.Builder().apiKey("AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk").build();
       }
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

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + _adapter.getItem(position) +
                " on row number " + position, Toast.LENGTH_SHORT).show();
    }

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

    private void setTrip(Trip trip) {
        _trip = trip;
        Log.d("ay", _trip.getName());
    }

    private void setStops(HashMap<String, Stop> stops) {
        _stops = stops;

        ArrayList<com.google.maps.model.LatLng> wayPoints = new ArrayList<>();

        for (Map.Entry mapElement : stops.entrySet()) {
            String key = (String)mapElement.getKey();

            if (((Stop) mapElement.getValue()).getType().equals("start")) {
                _startStop = (Stop) mapElement.getValue();

                // Add a marker in Sydney and move the camera
                LatLng start = new LatLng(_startStop.getLatitude(), _startStop.getLongitude());
                _map.addMarker(new MarkerOptions().position(start).title(_startStop.getName())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Start"));
                _map.moveCamera(CameraUpdateFactory.newLatLng(start));
            }
            else if (((Stop) mapElement.getValue()).getType().equals("end")) {
                _endStop = (Stop) mapElement.getValue();
                // Add a marker in Sydney and move the camera
                LatLng end = new LatLng(_endStop.getLatitude(), _endStop.getLongitude());
                _map.addMarker(new MarkerOptions().position(end).title(_endStop.getName())
                        .snippet("Destination"));
            } else {
                Stop stop = ((Stop) mapElement.getValue());
                LatLng stopLatLng = new LatLng(stop.getLatitude(),
                        stop.getLongitude());
                wayPoints.add(new com.google.maps.model.LatLng(stopLatLng.latitude, stopLatLng.longitude));

                _map.addMarker(new MarkerOptions().position(stopLatLng).title(stop.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            }


        }


        createRoute(wayPoints);
    }

    private void createRoute(ArrayList<com.google.maps.model.LatLng> wayPoints) {

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                _endStop.getLatitude(),
                _endStop.getLongitude()
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(_geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        _startStop.getLatitude(),
                        _startStop.getLongitude()
                )
        );


        if (wayPoints.size() != 0) {
            directions.waypoints(wayPoints.toArray(new com.google.maps.model.LatLng[0]));
        }

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult result) {
                new Handler(Looper.getMainLooper()).post(() -> {

                    for(DirectionsRoute route: result.routes){
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());


                        List<LatLng> newDecodedPath = new ArrayList<>();

                        // This loops through all the LatLng coordinates of ONE polyline.
                        for(com.google.maps.model.LatLng latLng: decodedPath){

                            newDecodedPath.add(new LatLng(
                                    latLng.lat,
                                    latLng.lng
                            ));
                        }
                        Polyline polyline = _map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        polyline.setClickable(true);

                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("boo", "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }
}
