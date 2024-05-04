package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.data.BackEndClient;
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private TextView userId, userName;
    SharedPreferences sharedPreferences;
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

        String email = sharedPreferences.getString("email", "");
        String userType = sharedPreferences.getString("user_type", "");

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
        MasterKey masterKey;
        try {
            masterKey = new MasterKey.Builder(HomeActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sharedPreferences = EncryptedSharedPreferences.create(
                    HomeActivity.this,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("Error in create preference key" , Objects.requireNonNull(e.getMessage()));
        }
    }
    private void fetchEmployeeInfo(String email) {
        BackEndClient.getINSTANCE().findEmployeeInfo(email).enqueue(new Callback<UserInfoModel>() {
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
        BackEndClient.getINSTANCE().findVisitorInfo(email).enqueue(new Callback<UserInfoModel>() {
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
        sharedPreferences.edit().putString("userId",userInfoModel.getId()).apply();
        sharedPreferences.edit().putString("Name",userInfoModel.getFirstName()+" "+userInfoModel.getLastName()).apply();
        sharedPreferences.edit().putString("ssn", userInfoModel.getSsn()).apply();
    }
    @SuppressLint("SetTextI18n")
    private void placeData(UserInfoModel userInfoModel) {
        userId.setText(userInfoModel.getId());
        userName.setText(userInfoModel.getFirstName() + " " + userInfoModel.getLastName());
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