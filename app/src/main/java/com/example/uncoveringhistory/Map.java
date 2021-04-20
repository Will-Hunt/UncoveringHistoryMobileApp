package com.example.uncoveringhistory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    DatabaseReference siteDbRef;
    String selectedSite = null;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button selectedMarker = findViewById(R.id.selected_marker);
        selectedMarker.setOnClickListener(v -> {
            if (selectedSite != null) {
                Intent intent = new Intent(getApplicationContext(), SitePage.class);
                intent.putExtra("selectedSite", selectedSite);
                startActivity(intent);
            } else Toast.makeText(Map.this, "Please select a Site First", Toast.LENGTH_LONG).show();
        });

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

    public LatLng getLocationFromAddress(String strAddress) {
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        final List<Address> address;
        LatLng latLng = null;
        try {
            address = geocoder.getFromLocationName(strAddress, 1);
            if (address == null) return null;
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            Toast.makeText(Map.this, "Error Occurred, Restart Device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return latLng;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String streetAddress = dataSnapshot.child("location").getValue(String.class);
                    LatLng location = getLocationFromAddress(streetAddress);
                    if (location != null) {
                        googleMap.addMarker(new MarkerOptions().position(location).title(name));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Map.this, "Error loading Markers", Toast.LENGTH_SHORT).show();
            }
        });

        googleMap.setOnMarkerClickListener(marker -> {
            selectedSite = marker.getTitle();
            Toast.makeText(Map.this, "Selected location is " + selectedSite, Toast.LENGTH_SHORT).show();
            return false;
        });
    }


}