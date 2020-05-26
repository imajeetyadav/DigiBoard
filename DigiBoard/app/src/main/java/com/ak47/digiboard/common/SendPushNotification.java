package com.ak47.digiboard.common;

public class SendPushNotification {
    private String senderEmail, receiverEmail;

    public SendPushNotification(String senderEmail, String receiverEmail) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
    }

    void sendMessage(String message) {
        // toDO: send notification
    }
}
