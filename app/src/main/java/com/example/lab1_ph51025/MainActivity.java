package com.example.lab1_ph51025;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserModel> list;
    FloatingActionButton btn_add;
    Button btn_them, btn_huy;
    TextInputEditText txt_name, txt_age, txt_occupation, txt_city;

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.reMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_add = findViewById(R.id.btn_add);

        btn_add.setOnClickListener(v -> {
            View view = LayoutInflater.from(this).inflate(R.layout.item_add, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            AlertDialog alertDialog = builder.create();

            btn_them = view.findViewById(R.id.btn_them);
            btn_huy = view.findViewById(R.id.btn_huy);
            txt_name = view.findViewById(R.id.edt_nameUser);
            txt_age = view.findViewById(R.id.edt_ageUser);
            txt_occupation = view.findViewById(R.id.edt_occupation);
            txt_city = view.findViewById(R.id.edt_cityUser);

            btn_them.setOnClickListener(v1 -> {
                String id = firestore.collection("users").document().getId();
                String userName = txt_name.getText().toString();
                String age = txt_age.getText().toString();
                String occupation = txt_occupation.getText().toString();
                String city = txt_city.getText().toString();

                if (!userName.isEmpty() && !age.isEmpty() && !occupation.isEmpty() && !city.isEmpty()) {
                    CollectionReference cities = firestore.collection("users");
                    UserModel newUser = new UserModel(id, userName, Integer.parseInt(age), occupation, city);

                    cities.add(newUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                alertDialog.dismiss();
                                docDulieu();  // Sau khi thêm, cập nhật lại dữ liệu
                                Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Error adding document", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            });

            btn_huy.setOnClickListener(v1 -> alertDialog.dismiss());
            alertDialog.show();
        });

        list = new ArrayList<>();

        // Khởi tạo userAdapter và gán vào RecyclerView
        userAdapter = new UserAdapter(this, list);
        recyclerView.setAdapter(userAdapter);

        ghiDulieu();


        findViewById(R.id.btnlogout).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FireBaseActivity.class);
            startActivity(intent);
        });
    }

    private void ghiDulieu() {
        CollectionReference users = firestore.collection("users");
        users.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                addDefaultUsers(users);
            } else {
                docDulieu();
            }
        });


    }

    private void addDefaultUsers(CollectionReference users) {
        // Thêm người dùng mặc định vào Firestore

        // Người dùng 1
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "John Doe");
        user1.put("age", 28);
        user1.put("occupation", "Engineer");
        user1.put("city", "San Francisco");
        users.document("u1").set(user1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User u1 added successfully.");
            } else {
                Log.e(TAG, "Error adding user u1", task.getException());
            }
        });

        // Người dùng 2
        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "Jane Smith");
        user2.put("age", 32);
        user2.put("occupation", "Designer");
        user2.put("city", "Los Angeles");
        users.document("u2").set(user2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User u2 added successfully.");
            } else {
                Log.e(TAG, "Error adding user u2", task.getException());
            }
        });

        // Người dùng 3
        Map<String, Object> user3 = new HashMap<>();
        user3.put("name", "Alice Johnson");
        user3.put("age", 24);
        user3.put("occupation", "Product Manager");
        user3.put("city", "New York");
        users.document("u3").set(user3).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User u3 added successfully.");
            } else {
                Log.e(TAG, "Error adding user u3", task.getException());
            }
        });

        // Người dùng 4
        Map<String, Object> user4 = new HashMap<>();
        user4.put("name", "Bob Brown");
        user4.put("age", 40);
        user4.put("occupation", "CEO");
        user4.put("city", "Chicago");
        users.document("u4").set(user4).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User u4 added successfully.");
            } else {
                Log.e(TAG, "Error adding user u4", task.getException());
            }

            docDulieu();
        });
    }

    private void docDulieu() {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            int age = document.getLong("age").intValue();
                            String occupation = document.getString("occupation");
                            String city = document.getString("city");

                            UserModel user = new UserModel(id, name, age, occupation, city);

                            list.add(user);
                        }

                        userAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
