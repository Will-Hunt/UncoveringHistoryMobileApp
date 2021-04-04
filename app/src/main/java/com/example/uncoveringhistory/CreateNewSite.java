package com.example.uncoveringhistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewSite extends AppCompatActivity {

    EditText siteName, siteDescription, siteLoc;
    Button submitBtn;
    DatabaseReference siteDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_site);

        siteName = findViewById(R.id.historical_site_name);
        siteDescription = findViewById(R.id.historical_site_description);
        siteLoc = findViewById(R.id.historical_site_location);
        submitBtn = findViewById(R.id.submit_historical_site);

        siteDbRef = FirebaseDatabase.getInstance().getReference().child("Historical Sites");
        submitBtn.setOnClickListener(v -> {
            String name = siteName.getText().toString();
            String description = siteDescription.getText().toString();
            String location = siteLoc.getText().toString();
            HistoricalSite site =  new HistoricalSite(name,description,location);

            siteDbRef.push().setValue(site);
            startActivity(new Intent(CreateNewSite.this, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            Toast.makeText(getApplicationContext(), "Google Authentication Successful", Toast.LENGTH_LONG).show();
        });
    }
}