package com.example.lab1_ph51025;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FireBaseActivity extends AppCompatActivity {

    private Button btnLoginEmail;
    private Button btnLoginPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fire_base);

        btnLoginEmail = findViewById(R.id.btnLoginEmail);
        btnLoginPhone = findViewById(R.id.btnLoginPhone);

        btnLoginEmail.setOnClickListener(view -> {
            Intent intent = new Intent(FireBaseActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnLoginPhone.setOnClickListener(view -> {
            Intent intent = new Intent(FireBaseActivity.this, PhoneLoginActivity.class);
            startActivity(intent);
        });
    }
}