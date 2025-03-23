package com.example.eventmanagement.admin.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventmanagement.databinding.ItemAdminEventCardBinding;
import com.example.eventmanagement.screens.model.Event;

import java.util.List;
import java.util.Locale;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder> {

    private List<Event> eventList;
    private EventApprovalListener listener;

    public interface EventApprovalListener {
        void onRejectEvent(Event event);
        void onApproveEvent(Event event);
    }

    public AdminEventAdapter(List<Event> eventList, EventApprovalListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminEventCardBinding binding = ItemAdminEventCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminEventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class AdminEventViewHolder extends RecyclerView.ViewHolder {
        private ItemAdminEventCardBinding binding;

        public AdminEventViewHolder(ItemAdminEventCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.textViewEventName.setText(event.getName());
            binding.textViewEventDate.setText(event.getStartDate() + " - " + event.getEndDate());
            binding.textViewEventLocation.setText(event.getLocation());
            binding.textViewEventPrice.setText(String.format(Locale.US, "%.2f", event.getPrice()));
            binding.textViewEventDescription.setText(event.getDescription());
            binding.textViewEventOrganizer.setText("By: " + event.getOrganizers());


            // Load image with Glide
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(event.getImageUrl(), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                    Glide.with(binding.imageViewEvent.getContext())
                            .load(decodedBitmap)
                            .centerCrop()
                            .into(binding.imageViewEvent);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this.itemView.getContext(), "Failed to load image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // Handle decoding error
                }
            }

            // Set click listeners for approve and reject buttons
            binding.buttonApprove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onApproveEvent(event);
                }
            });

            binding.buttonReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectEvent(event);
                }
            });
        }
    }
}