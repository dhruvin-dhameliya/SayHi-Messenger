package com.group_project.chatapplication.singleChat;

public class Chatmodel {

    private String sender,message,timestamp ,type;

    public Chatmodel() {
    }

    public Chatmodel(String sender, String message, String type) {
        this.sender = sender;
        this.message = message;
        this.type = type;
    }

    public Chatmodel(String sender, String message, String timestamp, String type) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
