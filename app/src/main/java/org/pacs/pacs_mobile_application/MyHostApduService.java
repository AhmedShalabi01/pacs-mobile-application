package org.pacs.pacs_mobile_application;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.pacs.pacs_mobile_application.pojo.CryptoManger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MyHostApduService extends HostApduService {

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d("HCE", "processCommandApdu");
        Log.d("HCE", Arrays.toString(commandApdu));


        return "Hello".getBytes();
//        return fetchAttributes().getBytes();
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d("HCE", "Deactivated: " + reason);
    }

    private String fetchAttributes() {
        CryptoManger cryptoManger = new CryptoManger();
        Gson gson = new Gson();
        File file = new File(getFilesDir(), "secret.txt");
        FileInputStream streamInput;

//        String json = gson.toJson(attributes).substring(0,239);

        try {
            streamInput = new FileInputStream(file);
            byte[] outputDecrypted = cryptoManger.decrypt(streamInput);
            return new String(outputDecrypted);
        } catch (Exception e) {
            Log.e("AttributeJson",e.getMessage());
            e.printStackTrace();
        }
        return "Fail";
    }
}