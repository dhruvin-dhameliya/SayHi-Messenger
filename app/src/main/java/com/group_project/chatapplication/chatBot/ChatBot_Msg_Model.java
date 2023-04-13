package com.group_project.chatapplication.chatBot;

public class ChatBot_Msg_Model {

    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    String message, sentBy;

    public ChatBot_Msg_Model() {
    }

    public ChatBot_Msg_Model(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

}
