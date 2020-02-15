package com.ceed.tripster;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TripListFragment extends Fragment {

    private Context mContext;
    private NavController mNavController;
    private ArrayList<String> tripNames = new ArrayList<>();

    public TripListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_list, container, false);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavController.navigate(R.id.action_tripListFragment2_to_newTripFragment);
            }
        });

        //View rootView = inflater.inflate(R.layout.tabbar_layout, container, false);

        initTripNames();

    }

    public void goToProfile(){
        if(mNavController != null){
            mNavController.navigate(R.id.action_tripListFragment2_to_profileFragment);
        }

    }

    private void initTripNames() {
        tripNames.add("Hello");
        tripNames.add("World");
        tripNames.add("Goodbye");
        tripNames.add("Hello");
        tripNames.add("World");
        tripNames.add("Goodbye");
        tripNames.add("Hello");
        tripNames.add("World");
        tripNames.add("Goodbye");
        tripNames.add("Hello");
        tripNames.add("World");
        tripNames.add("Goodbye");
        tripNames.add("Hello");
        tripNames.add("World");
        tripNames.add("Goodbye");
        tripNames.add("Hello");
        tripNames.add("World");
        tripNames.add("Goodbye");
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.TripList);
        if(recyclerView == null){
            Log.d("MAINACTIVITY", "recyclerview null");
        }
        Log.d("MAINACTIVITY", "TRIPLISTADAPTER CONSTRUCTOR BEING CALLED #############");
        TripListAdapter adapter = new TripListAdapter(mContext, tripNames, mNavController);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }


}
