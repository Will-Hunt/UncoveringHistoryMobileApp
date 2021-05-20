package com.example.uncoveringhistory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class user_registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
//      The following waits for the user to click on the Register button and a function is called to complete the task
        findViewById(R.id.register_new_user_button).setOnClickListener(view -> registerUsr());
//      The following takes the user back to the Main activity, such if they are a registered user that accidentally clicked
        findViewById(R.id.return_to_login_button).setOnClickListener(view -> startActivity(new Intent(user_registration.this, MainActivity.class)));
    }

    public void registerUsr() {
//      Now the user wants to register each of the inputs need to be set to variables
        EditText emailEditText = findViewById(R.id.register_EmailAddress);
        String email = emailEditText.getText().toString();
        EditText emailValEditText = findViewById(R.id.confirm_EmailAddress);
        String emailVal = emailValEditText.getText().toString();

        EditText passwordEditText = findViewById(R.id.register_Password);
        String password = passwordEditText.getText().toString();
        EditText passwordValEditText = findViewById(R.id.confirm_Password);
        String passwordVal = passwordValEditText.getText().toString();

//      For validation, the user is only register is the respective features equal,
//      Otherwise the user is added and a error message is given
        if (email.equals(emailVal) && password.equals(passwordVal)) {
//          CreateUserWithEmailAndPassword is a built-in function to ensure the user is correctly added
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
//                          Once the user has been added successfully, they are taken to the Map activity
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