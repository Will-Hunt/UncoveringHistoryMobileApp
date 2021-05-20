package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Routes extends AppCompatActivity {
    ListView listView;
    List<HistoricalSite> historicalSiteList;
    String routeList;
    Boolean favouritesFilter = false;
    DatabaseReference siteDbRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

//      Strings that need to be passed between activities are added to the intent
//      And need to be set to new variables, because activities are volatile
        routeList = getIntent().getStringExtra("routeList");

//      Main function for displaying the routes is called, this function is separate to improve readability
        updateRouteList();

//      The following waits for the user to click on the Filter Favourites button and a function is called to complete the task
        findViewById(R.id.filter_favourites).setOnClickListener(v -> {
            favouritesFilter = !(favouritesFilter);
            updateRouteList();
        });

//      The following waits for the user to click on the Create Route button and a function is called to complete the task
        findViewById(R.id.create_route).setOnClickListener(v -> {
            if (routeList.length() > 1) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("routeList", routeList);
                startActivity(intent);
            } else
                Toast.makeText(Routes.this, "Please select at least 2 Sites First", Toast.LENGTH_LONG).show();
        });

//      The following waits for the user to click on the Clear Route button and a function is called to complete the task
        findViewById(R.id.clear_route).setOnClickListener(v -> routeList = "");

//        The following allows users to switch between pages
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.routes);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.map:
                    startActivity(new Intent(getApplicationContext(), Map.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.routes:
                    return true;
                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    public void updateRouteList() {

        //      Gets the XML element that needs to be populated
        listView = findViewById(R.id.historical_site_list_view);
        historicalSiteList = new ArrayList<>();

//      Sets the database reference and then listens for the reference to be received
        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historicalSiteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HistoricalSite historicalSite = new HistoricalSite(dataSnapshot.child("name").getValue(String.class),
                            dataSnapshot.child("description").getValue(String.class),
                            dataSnapshot.child("type").getValue(String.class),
                            dataSnapshot.child("location").getValue(String.class),
                            dataSnapshot.child("imageName").getValue(String.class),
                            dataSnapshot.child("favourite").getValue(Boolean.class)
                    );
                    if (favouritesFilter) {
                        if (dataSnapshot.child("favourite").getValue(Boolean.class))
                            historicalSiteList.add(historicalSite);
                    } else historicalSiteList.add(historicalSite);

                }
                listView.setAdapter(new ListAdapter(Routes.this, historicalSiteList, routeList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Routes.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}