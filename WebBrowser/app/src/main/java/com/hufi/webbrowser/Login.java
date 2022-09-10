package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    Button btnLogin, btnRegister;
    EditText txtUsername, txtPassword;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new Database(Login.this);
        db.createTable();

        if (!db.checkUserExist("admin", "admin")) {
            NguoiDung nd = new NguoiDung("admin", "admin", "Trần Gia Phúc", 0);
            db.insert(nd);
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                boolean isExist = db.checkUserExist(username, password);

                if (username.equals("") == true || password.equals("") == true) {
                    Toast.makeText(Login.this, "Chưa nhập username/password.", Toast.LENGTH_SHORT).show();
                }
                else if (isExist) {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    NguoiDung nd = db.getNguoiDung(username);
                    String name = nd.getName();
                    int webCount = nd.getWebcount();
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("name", name);
                    intent.putExtra("webcount", webCount);
                    Toast.makeText(Login.this, "Xin chào " + name + ". Bạn đã duyệt " + webCount + " trang web.", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(Login.this, "Xin chào " + name + ".", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else
                    Toast.makeText(Login.this, "Sai username/password.", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }
}