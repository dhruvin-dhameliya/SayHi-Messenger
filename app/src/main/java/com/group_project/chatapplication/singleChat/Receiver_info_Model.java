package com.group_project.chatapplication.singleChat;

public class Receiver_info_Model {

    String receiverNo, receiverName, receiverProfileImg, receiverInfo, roomId;


    public Receiver_info_Model() {
    }

    public Receiver_info_Model(String receiverNo, String receiverName, String receiverProfileImg, String receiverInfo, String roomId) {
        this.receiverNo = receiverNo;
        this.receiverName = receiverName;
        this.receiverProfileImg = receiverProfileImg;
        this.receiverInfo = receiverInfo;
        this.roomId = roomId;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}