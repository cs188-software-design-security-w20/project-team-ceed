package com.ceed.tripster;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class ItineraryAdapter extends FirebaseRecyclerAdapter<Stop, TripViewActivity.StopsViewHolder> {
    private DatabaseReference _tripStopsDatabaseReference;
    private PlacesClient _placesClient;

    public interface CommunicationCallbacks {
        void onStopDeleted(String placeId);
        void onItemClicked(String placeId);
        boolean isTripActive();
        void setRating(String placeId, Double rating);
    }

    private CommunicationCallbacks _communicationCallbacks;




    public ItineraryAdapter(FirebaseRecyclerOptions<Stop> options,
                            DatabaseReference tripStopsDatabaseReference, PlacesClient placesClient,
                            CommunicationCallbacks communicationCallbacks){
        super(options);

        this._tripStopsDatabaseReference = tripStopsDatabaseReference;
        _communicationCallbacks = communicationCallbacks;
        _placesClient = placesClient;
    }



    @Override
    protected void onBindViewHolder(@NonNull final TripViewActivity.StopsViewHolder holder, int position, @NonNull Stop model) {
        final String listStopId = getRef(position).getKey();
        Log.d("Firebase", "ID: "+ listStopId);
        _tripStopsDatabaseReference.orderByChild("index").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child(listStopId).child("name").getValue() != null) {
                    final String stopName = dataSnapshot.child(listStopId).child("name").getValue().toString();
                    final String type = dataSnapshot.child(listStopId).child("type").getValue().toString();
                    final String stopAddress = dataSnapshot.child(listStopId).child("address").getValue().toString();

                    holder._textViewStopName.setText(stopName);
                    holder._textViewStopAddress.setText(stopAddress);
                    holder._placeId = listStopId;

                    Log.v("ac", _communicationCallbacks.isTripActive() + "");
                    if(!_communicationCallbacks.isTripActive()) {
                        holder._imageButton.setVisibility(View.GONE);
                    }

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.RATING);

                    // Construct a request object, passing the place ID and fields array.
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(listStopId, placeFields);

                    _placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        Log.v("rating", place.getRating() + "");
                        if (place.getRating() != null) {
                            _communicationCallbacks.setRating(listStopId, place.getRating());
                            holder._textViewRating.setText("Rating: " + place.getRating());
                        } else {
                            holder._textViewRating.setVisibility(View.GONE);
                        }

                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e("ERROR", "Place not found: " + exception.getMessage());
                        }
                    });


                    if (TextUtils.equals(type, "start")) {
                        holder._textViewStopType.setText("Start Stop");
                        holder._textViewStopType.setVisibility(View.VISIBLE);
                        holder._imageButton.setVisibility(View.GONE);
                    }
                    else if (TextUtils.equals(type, "end")) {
                        holder._textViewStopType.setText("End Stop");
                        holder._textViewStopType.setVisibility(View.VISIBLE);
                        holder._imageButton.setVisibility(View.GONE);
                    } else {
                        holder._textViewStopType.setText("");
                        holder._textViewStopType.setVisibility(View.GONE);
                        holder._imageButton.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", databaseError.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public TripViewActivity.StopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_item, parent, false);

        TripViewActivity.StopsViewHolder holder =
                new TripViewActivity.StopsViewHolder(view, _communicationCallbacks);
        return holder;
    }


}
