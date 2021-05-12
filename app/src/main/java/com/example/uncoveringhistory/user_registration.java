package com.example.uncoveringhistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class user_registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        findViewById(R.id.register_new_user_button).setOnClickListener(view -> {
            registerUsr();
        });

        findViewById(R.id.return_to_login_button).setOnClickListener(view -> startActivity(new Intent(user_registration.this, MainActivity.class)));
    }

    public void registerUsr() {
        EditText emailEditText = findViewById(R.id.register_EmailAddress);
        String email = emailEditText.getText().toString();
        EditText emailValEditText = findViewById(R.id.confirm_EmailAddress);
        String emailVal = emailValEditText.getText().toString();

        EditText passwordEditText = findViewById(R.id.register_Password);
        String password = passwordEditText.getText().toString();
        EditText passwordValEditText = findViewById(R.id.confirm_Password);
        String passwordVal = passwordValEditText.getText().toString();


        if (email.equals(emailVal) && password.equals(passwordVal)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(user_registration.this, Map.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(user_registration.this, "Please enter valid details.", Toast.LENGTH_SHORT).show();
        }
    }
}