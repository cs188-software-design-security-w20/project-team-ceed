package com.ceed.tripster;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Arrays;

public class TripViewActivity extends FragmentActivity
        implements OnMapReadyCallback, ItineraryAdapter.ItemClickListener {

    private GoogleMap _map;
    private BottomSheetBehavior _bottomSheetBehavior;
    private ItineraryAdapter _adapter;

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

        View searchCard = findViewById(R.id.serach_card_view);
        searchCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Log.d("tag", "Close");
                _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });

        Log.d("TRIPVIEW ACTIVITY", "onCreate: " + TripViewActivityArgs.fromBundle(getIntent().getExtras()).getTripID());
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

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + _adapter.getItem(position) +
                " on row number " + position, Toast.LENGTH_SHORT).show();
    }


}
