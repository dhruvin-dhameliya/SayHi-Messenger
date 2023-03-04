package com.group_project.chatapplication.stories;

import java.util.ArrayList;

public class UserStories_Model {

    String name, profileImage;
    long lastupdated;
    ArrayList<Stories_Model> statuses;

    public UserStories_Model() {
    }

    public UserStories_Model(String name, String profileImage, long lastupdated, ArrayList<Stories_Model> statuses) {
        this.name = name;
        this.profileImage = profileImage;
        this.lastupdated = lastupdated;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public ArrayList<Stories_Model> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Stories_Model> statuses) {
        this.statuses = statuses;
    }

}
