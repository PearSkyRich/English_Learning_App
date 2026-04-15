package com.example.english_learning_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseModel> courseList;

    public CourseAdapter(List<CourseModel> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_lv1, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseModel course = courseList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        holder.tvTitle.setText(course.getName());
        holder.tvType.setText(course.getTag());

        holder.tvTitle.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), PathInfo.class);
            // Gửi dữ liệu qua trang PathInfo
            intent.putExtra("COURSE_ID", course.getId());
            intent.putExtra("COURSE_NAME", course.getName());
            intent.putExtra("COURSE_DESC", course.getShort_description());
            intent.putExtra("COURSE_UNITS", course.getTotal_units());
            intent.putExtra("COURSE_IMAGE", course.getImage_url());
            v.getContext().startActivity(intent);
        });

        if (course.isExpanded()) {
            holder.rvChildUnits.setVisibility(View.VISIBLE);
            holder.ivArrow.setRotation(180f);

            // Cài đặt Adapter cho danh sách con
            UnitDetailAdapter childAdapter = new UnitDetailAdapter(course.getUnits());
            holder.rvChildUnits.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.rvChildUnits.setAdapter(childAdapter);
            holder.rvChildUnits.setNestedScrollingEnabled(false);
        } else {
            holder.rvChildUnits.setVisibility(View.GONE);
            holder.ivArrow.setRotation(0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;

            course.setExpanded(!course.isExpanded());

            if (course.isExpanded() && course.getUnits().isEmpty()) {
                db.collection("Units")
                        .whereEqualTo("course_id", course.getId()) // Đã fix thành String cho bạn
                        .get()
                        .addOnSuccessListener(snapshots -> {
                            List<UnitModel> fetchedUnits = new ArrayList<>();
                            for (DocumentSnapshot doc : snapshots) {
                                UnitModel unit = doc.toObject(UnitModel.class);
                                unit.setId(doc.getId());
                                fetchedUnits.add(unit);
                            }
                            course.setUnits(fetchedUnits);
                            notifyItemChanged(adapterPos);
                        });
            } else {
                notifyItemChanged(adapterPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList != null ? courseList.size() : 0;
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvType;
        ImageView ivArrow;
        RecyclerView rvChildUnits;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_unit_title);
            tvType = itemView.findViewById(R.id.tv_unit_type);
            ivArrow = itemView.findViewById(R.id.iv_expand_arrow);
            rvChildUnits = itemView.findViewById(R.id.rv_child_units);
        }
    }
}