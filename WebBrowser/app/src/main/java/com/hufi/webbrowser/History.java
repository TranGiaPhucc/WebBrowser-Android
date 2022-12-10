package com.hufi.webbrowser;

public class History {
    String url;
    String title;
    int count = 0;

    public History(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public History(String url, String title, int count) {
        this.url = url;
        this.title = title;
        this.count = count;
    }

    @Override
    public String toString() {
        return "History{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", count=" + count +
                '}';
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
