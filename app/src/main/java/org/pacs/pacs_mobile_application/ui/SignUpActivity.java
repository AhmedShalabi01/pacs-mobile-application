package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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


import com.google.gson.Gson;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.data.BackEndClient;

import org.pacs.pacs_mobile_application.utils.CryptoManager;
import org.pacs.pacs_mobile_application.utils.CustomSharedPreferences;
import org.pacs.pacs_mobile_application.utils.NonceGenerator;
import org.pacs.pacs_mobile_application.utils.ValidationPattern;
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText employeeIdEditText, firstNameEditText, lastNameEditText, emailEditText, ssnEditText, passwordEditText, confirmEditText;
    private boolean guestOption;
    private CustomSharedPreferences customSharedPreferences;
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
       customSharedPreferences = CustomSharedPreferences.getInstance(this);
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

    public void goToLoginActivity(View view) {
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

                    String seed = response.headers().get("Server-Seed");
                    generateAndSaveAuthenticationNonce(seed);
                    saveCredentialsToEncryptedPreferences(registrationModel.getEmail(), registrationModel.getPassword(), guestOption);
                    emptyForm();
                    encryptAndSaveAttributesToFile(gson.toJson(response.body()));
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

                    String seed = response.headers().get("Server-Seed");
                    generateAndSaveAuthenticationNonce(seed);
                    saveCredentialsToEncryptedPreferences(registrationModel.getEmail(), registrationModel.getPassword(), guestOption);
                    emptyForm();
                    encryptAndSaveAttributesToFile(gson.toJson(response.body()));
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
            Log.e("error in encryption" , Objects.requireNonNull(e.getMessage()));
        }
    }
    private void generateAndSaveAuthenticationNonce(String seed) {
        List<String> noncesAsList = NonceGenerator.generateNonceSequence(seed,10);
        StringBuilder noncesAsString = new StringBuilder();
        for (String item : noncesAsList) {
            noncesAsString.append(item).append(",");
        }
        // Remove the last comma
        if (!noncesAsList.isEmpty()) {
            noncesAsString.deleteCharAt(noncesAsString.length() - 1);
        }
        customSharedPreferences.saveData("noncesList", noncesAsString.toString());
        customSharedPreferences.saveData("currentIndex", "0");
    }

    private void saveCredentialsToEncryptedPreferences(String email, String password, boolean guestOption ) {
        customSharedPreferences.saveData("email", email);
        customSharedPreferences.saveData("user_type", String.valueOf(guestOption));
        customSharedPreferences.saveData("pass", password);
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