package com.group_project.chatapplication.registration;

public class User_Model {

    String Id, Name, Phone, About, Profile_image, OnlineStatus, TypingStatus;

    public User_Model() {
    }

    public User_Model(String id, String name, String phone, String about, String profile_image, String onlineStatus, String typingStatus) {
        Id = id;
        Name = name;
        Phone = phone;
        About = about;
        Profile_image = profile_image;
        OnlineStatus = onlineStatus;
        TypingStatus = typingStatus;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getProfile_image() {
        return Profile_image;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getTypingStatus() {
        return TypingStatus;
    }

    public void setTypingStatus(String typingStatus) {
        TypingStatus = typingStatus;
    }

    public void setProfile_image(String profile_image) {
        Profile_image = profile_image;
    }

}
