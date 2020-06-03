package com.ak47.digiboard.model;

public class NotificationModel {
    String title, message, notificationTime, type;

    public NotificationModel(String title, String message, String notificationTime, String type) {
        this.title = title;
        this.message = message;
        this.notificationTime = notificationTime;
        this.type = type;
    }

    public NotificationModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", notificationTime='" + notificationTime + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
