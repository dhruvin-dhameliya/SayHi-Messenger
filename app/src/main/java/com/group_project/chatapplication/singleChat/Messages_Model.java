package com.group_project.chatapplication.singleChat;

public class Messages_Model {

    private String name, mobile, lastMessage, dp_img, chatKey;
    private int unseenMessages;

    public Messages_Model() {
    }

    public Messages_Model(String name, String mobile, String lastMessage, String dp_img, String chatKey, int unseenMessages) {
        this.name = name;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.dp_img = dp_img;
        this.chatKey = chatKey;
        this.unseenMessages = unseenMessages;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getDp_img() {
        return dp_img;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }

}
