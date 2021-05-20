package com.example.uncoveringhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

//      Strings that need to be passed between activities are added to the intent
//      And need to be set to new variables, because activities are volatile
        siteName = getIntent().getStringExtra("selectedSite");
        routeList = getIntent().getStringExtra("routeList");

//      Sets the database reference and then listens for the reference to be received
        siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");
        siteDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                  Only the site the user selected is required and the For loop needs to broken once found
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
//              The showSite function is called to display the Site on the Site Page activity
                showSite(historicalSite);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SitePage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        The following allows users to switch between pages
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
//          A Try Catch is required in case a temporary file cannot be made, as a result of the createTempFile function
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

//      Gets the XML element that needs to be populated
        TextView siteName = findViewById(R.id.site_page_name);
//      Populates the element
        siteName.setText(historicalSite.getName());

        TextView siteType = findViewById(R.id.site_page_type);
        siteType.setText(historicalSite.getType());

        TextView siteLocation = findViewById(R.id.site_page_location);
        siteLocation.setText(historicalSite.getLocation());

        TextView siteDes = findViewById(R.id.site_page_description);
        siteDes.setText(historicalSite.getDescription());

//      The following waits for the user to click on the Add To Favourites button and a function is called to complete the task
        findViewById(R.id.add_to_favourite).setOnClickListener(v -> {
//      Sets the database reference and then listens for the reference to be received
            siteDbRef = FirebaseDatabase.getInstance().getReference("Historical Sites");
            siteDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (historicalSite.getName().equals(dataSnapshot.child("name").getValue(String.class))) {
                            Log.d("UncoveringHistory", "rwadfsg " + dataSnapshot.getKey());
                            siteDbRef.child(dataSnapshot.getKey()).child("favourite").setValue(!historicalSite.getFavourite());
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SitePage.this, "Favourites Status Couldn't be updated.", Toast.LENGTH_LONG).show();
                }
            });
        });

//      The following waits for the user to click on the Add To Route button and a function is called to complete the task
        findViewById(R.id.add_to_route).setOnClickListener(v -> {
//          The Historical Site Name is added to the String of Routes
            routeList += historicalSite.getName();
//          An Intent is made, the Route List is added and the intent to the Routes is called
            Intent intent = new Intent(getApplicationContext(), Routes.class);
            intent.putExtra("routeList", routeList);
            startActivity(intent);
        });

    }
}