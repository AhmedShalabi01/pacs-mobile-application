package org.pacs.pacs_mobile_application.ui;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.data.BackEndClient;
import org.pacs.pacs_mobile_application.utils.CryptoManager;
import org.pacs.pacs_mobile_application.utils.ValidationPattern;
import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;


import java.io.File;

import java.io.FileOutputStream;

import java.util.Objects;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private boolean guestOption;
    private final Gson gson = new Gson();
    private BiometricPrompt biometricPrompt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeSharedPreferences();
        setupSignInButton();
        setupGuestSwitch();
        setupBiometricPrompt();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.email_login);
        passwordEditText = findViewById(R.id.password_login);
    }

    private void initializeSharedPreferences() {
        MasterKey masterKey;
        try {
            masterKey = new MasterKey.Builder(LoginActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    LoginActivity.this,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("Error in create preference key" , Objects.requireNonNull(e.getMessage()));
        }
    }

    private void setupSignInButton() {
        Button signInButton = findViewById(R.id.sign_in_btn);
        signInButton.setOnClickListener(view -> processFormFields());
    }

    private void setupGuestSwitch() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch guestSwitch = findViewById(R.id.guest);
        guestSwitch.setOnCheckedChangeListener((compoundButton, b) -> guestOption = b);
    }

    private void setupBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(LoginActivity.this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, createAuthenticationCallback());
    }

    public void goToSignUpActivity(View view) {
        Intent moveToLoginActivity = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(moveToLoginActivity);
        finish();
    }

    private void goToHomeActivity() {
        Intent moveToHomeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(moveToHomeActivity);
        finish();
    }

    public void processFormFields(){
        if (guestOption){
            if(validateField(emailEditText, ValidationPattern.EMAIL) || validateField(passwordEditText, ValidationPattern.PASSWORD)){
                return;
            }
            LoginModel loginModel = new LoginModel(emailEditText.getText().toString(), passwordEditText.getText().toString());
            validateVisitor(loginModel);
        } else {
            if(validateField(emailEditText, ValidationPattern.EMAIL) || validateField(passwordEditText, ValidationPattern.PASSWORD)){
                return;
            }
            LoginModel loginModel = new LoginModel(emailEditText.getText().toString(), passwordEditText.getText().toString());
            validateEmployee(loginModel);
        }
    }

    private void validateEmployee(LoginModel loginModel) {

        BackEndClient.getINSTANCE().validateEmployee(loginModel).enqueue(new Callback<EmployeeAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttributesModel> call, @NonNull Response<EmployeeAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    String nonce = response.headers().get("Server-Nonce");
                    JsonObject digitalKey = gson.toJsonTree(response.body()).getAsJsonObject();
                    digitalKey.addProperty("Server-Nonce", nonce);
                    saveCredentialsToEncryptedPreferences();
                    emailEditText.setText(null);
                    passwordEditText.setText(null);
                    encryptAndSaveAttributesToFile(digitalKey.toString());
                    goToHomeActivity();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<EmployeeAttributesModel> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void validateVisitor(LoginModel loginModel) {
        BackEndClient.getINSTANCE().validateVisitor(loginModel).enqueue(new Callback<VisitorAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<VisitorAttributesModel> call, @NonNull Response<VisitorAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    String nonce = response.headers().get("Server-Nonce");
                    JsonObject digitalKey = gson.toJsonTree(response.body()).getAsJsonObject();
                    digitalKey.addProperty("Server-Nonce", nonce);
                    saveCredentialsToEncryptedPreferences();
                    emailEditText.setText(null);
                    passwordEditText.setText(null);
                    encryptAndSaveAttributesToFile(gson.toJson(response.body()));
                    goToHomeActivity();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<VisitorAttributesModel> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void encryptAndSaveAttributesToFile(String attributes) {
        CryptoManager cryptoManager = new CryptoManager();
        File file = new File(getFilesDir(), "secret.txt");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream streamOutput = new FileOutputStream(file);
            cryptoManager.encrypt(attributes.getBytes(), streamOutput);
        } catch (Exception e) {
            Log.e("Error in encryption" , Objects.requireNonNull(e.getMessage()));
        }
    }

    private void saveCredentialsToEncryptedPreferences() {
            sharedPreferences.edit().putString("email", emailEditText.getText().toString()).apply();
            sharedPreferences.edit().putString("user_type", String.valueOf(guestOption)).apply();
            sharedPreferences.edit().putString("pass", passwordEditText.getText().toString()).apply();
    }

    private boolean validateField(EditText editText, ValidationPattern pattern) {
        String fieldValue = editText.getText().toString().trim();

        if (fieldValue.isEmpty()) {
            editText.setError("Field can not be empty!");
            return true;
        } else if (!fieldValue.matches(pattern.getRegex())) {
            editText.setError(pattern.getErrorMessage());
            return true;
        } else {
            editText.setError(null);
            return false;
        }
    }

    private void handleErrorResponse(ResponseBody errorBody) {
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(LoginActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }

    public void startBiometricAuthentication(View view) {
        if (!isBiometricSupported()) {
            Toast.makeText(LoginActivity.this, "Biometric authentication is not supported on this device.", Toast.LENGTH_LONG).show();
            return;
        }

        BiometricPrompt.PromptInfo promptInfo = buildBiometricPromptInfo();
        biometricPrompt.authenticate(promptInfo);
    }

    private boolean isBiometricSupported() {
        BiometricManager manager = BiometricManager.from(this);
        int canAuthenticate = manager.canAuthenticate(BIOMETRIC_WEAK | BIOMETRIC_STRONG);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }

    private BiometricPrompt.PromptInfo buildBiometricPromptInfo() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private BiometricPrompt.AuthenticationCallback createAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Authentication error: " + errString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(LoginActivity.this, "Authentication succeeded!", Toast.LENGTH_LONG).show();

                String userType = sharedPreferences.getString("user_type", "");
                String email = sharedPreferences.getString("email", "");
                String password = sharedPreferences.getString("pass", "");

                if ("false".equalsIgnoreCase(userType)) {
                    validateEmployee(new LoginModel(email,password));
                } else {
                    validateVisitor(new LoginModel(email,password));
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
            }
        };
    }



}