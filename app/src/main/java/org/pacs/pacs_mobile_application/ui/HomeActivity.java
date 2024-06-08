package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.data.BackEndClient;
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;
import org.pacs.pacs_mobile_application.ui.main.MainActivity;
import org.pacs.pacs_mobile_application.utils.CustomSharedPreferences;


import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private TextView userId, userName;
    private CustomSharedPreferences customSharedPreferences;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeSharedPreferences();

        String email = customSharedPreferences.readData("email", "");
        String userType = customSharedPreferences.readData("user_type", "");

        if ("false".equalsIgnoreCase(userType)) {
            fetchEmployeeInfo(email);
        } else {
            fetchVisitorInfo(email);
        }

        setupBottomNavigationView();

    }

    private void initializeViews() {
        userId = findViewById(R.id.user_id);
        userName = findViewById(R.id.user_name);
    }
    private void initializeSharedPreferences() {
        customSharedPreferences = CustomSharedPreferences.getInstance(this);
    }

    public void goToMainActivity(View view) {
        deleteAttributesFile();
        customSharedPreferences.deleteData("noncesList");
        customSharedPreferences.deleteData("currentIndex");
        Intent moveToMainActivity = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(moveToMainActivity);
        finish();
    }

    private void fetchEmployeeInfo(String email) {
        BackEndClient.getINSTANCE(getApplicationContext()).findEmployeeInfo(email).enqueue(new Callback<UserInfoModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<UserInfoModel> call, @NonNull Response<UserInfoModel> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    saveData(response.body());
                    placeData(response.body());
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserInfoModel> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void fetchVisitorInfo(String email) {
        BackEndClient.getINSTANCE(getApplicationContext()).findVisitorInfo(email).enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoModel> call, @NonNull Response<UserInfoModel> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    saveData(response.body());
                    placeData(response.body());
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserInfoModel> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveData(UserInfoModel userInfoModel) {
        customSharedPreferences.saveData("userId",userInfoModel.getId());
        customSharedPreferences.saveData("Name",userInfoModel.getFirstName()+" "+userInfoModel.getLastName());
        customSharedPreferences.saveData("ssn", userInfoModel.getSsn());
    }

    private void placeData(UserInfoModel userInfoModel) {
        userId.setText(userInfoModel.getId());
        userName.setText(String.format("%s %s", userInfoModel.getFirstName(), userInfoModel.getLastName()));
    }
    private void handleErrorResponse(ResponseBody errorBody) {
        Gson gson = new Gson();
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(HomeActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAttributesFile() {
        File fileToDelete = new File(getFilesDir(), "secret.txt");
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                Log.d("File Deletion", "File deleted successfully");
            } else {
                Log.e("File Deletion", "Failed to delete the file");
            }
        }
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_history) {
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }

            return false;
        });
    }
}