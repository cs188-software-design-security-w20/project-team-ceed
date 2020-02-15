package com.ceed.tripster;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTripFragment extends Fragment {

    private DatabaseReference _databaseRoot;
    private FirebaseAuth _firebaseAuth;
    private Place _startPlace;
    private Place _endPlace;

    public NewTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_trip, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        _firebaseAuth = FirebaseAuth.getInstance();
        _databaseRoot = FirebaseDatabase.getInstance().getReference();



        if (!Places.isInitialized()) {
            Places.initialize(getContext().getApplicationContext(), "AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk");
        }


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment startAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.enter_start_autocomplete_fragment);

        // Specify the types of place data to return.
        startAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));



        // Set up a PlaceSelectionListener to handle the response.
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setStartPlace(place);
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment endAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.enter_end_autocomplete_fragment);

        // Specify the types of place data to return.
        endAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


        Place end_place;


        // Set up a PlaceSelectionListener to handle the response.
        endAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setEndPlace(place);

                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        Button addTripButton = view.findViewById(R.id.createTrip);
        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = _firebaseAuth.getCurrentUser().getUid();
                DatabaseReference newTrip = _databaseRoot.child("Trips").push();
                String newTripId = newTrip.getKey();

                Trip trip = new Trip();

                HashMap<String, Stop> stops = new HashMap<>();

                Stop startStop = new Stop();
                startStop.setName(_startPlace.getName());
                startStop.setType(0);

                Stop endStop = new Stop();
                endStop.setName(_endPlace.getName());
                endStop.setType(0);


                stops.put(_startPlace.getId(), startStop);
                stops.put(_endPlace.getId(), endStop);

                trip.setStops(stops);

                HashMap<String, Integer> members = new HashMap<>();
                members.put(uid, 1);

                trip.setMemberIds(members);

                trip.setName("NAME");

                _databaseRoot.child("Trips").child(newTripId).setValue(trip);
            }
        });
    }

    public void setStartPlace(Place place) {
        _startPlace = place;
    }

    public void setEndPlace(Place place) {
        _endPlace = place;
    }
}
