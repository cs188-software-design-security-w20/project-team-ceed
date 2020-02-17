package com.ceed.tripster;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class addPersonFragment extends DialogFragment implements View.OnClickListener{


    Button _submit;
    EditText emailField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_person, container, false);
        _submit = (Button) view.findViewById(R.id.submit);
        emailField = (EditText) view.findViewById(R.id.email);
        _submit.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        Log.d("ADDPERSONFRAGMENT", v.toString());
        if(v.getId() == R.id.submit){
            String email = emailField.getText().toString();
            ((TripViewActivity) getActivity()).addUserToTrip(email);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }
}
