package com.group_project.chatapplication.singleChat.single_chat_list;

public class Chat_List_Model {

    String timestamp;
    String last_message;
    String receiver_name;
    String receiver_no;
    String receiver_profileImage;
    String receiver_info;

    public Chat_List_Model() {
    }

    public Chat_List_Model(String timestamp, String last_message, String receiver_name, String receiver_no, String receiver_profileImage, String receiver_info) {
        this.timestamp = timestamp;
        this.last_message = last_message;
        this.receiver_name = receiver_name;
        this.receiver_no = receiver_no;
        this.receiver_profileImage = receiver_profileImage;
        this.receiver_info = receiver_info;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_no() {
        return receiver_no;
    }

    public void setReceiver_no(String receiver_no) {
        this.receiver_no = receiver_no;
    }

    public String getReceiver_profileImage() {
        return receiver_profileImage;
    }

    public void setReceiver_profileImage(String receiver_profileImage) {
        this.receiver_profileImage = receiver_profileImage;
    }

    public String getReceiver_info() {
        return receiver_info;
    }

    public void setReceiver_info(String receiver_info) {
        this.receiver_info = receiver_info;
    }
}
