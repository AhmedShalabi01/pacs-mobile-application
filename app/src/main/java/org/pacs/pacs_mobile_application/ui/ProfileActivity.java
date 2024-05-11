package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.utils.CustomSharedPreferences;


public class ProfileActivity extends AppCompatActivity {
    private TextView userId, fullName, email_e, ssn;
    private CustomSharedPreferences customSharedPreferences;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        initializeSharedPreferences();
        placeData();
        setupBottomNavigationView();

    }

    private void initializeViews() {
        userId = findViewById(R.id.userId);
        fullName = findViewById(R.id.fullName);
        email_e = findViewById(R.id.email_profile);
        ssn = findViewById(R.id.ssn);
    }

    private void initializeSharedPreferences() {
        customSharedPreferences = CustomSharedPreferences.getInstance(this);
    }

    private void placeData() {
        userId.setText(customSharedPreferences.readData("userId",""));
        fullName.setText(customSharedPreferences.readData("Name",""));
        email_e.setText(customSharedPreferences.readData("email",""));
        ssn.setText(customSharedPreferences.readData("ssn",""));
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
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