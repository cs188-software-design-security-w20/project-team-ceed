package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewLogin;

    private ProgressBar progressBarRegister;


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail= (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(this);
        textViewLogin.setOnClickListener(this);
        progressBarRegister = new ProgressBar(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        }
        else if (view == textViewLogin) {
            // open sign-in activity
            Toast.makeText(this, "Path not developed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate the email and password inputted by the user
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                    this,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                    this,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        // Show the progress bar
        progressBarRegister.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "User registered successfully.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        else {
                            if (task.getException() != null) {
                                Toast.makeText(
                                        MainActivity.this,
                                        task.getException().getMessage() + " " + "Please try again.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                            else {
                                Toast.makeText(
                                        MainActivity.this,
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
