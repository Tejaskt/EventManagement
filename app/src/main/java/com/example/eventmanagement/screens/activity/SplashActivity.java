package com.example.eventmanagement.screens.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanagement.R;
import com.example.eventmanagement.admin.screens.AdminDashboard;
import com.example.eventmanagement.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private MediaPlayer mediaPlayer;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        ImageView logo = findViewById(R.id.imageViewLogo);

        // Fade-in animation
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000); // 2 seconds
        logo.startAnimation(fadeIn);

        // Play sound effect
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_sound);
        mediaPlayer.start();

        // Delay and move to MainActivity
        new Handler().postDelayed(this::UserLogInData, SPLASH_DURATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    /*
    private void UserLogInData(){
        // saved login data
        if (mAuth.getCurrentUser() != null) {

            // User is logged in, go to Dashboard
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // No user logged in, go to Login screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }*/

    private void UserLogInData() {
        if (mAuth.getCurrentUser() != null) {
            String userEmail = mAuth.getCurrentUser().getEmail();

            if (userEmail != null && userEmail.equals("admin@eventmanagement.com")) {
                startActivity(new Intent(this, AdminDashboard.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

}


