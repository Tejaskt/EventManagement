package com.example.eventmanagement.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.eventmanagement.auth.LoginActivity;
import com.example.eventmanagement.databinding.FragmentProfileBinding;
import com.example.eventmanagement.screens.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User currentUser;
    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up click listeners
        binding.buttonEditProfile.setOnClickListener(v -> toggleEditMode());
        binding.buttonSaveProfile.setOnClickListener(v -> saveUserProfile());
        binding.buttonLogout.setOnClickListener(v -> logoutUser());

        // Load user profile
        loadUserProfile();
    }

    private void loadUserProfile() {

        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            // Not logged in, redirect to login
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }

        String userId = firebaseUser.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {

                    //Prevent accessing null binding
                    if (binding == null) return;

                    binding.progressBar.setVisibility(View.GONE);

                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        displayUserProfile();
                    } else {
                        // Create a new user profile if it doesn't exist
                        currentUser = new User();
                        currentUser.setId(userId);
                        currentUser.setEmail(firebaseUser.getEmail());

                        // Try to get display name from Firebase Auth
                        if (firebaseUser.getDisplayName() != null) {
                            String[] names = firebaseUser.getDisplayName().split(" ", 2);
                            if (names.length > 0) {
                                currentUser.setFirstName(names[0]);
                                if (names.length > 1) {
                                    currentUser.setLastName(names[1]);
                                }
                            }
                        }

                        displayUserProfile();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayUserProfile() {
        binding.editTextFirstName.setText(currentUser.getFirstName());
        binding.editTextLastName.setText(currentUser.getLastName());
        binding.editTextEmail.setText(currentUser.getEmail());
        binding.editTextPhone.setText(currentUser.getPhone());

        // Set email field as non-editable
        binding.editTextEmail.setEnabled(false);

        // Set fields as non-editable initially
        setFieldsEditable(false);
    }

    private void toggleEditMode() {
        isEditing = !isEditing;
        setFieldsEditable(isEditing);

        if (isEditing) {
            binding.buttonEditProfile.setVisibility(View.GONE);
            binding.buttonSaveProfile.setVisibility(View.VISIBLE);
        } else {
            binding.buttonEditProfile.setVisibility(View.VISIBLE);
            binding.buttonSaveProfile.setVisibility(View.GONE);

            // Reset fields to original values
            displayUserProfile();
        }
    }

    private void setFieldsEditable(boolean editable) {
        binding.editTextFirstName.setEnabled(editable);
        binding.editTextLastName.setEnabled(editable);
        binding.editTextPhone.setEnabled(editable);
    }

    private void saveUserProfile() {
        String firstName = binding.editTextFirstName.getText().toString().trim();
        String lastName = binding.editTextLastName.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();

        if (firstName.isEmpty()) {
            binding.editTextFirstName.setError("First name is required");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // Update user object
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setPhone(phone);

        // Save to Firestore
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("phone", phone);
        userMap.put("email", currentUser.getEmail());

        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    toggleEditMode();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}