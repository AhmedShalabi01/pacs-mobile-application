package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import org.pacs.pacs_mobile_application.pojo.requestmodel.LoginModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.EmployeeAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.VisitorAttributesModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private boolean guestOption;

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

        email = findViewById(R.id.email);
        password     = findViewById(R.id.password);

        Button sign_in_btn = findViewById(R.id.sign_in_btn);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch guest = findViewById(R.id.guest);

        sign_in_btn.setOnClickListener(view -> processFormFields());
        guest.setOnCheckedChangeListener((compoundButton, b) -> guestOption = b);
    }

    public void goToSignUpAct(View view) {
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
            if(!validateEmail() || !validatePassword()){
                return;
            }
            LoginModel loginModel = new LoginModel(email.getText().toString(), password.getText().toString());
            validateVisitor(loginModel);
        } else {
            if(!validateEmail() || !validatePassword()){
                return;
            }
            LoginModel loginModel = new LoginModel(email.getText().toString(), password.getText().toString());
            validateEmployee(loginModel);
        }
    }


    private void validateVisitor(LoginModel loginModel) {
        BackEndClient.getINSTANCE().validateVisitor(loginModel).enqueue(new Callback<VisitorAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<VisitorAttributesModel> call, @NonNull Response<VisitorAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    email.setText(null);
                    password.setText(null);
                    saveData();
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

    private void validateEmployee(LoginModel loginModel) {

        BackEndClient.getINSTANCE().validateEmployee(loginModel).enqueue(new Callback<EmployeeAttributesModel>() {
            @Override
            public void onResponse(@NonNull Call<EmployeeAttributesModel> call, @NonNull Response<EmployeeAttributesModel> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    email.setText(null);
                    password.setText(null);
                    saveData();
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


    private boolean validateEmail(){
        String email_e = this.email.getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email_e.isEmpty()){
            this.email.setError("Email cannot be empty!");
            return false;
        } else if(!email_e.matches(emailPattern)){
            this.email.setError("Please enter a valid email");
            return false;
        } else{
            this.email.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String password_p = password.getText().toString();

        if(password_p.isEmpty()){
            password.setError("Password cannot be empty!");
            return false;
        } else{
            password.setError(null);
            return true;
        }
    }

    private void handleErrorResponse(ResponseBody errorBody) {
        Gson gson = new Gson();
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(LoginActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref_Info",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email.getText().toString());
        editor.putString("user_type", String.valueOf(guestOption));
        editor.apply();
    }

}