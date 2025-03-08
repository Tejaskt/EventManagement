package com.example.eventmanagement.admin.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanagement.auth.LoginActivity;
import com.example.eventmanagement.databinding.ActivityAdminDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboard extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Set up click listeners
        binding.cardPendingEvents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminEventApproval.class);
            startActivity(intent);
        });

        binding.buttonLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(AdminDashboard.this, LoginActivity.class));
            finish();
        });

        // Load statistics
        loadStatistics();
    }

    private void loadStatistics() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // Count users
        db.collection("users").get().addOnSuccessListener(userSnapshot -> {
            int userCount = userSnapshot.size();
            binding.textViewUserCount.setText(String.valueOf(userCount));

            // Count events
            db.collection("events").get().addOnSuccessListener(eventSnapshot -> {
                int totalEvents = eventSnapshot.size();
                binding.textViewEventCount.setText(String.valueOf(totalEvents));

                // Count pending events
                db.collection("events")
                        .whereEqualTo("approved", false)
                        .get()
                        .addOnSuccessListener(pendingSnapshot -> {
                            int pendingCount = pendingSnapshot.size();
                            binding.textViewPendingCount.setText(String.valueOf(pendingCount));

                            // Count participations
                            db.collection("participations").get().addOnSuccessListener(participationSnapshot -> {
                                int participationCount = participationSnapshot.size();
                                binding.textViewParticipationCount.setText(String.valueOf(participationCount));
                                binding.progressBar.setVisibility(View.GONE);
                            });
                        });
            });
        }).addOnFailureListener(e -> binding.progressBar.setVisibility(View.GONE));
    }
}