package com.ceed.tripster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth _firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyCCuUByT1YxzVcehC492h1oYERb59Nuswk");

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        _firebaseAuth = FirebaseAuth.getInstance();

        if (_firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_start);
    }
}
