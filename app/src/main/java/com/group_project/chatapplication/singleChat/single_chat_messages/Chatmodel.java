package com.group_project.chatapplication.singleChat.single_chat_messages;

public class Chatmodel {

    private String sender,message,timestamp ,type;
    int feeling;

    public Chatmodel() {
    }

    public Chatmodel(String sender, String message, String type,int feeling) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.feeling=feeling;
    }

    public Chatmodel(String sender, String message, String timestamp, String type,int feeling) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.feeling=feeling;
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

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}
