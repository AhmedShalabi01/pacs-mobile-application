package org.pacs.pacs_mobile_application.utils;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Objects;

public class MyHostApduService extends HostApduService {

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d("HCE", "processCommandApdu");
        Log.d("HCE", Arrays.toString(commandApdu));

        return fetchAttributes().getBytes();
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d("HCE", "Deactivated: " + reason);
    }


    private String fetchAttributes() {
        CryptoManager cryptoManager = new CryptoManager();
        File file = new File(getFilesDir(), "secret.txt");
        FileInputStream streamInput;

        try {
            streamInput = new FileInputStream(file);
            byte[] outputDecrypted = cryptoManager.decrypt(streamInput);
            return new String(outputDecrypted);
        } catch (Exception e) {
            Log.e("Error in decryption", Objects.requireNonNull(e.getMessage()));
        }
        return "Fail";
    }
}