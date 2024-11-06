package com.example.lab1_ph51025;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserModel> list;
    private FirebaseFirestore firestore;
    private Context context;

    public UserAdapter(Context context, List<UserModel> list) {
        this.list = list;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = list.get(position);

        // Thiết lập dữ liệu cho các TextView
        holder.textView.setText("User Name: " + user.getName());
        holder.tv2.setText("Age: " + user.getAge());
        holder.tv3.setText("Occupation: " + user.getOccupation());
        holder.tv4.setText("City: " + user.getCity());

        // Sự kiện nút sửa
        holder.btnEdit.setOnClickListener(v -> showEditDialog(user, position));

        // Sự kiện nút xóa
        holder.btnDelete.setOnClickListener(v -> deleteUser(user, position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showEditDialog(UserModel user, int position) {
        // Tạo một AlertDialog để hiển thị giao diện sửa
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Tạo view từ layout của bạn
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_edit_user, null);

        // Lấy các EditText từ layout của Dialog
        final EditText edtName = dialogView.findViewById(R.id.edt_nameUsered);
        final EditText edtAge = dialogView.findViewById(R.id.edt_ageUsered);
        final EditText edtOccupation = dialogView.findViewById(R.id.edt_occupationed);
        final EditText edtCity = dialogView.findViewById(R.id.edt_cityUsered);

        // Thiết lập giá trị mặc định từ đối tượng User
        edtName.setText(user.getName());
        edtAge.setText(String.valueOf(user.getAge()));
        edtOccupation.setText(user.getOccupation());
        edtCity.setText(user.getCity());

        // Đặt view vào AlertDialog
        builder.setView(dialogView);

        // Thêm các nút "Cập nhật" và "Hủy"
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String name = edtName.getText().toString();
            String ageString = edtAge.getText().toString();
            String occupation = edtOccupation.getText().toString();
            String city = edtCity.getText().toString();

            if (name.isEmpty() || ageString.isEmpty() || occupation.isEmpty() || city.isEmpty()) {
                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra tuổi có phải là số hợp lệ
            int age;
            try {
                age = Integer.parseInt(ageString);
                if (age <= 0) {
                    Toast.makeText(context, "Vui lòng nhập tuổi hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Vui lòng nhập đúng tuổi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật dữ liệu vào Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("age", age);
            updates.put("occupation", occupation);
            updates.put("city", city);

            firestore.collection("users").document(user.getId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật danh sách và thông báo
                        list.set(position, new UserModel(user.getId(), name, age, occupation, city));
                        notifyItemChanged(position);
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        // Hiển thị hộp thoại
        builder.show();
    }

    private void deleteUser(UserModel user, int position) {
        firestore.collection("users").document(user.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật danh sách và thông báo
                    list.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Người dùng đã được xóa", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa người dùng", Toast.LENGTH_SHORT).show());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, tv2, tv3, tv4;
        Button btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.userNameTextView);
            tv2 = itemView.findViewById(R.id.ageTextView);
            tv3 = itemView.findViewById(R.id.occupationTextView);
            tv4 = itemView.findViewById(R.id.cityTextView);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}

