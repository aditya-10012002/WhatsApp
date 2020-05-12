package com.example.whatsapp.Model;

public class Message {

    public String sender, reciever, message;
    public boolean seen;

    public Message (String sender, String reciever, String message, boolean seen) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.seen = seen;
    }

    public Message() {
    }
}
