package com.example.eventmanagement.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanagement.MainActivity;
import com.example.eventmanagement.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText firstNameET, lastNameET, emailET, phoneNumberET, passwordET, confirmPasswordET;
    private MaterialButton signUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase if not already initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }


        // Initialize Firebase Auth & Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameET = findViewById(R.id.signUpFirstNameET);
        lastNameET = findViewById(R.id.signUpLastNameET);
        emailET = findViewById(R.id.signUpEmailET);
        phoneNumberET = findViewById(R.id.signUpPhoneNumberET);
        passwordET = findViewById(R.id.signUpPasswordET);
        confirmPasswordET = findViewById(R.id.signUpConfirmPasswordET);
        signUpBtn = findViewById(R.id.signUpBtn);
        progressBar = findViewById(R.id.signUpProgressBar);

        signUpBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        String fName = Objects.requireNonNull(firstNameET.getText()).toString().trim();
        String lName = Objects.requireNonNull(lastNameET.getText()).toString().trim();
        String email = Objects.requireNonNull(emailET.getText()).toString().trim();
        String phoneNumber = Objects.requireNonNull(phoneNumberET.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordET.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordET.getText()).toString().trim();

        if (TextUtils.isEmpty(fName)) {
            firstNameET.setError(getString(R.string.fname_val));
            return;
        }
        if (TextUtils.isEmpty(lName)) {
            lastNameET.setError(getString(R.string.lname_val));
            return;
        }
        if (!isValidEmail(email)) {
            emailET.setError(getString(R.string.email_val));
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberET.setError(getString(R.string.phone_val));
            return;
        }
        if (!isValidPassword(password)) {
            passwordET.setError(getString(R.string.password_val));
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Firebase Authentication - Creating User
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        saveUserData(userId, fName, lName, email, phoneNumber);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupActivity.this, "Signup Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(String userId, String fName, String lName, String email, String phoneNumber) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", fName);
        user.put("lastName", lName);
        user.put("email", email);
        user.put("phoneNumber", phoneNumber);

        db.collection("users").document(userId).set(user)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Error saving data: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("SignupActivity", "Error saving data: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPassword(CharSequence password) {
        return (!TextUtils.isEmpty(password) && password.length() > 8);
    }

    public void goToLogin(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
}
