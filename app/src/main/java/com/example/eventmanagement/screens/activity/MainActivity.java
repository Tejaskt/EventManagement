package com.example.eventmanagement.screens.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eventmanagement.R;
import com.example.eventmanagement.auth.LoginActivity;
import com.example.eventmanagement.databinding.ActivityMainBinding;
import com.example.eventmanagement.screens.fragments.BookmarksFragment;
import com.example.eventmanagement.screens.fragments.DashboardFragment;
import com.example.eventmanagement.screens.fragments.ProfileFragment;
import com.example.eventmanagement.screens.fragments.TicketsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Not signed in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set up bottom navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);

        // Set up FAB for creating events
        binding.fabCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEvent.class);
            startActivity(intent);
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.navigation_dashboard) {
                        selectedFragment = new DashboardFragment();
                    } else if (itemId == R.id.navigation_tickets) {
                        selectedFragment = new TicketsFragment();
                    } else if (itemId == R.id.navigation_bookmarks) {
                        selectedFragment = new BookmarksFragment();
                    } else if (itemId == R.id.navigation_profile) {
                        selectedFragment = new ProfileFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };
}