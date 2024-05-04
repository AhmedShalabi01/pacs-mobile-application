package org.pacs.pacs_mobile_application.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.pacs.pacs_mobile_application.R;
import org.pacs.pacs_mobile_application.ui.LoginActivity;
import org.pacs.pacs_mobile_application.ui.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    Button sign_up, login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sign_up = findViewById(R.id.sign_up);
        login = findViewById(R.id.login);

        sign_up.setOnClickListener(this::goToSignUp);
        login.setOnClickListener(this::goToLogin);
    }

    public void goToSignUp(View view) {
        Intent moveToSignUpActivity = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(moveToSignUpActivity);
        finish();
    }

    public void goToLogin(View view) {
        Intent moveToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(moveToLoginActivity);
        finish();
    }


}