package com.hufi.webbrowser;

public class History {
    int id;
    String url;

    public History(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public History(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
