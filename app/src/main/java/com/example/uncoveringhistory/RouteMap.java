package com.example.uncoveringhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import okhttp3.Route;

public class RouteMap extends AppCompatActivity {

    HistoricalSite historicalSite;
    List<HistoricalSite> routeList;
    String routeListAsString;
    DatabaseReference siteDbRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);

        routeListAsString = getIntent().getStringExtra("routeList");
        Log.d("UncoveringHistory", "onCreate: " + routeListAsString);
        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    if (Objects.equals(dataSnapshot.child("name").getValue(String.class), siteName)) {
//                        historicalSite = new HistoricalSite(dataSnapshot.child("name").getValue(String.class),
//                                dataSnapshot.child("description").getValue(String.class),
//                                dataSnapshot.child("type").getValue(String.class),
//                                dataSnapshot.child("location").getValue(String.class),
//                                dataSnapshot.child("imageName").getValue(String.class),
//                                dataSnapshot.child("checked").getValue(Boolean.class)
//                        );
//                        break;
//                    }
//                }
//                showSite(historicalSite);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RouteMap.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.routes);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.map:
                    startActivity(new Intent(getApplicationContext(), Map.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.routes:
                    startActivity(new Intent(getApplicationContext(), Route.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.favourites:
                    startActivity(new Intent(getApplicationContext(), Favourites.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }
}