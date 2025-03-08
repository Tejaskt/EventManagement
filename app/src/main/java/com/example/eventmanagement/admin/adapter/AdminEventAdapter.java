package com.example.eventmanagement.admin.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
            binding.textViewEventPrice.setText(String.format(Locale.US, "$%.2f", event.getPrice()));
            binding.textViewEventDescription.setText(event.getDescription());
            binding.textViewEventOrganizer.setText("By: " + event.getOrganizers());

            // Load image from Base64 or URL
            Bitmap base64Image = event.getDecodedImage();
            if (base64Image != null) {
                binding.imageViewEvent.setImageBitmap(base64Image);
            } else if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(binding.imageViewEvent.getContext())
                        .load(event.getImageUrl())
                        .centerCrop()
                        .into(binding.imageViewEvent);
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