package com.group_project.chatapplication.groupChat.group_chat_messages;

public class Model_Group_Chat_Messages {
    String message, sender, timestamp, type;

    public Model_Group_Chat_Messages() {
    }

    public Model_Group_Chat_Messages(String message, String sender, String timestamp, String type) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
