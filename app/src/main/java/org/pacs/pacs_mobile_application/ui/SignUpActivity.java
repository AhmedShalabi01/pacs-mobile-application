package org.pacs.pacs_mobile_application.ui;

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
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText employeeIdEditText, firstNameEditText, lastNameEditText, emailEditText, ssnEditText, passwordEditText, confirmEditText;
    private boolean guestOption;
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeSharedPreferences();
        setupSignUpButton();
        setupGuestSwitch();

    }

    private void initializeViews() {
        employeeIdEditText = findViewById(R.id.employee_id);
        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        ssnEditText = findViewById(R.id.ssn);
        emailEditText = findViewById(R.id.email_signup);
        passwordEditText = findViewById(R.id.password_signup);
        confirmEditText = findViewById(R.id.confirm_signup);
    }

    private void initializeSharedPreferences() {
        MasterKey masterKey;
        try {
            masterKey = new MasterKey.Builder(SignUpActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    SignUpActivity.this,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("Error in create preference key" , Objects.requireNonNull(e.getMessage()));
        }
    }

    private void setupSignUpButton() {
        Button sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(view -> processFormFields());
    }

    private void setupGuestSwitch() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch guest = findViewById(R.id.guest);
        guest.setOnCheckedChangeListener((compoundButton, b) -> {
            guestOption = b;
            updateGuestSwitchUI(b);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateGuestSwitchUI(boolean isGuest) {
        employeeIdEditText.setEnabled(!isGuest);
        int backgroundResource = isGuest ? R.drawable.input_text_field_styling_switch_on : R.drawable.input_text_field_styling_switch_off;
        employeeIdEditText.setBackground(getResources().getDrawable(backgroundResource, null));
    }

    public void goToLoginAct(View view) {
        Intent moveToLoginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(moveToLoginActivity);
        finish();
    }

    private void goToHomeActivity() {
        Intent moveToHomeActivity = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(moveToHomeActivity);
        finish();
    }

    public void processFormFields(){
        if (guestOption){
            if( validateField(firstNameEditText,ValidationPattern.FIRST_NAME) || validateField(lastNameEditText,ValidationPattern.LAST_NAME) ||
                    validateField(ssnEditText,ValidationPattern.SSN) ||
                    validateField(emailEditText,ValidationPattern.EMAIL) || validatePasswordAndConfirm(passwordEditText, confirmEditText)
                    ){
                return;
            }
            RegistrationModel registrationModel = new RegistrationModel(firstNameEditText.getText().toString(),
                    lastNameEditText.getText().toString(),
                    ssnEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
            registerVisitor(registrationModel, guestOption);
        } else {
            if( validateField(firstNameEditText,ValidationPattern.FIRST_NAME) || validateField(lastNameEditText,ValidationPattern.LAST_NAME) ||
                    validateField(employeeIdEditText,ValidationPattern.ID) || validateField(ssnEditText,ValidationPattern.SSN) ||
                    validateField(emailEditText,ValidationPattern.EMAIL) || validatePasswordAndConfirm(passwordEditText, confirmEditText) ){
                return;
            }
            RegistrationModel registrationModel = new RegistrationModel(employeeIdEditText.getText().toString(),
                    firstNameEditText.getText().toString(),
                    lastNameEditText.getText().toString(),
                    ssnEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
            registerEmployee(registrationModel, guestOption);
        }
    }

    private void registerEmployee(RegistrationModel registrationModel, boolean guestOption) {

        BackEndClient.getINSTANCE(getApplicationContext()).registerEmployee(registrationModel).enqueue(new Callback<EmployeeAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttributesModel> call, @NonNull Response<EmployeeAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    String nonce = response.headers().get("Server-Nonce");
                    JsonObject digitalKey = gson.toJsonTree(response.body()).getAsJsonObject();
                    digitalKey.addProperty("SN", nonce);
                    saveCredentialsToEncryptedPreferences(registrationModel.getEmail(), registrationModel.getPassword(), guestOption);
                    emptyForm();
                    encryptAndSaveAttributesToTempFile(digitalKey.toString(), SignUpActivity.this);
                    goToHomeActivity();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<EmployeeAttributesModel> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerVisitor(RegistrationModel registrationModel, boolean guestOption) {
        BackEndClient.getINSTANCE(getApplicationContext()).registerVisitor(registrationModel).enqueue(new Callback<VisitorAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<VisitorAttributesModel> call, @NonNull Response<VisitorAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    String nonce = response.headers().get("Server-Nonce");
                    JsonObject digitalKey = gson.toJsonTree(response.body()).getAsJsonObject();
                    digitalKey.addProperty("SN", nonce);
                    saveCredentialsToEncryptedPreferences(registrationModel.getEmail(), registrationModel.getPassword(), guestOption);
                    emptyForm();
                    encryptAndSaveAttributesToTempFile(digitalKey.toString(), SignUpActivity.this);
                    goToHomeActivity();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<VisitorAttributesModel> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void saveCredentialsToEncryptedPreferences(String email, String password, boolean guestOption ) {
        sharedPreferences.edit().putString("email", email).apply();
        sharedPreferences.edit().putString("user_type", String.valueOf(guestOption)).apply();
        sharedPreferences.edit().putString("pass", password).apply();
    }

    private void encryptAndSaveAttributesToTempFile(String attributes, SignUpActivity context) {
        CryptoManager cryptoManager = new CryptoManager();
        File tempFile = null;

        try {
            tempFile = File.createTempFile("secret", ".txt", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            cryptoManager.encrypt(attributes.getBytes(), outputStream);
        } catch (IOException e) {
            Log.e("Error in encryption", Objects.requireNonNull(e.getMessage()));
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.deleteOnExit();
            }
        }
    }

    private boolean validateField(EditText editText, ValidationPattern pattern) {
        String fieldValue = editText.getText().toString().trim();

        if (fieldValue.isEmpty()) {
            editText.setError(pattern.getErrorMessage());
            return true;
        } else if (!fieldValue.matches(pattern.getRegex())) {
            editText.setError(pattern.getErrorMessage());
            return true;
        } else {
            editText.setError(null);
            return false;
        }
    }

    private boolean validatePasswordAndConfirm(EditText passwordEditText, EditText confirmEditText) {
        String password = passwordEditText.getText().toString().trim();
        String confirm = confirmEditText.getText().toString().trim();

        if (!password.equals(confirm)) {
            passwordEditText.setError("Passwords do not match!");
            confirmEditText.setError("Passwords do not match!");
            return true;
        } else if (validateField(passwordEditText, ValidationPattern.PASSWORD) || validateField(confirmEditText, ValidationPattern.PASSWORD)) {
            return true;
        } else {
            passwordEditText.setError(null);
            confirmEditText.setError(null);
            return false;
        }
    }

    private void handleErrorResponse(ResponseBody errorBody) {
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(SignUpActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(SignUpActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }

    public void emptyForm() {
        employeeIdEditText.setText(null);
        firstNameEditText.setText(null);
        lastNameEditText.setText(null);
        ssnEditText.setText(null);
        emailEditText.setText(null);
        passwordEditText.setText(null);
        confirmEditText.setText(null);
    }


}