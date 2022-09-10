package com.hufi.webbrowser;

public class NguoiDung {
    String username;
    String password;
    String name;
    int webcount;

    public NguoiDung(String username, String password, String name, int webcount) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.webcount = webcount;
    }

    public NguoiDung(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWebcount() {
        return webcount;
    }

    public void setWebcount(int webcount) {
        this.webcount = webcount;
    }

    @Override
    public String toString() {
        return "NguoiDung{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", webcount=" + webcount +
                '}';
    }
}
