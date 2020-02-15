package com.ceed.tripster;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTripFragment extends Fragment implements View.OnClickListener {

    private Context _context;
    private DatabaseReference _databaseRoot;
    private FirebaseAuth _firebaseAuth;
    private Button _buttonAddTrip;
    private EditText _editTextTripName;
    private Place _startPlace;
    private Place _endPlace;

    public NewTripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this._context = context;
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
        _buttonAddTrip = view.findViewById(R.id.buttonCreateTrip);
        _editTextTripName = view.findViewById(R.id.editTextTripName);
        _buttonAddTrip.setOnClickListener(this);

        if (!Places.isInitialized()) {
            Places.initialize(_context.getApplicationContext(), "AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk");
        }


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment startAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.enter_start_autocomplete_fragment);

        // Specify the types of place data to return.
        startAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));



        // Set up a PlaceSelectionListener to handle the response.
        startAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                setStartPlace(place);
                // TODO: Get info about the selected place.
                Log.i("Places Autocomplete", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("Places Autocomplete", "An error occurred: " + status);
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
            public void onPlaceSelected(@NonNull Place place) {
                setEndPlace(place);

                // TODO: Get info about the selected place.
                Log.i("Places Autocomplete", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("Places Autocomplete", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == _buttonAddTrip && checkCreateTripFields()) {
            writeTripToDatabase();
        }
    }

    private void writeTripToDatabase() {
        final String tripName = _editTextTripName.getText().toString().trim();
        String uid = _firebaseAuth.getCurrentUser().getUid();
        DatabaseReference newTrip = _databaseRoot.child("Trips").push();
        String newTripId = newTrip.getKey();

        // Initializing stops
        Stop startStop = new Stop(_startPlace.getName(), 0);
        Stop endStop = new Stop(_endPlace.getName(), 0);

        HashMap<String, Stop> stops = new HashMap<>();

        stops.put(_startPlace.getId(), startStop);
        stops.put(_endPlace.getId(), endStop);

        // Initializing trip
        Trip trip = new Trip();
        trip.setStops(stops);

        // Initially, the only member is the creator
        HashMap<String, Integer> members = new HashMap<>();
        members.put(uid, 1);

        trip.setMemberIds(members);
        trip.setName(tripName);

        _databaseRoot.child("Trips").child(newTripId).setValue(trip).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                _context,
                                "Successfully created trip",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                        else {
                            Toast.makeText(
                                _context,
                                "Failed to create trip",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }

    private boolean checkCreateTripFields() {
        final String tripName = _editTextTripName.getText().toString().trim();
        // Validate the trip name
        if (TextUtils.isEmpty(tripName)) {
            Toast.makeText(
                    _context,
                    "Please enter a trip name",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        // Validate the start place
        if (_startPlace == null) {
            Toast.makeText(
                    _context,
                    "Please enter a starting location",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        // Validate the end place
        if (_endPlace == null) {
            Toast.makeText(
                    _context,
                    "Please enter an ending location",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        return true;
    }

    private void setStartPlace(Place place) {
        _startPlace = place;
    }

    private void setEndPlace(Place place) {
        _endPlace = place;
    }
}
