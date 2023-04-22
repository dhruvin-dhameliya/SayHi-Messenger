package com.group_project.chatapplication.singleChat.single_chat_messages;

public class Chatmodel {

    private String sender, message, timestamp, type, receiver;
    boolean isSeen;

    public Chatmodel() {
    }

    public Chatmodel(String sender, String message, String type, boolean isSeen, String receiver) {
        this.sender = sender;
        this.message = message;
        this.type = type;
        this.isSeen = isSeen;
        this.receiver = receiver;
        //this.feeling=feeling;
    }

    public Chatmodel(String sender, String message, String timestamp, String type) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        // this.feeling=feeling;
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

    public boolean onClick(float x, float y) {
        return false;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
