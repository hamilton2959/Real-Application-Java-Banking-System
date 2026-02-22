package com.example.bankingapplication.controllers;

import java.math.BigDecimal;

// Utility class for validation
public class ValidationUtil {

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("ACC-\\d{5}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
