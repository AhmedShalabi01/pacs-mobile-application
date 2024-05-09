package org.pacs.pacs_mobile_application.utils;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class MyHostApduService extends HostApduService {

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d("HCE", "processCommandApdu");
        Log.d("HCE", Arrays.toString(commandApdu));

        return readAttributesFromTempFile(this).getBytes();
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d("HCE", "Deactivated: " + reason);
    }

    private String readAttributesFromTempFile(MyHostApduService context) {
        CryptoManager cryptoManager = new CryptoManager();
        File tempFile = new File(context.getCacheDir(), "secret.txt");
        String decryptedAttributes;
        FileInputStream streamInput;

        try {
            streamInput = new FileInputStream(tempFile);
            byte[] decryptedBytes = cryptoManager.decrypt(streamInput);
            decryptedAttributes = new String(decryptedBytes);
        } catch (IOException e) {
            Log.e("Error in reading file", Objects.requireNonNull(e.getMessage()));
            decryptedAttributes = "6A0F";
        }

        return decryptedAttributes;
    }
}