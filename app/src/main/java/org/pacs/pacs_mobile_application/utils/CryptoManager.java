package org.pacs.pacs_mobile_application.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import lombok.SneakyThrows;

public class CryptoManager {
    private final KeyStore keyStore;
    private static final String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING;

    @SneakyThrows
    public CryptoManager() {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
    }

    @SneakyThrows
    private Cipher getEncryptCipher() {
        Cipher encryptCipher = Cipher.getInstance(TRANSFORMATION);
        encryptCipher.init(Cipher.ENCRYPT_MODE, getKey());
        return encryptCipher;
    }
    @SneakyThrows
    private Cipher getDecryptCipherForIv(byte[] iv) {
        Cipher decryptCipher = Cipher.getInstance(TRANSFORMATION);
        decryptCipher.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(iv));
        return decryptCipher;
    }

    @SneakyThrows
    private SecretKey getKey()  {
        KeyStore.SecretKeyEntry existingKey = (KeyStore.SecretKeyEntry) keyStore.getEntry("pacs", null);
        return (existingKey != null) ? existingKey.getSecretKey() : createKey();
    }

    @SneakyThrows
    private SecretKey createKey()  {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM, "AndroidKeyStore");
        keyGenerator.init(new KeyGenParameterSpec.Builder(
                "pacs",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setKeySize(16)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build());
        return keyGenerator.generateKey();
    }

    @SneakyThrows
    public void encrypt(byte[] bytes, FileOutputStream outputStream)  {
        Cipher cipher = getEncryptCipher();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] encryptedBytes;
        try{
            encryptedBytes = cipher.doFinal(bytes);
            buffer.putInt(encryptedBytes.length);

            outputStream.write(cipher.getIV().length);
            outputStream.write(cipher.getIV());
            outputStream.write(buffer.array());
            outputStream.write(encryptedBytes);

        }catch (Exception e) {
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }

    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public byte[] decrypt(FileInputStream inputStream)  {

        int ivSize = inputStream.read();

        byte[] iv = new byte[ivSize];
        inputStream.read(iv);

        byte[] sizeBytes = new byte[4];
        inputStream.read(sizeBytes);

        int encryptedBytesSize = ByteBuffer.wrap(sizeBytes).getInt();
        byte[] encryptedBytes = new byte[encryptedBytesSize];
        inputStream.read(encryptedBytes);

        Cipher cipher = getDecryptCipherForIv(iv);

        return cipher.doFinal(encryptedBytes);
    }

}
