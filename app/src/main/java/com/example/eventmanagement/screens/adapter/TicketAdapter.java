package com.example.eventmanagement.screens.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanagement.databinding.ItemTicketBinding;
import com.example.eventmanagement.screens.model.Participation;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

        /*
        void bind(Participation ticket) {
            binding.textViewEventName.setText(ticket.getEventName());
            binding.textViewEventDate.setText(ticket.getEventDate());
            binding.textViewEventTime.setText(ticket.getEventTime());
            binding.textViewEventLocation.setText(ticket.getEventLocation());

            // Format participation date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String participationDate = dateFormat.format(new Date(ticket.getParticipationDate()));
            binding.textViewParticipationDate.setText("Registered on: " + participationDate);

            // generate qr
            generateQRCode(qrCodeImage, ticket.getId());

            // Set click listener for cancel button
            binding.buttonCancelTicket.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelTicket(ticket);
                }
            });
        }
        */

        void bind(Participation ticket) {
            binding.textViewEventName.setText(ticket.getEventName());
            binding.textViewEventDate.setText(ticket.getEventDate());
            binding.textViewEventTime.setText(ticket.getEventTime());
            binding.textViewEventLocation.setText(ticket.getEventLocation());

            // Format participation date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String participationDate = dateFormat.format(new Date(ticket.getParticipationDate()));
            binding.textViewParticipationDate.setText("Registered on: " + participationDate);

            // Generate QR code and set it to ImageView
            generateQRCode(binding.imageViewQrCode, ticket.getId());

            // Set click listener for cancel button
            binding.buttonCancelTicket.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelTicket(ticket);
                }
            });
        }

        private void generateQRCode(ImageView imageView, String text) {
            try {
                MultiFormatWriter writer = new MultiFormatWriter();
                BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }
}