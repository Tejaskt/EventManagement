package com.example.eventmanagement.screens.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventmanagement.screens.activity.EventDetails;
import com.example.eventmanagement.databinding.ItemEventCardBinding;
import com.example.eventmanagement.screens.model.Event;

import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventCardBinding binding = ItemEventCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private ItemEventCardBinding binding;

        public EventViewHolder(ItemEventCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.textViewEventName.setText(event.getName());
            binding.textViewEventDate.setText(event.getStartDate());
            binding.textViewEventLocation.setText(event.getLocation());
            binding.textViewEventPrice.setText(String.format(Locale.US, "$%.2f", event.getPrice()));

            // Load image with Glide
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(binding.imageViewEvent.getContext())
                        .load(event.getImageUrl())
                        .centerCrop()
                        .into(binding.imageViewEvent);
            }

            // Set click listener for the view details button
            binding.buttonViewDetails.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EventDetails.class);
                intent.putExtra("EVENT_ID", event.getId());
                v.getContext().startActivity(intent);
            });

            // Set click listener for the entire card
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EventDetails.class);
                intent.putExtra("EVENT_ID", event.getId());
                v.getContext().startActivity(intent);
            });


            // Extra
            /*binding.buttonApprove.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EventDetails.class);
                intent.putExtra("EVENT_ID", event.getId());
                v.getContext().startActivity(intent);
            });*/


        }
    }
}