package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bạn có thể giữ lại setContentView nếu file activity_main.xml của bạn
        // đang chứa logo app (để làm màn hình chờ Splash Screen)
        setContentView(R.layout.activity_main);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Kiểm tra trạng thái đăng nhập
        if (currentUser != null) {
            // Trường hợp 1: Đã đăng nhập -> Nhảy thẳng vào Home
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
        } else {
            // Trường hợp 2: Chưa đăng nhập -> Chuyển sang trang SignIn
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
            finish();
        }
    }
}