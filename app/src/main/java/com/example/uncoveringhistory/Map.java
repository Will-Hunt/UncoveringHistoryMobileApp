package com.example.uncoveringhistory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "UncoveringHistory";
    DatabaseReference siteDbRef;
    ArrayList<LatLng> markerPoints;
    String selectedSite = null;
    String routeList, name, streetAddress;
    LatLng markerLocation, currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        routeList = getIntent().getStringExtra("routeList");

        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Button selectedMarker = findViewById(R.id.selected_marker);
        selectedMarker.setOnClickListener(v -> {
            if (selectedSite != null || selectedSite.equals("Current Location")) {
                Intent intent = new Intent(getApplicationContext(), SitePage.class);
                intent.putExtra("selectedSite", selectedSite);
                startActivity(intent);
            } else
                Toast.makeText(Map.this, "Please select a Site First", Toast.LENGTH_LONG).show();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (routeList != null) {
                    markerPoints = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (routeList.contains(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class)))) {
                            markerPoints.add(addMarker(googleMap, dataSnapshot));
                        }
                    }
                    googleMap.addPolyline(new PolylineOptions()
                            .color(Color.BLUE)
                            .width(7)
                            .clickable(false)
                            .addAll(markerPoints));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPoints.get(0), 13));
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        addMarker(googleMap, dataSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Map.this, "Error loading Markers", Toast.LENGTH_SHORT).show();
            }
        });

        currentLocation = fetchLastLocation();
        if (currentLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }

        googleMap.setOnMarkerClickListener(marker -> {
            selectedSite = marker.getTitle();
            Toast.makeText(Map.this, "Selected location is " + selectedSite, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Map.this, "Error Occurred, Restart Device", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return latLng;
    }

    public LatLng addMarker(GoogleMap googleMap, DataSnapshot dataSnapshot) {
        name = dataSnapshot.child("name").getValue(String.class);
        streetAddress = dataSnapshot.child("location").getValue(String.class);
        markerLocation = getLocationFromAddress(streetAddress);
        if (markerLocation != null) {
            googleMap.addMarker(new MarkerOptions().position(markerLocation).title(name));
        }
        return markerLocation;
    }

    private LatLng fetchLastLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 78);
            return null;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
        if (currentLocation == null) currentLocation = new LatLng(51.509865, -0.118092);
        return currentLocation;
    }
}