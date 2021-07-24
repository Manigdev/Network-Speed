package com.app.dvara;

public class NetworkModel {

    String mobile_number;
    String date_time;
    String download_speed;
    String upload_speed;

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getDownload_speed() {
        return download_speed;
    }

    public void setDownload_speed(String download_speed) {
        this.download_speed = download_speed;
    }

    public String getUpload_speed() {
        return upload_speed;
    }

    public void setUpload_speed(String upload_speed) {
        this.upload_speed = upload_speed;
    }
}
