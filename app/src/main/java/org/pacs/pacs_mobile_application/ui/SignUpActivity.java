package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import org.pacs.pacs_mobile_application.pojo.requestmodel.RegistrationModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText employeeId,first_name, last_name, email,ssn, password, confirm;
    private boolean guestOption;


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

        employeeId  = findViewById(R.id.employee_id);
        first_name   = findViewById(R.id.first_name);
        last_name    = findViewById(R.id.last_name);
        ssn          = findViewById(R.id.ssn);
        email        = findViewById(R.id.email);
        password     = findViewById(R.id.password);
        confirm      = findViewById(R.id.confirm);

        Button sign_up_btn = findViewById(R.id.sign_up_btn);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch guest = findViewById(R.id.guest);

        sign_up_btn.setOnClickListener(view -> processFormFields());
        guest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    guestOption = true;
                    employeeId.setEnabled(false);
                    employeeId.setBackground(getResources().getDrawable(R.drawable.input_text_field_styling_switch_on));

                } else {
                    guestOption = false;
                    employeeId.setEnabled(true);
                    employeeId.setBackground(getResources().getDrawable(R.drawable.input_text_field_styling_switch_off));

                }
            }
        });

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
            if(!validateFirstName() || !validateLastName() ||
                    !validateEmail() || !validatePasswordAndConfirm() || !validateSsn()){
                return;
            }
            RegistrationModel registrationModel = new RegistrationModel(first_name.getText().toString(),
                    last_name.getText().toString(),
                    ssn.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString());
            registerVisitor(registrationModel);
        } else {
            if(!validateId() || !validateFirstName() ||
                    !validateLastName() || !validateEmail() ||
                    !validatePasswordAndConfirm() || !validateSsn()){
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


    private void registerVisitor(RegistrationModel registrationModel) {
        BackEndClient.getINSTANCE().registerVisitor(registrationModel).enqueue(new Callback<VisitorAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<VisitorAttributesModel> call, @NonNull Response<VisitorAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    emptyForm();
                    saveData();
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

    private void registerEmployee(RegistrationModel registrationModel) {
        BackEndClient.getINSTANCE().registerEmployee(registrationModel).enqueue(new Callback<EmployeeAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttributesModel> call, @NonNull Response<EmployeeAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                    emptyForm();
                    saveData();
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
    private boolean validateId(){
        String employee_Id = employeeId.getText().toString();
        String noWhiteSpace = "\\A\\w{1,20}\\z";

        if(employee_Id.isEmpty() || !employee_Id.matches(noWhiteSpace)){
            employeeId.setError("Employee ID cannot be empty!");
            return false;
        } else {
            employeeId.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(){
        String firstName = first_name.getText().toString();
        String noWhiteSpace = "\\A\\w{2,20}\\z";
        String firstNamePattern = "^[a-zA-Z]+$";

        if(firstName.isEmpty() || !firstName.matches(noWhiteSpace)){
            first_name.setError("First name cannot be empty!");
            return false;
        } else if (!firstName.matches(firstNamePattern)) {
            first_name.setError("First Name must include letters only");
            return false;
        } else {
            first_name.setError(null);
            return true;
        }
    }

    private boolean validateLastName(){
        String lastName = last_name.getText().toString();
        String noWhiteSpace = "\\A\\w{2,20}\\z";
        String lastNamePattern = "^[a-zA-Z]+$";

        if(lastName.isEmpty() || !lastName.matches(noWhiteSpace)){
            last_name.setError("Last name cannot be empty!");
            return false;
        } else if (!lastName.matches(lastNamePattern)) {
            last_name.setError("Last Name must include letters only");
            return false;
        } else {
            last_name.setError(null);
            return true;
        }
    }

    private boolean validateSsn(){
        String ssn_e = ssn.getText().toString();
        String ssnPattern = "^\\d{3}-\\d{2}-\\d{4}$";

        if(ssn_e.isEmpty()){
            ssn.setError("SSN cannot be empty!");
            return false;
        } else if (!ssn_e.matches(ssnPattern)) {
            ssn.setError("SSN does not follow the standard format");
            return false;
        } else {
            ssn.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String email_e = email.getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email_e.isEmpty()){
            email.setError("Email cannot be empty!");
            return false;
        } else if(!email_e.matches(emailPattern)){
            email.setError("Please enter a valid email");
            return false;
        } else{
            email.setError(null);
            return true;
        }
    }

    private boolean validatePasswordAndConfirm(){
        String password_p = password.getText().toString();
        String confirm_p = confirm.getText().toString();
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";

        if(password_p.isEmpty()){
            password.setError("Password cannot be empty!");
            return false;
        } else if(confirm_p.isEmpty()){
            confirm.setError("Confirm field cannot be empty!");
            return false;
        } else if (!password_p.matches(passwordPattern)){
            password.setError("must include one number one capital letter and one symbol");
            return false;
        } else if (!password_p.equals(confirm_p)){
            password.setError("Passwords do not match!");
            return false;
        }   else{
            password.setError(null);
            confirm.setError(null);
            return true;
        }
    }

    private void handleErrorResponse(ResponseBody errorBody) {
        Gson gson = new Gson();
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(SignUpActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref_Info",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email.getText().toString());
        editor.putString("user_type", String.valueOf(guestOption));
        editor.apply();
    }
}