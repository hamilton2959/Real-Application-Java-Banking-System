package com.example.bankingapplication.config;

import java.util.prefs.Preferences;

public class SessionManager {

    private static String currentUser;
    private static String role;
    private static final Preferences prefs =
            Preferences.userRoot().node("banking_system_db");

    public static void setUser(String username, String userRole) {
        currentUser = username;
        role = userRole;
    }

    public static String getUser() { return currentUser; }
    public static String getRole() { return role; }

    public static void rememberUser(String username) {
        prefs.put("rememberedUser", username);
    }

    public static String getRememberedUser() {
        return prefs.get("rememberedUser", "");
    }

    public static void clearRememberedUser() {
        prefs.remove("rememberedUser");
    }
}