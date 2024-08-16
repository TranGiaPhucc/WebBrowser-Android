package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FTPActivity extends AppCompatActivity {
    /*String serverAddress = "ftp.example.com";
    String username = "your_username";
    String password = "your_password";

    String localFilePath = "/storage/emulated/0/Download/myfile.txt";  // Replace with your local file path
    String remoteFilePath = "/public_html/myfile.txt";  // Replace with your remote file path*/

    private FTP ftp;
    private boolean isConnected = false;

    Button btnConnect;
    EditText txtFTP, txtUsername, txtPassword;
    TextView lbFTPList;
    CheckBox cbxAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);

        btnConnect = findViewById(R.id.btnConnect);
        txtFTP = findViewById(R.id.txtFTP);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        lbFTPList = findViewById(R.id.lbFTPList);
        cbxAnonymous = findViewById(R.id.cbxAnonymous);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected) {
                    String ftpAddress = txtFTP.getText().toString();
                    String username = txtUsername.getText().toString();
                    String password = txtPassword.getText().toString();

                    if (cbxAnonymous.isChecked()) {
                        username = "anonymous";
                        password = "";
                    }

                    if (ftpAddress.isEmpty())
                        ftpAddress = "";
                    if (username.isEmpty())
                        username = "";
                    if (password.isEmpty())
                        password = "";

                    //try {
                    //ftp = new FTP(ftpAddress, username, password);

                    boolean connecting = false;
                    try {
                        ftp = new FTP();
                        connecting = ftp.execute(ftpAddress, username, password).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        Toast.makeText(FTPActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(FTPActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                    if (connecting) {
                        isConnected = true;

                        txtFTP.setEnabled(false);
                        txtUsername.setEnabled(false);
                        txtPassword.setEnabled(false);
                        btnConnect.setText("Disconnect");

                        getFileList();

                        //ftp.downloadFile(remoteFilePath, localFilePath);
                        //ftp.uploadFile(localFilePath, remoteFilePath);
                    /*} catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(FTPActivity.this, e + "", Toast.LENGTH_LONG).show();
                    }*/
                    }
                }
                else {
                    disconnect();
                }
            }
        });
    }

    public void getFileList(){
        FTPFile[] files = ftp.getFilesList();
        int count = 0;
        if (files != null) {
            for (FTPFile file : files) {
                //Log.d("FTP Files", file.getName());
                count++;
                lbFTPList.setText(lbFTPList.getText().toString() + file.getName() + " (" + (file.isDirectory() ? "Directory" : "File") + ")" + "\n");
            }
            lbFTPList.setText(lbFTPList.getText().toString() + "\n" + "Count: " + count);
        }
        else
            lbFTPList.setText("null");
        //lbFTPList.setText(ftp.getCurrentDirectory());
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnect();
    }

    public void disconnect() {
        try {
            ftp.disconnect();

            isConnected = false;

            txtFTP.setEnabled(true);
            txtUsername.setEnabled(true);
            txtPassword.setEnabled(true);
            btnConnect.setText("Connect");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(FTPActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}