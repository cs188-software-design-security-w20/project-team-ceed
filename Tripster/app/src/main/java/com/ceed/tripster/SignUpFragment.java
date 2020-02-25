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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    private Context _context;
    private NavController _navController;
    private Button _buttonRegister;
    private EditText _editTextEmail;
    private EditText _editTextPassword;
    private EditText _editTextDisplayName;
    private TextView _textViewLogin;

    private ProgressBar _progressBarRegister;

    private FirebaseAuth _firebaseAuth;
    private DatabaseReference _databaseRoot;

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
        _databaseRoot = FirebaseDatabase.getInstance().getReference();
        _navController = Navigation.findNavController(view);

        _buttonRegister = (Button) view.findViewById(R.id.buttonRegister);
        _editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        _editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        _editTextDisplayName = (EditText) view.findViewById(R.id.editTextDisplayName);
        _textViewLogin = (TextView) view.findViewById(R.id.textViewLogin);

        _buttonRegister.setOnClickListener(this);
        _textViewLogin.setOnClickListener(this);
        _progressBarRegister = new ProgressBar(_context);

    }

    @Override
    public void onClick(View view) {
        if (view == _buttonRegister) {
            //_navController.navigate(R.id.action_signUpFragment_to_mainActivity);
            registerUser();
        } else if (view == _textViewLogin) {
            // open sign-in activity
            _navController.navigate(R.id.action_signUpFragment_to_logInFragment);
        }
    }

    private void registerUser() {
        final String email = _editTextEmail.getText().toString().trim();
        String password = _editTextPassword.getText().toString().trim();
        final String displayName = _editTextDisplayName.getText().toString().trim();

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
        if (TextUtils.isEmpty(displayName)) {
            Toast.makeText(
                    _context,
                    "Please enter your display name",
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
                                    registerSuccessful(displayName, email);
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

    private void registerSuccessful(String displayName, String email) {
        FirebaseUser user = _firebaseAuth.getCurrentUser();
        if (user != null) {
            // Setting user's display name
            UserProfileChangeRequest profileChangeRequest =
                    new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    }
                    else {
                    }
                }
            });

            // Creating a userMap
            HashMap<String, String> newUserMap = createNewUserMap(email, displayName);

            // Writing new user to database
            writeUserToDatabase(user, newUserMap);
        }
    }

    private HashMap<String, String> createNewUserMap(String email, String displayName) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("display_name", displayName);
        userMap.put("profile_photo", "");
        return userMap;
    }

    private void deleteCurrentUser(final FirebaseUser user) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
                else {
                }
            }
        });
    }

    private void writeUserToDatabase(final FirebaseUser user, final HashMap<String, String> userMap) {
        final String userId = user.getUid();
        _databaseRoot.child("Users").child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Write user email to mapping
                    writeUserEmailToDatabase(replacePeriodsFromEmail(userMap.get("email")), user);
                } else {
                    deleteCurrentUser(user);
                }
            }
        });
    }

    private void writeUserEmailToDatabase(final String email, final FirebaseUser user) {
        final String userId = user.getUid();
        _databaseRoot.child("User Email to UID").child(email).setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Navigate to main page
                    _navController.navigate(R.id.action_signUpFragment_to_mainActivity);
                } else {
                    deleteCurrentUser(user);
                }
            }
        });
    }

    private String replacePeriodsFromEmail(String email) {
        return email.replaceAll("\\.", ",");
    }
}
