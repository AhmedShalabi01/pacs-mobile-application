package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.data.BackEndClient;
import org.pacs.pacs_mobile_application.pojo.responsemodel.AccessAttemptModel;
import org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel.ErrorBody;
import org.pacs.pacs_mobile_application.utils.AccessAttemptAdapter;
import org.pacs.pacs_mobile_application.utils.CustomSharedPreferences;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private List<AccessAttemptModel> accessAttempts;
    private CustomSharedPreferences customSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeSharedPreferences();

        String id = customSharedPreferences.readData("id", "");
        String userType = customSharedPreferences.readData("user_type", "");

        if ("false".equalsIgnoreCase(userType)) {
            fetchEmployeeHistory(id);
        } else {
            fetchVisitorHistory(id);
        }

        setupRecyclerView();
        setupBottomNavigationView();


    }

    private void initializeSharedPreferences() {
        customSharedPreferences = CustomSharedPreferences.getInstance(this);
    }

    private void fetchEmployeeHistory(String id) {
        BackEndClient.getINSTANCE(getApplicationContext()).findEmployeeHistory(id).enqueue(new Callback<List<AccessAttemptModel>> () {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<AccessAttemptModel>> call, @NonNull Response<List<AccessAttemptModel>> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    accessAttempts = response.body();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<AccessAttemptModel>> call, @NonNull Throwable t) {
                Toast.makeText(HistoryActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void fetchVisitorHistory(String id) {
        BackEndClient.getINSTANCE(getApplicationContext()).findEmployeeHistory(id).enqueue(new Callback<List<AccessAttemptModel>> () {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<AccessAttemptModel>> call, @NonNull Response<List<AccessAttemptModel>> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    accessAttempts = response.body();
                } else {
                    handleErrorResponse(response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<AccessAttemptModel>> call, @NonNull Throwable t) {
                Toast.makeText(HistoryActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccessAttemptAdapter adapter = new AccessAttemptAdapter(accessAttempts);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_history);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_history) {
                return true;
            }
            return false;
        });
    }

    private void handleErrorResponse(ResponseBody errorBody) {
        Gson gson = new Gson();
        try {
            ErrorBody error = gson.fromJson(errorBody.charStream(), ErrorBody.class);
            Toast.makeText(HistoryActivity.this, error.getErrorMessages(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(HistoryActivity.this, "Error parsing error response", Toast.LENGTH_LONG).show();
        }
    }
}