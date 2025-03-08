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

import com.example.eventmanagement.screens.adapter.TicketAdapter;
import com.example.eventmanagement.databinding.FragmentTicketsBinding;
import com.example.eventmanagement.screens.model.Participation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment implements TicketAdapter.TicketActionListener {

    private FragmentTicketsBinding binding;
    private TicketAdapter ticketAdapter;
    private List<Participation> ticketList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ticketList = new ArrayList<>();

        ticketAdapter = new TicketAdapter(ticketList, this);
        binding.recyclerViewTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTickets.setAdapter(ticketAdapter);

        loadUserTickets();
    }

    private void loadUserTickets() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("participations")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        ticketList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Participation participation = document.toObject(Participation.class);
                            participation.setId(document.getId());
                            ticketList.add(participation);
                        }

                        if (ticketList.isEmpty()) {
                            binding.textViewNoTickets.setVisibility(View.VISIBLE);
                        } else {
                            binding.textViewNoTickets.setVisibility(View.GONE);
                        }

                        ticketAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading tickets: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCancelTicket(Participation participation) {
        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("participations").document(participation.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    ticketList.remove(participation);
                    ticketAdapter.notifyDataSetChanged();

                    if (ticketList.isEmpty()) {
                        binding.textViewNoTickets.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(getContext(), "Ticket cancelled successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to cancel ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}