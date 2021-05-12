package com.example.uncoveringhistory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    AuthCredential authCredential;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference siteDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call Map straight away
        // startActivity(new Intent(this, Map.class));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) { //If a user is signed in
            startActivity(new Intent(MainActivity.this, Map.class));
        }

        // Initializing Sign In Options
        googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("823499817950-cq25ea5eqkftbj9aul54o7u6u2ceg8lc.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Initializing Sign In Client
        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

        findViewById(R.id.google_login).setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100); //Start Activity for Result
        });

        findViewById(R.id.login_button).setOnClickListener(view -> {
            login();
        });

        findViewById(R.id.register_button).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, user_registration.class)));

    }

    private void firebaseAuthentication(AuthCredential authCredential) {
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) { //If the Authentication was successful call the Profile Class
                        Toast.makeText(getApplicationContext(), "Google Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Map.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        Toast.makeText(getApplicationContext(), "Google Login Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) { //If the RequestCode is for Firebase Authentication then continue
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Google Authentication Successful", Toast.LENGTH_SHORT).show();
            }
            try {
                googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    firebaseAuthentication(authCredential);
                }

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    public void login() {
        EditText emailEditText = findViewById(R.id.login_EmailAddress);
        String email = emailEditText.getText().toString();
        EditText passwordEditText = findViewById(R.id.login_Password);
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(MainActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, Map.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}