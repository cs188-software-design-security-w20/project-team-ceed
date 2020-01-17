package com.ceed.tripster;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LogInFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private NavController navController;
    private Button buttonLogIn;
    private EditText editTextEmailLogIn;
    private EditText editTextPasswordLogIn;
    private TextView textViewRegister;

    private ProgressBar progressBarRegister;


    private FirebaseAuth firebaseAuth;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        buttonLogIn = (Button) view.findViewById(R.id.buttonLogIn);
        editTextEmailLogIn = (EditText) view.findViewById(R.id.editTextEmailLogIn);
        editTextPasswordLogIn = (EditText) view.findViewById(R.id.editTextPasswordLogIn);
        textViewRegister = (TextView) view.findViewById(R.id.textViewRegister);

        buttonLogIn.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        progressBarRegister = new ProgressBar(context);

        firebaseAuth = FirebaseAuth.getInstance();

        navController = Navigation.findNavController(view);

    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogIn) {
            logIn();
        } else if (view == textViewRegister) {
            // open sign-in activity
            navController.navigate(R.id.action_logInFragment_to_signUpFragment);
        }
    }

    private void logIn() {
        String email = editTextEmailLogIn.getText().toString().trim();
        String password = editTextPasswordLogIn.getText().toString().trim();

        // Validate the email and password inputted by the user
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Show the progress bar
        progressBarRegister.setVisibility(View.VISIBLE);


    }

}
