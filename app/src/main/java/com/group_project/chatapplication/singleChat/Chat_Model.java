package com.group_project.chatapplication.singleChat;

public class Chat_Model {

    private String mobile,name,messages,date;

    public Chat_Model() {
    }

    public Chat_Model(String mobile, String name, String messages, String date) {
        this.mobile = mobile;
        this.name = name;
        this.messages = messages;
        this.date = date;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
