package org.pacs.pacs_mobile_application.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
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

public class ProfileActivity extends AppCompatActivity {
    private TextView userId, fullName, email_e, ssn;
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

        userId = findViewById(R.id.userId);
        fullName = findViewById(R.id.fullName);
        email_e = findViewById(R.id.email_profile);
        ssn = findViewById(R.id.ssn);
        placeData();

    }

    private void placeData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPref_Information",MODE_PRIVATE);
        userId.setText(sharedPreferences.getString("userId",""));
        fullName.setText(sharedPreferences.getString("Name",""));
        email_e.setText(sharedPreferences.getString("email",""));
        ssn.setText(sharedPreferences.getString("ssn",""));
    }
}