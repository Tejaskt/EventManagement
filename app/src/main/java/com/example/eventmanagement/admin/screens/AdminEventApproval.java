package com.example.eventmanagement.admin.screens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventmanagement.admin.adapter.AdminEventAdapter;
import com.example.eventmanagement.databinding.ActivityAdminEventApprovalBinding;
import com.example.eventmanagement.screens.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventApproval extends AppCompatActivity implements AdminEventAdapter.EventApprovalListener {

    private ActivityAdminEventApprovalBinding binding;
    private AdminEventAdapter adapter;
    private List<Event> pendingEvents;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminEventApprovalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        pendingEvents = new ArrayList<>();

        adapter = new AdminEventAdapter(pendingEvents, this);
        binding.recyclerViewPendingEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPendingEvents.setAdapter(adapter);

        loadPendingEvents();
    }

    private void loadPendingEvents() {
        binding.progressBarAdmin.setVisibility(View.VISIBLE);
        binding.textViewNoPendingEvents.setVisibility(View.GONE);

        db.collection("events")
                .whereEqualTo("approved", false)
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBarAdmin.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        pendingEvents.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());

                            // Get Base64 image data if available
                            if (document.contains("imageBase64")) {
                                event.setImageBase64(document.getString("imageBase64"));
                            }

                            pendingEvents.add(event);
                        }

                        if (pendingEvents.isEmpty()) {
                            binding.textViewNoPendingEvents.setVisibility(View.VISIBLE);
                        } else {
                            binding.textViewNoPendingEvents.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading events: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onApproveEvent(Event event) {
        db.collection("events").document(event.getId())
                .update("approved", true)
                .addOnSuccessListener(aVoid -> {
                    pendingEvents.remove(event);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Event approved successfully!", Toast.LENGTH_SHORT).show();

                    if (pendingEvents.isEmpty()) {
                        binding.textViewNoPendingEvents.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to approve event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRejectEvent(Event event) {
        db.collection("events").document(event.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    pendingEvents.remove(event);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Event rejected and deleted!", Toast.LENGTH_SHORT).show();

                    if (pendingEvents.isEmpty()) {
                        binding.textViewNoPendingEvents.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to reject event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
