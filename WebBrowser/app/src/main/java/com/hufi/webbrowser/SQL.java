package com.hufi.webbrowser;

import android.content.Context;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQL {
    private static String ip = "192.168.1.65";
    private static String port = "1433";
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "WebBrowser";
    private static String username = "client";
    private static String password = "123";
    private static String url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database;
    private static int timeout = 5;     //Seconds

    private Connection connection;

    public SQL() { connection = Connect(); }

    public void Close() throws SQLException { connection.close(); }

    public Connection Connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection objConn = null;
        try {
            Class.forName(Classes);
            DriverManager.setLoginTimeout(timeout);
            objConn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return objConn;
    }

    public boolean isConnected() {
        if (connection != null)
            return true;
        return false;
    }
/*
    public boolean isUserExist(String username, String password) {
        if (connection == null)
            return false;
        //return true;

        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Account where Email = '" + username + "' and Password = '" + password + "'");
            //while (resultSet.next()){
            //    textView.setText(resultSet.getS tring(1));
            //}
            if (resultSet.next())//nếu trong resultset không null thì sẽ trả về True
            {
                //hoten = resultSet.getString("Email");//Hàm lấy giá trị của tên cột (trường: field name) truyền vào
                //connection.close();
                return true;
            }
            else {
                //z = "Tài khoản không tồn tại !";
                //connection.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
 */
    public void insertAd(String url) {
        if (connection == null)
            return;

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("insert into Ad values ('" + url + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUrl(String url, String title) {
        if (connection == null)
            return;

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("insert into History values ('" + url + "', N'" + title + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertBookmark(String url, String title) {
        if (connection == null)
            return;

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("insert into Bookmark values ('" + url + "', N'" + title + "')" +
                    "where not exists (select * from Bookmark where url = '" + url + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBookmark(String url) {
        if (connection == null)
            return;

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("delete from Bookmark where url = '" + url + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBookmarkAll() {
        if (connection == null)
            return;

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery("delete from Bookmark");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  ArrayList<History> loadHistorySQL() throws SQLException {
        ArrayList<History> list = new ArrayList<>();
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "select * from History";
        // Thực thi câu lệnh SQL trả về đối tượng ResultSet. // Mọi kết quả trả về sẽ được lưu trong ResultSet
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            list.add(new History(rs.getString("url"), rs.getString("title")));
        }
        //connection.close();// Đóng kết nối
        return list;
    }

    public  ArrayList<Bookmark> loadBookmarkSQL() throws SQLException {
        ArrayList<Bookmark> list = new ArrayList<>();
        Statement statement = connection.createStatement();// Tạo đối tượng Statement.
        String sql = "select * from Bookmark";
        // Thực thi câu lệnh SQL trả về đối tượng ResultSet. // Mọi kết quả trả về sẽ được lưu trong ResultSet
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            list.add(new Bookmark(rs.getString("url"), rs.getString("title")));
        }
        //connection.close();// Đóng kết nối
        return list;
    }
}
