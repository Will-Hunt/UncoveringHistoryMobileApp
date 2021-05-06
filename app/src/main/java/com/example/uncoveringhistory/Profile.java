package com.example.uncoveringhistory;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.profile_about_button).setOnClickListener(view -> startActivity(new Intent(Profile.this, AboutPage.class)));
        findViewById(R.id.profile_notifications_button).setOnClickListener(view -> startActivity(new Intent(Profile.this, WorkInProgressPage.class)));
        findViewById(R.id.profile_account_button).setOnClickListener(view -> startActivity(new Intent(Profile.this, WorkInProgressPage.class)));
        findViewById(R.id.profile_social_button).setOnClickListener(view -> startActivity(new Intent(Profile.this, WorkInProgressPage.class)));
        findViewById(R.id.profile_help_button).setOnClickListener(view -> startActivity(new Intent(Profile.this, WorkInProgressPage.class)));
        findViewById(R.id.add_new_site).setOnClickListener(view -> startActivity(new Intent(Profile.this, WorkInProgressPage.class)));

        firebaseAuth = FirebaseAuth.getInstance();

        googleSignInClient = GoogleSignIn.getClient(Profile.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        findViewById(R.id.logout_btn).setOnClickListener(view -> googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseAuth.signOut();
                Toast.makeText(getApplicationContext(), "Successfully Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Profile.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.profile);

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
                    startActivity(new Intent(getApplicationContext(), Favourites.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.profile:
                    return true;
            }
            return false;
        });

    }
}