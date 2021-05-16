package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private static final String TAG = "UncoveringHistory";
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

        routeList = getIntent().getStringExtra("routeList");

        updateRouteList();

        Button favouritesButton = findViewById(R.id.filter_favourites);
        favouritesButton.setOnClickListener(v -> {
            favouritesFilter = !(favouritesFilter);
            updateRouteList();
        });

        Button createRouteButton = findViewById(R.id.create_route);
        createRouteButton.setOnClickListener(v -> {
            if (routeList.length() > 1) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("routeList", routeList);
                startActivity(intent);
            } else
                Toast.makeText(Routes.this, "Please select at least 2 Sites First", Toast.LENGTH_LONG).show();
        });
        Button clearRouteButton = findViewById(R.id.clear_route);
        clearRouteButton.setOnClickListener(v -> routeList = "");

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

        listView = findViewById(R.id.historical_site_list_view);
        historicalSiteList = new ArrayList<>();

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