package com.ceed.tripster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth _firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _firebaseAuth = FirebaseAuth.getInstance();

        if (_firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);
    }
}
