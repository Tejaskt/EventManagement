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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventmanagement.screens.adapter.BookmarkAdapter;
import com.example.eventmanagement.databinding.FragmentBookmarksBinding;
import com.example.eventmanagement.screens.activity.EventDetails;
import com.example.eventmanagement.screens.model.Bookmark;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment implements BookmarkAdapter.BookmarkActionListener {

    private FragmentBookmarksBinding binding;
    private BookmarkAdapter bookmarkAdapter;
    private List<Bookmark> bookmarkList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        bookmarkList = new ArrayList<>();

        bookmarkAdapter = new BookmarkAdapter(bookmarkList, this);
        binding.recyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewBookmarks.setAdapter(bookmarkAdapter);

        loadUserBookmarks();
    }

    private void loadUserBookmarks() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("bookmarks")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {

                    //Prevent accessing null binding
                    if (binding == null) return;

                    binding.progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        bookmarkList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Bookmark bookmark = document.toObject(Bookmark.class);
                            bookmark.setId(document.getId());
                            bookmarkList.add(bookmark);
                        }

                        if (bookmarkList.isEmpty()) {
                            binding.textViewNoBookmarks.setVisibility(View.VISIBLE);
                        } else {
                            binding.textViewNoBookmarks.setVisibility(View.GONE);
                        }

                        bookmarkAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading bookmarks: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewEvent(Bookmark bookmark) {
        Intent intent = new Intent(getActivity(), EventDetails.class);
        intent.putExtra("EVENT_ID", bookmark.getEventId());
        startActivity(intent);
    }

    @Override
    public void onRemoveBookmark(Bookmark bookmark) {
        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("bookmarks").document(bookmark.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    bookmarkList.remove(bookmark);
                    bookmarkAdapter.notifyDataSetChanged();

                    if (bookmarkList.isEmpty()) {
                        binding.textViewNoBookmarks.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(getContext(), "Bookmark removed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}