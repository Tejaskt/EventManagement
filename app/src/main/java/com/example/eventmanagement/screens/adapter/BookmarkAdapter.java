package com.example.eventmanagement.screens.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventmanagement.databinding.ItemBookmarkBinding;
import com.example.eventmanagement.screens.model.Bookmark;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Bookmark> bookmarkList;
    private BookmarkActionListener listener;

    public interface BookmarkActionListener {
        void onViewEvent(Bookmark bookmark);
        void onRemoveBookmark(Bookmark bookmark);

    }

    public BookmarkAdapter(List<Bookmark> bookmarkList, BookmarkActionListener listener) {
        this.bookmarkList = bookmarkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookmarkBinding binding = ItemBookmarkBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookmarkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.bind(bookmark);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private ItemBookmarkBinding binding;

        public BookmarkViewHolder(ItemBookmarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Bookmark bookmark) {
            binding.textViewEventName.setText(bookmark.getEventName());
            binding.textViewEventDate.setText(bookmark.getEventDate());

            // Load event image
            if (bookmark.getEventImageUrl() != null && !bookmark.getEventImageUrl().isEmpty()) {
                Glide.with(binding.imageViewEvent.getContext())
                        .load(bookmark.getEventImageUrl())
                        .centerCrop()
                        .into(binding.imageViewEvent);
            }

            // Set click listeners
            binding.buttonViewEvent.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewEvent(bookmark);
                }
            });

            binding.buttonRemoveBookmark.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveBookmark(bookmark);
                }
            });
        }
    }
}