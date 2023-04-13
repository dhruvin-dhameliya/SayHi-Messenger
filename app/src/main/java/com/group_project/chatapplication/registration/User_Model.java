package com.group_project.chatapplication.registration;

public class User_Model {

    String Id, Name, Phone, About, Profile_image, onlineStatus, typing;

    public User_Model() {
    }

    public User_Model(String id, String name, String phone, String about, String profile_image, String onlineStatus, String typing) {
        Id = id;
        Name = name;
        Phone = phone;
        About = about;
        Profile_image = profile_image;
        this.onlineStatus = onlineStatus;
        this.typing = typing;
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

    public void setProfile_image(String profile_image) {
        Profile_image = profile_image;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }
}
