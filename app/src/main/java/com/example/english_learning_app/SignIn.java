package com.example.english_learning_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private LinearLayout btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ImageView imgShowPassword;

    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false; // Biến trạng thái ẩn/hiện mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in); // Thay bằng tên file XML thực tế của bạn

        mAuth = FirebaseAuth.getInstance();

        // 1. Kiểm tra trạng thái đăng nhập
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignIn.this, Home.class));
            finish();
        }

        // 2. Ánh xạ View từ XML
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        imgShowPassword = findViewById(R.id.imgShowPassword);

        // 3. Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            // Lưu ý: Firebase sử dụng Email để đăng nhập, nên ô edtUsername này thực chất là nhập Email
            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Email và Mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this, Home.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi: Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 4. Xử lý nút "Chưa có tài khoản? Đăng ký ngay!"
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
            // Không dùng finish() ở đây để người dùng có thể ấn nút Back quay lại trang Login
        });

        // 5. Xử lý tính năng Ẩn/Hiện mật khẩu
        imgShowPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Đang hiện -> Chuyển sang Ẩn
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                // (Tùy chọn) Đổi icon thành mắt nhắm: imgShowPassword.setImageResource(R.drawable.icon_mat_nham);
                isPasswordVisible = false;
            } else {
                // Đang ẩn -> Chuyển sang Hiện
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // (Tùy chọn) Đổi icon thành mắt mở: imgShowPassword.setImageResource(R.drawable.icon_mat_mo);
                isPasswordVisible = true;
            }
            // Đưa con trỏ văn bản về cuối dòng sau khi đổi trạng thái
            edtPassword.setSelection(edtPassword.getText().length());
        });
    }
}