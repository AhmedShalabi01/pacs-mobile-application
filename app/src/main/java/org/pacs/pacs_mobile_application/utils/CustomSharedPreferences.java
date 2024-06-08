package org.pacs.pacs_mobile_application.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.Objects;

public class CustomSharedPreferences {
    private static CustomSharedPreferences instance;
    private SharedPreferences sharedPreferences;

    private CustomSharedPreferences(Context context) {
        initializeSharedPreferences(context);
    }

    public static synchronized CustomSharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new CustomSharedPreferences(context.getApplicationContext());
        }
        return instance;
    }

    private void initializeSharedPreferences(Context context) {
        MasterKey masterKey;
        try {
            masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("Error in create preference key" , Objects.requireNonNull(e.getMessage()));
        }
    }

    public void saveData(String key, String value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public String readData(String key, String defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public void deleteData(String key) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }
}