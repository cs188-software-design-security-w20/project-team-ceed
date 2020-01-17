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


public class SignUpFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private NavController navController;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewLogin;

    private ProgressBar progressBarRegister;


    private FirebaseAuth firebaseAuth;

    public SignUpFragment() {
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        textViewLogin = (TextView) view.findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        progressBarRegister = new ProgressBar(context);

        firebaseAuth = FirebaseAuth.getInstance();

        navController = Navigation.findNavController(view);

    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        } else if (view == textViewLogin) {
            // open sign-in activity
            navController.navigate(R.id.action_signUpFragment_to_logInFragment);
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate the email and password inputted by the user
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                    context,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                    context,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        // Show the progress bar
        progressBarRegister.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(
                                            context,
                                            "User registered successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    if (task.getException() != null) {
                                        Toast.makeText(
                                                context,
                                                task.getException().getMessage()
                                                        + " " + "Please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    } else {
                                        Toast.makeText(
                                                context,
                                                "Could not create user." + " " + "Please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                }
                                progressBarRegister.setVisibility(View.GONE);
                            }
                        });


    }

}
