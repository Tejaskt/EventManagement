package com.example.eventmanagement.screens.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanagement.databinding.ActivityCreateEventBinding;
import com.example.eventmanagement.screens.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


public class CreateEvent extends AppCompatActivity {

    private ActivityCreateEventBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Calendar startDateCalendar, endDateCalendar;
    private SimpleDateFormat dateFormat, timeFormat;
    private String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.fabAddImage.setOnClickListener(v -> openImagePicker());
        binding.editTextStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.editTextEndDate.setOnClickListener(v -> showDatePicker(false));
        binding.editTextEventTime.setOnClickListener(v -> showTimePicker());
        binding.buttonCreateEvent.setOnClickListener(v -> validateAndCreateEvent());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imageViewEventBanner.setImageURI(imageUri);
            convertImageToBase64();
        }
    }

    private void convertImageToBase64() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("CreateEvent", "Image conversion failed", e);
        }
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if (isStartDate) {
                binding.editTextStartDate.setText(dateFormat.format(calendar.getTime()));
            } else {
                binding.editTextEndDate.setText(dateFormat.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            binding.editTextEventTime.setText(timeFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void validateAndCreateEvent() {
        String eventName = binding.editTextEventName.getText().toString().trim();
        String startDate = binding.editTextStartDate.getText().toString().trim();
        String endDate = binding.editTextEndDate.getText().toString().trim();
        String eventTime = binding.editTextEventTime.getText().toString().trim();
        String eventLocation = binding.editTextEventLocation.getText().toString().trim();
        String priceStr = binding.editTextRegistrationPrice.getText().toString().trim();
        String description = binding.editTextEventDescription.getText().toString().trim();
        String organizers = binding.editTextOrganizers.getText().toString().trim();
        String paymentMethods = binding.editTextPaymentMethods.getText().toString().trim();
        String contactPhone = binding.editTextContactPhone.getText().toString().trim();
        String contactEmail = binding.editTextContactEmail.getText().toString().trim();

        if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() ||
                eventTime.isEmpty() || eventLocation.isEmpty() || priceStr.isEmpty() ||
                description.isEmpty() || organizers.isEmpty() || paymentMethods.isEmpty() ||
                contactPhone.isEmpty() || contactEmail.isEmpty() || base64Image == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an event image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        binding.progressBarCreateEvent.setVisibility(View.VISIBLE);
        binding.buttonCreateEvent.setEnabled(false);


        Event event = new Event();
        event.setName(eventName);
        event.setImageUrl(base64Image);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setTime(eventTime);
        event.setLocation(eventLocation);
        event.setPrice(price);
        event.setDescription(description);
        event.setOrganizers(organizers);
        event.setPaymentMethods(paymentMethods);
        event.setContactPhone(contactPhone);
        event.setContactEmail(contactEmail);
        event.setCreatorId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        event.setCreatedAt(System.currentTimeMillis());
        event.setApproved(false);

        saveEventToFirestore(event);
    }

    private void saveEventToFirestore(Event event) {
        db.collection("events").add(event).addOnSuccessListener(documentReference -> {
            binding.progressBarCreateEvent.setVisibility(View.GONE);
            Toast.makeText(this, "Event created successfully! Waiting for admin approval.", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            binding.progressBarCreateEvent.setVisibility(View.GONE);
            binding.buttonCreateEvent.setEnabled(true);
            Toast.makeText(this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
