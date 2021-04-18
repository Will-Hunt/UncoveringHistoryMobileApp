package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    DatabaseReference siteDbRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.map);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.map:
                    return true;
                case R.id.routes:
                    startActivity(new Intent(getApplicationContext(), Routes.class));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    Double latitude = dataSnapshot.child("location").child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("location").child("longitude").getValue(Double.class);
                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(location).title(name));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Map.this, "Error loading Markers", Toast.LENGTH_SHORT).show();
            }
        });
    }


//        // adding on click listener to marker of google maps.
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                // on marker click we are getting the title of our marker
//                // which is clicked and displaying it in a toast message.
//                String markerName = marker.getTitle();
//                Toast.makeText(MapsActivity.this, "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
}