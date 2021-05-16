package com.example.uncoveringhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SitePage extends AppCompatActivity {
    HistoricalSite historicalSite;
    StorageReference imageToShow;
    DatabaseReference siteDbRef;
    String siteName, routeList;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);

        siteName = getIntent().getStringExtra("selectedSite");
        routeList = getIntent().getStringExtra("routeList");

        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot.child("name").getValue(String.class), siteName)) {
                        historicalSite = new HistoricalSite(dataSnapshot.child("name").getValue(String.class),
                                dataSnapshot.child("description").getValue(String.class),
                                dataSnapshot.child("type").getValue(String.class),
                                dataSnapshot.child("location").getValue(String.class),
                                dataSnapshot.child("imageName").getValue(String.class),
                                dataSnapshot.child("favourite").getValue(Boolean.class)
                        );
                        break;
                    }
                }
                showSite(historicalSite);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SitePage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.map);

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
                case R.id.profile:
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    public void showSite(HistoricalSite historicalSite) {
        try {
            ImageView siteImage = findViewById(R.id.site_page_image);
            File file = File.createTempFile("image", "jpg");
            String image = "gs://uncovering-history-mobile-app.appspot.com/images/" + historicalSite.getImageName();
            imageToShow = FirebaseStorage.getInstance().getReferenceFromUrl(image);
            imageToShow.getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                siteImage.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView siteName = findViewById(R.id.site_page_name);
        siteName.setText(historicalSite.getName());

        TextView siteType = findViewById(R.id.site_page_type);
        siteType.setText(historicalSite.getType());

        TextView siteLocation = findViewById(R.id.site_page_location);
        siteLocation.setText(historicalSite.getLocation());

        TextView siteDes = findViewById(R.id.site_page_description);
        siteDes.setText(historicalSite.getDescription());

        Button favouriteButton = findViewById(R.id.add_to_favourite);
        favouriteButton.setOnClickListener(v -> {
            siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");

            siteDbRef.updateChildren(cust1, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(MainActivity.this,"Inserted successfully",Toast.LENGTH_LONG).show();
                }
            });
        });

        Button addSiteButton = findViewById(R.id.add_to_route);
        addSiteButton.setOnClickListener(v -> {
            routeList += historicalSite.getName();
            Intent intent = new Intent(getApplicationContext(), Routes.class);
            intent.putExtra("routeList", routeList);
            startActivity(intent);
        });

    }
}