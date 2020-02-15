package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity{

    FirebaseAuth _firebaseAuth;
    private ArrayList<String> tripNames = new ArrayList<>();
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
        Log.d("MAINACTIVITY", "Option selected");
        //TripListFragment t_fragment = (TripListFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_main);
        //t_fragment.goToProfile();

        if (mNavController.getCurrentDestination().getId() == mNavController.getGraph().getStartDestination()) {
            mNavController.navigate(R.id.action_tripListFragment2_to_profileFragment);
        }

        return super.onOptionsItemSelected(item);
    }


}
