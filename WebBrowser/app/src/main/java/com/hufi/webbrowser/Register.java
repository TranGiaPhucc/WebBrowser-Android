package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    Button btnRegAccount;
    EditText txtUsernameReg, txtPasswordReg, txtNameReg;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new Database(Register.this);
        db.createTable();

        txtUsernameReg = findViewById(R.id.txtUsernameReg);
        txtPasswordReg = findViewById(R.id.txtPasswordReg);
        txtNameReg = findViewById(R.id.txtNameReg);
        btnRegAccount = findViewById(R.id.btnRegAccount);

        btnRegAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsernameReg.getText().toString();
                String password = txtPasswordReg.getText().toString();
                String name = txtNameReg.getText().toString();

                boolean isExist = db.checkUserNameExist(username);

                if (username.equals("") == true)
                {
                    Toast.makeText(Register.this, "Chưa nhập username.", Toast.LENGTH_SHORT).show();
                }
                else if (isExist)
                {
                    Toast.makeText(Register.this, "Username đã tồn tại.", Toast.LENGTH_SHORT).show();
                }
                else if (password.equals("") == true || name.equals("") == true) {
                    Toast.makeText(Register.this, "Chưa nhập password/name.", Toast.LENGTH_SHORT).show();
                }
                else {
                    NguoiDung nd = new NguoiDung(username, password, name, 0);
                    db.insert(nd);
                    Toast.makeText(Register.this, "Đăng ký tài khoản thành công.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}