package com.ceed.tripster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;


public class LogInFragment extends Fragment implements View.OnClickListener {

    private Context _context;
    private NavController _navController;
    private Button _buttonLogIn;
    private EditText _editTextEmailLogIn;
    private EditText _editTextPasswordLogIn;
    private TextView _textViewRegister;

    private String captchaSiteKey = "6Le8z9UUAAAAALkZDnjTgDn7Hcxw8xvuPSHmj97W";
    private Integer MAX_TRIES = 3;
    private Integer _attempts = 0;

    private ProgressBar _progressBarRegister;

    private FirebaseAuth _firebaseAuth;

    public LogInFragment() {
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
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _firebaseAuth = FirebaseAuth.getInstance();
        _navController = Navigation.findNavController(view);

        _buttonLogIn = (Button) view.findViewById(R.id.buttonLogIn);
        _editTextEmailLogIn = (EditText) view.findViewById(R.id.editTextEmailLogIn);
        _editTextPasswordLogIn = (EditText) view.findViewById(R.id.editTextPasswordLogIn);
        _textViewRegister = (TextView) view.findViewById(R.id.textViewRegister);

        _buttonLogIn.setOnClickListener(this);
        _textViewRegister.setOnClickListener(this);
        _progressBarRegister = new ProgressBar(_context);

    }

    @Override
    public void onClick(View view) {
        if (view == _buttonLogIn && _attempts < MAX_TRIES) {
            logIn();
        } else if (view == _buttonLogIn && _attempts >= MAX_TRIES) {
            captcha();
        }
        else if (view == _textViewRegister) {
            // open sign-in activity
            _navController.navigate(R.id.action_logInFragment_to_signUpFragment);
        }
    }

    private void captcha() {
        SafetyNet.getClient(_context).verifyWithRecaptcha(captchaSiteKey)
                .addOnSuccessListener((Activity) _context,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                String userResponseToken = response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                }
                                logIn();
                            }
                        })
                .addOnFailureListener((Activity) _context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Toast.makeText(
                                    _context,
                                    "Error: " + CommonStatusCodes
                                            .getStatusCodeString(statusCode),
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            // A different, unknown type of error occurred.
                            Toast.makeText(
                                    _context,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }

    private void logIn() {
        String email = _editTextEmailLogIn.getText().toString().trim();
        String password = _editTextPasswordLogIn.getText().toString().trim();

        // Validate the email and password inputted by the user
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(_context, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        // Show the progress bar
        _progressBarRegister.setVisibility(View.VISIBLE);

        //logging in the user
        _firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) _context,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    _navController.navigate(R.id.action_logInFragment_to_mainActivity);
                                    ((Activity) _context).finish();
                                    //startActivity(new Intent(getActivity(), MainActivity.class));
                                    _attempts = 0;
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
                                                "Failed to login." + " " + "Please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                    _attempts += 1;
                                }
                                _progressBarRegister.setVisibility(View.GONE);
                            }
                        });


    }

}
