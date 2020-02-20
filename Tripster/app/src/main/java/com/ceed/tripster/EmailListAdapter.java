package com.ceed.tripster;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EmailListAdapter extends RecyclerView.Adapter<EmailListAdapter.ViewHolder> {

    private List<String> _memberIds;
    private DatabaseReference _usersDatabaseReference;

    public EmailListAdapter(DatabaseReference tripDatabaseReference, DatabaseReference usersDatabaseReference, List<String> memberIds){
        this._usersDatabaseReference = usersDatabaseReference;
        this._memberIds = memberIds;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("EMAILLISTADAPTER", "onCreateViewHolder called");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_email_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("EMAILLISTADAPTER", "Email bindviewholder called");
        _usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("EMAILLISTADAPTER", "onDataChange called");
                if(dataSnapshot.exists()){
                    Log.d("EMAILLISTADAPTER", "User datasnapshot exists");
                    String memberId = _memberIds.get(position);
                    Log.d("EMAILLISTADAPTER", "Member Email1: " + dataSnapshot.child(memberId).child("email").toString());
                    String memberEmail = dataSnapshot.child(memberId).child("email").getValue().toString();
                    Log.d("EMAILLISTADAPTER", "Member Email2: " + memberEmail);
                    holder._textViewUserEmail.setText(memberEmail);

                } else {
                    holder._textViewUserEmail.setText("dataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("EMAILLISTADAPTER", databaseError.toString());
            }
        });
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
