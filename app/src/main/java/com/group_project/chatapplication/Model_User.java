package com.group_project.chatapplication;

public class Model_User {

//    String user_id, user_name, user_phone, user_email, user_password;  // (Future - use)

    String user_id, user_phone;

    public Model_User() {
    }

    public Model_User(String user_id, String user_phone) {
        this.user_id = user_id;
        this.user_phone = user_phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

}
