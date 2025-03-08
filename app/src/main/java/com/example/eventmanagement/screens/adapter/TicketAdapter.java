package com.example.eventmanagement.screens.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanagement.databinding.ItemTicketBinding;
import com.example.eventmanagement.screens.model.Participation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Participation> ticketList;
    private TicketActionListener listener;

    public interface TicketActionListener {
        void onCancelTicket(Participation participation);

    }

    public TicketAdapter(List<Participation> ticketList, TicketActionListener listener) {
        this.ticketList = ticketList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketBinding binding = ItemTicketBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Participation ticket = ticketList.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {
        private ItemTicketBinding binding;

        public TicketViewHolder(ItemTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Participation ticket) {
            binding.textViewEventName.setText(ticket.getEventName());
            binding.textViewEventDate.setText(ticket.getEventDate());
            binding.textViewEventTime.setText(ticket.getEventTime());
            binding.textViewEventLocation.setText(ticket.getEventLocation());

            // Format participation date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String participationDate = dateFormat.format(new Date(ticket.getParticipationDate()));
            binding.textViewParticipationDate.setText("Registered on: " + participationDate);

            // Set click listener for cancel button
            binding.buttonCancelTicket.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelTicket(ticket);
                }
            });
        }
    }
}