package com.example.uncoveringhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Routes extends AppCompatActivity {

    ListView listView;
    List<HistoricalSite> historicalSiteList;
    List<String> routeList = new ArrayList<>();
    DatabaseReference siteDbRef;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

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
                            dataSnapshot.child("checked").getValue(Boolean.class)
                    );
                    historicalSiteList.add(historicalSite);
                }
                listView.setAdapter(new ListAdapter(Routes.this, historicalSiteList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Routes.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button createRouteButton = findViewById(R.id.create_route);
        createRouteButton.setOnClickListener(v -> {
            int selectSites = 0;
            for (int index = 0; index < historicalSiteList.size(); index++) {
                    routeList.add(historicalSiteList.get(index).getName());
                selectSites += 1;
            }
            if (selectSites > 1 && routeList != null) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("routeList", routeList.toString());
                startActivity(intent);
            } else Toast.makeText(Routes.this, "Please select at least 2 Sites First", Toast.LENGTH_LONG).show();
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