package com.example.english_learning_app;

import android.content.Intent; // Đừng quên import Intent
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FlashCardLearning extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flash_card_learning);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ 2 nút
        ImageView btnCardOptions = findViewById(R.id.btn_card_options); // Nút 3 chấm
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_flashcard); // Nút dấu cộng (+)

        // ==========================================
        // 1. SỰ KIỆN NÚT 3 CHẤM -> MỞ BOTTOM SHEET
        // ==========================================
        btnCardOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FlashCardLearning.this);
                View sheetView = getLayoutInflater().inflate(R.layout.flashcard_layout_bottom_sheet_options, null);
                bottomSheetDialog.setContentView(sheetView);

                LinearLayout btnEdit = sheetView.findViewById(R.id.btn_edit_option);
                LinearLayout btnDelete = sheetView.findViewById(R.id.btn_delete_option);

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý mở màn hình Chỉnh sửa
                        bottomSheetDialog.dismiss();
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý Xóa
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        // ==========================================
        // 2. SỰ KIỆN NÚT DẤU CỘNG (+) -> CHUYỂN TRANG
        // ==========================================
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dùng Intent để chuyển sang Activity NewFlashcard
                Intent intent = new Intent(FlashCardLearning.this, FlashCardNew.class);
                startActivity(intent);
            }
        });

    }
}