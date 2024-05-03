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

import com.google.gson.Gson;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.data.BackEndClient;
import org.pacs.pacs_mobile_application.utils.CryptoManager;
import org.pacs.pacs_mobile_application.utils.ValidationPattern;
import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private boolean guestOption;
    private final Gson gson = new Gson();
    private BiometricPrompt biometricPrompt;

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

        email = findViewById(R.id.email_login);
        password     = findViewById(R.id.password);

        Button sign_in_btn = findViewById(R.id.sign_in_btn);


        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch guest_switch = findViewById(R.id.guest);

        sign_in_btn.setOnClickListener(view -> processFormFields());
        guest_switch.setOnCheckedChangeListener((compoundButton, b) -> guestOption = b);


        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, createAuthenticationCallback());
    }

    // Method to check if biometric authentication is supported
    private boolean isBiometricSupported() {
        BiometricManager manager = BiometricManager.from(this);
        int canAuthenticate = manager.canAuthenticate(BIOMETRIC_WEAK | BIOMETRIC_STRONG);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }

    // Method to display a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to configure the biometric prompt dialog
    private BiometricPrompt.PromptInfo buildBiometricPromptInfo() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();
    }

    // Method to handle biometric authentication callbacks
    private BiometricPrompt.AuthenticationCallback createAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showToast("Authentication error: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                showToast("Authentication succeeded!");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showToast("Authentication failed");
            }
        };
    }

    // Method to initiate biometric authentication
    public void startBiometricAuthentication() {
        if (!isBiometricSupported()) {
            Toast.makeText(this, "Biometric authentication is not supported on this device.", Toast.LENGTH_SHORT).show();
            showToast("Biometric authentication is not supported on this device.");
            return;
        }

        BiometricPrompt.PromptInfo promptInfo = buildBiometricPromptInfo();
        biometricPrompt.authenticate(promptInfo);
    }

    // Method invoked when the biometric login button is clicked
    public void onBiometricLoginClicked(View view) {
        startBiometricAuthentication();
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
            if(validateField(email, ValidationPattern.EMAIL) || validateField(password, ValidationPattern.PASSWORD)){
                return;
            }
            LoginModel loginModel = new LoginModel(email.getText().toString(), password.getText().toString());
            validateVisitor(loginModel);
        } else {
            if(validateField(email, ValidationPattern.EMAIL) || validateField(password, ValidationPattern.PASSWORD)){
                return;
            }
            LoginModel loginModel = new LoginModel(email.getText().toString(), password.getText().toString());
            validateEmployee(loginModel);
        }
    }
    private void validateEmployee(LoginModel loginModel) {

        BackEndClient.getINSTANCE().validateEmployee(loginModel).enqueue(new Callback<EmployeeAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttributesModel> call, @NonNull Response<EmployeeAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    saveDataToSharedPreferences();
                    email.setText(null);
                    password.setText(null);
                    encryptAndSaveAttributesToFile(gson.toJson(response.body()));
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
                    saveDataToSharedPreferences();
                    email.setText(null);
                    password.setText(null);
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
    private void saveDataToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref_Information",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email.getText().toString());
        editor.putString("user_type", String.valueOf(guestOption));
        editor.apply();
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



}