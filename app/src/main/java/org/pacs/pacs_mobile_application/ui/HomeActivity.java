package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
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
import org.pacs.pacs_mobile_application.pojo.responsemodel.UserInfoModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private TextView userId, UserName;
    SharedPreferences sharedPreferences;
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

        userId = findViewById(R.id.user_id);
        UserName = findViewById(R.id.user_name);

        sharedPreferences = getSharedPreferences("sharedPref_Info",MODE_PRIVATE);
        if(sharedPreferences.getString("user_type","").equalsIgnoreCase("false")) {
            fetchEmployeeInfo(sharedPreferences.getString("email",""));
        } else {
            fetchVisitorInfo(sharedPreferences.getString("email",""));
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
    private void handleErrorResponse(ResponseBody errorBody) {
        Gson gson = new Gson();
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(HomeActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(HomeActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }

    private void saveData(UserInfoModel userInfoModel) {
        sharedPreferences = getSharedPreferences("sharedPref_Info",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId",userInfoModel.getId());
        editor.putString("Name",userInfoModel.getFirstName()+" "+userInfoModel.getLastName());
        editor.putString("ssn", userInfoModel.getSsn());
        editor.apply();
    }
    @SuppressLint("SetTextI18n")
    private void placeData(UserInfoModel userInfoModel) {
        userId.setText(userInfoModel.getId());
        UserName.setText(userInfoModel.getFirstName() + " " + userInfoModel.getLastName());
    }

}