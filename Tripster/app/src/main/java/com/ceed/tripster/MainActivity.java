package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity{

    FirebaseAuth _firebaseAuth;
    private NavController mNavController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _firebaseAuth = FirebaseAuth.getInstance();

        if (_firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_app_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.quantum_white_text));
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavController = findNavController(this, R.id.nav_host_fragment_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mNavController.getCurrentDestination().getId() == mNavController.getGraph().getStartDestination()) {
            mNavController.navigate(R.id.action_tripListFragment2_to_profileFragment);
        }

        return super.onOptionsItemSelected(item);
    }


}
