package com.example.lab1_ph51025;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "PhoneLoginActivity";
    private String mVerificationId;

    private EditText editTextPhoneNumber;
    private EditText editTextOTP;
    private Button buttonGetOTP;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        editTextPhoneNumber = findViewById(R.id.etPhoneNumber);
        editTextOTP = findViewById(R.id.editOTP);
        buttonGetOTP = findViewById(R.id.btnGetOTP);
        buttonLogin = findViewById(R.id.btnLoginOTP);


        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        editTextOTP.setText(credential.getSmsCode());
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);
                        Toast.makeText(PhoneLoginActivity.this,
                                "Xác thực thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        mVerificationId = verificationId;
                        Toast.makeText(PhoneLoginActivity.this,
                                "Mã OTP đã được gửi!", Toast.LENGTH_SHORT).show();
                    }
                };

        // Bên trong buttonGetOTP
        buttonGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();

                // Kiểm tra và thêm mã quốc gia nếu cần
                if (!phoneNumber.startsWith("+")) {
                    phoneNumber = "+84" + phoneNumber;
                }

                if (!phoneNumber.isEmpty()) {
                    getOTP(phoneNumber, mCallbacks);  // Gọi phương thức gửi OTP
                } else {
                    Toast.makeText(PhoneLoginActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                }
            }
        });



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpInput = editTextOTP.getText().toString().trim();
                if (otpInput.isEmpty() && mVerificationId == null) {
                    Toast.makeText(PhoneLoginActivity.this,
                            "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOTP(otpInput);
                }
            }
        });
    }

    private void getOTP(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)  // Giữ nguyên số phoneNumber đã thêm +84
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOTP(String otpInput) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpInput);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(PhoneLoginActivity.this,
                                    "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(PhoneLoginActivity.this,
                                    "Đăng nhập thất bại: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
