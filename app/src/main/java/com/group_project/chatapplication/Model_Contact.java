package com.group_project.chatapplication;

public class Model_Contact {

    String contact_number, contact_name;

    public Model_Contact() {
    }

    public Model_Contact(String contact_number, String contact_name) {
        this.contact_number = contact_number;
        this.contact_name = contact_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

}
