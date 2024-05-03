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

import com.google.gson.Gson;

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
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText employeeId,first_name, last_name, email,ssn, password, confirm;
    private boolean guestOption;
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

        setupSignUpButton();

        setupGuestSwitch();

    }


    private void initializeViews() {
        employeeId = findViewById(R.id.employee_id);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        ssn = findViewById(R.id.ssn);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
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
        employeeId.setEnabled(!isGuest);
        int backgroundResource = isGuest ? R.drawable.input_text_field_styling_switch_on : R.drawable.input_text_field_styling_switch_off;
        employeeId.setBackground(getResources().getDrawable(backgroundResource, null));
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
            if( validateField(first_name,ValidationPattern.FIRST_NAME) || validateField(last_name,ValidationPattern.LAST_NAME) ||
                    validateField(ssn,ValidationPattern.SSN) ||
                    validateField(email,ValidationPattern.EMAIL) || validatePasswordAndConfirm(password,confirm)
                    ){
                return;
            }
            RegistrationModel registrationModel = new RegistrationModel(first_name.getText().toString(),
                    last_name.getText().toString(),
                    ssn.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString());
            registerVisitor(registrationModel);
        } else {
            if( validateField(first_name,ValidationPattern.FIRST_NAME) || validateField(last_name,ValidationPattern.LAST_NAME) ||
                    validateField(employeeId,ValidationPattern.ID) || validateField(ssn,ValidationPattern.SSN) ||
                    validateField(email,ValidationPattern.EMAIL) || validatePasswordAndConfirm(password,confirm) ){
                return;
            }
            RegistrationModel registrationModel = new RegistrationModel(employeeId.getText().toString(),
                    first_name.getText().toString(),
                    last_name.getText().toString(),
                    ssn.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString());
            registerEmployee(registrationModel);
        }
    }

    private void registerEmployee(RegistrationModel registrationModel) {
        BackEndClient.getINSTANCE().registerEmployee(registrationModel).enqueue(new Callback<EmployeeAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttributesModel> call, @NonNull Response<EmployeeAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    saveDataToSharedPreferences();
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
    private void registerVisitor(RegistrationModel registrationModel) {
        BackEndClient.getINSTANCE().registerVisitor(registrationModel).enqueue(new Callback<VisitorAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<VisitorAttributesModel> call, @NonNull Response<VisitorAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    saveDataToSharedPreferences();
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

        Log.i("AttributeJson",attributes);
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
        employeeId.setText(null);
        first_name.setText(null);
        last_name.setText(null);
        ssn.setText(null);
        email.setText(null);
        password.setText(null);
        confirm.setText(null);
    }


}