package com.hufi.webbrowser;

public class History {
    int id;
    String url;
    String title;

    public History(String url, String title) {
        this.url = url;
        this.title = title;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
