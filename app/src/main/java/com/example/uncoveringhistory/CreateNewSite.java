package com.example.uncoveringhistory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateNewSite extends AppCompatActivity {

    final int PICK_IMAGE_REQUEST = 111;
    String name, description, location, imageName, historicalTypeSelected;
    EditText siteName, siteDescription, siteLoc;
    Spinner siteType;
    Button selectImg, submitBtn;
    ImageView siteImg;
    Uri imageUri;
    DatabaseReference databaseReference;
    StorageReference imageToUpload;
    StorageTask uploadInProgress;

    List<String> historicalTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_site);

        siteType = findViewById(R.id.historical_site_type);
        historicalTypes.add("Medieval");
        historicalTypes.add("Early Modern");
        historicalTypes.add("Modern");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, historicalTypes);
        siteType.setAdapter(dataAdapter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        siteType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                historicalTypeSelected = historicalTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectImg = findViewById(R.id.historical_site_selectImg);
        selectImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        submitBtn = findViewById(R.id.submit_historical_site);
        submitBtn.setOnClickListener(v -> {
            uploadSite();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        siteImg = findViewById(R.id.historical_site_imgView);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            siteImg.setImageURI(imageUri);
        }
    }

    // UploadImage method
    private void uploadImage() {
        imageToUpload = FirebaseStorage.getInstance().getReference().child("images/" + imageName);

        // Code for showing progressDialog while uploading
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        uploadInProgress = imageToUpload.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Upload Unsuccessful", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                });
    }

    private void uploadSite() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Historical Sites");
        siteName = findViewById(R.id.historical_site_name);
        siteDescription = findViewById(R.id.historical_site_description);
        siteLoc = findViewById(R.id.historical_site_location);

        name = siteName.getText().toString();
        description = siteDescription.getText().toString();
        location = siteLoc.getText().toString();
        imageName = UUID.randomUUID().toString();
        uploadImage();

        HistoricalSite site = new HistoricalSite(name, description, historicalTypeSelected, location, imageName, false);
        databaseReference.push().setValue(site);
        Toast.makeText(getApplicationContext(), "Site Upload Successful", Toast.LENGTH_LONG).show();
        startActivity(new Intent(CreateNewSite.this, Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}