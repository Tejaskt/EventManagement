package com.example.eventmanagement.screens.payment;

import android.app.Activity;
import android.util.Log;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

public class PaymentUtils {
    private static final String TAG = "PaymentUtils";

    // Initialize Razorpay Checkout
    public static void initRazorpay(Activity activity) {
        Checkout.preload(activity.getApplicationContext());
    }

    // Start payment for event creation
    public static void startEventCreationPayment(Activity activity, String eventName,
                                                 double amount, String email, String phone,
                                                 String razorpayKey) {
        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID(razorpayKey);

            JSONObject options = new JSONObject();
            options.put("name", "Event Management");
            options.put("description", "Event Creation Fee: " + eventName);
            options.put("currency", "INR");
            options.put("amount", amount * 100); // Convert to paise

            JSONObject prefill = new JSONObject();
            prefill.put("email", email);
            prefill.put("contact", phone);
            options.put("prefill", prefill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting payment: " + e.getMessage());
        }
    }

    // Start payment for event participation
    public static void startEventParticipationPayment(Activity activity, String eventName,
                                                      double amount, String email, String phone,
                                                      String razorpayKey) {
        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID(razorpayKey);

            JSONObject options = new JSONObject();
            options.put("name", "Event Management");
            options.put("description", "Event Participation Fee: " + eventName);
            options.put("currency", "INR");
            options.put("amount", amount * 100); // Convert to paise

            JSONObject prefill = new JSONObject();
            prefill.put("email", email);
            prefill.put("contact", phone);
            options.put("prefill", prefill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting payment: " + e.getMessage());
        }
    }
}