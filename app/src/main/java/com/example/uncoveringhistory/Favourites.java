package com.example.uncoveringhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity {
    ListView listView;
    List<HistoricalSite> historicalSiteList = new ArrayList<>();
    DatabaseReference siteDbRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        listView = findViewById(R.id.favourites_site_list_view);
        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");

        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historicalSiteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!dataSnapshot.child("favourite").getValue(Boolean.class)){
                        HistoricalSite historicalSite = new HistoricalSite(dataSnapshot.child("name").getValue(String.class),
                                dataSnapshot.child("description").getValue(String.class),
                                dataSnapshot.child("type").getValue(String.class),
                                dataSnapshot.child("location").getValue(String.class),
                                dataSnapshot.child("imageName").getValue(String.class),
                                dataSnapshot.child("favourite").getValue(Boolean.class)
                        );
                        historicalSiteList.add(historicalSite);
                    }
                }
                listView.setAdapter(new ListAdapter(Favourites.this, historicalSiteList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Favourites.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.favourites);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.map:
                    startActivity(new Intent(getApplicationContext(), Map.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.routes:
                    startActivity(new Intent(getApplicationContext(), Routes.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.favourites:
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