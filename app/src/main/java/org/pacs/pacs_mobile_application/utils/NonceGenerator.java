package org.pacs.pacs_mobile_application.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NonceGenerator {
    public static List<String> generateNonceSequence(String seed, int sequenceLength) {
        List<String> nonceSequence = new ArrayList<>();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] seedBytes = seed.getBytes();
            byte[] hash = digest.digest(seedBytes);

            for (int i = 0; i < sequenceLength; i++) {
                nonceSequence.add(bytesToHex(hash));
                hash = digest.digest(hash);
            }
        } catch (Exception e) {
            Log.i("Nonce Generation Exception", Objects.requireNonNull(e.getMessage()));
        }
        return nonceSequence;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}