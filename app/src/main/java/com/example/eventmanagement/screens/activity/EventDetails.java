package com.example.eventmanagement.screens.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eventmanagement.R;
import com.example.eventmanagement.databinding.ActivityEventDetailsBinding;
import com.example.eventmanagement.screens.model.Event;
import com.example.eventmanagement.screens.model.Participation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventDetails extends AppCompatActivity {

    private ActivityEventDetailsBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String eventId;
    private Event event;
    private boolean isParticipating = false;
    private boolean isBookmarked = false;
    private String participationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get event ID from intent
        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up click listeners
        binding.buttonBack.setOnClickListener(v -> finish());

        binding.buttonParticipate.setOnClickListener(v -> {
            if (isParticipating) {
                cancelParticipation();
            } else {
                participateInEvent();
            }
        });

        binding.buttonBookmark.setOnClickListener(v -> {
            if (isBookmarked) {
                removeBookmark();
            } else {
                bookmarkEvent();
            }
        });

        // Load event details
        loadEventDetails();
    }

    private void loadEventDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);

                    if (documentSnapshot.exists()) {
                        event = documentSnapshot.toObject(Event.class);
                        event.setId(documentSnapshot.getId());
                        displayEventDetails();

                        // Check if user is participating
                        checkParticipationStatus();

                        // Check if event is bookmarked
                        checkBookmarkStatus();
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayEventDetails() {
        // Set event details in the UI
        binding.textViewEventName.setText(event.getName());
        binding.textViewEventDate.setText(event.getStartDate() + " - " + event.getEndDate());
        binding.textViewEventTime.setText(event.getTime());
        binding.textViewEventLocation.setText(event.getLocation());

        // Format price with currency
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setCurrency(Currency.getInstance("USD"));
        binding.textViewEventPrice.setText(format.format(event.getPrice()));

        binding.textViewEventDescription.setText(event.getDescription());
        binding.textViewEventOrganizers.setText(event.getOrganizers());
        binding.textViewPaymentMethods.setText(event.getPaymentMethods());
        binding.textViewContactPhone.setText(event.getContactPhone());
        binding.textViewContactEmail.setText(event.getContactEmail());

        // Load event image
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(event.getImageUrl())
                    .centerCrop()
                    .into(binding.imageViewEvent);
        }
    }

    private void checkParticipationStatus() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("participations")
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User is already participating
                        isParticipating = true;
                        participationId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        binding.buttonParticipate.setText("Cancel Participation");
                    } else {
                        isParticipating = false;
                        binding.buttonParticipate.setText("Participate");
                    }
                });
    }

    private void checkBookmarkStatus() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("bookmarks")
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Event is bookmarked
                        isBookmarked = true;
                        binding.buttonBookmark.setImageResource(R.drawable.ic_bookmark);
                    } else {
                        isBookmarked = false;
                        binding.buttonBookmark.setImageResource(R.drawable.ic_bookmark);
                    }
                });
    }

    private void participateInEvent() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        // Create participation object
        Participation participation = new Participation();
        participation.setUserId(userId);
        participation.setEventId(eventId);
        participation.setEventName(event.getName());
        participation.setEventDate(event.getStartDate());
        participation.setEventTime(event.getTime());
        participation.setEventLocation(event.getLocation());
        participation.setParticipationDate(System.currentTimeMillis());

        // Save to Firestore
        db.collection("participations")
                .add(participation)
                .addOnSuccessListener(documentReference -> {
                    binding.progressBar.setVisibility(View.GONE);
                    participationId = documentReference.getId();
                    isParticipating = true;
                    binding.buttonParticipate.setText("Cancel Participation");
                    Toast.makeText(this, "Successfully registered for the event!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to register: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void cancelParticipation() {
        if (participationId == null) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("participations").document(participationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    isParticipating = false;
                    binding.buttonParticipate.setText("Participate");
                    Toast.makeText(this, "Participation cancelled", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to cancel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void bookmarkEvent() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        // Create bookmark object
        Map<String, Object> bookmark = new HashMap<>();
        bookmark.put("userId", userId);
        bookmark.put("eventId", eventId);
        bookmark.put("eventName", event.getName());
        bookmark.put("eventDate", event.getStartDate());
        bookmark.put("eventImageUrl", event.getImageUrl());
        bookmark.put("bookmarkedAt", System.currentTimeMillis());

        // Save to Firestore
        db.collection("bookmarks")
                .add(bookmark)
                .addOnSuccessListener(documentReference -> {
                    binding.progressBar.setVisibility(View.GONE);
                    isBookmarked = true;
                    binding.buttonBookmark.setImageResource(R.drawable.ic_bookmark);
                    Toast.makeText(this, "Event bookmarked", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void removeBookmark() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("bookmarks")
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        documentSnapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    isBookmarked = false;
                                    binding.buttonBookmark.setImageResource(R.drawable.ic_bookmark);
                                    Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}