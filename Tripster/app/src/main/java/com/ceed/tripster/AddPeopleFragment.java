package com.ceed.tripster;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddPeopleFragment extends DialogFragment implements View.OnClickListener {

    private Context _context;
    private Button _submit;
    private EditText emailField;
    private EmailListAdapter _adapter;
    private String _tripID;

    private DatabaseReference _tripDatabaseReference;
    private DatabaseReference _usersDatabaseReference;
    private DatabaseReference _userTripsDatabaseReference;
    private List<String> _memberIds;

    private RecyclerView _emailList;
    private View _emailListView;

    public AddPeopleFragment(String tripID){
        this._tripID = tripID;
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
        View view = inflater.inflate(R.layout.fragment_add_person, container, false);
        //_emailListView = inflater.inflate(R.layout.fragment_trip_list, container, false);

        _submit = (Button) view.findViewById(R.id.submit);
        emailField = (EditText) view.findViewById(R.id.email);
        _submit.setOnClickListener(this);

        _emailList = view.findViewById(R.id.recyclerEmailList);
        _emailList.setLayoutManager(new LinearLayoutManager(_context));



        _tripDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Trips").child(_tripID);
        _usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        _userTripsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User Trips");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _tripDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.child("memberIds").getValue() != null){

                    _memberIds = new ArrayList<String>(((HashMap<String, String>) dataSnapshot.child("memberIds").getValue()).keySet());
                    Log.d("ADDPERSONFRAGMENT", _memberIds.get(0));
                    _adapter = new EmailListAdapter( _tripDatabaseReference, _usersDatabaseReference,
                            _userTripsDatabaseReference, _memberIds, _tripID, getActivity(), FirebaseAuth.getInstance());
                    _emailList.setAdapter(_adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        Log.d("ADDPERSONFRAGMENT", v.toString());
        if (v.getId() == R.id.submit) {
            String email = emailField.getText().toString();
            ((TripViewActivity) getActivity()).addUserToTrip(email);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

}
