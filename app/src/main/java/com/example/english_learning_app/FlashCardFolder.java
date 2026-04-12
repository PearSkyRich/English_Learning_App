package com.example.english_learning_app;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FlashCardFolder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flash_card_folder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayout btnSaveFolder = findViewById(R.id.btn_save_folder);
        
        btnSaveFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ... (Xử lý code lưu dữ liệu vào Database ở đây) ...

                // Gọi hàm finish() để đóng Activity này,
                // tự động quay về Activity trước đó (Flashcards)
                finish();
            }
        });

        // (Tùy chọn) Ánh xạ nút Back trên cùng góc trái để quay về
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}