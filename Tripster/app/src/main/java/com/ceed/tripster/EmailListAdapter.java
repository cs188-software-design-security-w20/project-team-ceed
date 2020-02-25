package com.ceed.tripster;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EmailListAdapter extends RecyclerView.Adapter<EmailListAdapter.ViewHolder> {

    private List<String> _memberIds;
    private DatabaseReference _tripDatabaseReference;
    private DatabaseReference _usersDatabaseReference;
    private DatabaseReference _userTripsDatabaseReference;
    private FirebaseAuth _auth;
    private String _tripId;
    private Activity _activity;


    private ImageButton _removeButton;

    public EmailListAdapter(DatabaseReference tripDatabaseReference, DatabaseReference usersDatabaseReference, DatabaseReference userTripsDatabaseReference, List<String> memberIds, String tripId, Activity activity, FirebaseAuth auth){
        this._tripDatabaseReference = tripDatabaseReference;
        this._usersDatabaseReference = usersDatabaseReference;
        this._userTripsDatabaseReference = userTripsDatabaseReference;
        this._memberIds = memberIds;
        this._tripId = tripId;
        _activity = activity;
        _auth = auth;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_email_item, parent, false);
        _removeButton = v.findViewById(R.id.removeUserButton);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String memberId = _memberIds.get(position);
        _removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _tripDatabaseReference.child("memberIds").child(memberId).removeValue();
                _memberIds.remove(memberId);
                _userTripsDatabaseReference.child(memberId).child(_tripId).removeValue();
                if (memberId.equals(_auth.getUid())) {
                    _activity.finish();
                }
            }
        });
        if(_memberIds.contains(memberId)) {
            _tripDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && _memberIds.contains(memberId)) {
                        if (TextUtils.equals(dataSnapshot.child("memberIds").child(memberId).getValue().toString(), "active")
                                || TextUtils.equals(dataSnapshot.child("memberIds").child(memberId).getValue().toString(), "owner")) {
                            _usersDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String memberEmail = dataSnapshot.child(memberId).child("email").getValue().toString();
                                        holder._textViewUserEmail.setText(memberEmail);

                                    } else {
                                        holder._textViewUserEmail.setText("dataSnapshot does not exist");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        } else {
                            _memberIds.remove(memberId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return _memberIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public String _email;
        public TextView _textViewUserEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _textViewUserEmail = (TextView) itemView.findViewById(R.id.textViewUserEmail);
        }
    }
}
