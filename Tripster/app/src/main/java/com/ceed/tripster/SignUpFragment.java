package com.ceed.tripster;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
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

    private Context _context;
    private NavController _navController;
    private Button _buttonRegister;
    private EditText _editTextEmail;
    private EditText _editTextPassword;
    private TextView _textViewLogin;

    private ProgressBar _progressBarRegister;

    private FirebaseAuth _firebaseAuth;

    public SignUpFragment() {
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        _firebaseAuth = FirebaseAuth.getInstance();
        _navController = Navigation.findNavController(view);

        _buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
        _editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        _editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        _textViewLogin = (TextView) view.findViewById(R.id.textViewLogin);

        _buttonRegister.setOnClickListener(this);
        _textViewLogin.setOnClickListener(this);
        _progressBarRegister = new ProgressBar(_context);

    }

    @Override
    public void onClick(View view) {
        if (view == _buttonRegister) {
            registerUser();
        } else if (view == _textViewLogin) {
            // open sign-in activity
            _navController.navigate(R.id.action_signUpFragment_to_logInFragment);
        }
    }

    private void registerUser() {
        String email = _editTextEmail.getText().toString().trim();
        String password = _editTextPassword.getText().toString().trim();

        // Validate the email and password inputted by the user
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                    _context,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                    _context,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        // Show the progress bar
        _progressBarRegister.setVisibility(View.VISIBLE);

        _firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) _context,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(
                                            _context,
                                            "User registered successfully.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    if (task.getException() != null) {
                                        Toast.makeText(
                                                _context,
                                                task.getException().getMessage()
                                                        + " " + "Please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    } else {
                                        Toast.makeText(
                                                _context,
                                                "Could not create user." + " " + "Please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                }
                                _progressBarRegister.setVisibility(View.GONE);
                            }
                        });


    }

}
