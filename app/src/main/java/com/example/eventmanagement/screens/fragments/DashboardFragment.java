package com.example.eventmanagement.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventmanagement.screens.adapter.EventAdapter;
import com.example.eventmanagement.databinding.FragmentDashboardBinding;
import com.example.eventmanagement.screens.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);

        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewEvents.setAdapter(eventAdapter);

        loadApprovedEvents();
    }

    private void loadApprovedEvents() {
        binding.progressBarDashboard.setVisibility(View.VISIBLE);

        db.collection("events")
                .whereEqualTo("approved", true)
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBarDashboard.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());
                            eventList.add(event);
                        }

                        if (eventList.isEmpty()) {
                            binding.textViewNoEvents.setVisibility(View.VISIBLE);
                        } else {
                            binding.textViewNoEvents.setVisibility(View.GONE);
                        }

                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading events: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}