package com.example.eventmanagement.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanagement.screens.activity.MainActivity;
import com.example.eventmanagement.R;
import com.example.eventmanagement.admin.screens.AdminDashboard;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // Define admin credentials
    private static final String ADMIN_EMAIL = "admin@eventmanagement.com";
    private static final String ADMIN_PASSWORD = "admin123456";

    private TextInputEditText emailET, passwordET;
    private MaterialButton loginBtn, forgotPassBtn;
    private TextView signUpLink;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.logInEmail);
        passwordET = findViewById(R.id.logInPassword);
        loginBtn = findViewById(R.id.login_btn);
        forgotPassBtn = findViewById(R.id.forgot_pass);
        signUpLink = findViewById(R.id.signUpLink);
        progressBar = findViewById(R.id.logInProgressBar);

        loginBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailET.getText()).toString().trim();
            String password = Objects.requireNonNull(passwordET.getText()).toString().trim();

            if (!isValidEmail(email)) {
                emailET.setError(getString(R.string.email_val));
                return;
            }

            if (!isValidPassword(password)) {
                passwordET.setError(getString(R.string.password_val));
                return;
            }

            // Check if admin credentials were entered
            if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                loginAsAdmin();
            } else {
                loginUser(email, password);
            }
        });

        forgotPassBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            finish();
        });

        signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void loginAsAdmin() {
        progressBar.setVisibility(View.VISIBLE);

        // Sign in with admin credentials
        mAuth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                        // Redirect to admin dashboard
                        Intent adminIntent = new Intent(LoginActivity.this, AdminDashboard.class);
                        startActivity(adminIntent);
                        finish();
                    } else {
                        // If admin account doesn't exist in Firebase, create it
                        createAdminAccount();
                    }
                });
    }

    private void createAdminAccount() {
        mAuth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Admin account created and logged in", Toast.LENGTH_SHORT).show();
                        Intent adminIntent = new Intent(LoginActivity.this, AdminDashboard.class);
                        startActivity(adminIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Admin login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) { //&& user.isEmailVerified()
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email first", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean isValidPassword(CharSequence password) {
        return (!TextUtils.isEmpty(password) && password.length() > 8);
    }
}