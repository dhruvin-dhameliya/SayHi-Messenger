package com.group_project.chatapplication.singleChat;

public class Msg_Model {

    String receiverNo, receiverName, receiverProfileImg, receiverInfo;

    public Msg_Model() {
    }

    public Msg_Model(String receiverNo, String receiverName, String receiverProfileImg, String receiverInfo) {
        this.receiverNo = receiverNo;
        this.receiverName = receiverName;
        this.receiverProfileImg = receiverProfileImg;
        this.receiverInfo = receiverInfo;
    }

    public String getReceiverNo() {
        return receiverNo;
    }

    public void setReceiverNo(String receiverNo) {
        this.receiverNo = receiverNo;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverProfileImg() {
        return receiverProfileImg;
    }

    public void setReceiverProfileImg(String receiverProfileImg) {
        this.receiverProfileImg = receiverProfileImg;
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(String receiverInfo) {
        this.receiverInfo = receiverInfo;
    }
}
