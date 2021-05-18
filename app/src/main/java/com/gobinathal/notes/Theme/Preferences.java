package com.gobinathal.notes.Theme;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {
    public static void setPreferredTheme(AppCompatActivity a) {
        SharedPreferences sharedPreferences = a.getSharedPreferences("ThemePref", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("Theme", 0);
        if (theme == 0)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if (theme == 1)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (theme == 2)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
