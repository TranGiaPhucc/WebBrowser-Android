package com.hufi.webbrowser;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FTP extends AsyncTask<String, String, Boolean> {
    private FTPClient ftpClient;
    private FTPFile[] files;

    @Override
    protected Boolean doInBackground(String... params) {
        String serverAddress = params[0];
        String username = params[1];
        String password = params[2];

        try {
            ftpClient = new FTPClient();
            ftpClient.connect(serverAddress, 21);       //"192.168.1.77"
            ftpClient.login(username, password);

            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);      //SYST_UNIX
            conf.setUnparseableEntries(true);
            ftpClient.configure(conf);

            int replyCode = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new IOException("Connection failed: Server replied with code " + replyCode);
            }

            //Enter passive mode (optional, but recommended for firewalled environments)
            //ftpClient.enterLocalPassiveMode();

            String path = "/AiDisk_a1";
            files = ftpClient.listFiles(path);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public FTPFile[] getFilesList() {
        /*try {
            FTPFile[] files = ftpClient.listFiles();          //listFiles()    listDirectories()
            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
        return files;
    }

    public String[] getFilesNameList() {
        try {
            String[] files = ftpClient.listNames();
            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCurrentDirectory() {
        String dir = "";
        try {
            dir = ftpClient.printWorkingDirectory();
            int replyCode = ftpClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Current working directory: " + dir);
            } else {
                System.out.println("Failed to get working directory: Reply code " + replyCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }

        //ftpClient.connect(serverAddress, 8200);       //serverAddress[, port 8200]



        /*int replyCode = ftpClient.getReplyCode();

        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new IOException("Connection failed: Server replied with code " + replyCode);
        }

        if (!ftpClient.login(username, password)) {
            throw new IOException("Login failed: Username or password incorrect");
        }*/

    public void downloadFile(String remoteFilePath, String localFilePath) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(localFilePath));
            if (!ftpClient.retrieveFile(remoteFilePath, outputStream)) {
                throw new IOException("Download failed");
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void uploadFile(String localFilePath, String remoteFilePath) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(localFilePath));
            if (!ftpClient.storeFile(remoteFilePath, inputStream)) {
                throw new IOException("Upload failed");
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }
        /*ftpClient.login("FTP_User", "ravi1998");

        ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);

        InputStream inStream = ftpClient.retrieveFileStream("record.xls");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FTP.this, "done", Toast.LENGTH_SHORT).show();
            }
        });
        ftpClient.disconnect();
        Workbook wb = Workbook.getWorkbook(inStream);
        Sheet s = wb.getSheet(0);
        int row = s.getRows();
        int col = s.getColumns();

        for (int i = 0; i < row; i++) {
            for(int c = 0, c< col; c++){
                Cell z = s.getCell(c, i);
                String content = z.getContents();
            }
        }*/
}
