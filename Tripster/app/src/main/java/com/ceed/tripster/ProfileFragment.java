package com.ceed.tripster;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context _context;
    private NavController _navController;
    private TextView _textViewUserEmail;
    private Button _buttonLogOut;

    private FirebaseAuth _firebaseAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this._context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        _firebaseAuth = FirebaseAuth.getInstance();
        _navController = Navigation.findNavController(view);

        FirebaseUser user = _firebaseAuth.getCurrentUser();

        // Get resources for the views
        _buttonLogOut = (Button) view.findViewById(R.id.buttonLogout);
        _textViewUserEmail = (TextView) view.findViewById(R.id.textViewUserEmail);


        // Set the display text
        _textViewUserEmail.setText("Welcome " + user.getDisplayName());

        // Initialize the button
        _buttonLogOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == _buttonLogOut) {
            // Logout
            _firebaseAuth.signOut();
            // Switch to the login fragment
            _navController.navigate(R.id.action_profileFragment_to_startActivity);
            ((Activity) _context).finish();
        }
    }
}
