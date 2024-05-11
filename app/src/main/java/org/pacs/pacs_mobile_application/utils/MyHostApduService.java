package org.pacs.pacs_mobile_application.utils;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.pacs.pacs_mobile_application.ui.LoginActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MyHostApduService extends HostApduService {

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d("HCE", "processCommandApdu");
        Log.d("HCE", Arrays.toString(commandApdu));

        String updatedAttributesJson = "6A0F";

        try {
            Gson gson = new Gson();
            String attributesJson = readAttributesFromTempFile();
            String nonce = fetchAccessNonce();
            JsonObject payload = new JsonObject();
            payload.addProperty("UAT", attributesJson);
            payload.addProperty("NC", nonce);
            updatedAttributesJson = gson.toJson(payload);

        } catch (Exception e) {
            Log.e("Error Parsing Json Object", Objects.requireNonNull(e.getMessage()));
        }

        return updatedAttributesJson.getBytes();
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d("HCE", "Deactivated: " + reason);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, MyHostApduService.class);
        stopService(serviceIntent);
    }

    private String readAttributesFromTempFile() {
        CryptoManager cryptoManager = new CryptoManager();
        File tempFile = new File(getCacheDir(), "secretATT.txt");
        String decryptedAttributes = null;
        if (tempFile.exists()) {
            try (FileInputStream streamInput = new FileInputStream(tempFile)) {
                byte[] decryptedBytes = cryptoManager.decrypt(streamInput);
                decryptedAttributes = new String(decryptedBytes);
            } catch (IOException e) {
                Log.e("Error in reading file", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            Log.e("FileNotFound", "Temporary file not found");
        }

        return decryptedAttributes;
    }

    private String fetchAccessNonce() {
        String nonce = null;
        CustomSharedPreferences customSharedPreferences = CustomSharedPreferences.getInstance(this);

        try {
            String noncesString = customSharedPreferences.readData("noncesList","");
            int index = Integer.parseInt(customSharedPreferences.readData("currentIndex",""));

            List<String> noncesList = convertStringToList(noncesString);

            if(index<=10) {
                nonce = noncesList.get(index);
                customSharedPreferences.saveData("currentIndex",String.valueOf(index+1));
            } else {
              Toast.makeText(this,"Key Session is Expired, Please Login again",Toast.LENGTH_LONG).show();
              goToLoginActivity();
            }
        } catch (Exception e) {
            Log.e("Error in fetching the nonce", Objects.requireNonNull(e.getMessage()));
        }
        return nonce;
    }

    private List<String> convertStringToList(String input) {
        List<String> resultList = new ArrayList<>();
        if (input != null && !input.isEmpty()) {
            String[] items = input.split(",");
            Collections.addAll(resultList, items);
        }
        return resultList;
    }

    private void goToLoginActivity() {
        Intent moveToLoginActivity = new Intent(this, LoginActivity.class);
        startActivity(moveToLoginActivity);
    }
}