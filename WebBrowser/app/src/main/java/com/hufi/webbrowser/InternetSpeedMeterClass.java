package com.hufi.webbrowser;

import java.util.Date;

public class InternetSpeedMeterClass {
    Date date;
    double upload;
    double download;

    public InternetSpeedMeterClass(Date date, double upload, double download) {
        this.date = date;
        this.upload = upload;
        this.download = download;
    }

    @Override
    public String toString() {
        return "InternetSpeedMeterClass{" +
                "date=" + date +
                ", upload=" + upload +
                ", download=" + download +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getUpload() {
        return upload;
    }

    public void setUpload(double upload) {
        this.upload = upload;
    }

    public double getDownload() {
        return download;
    }

    public void setDownload(double download) {
        this.download = download;
    }
}
