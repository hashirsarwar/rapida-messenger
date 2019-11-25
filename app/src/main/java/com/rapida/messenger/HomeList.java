package com.rapida.messenger;

public class HomeList implements Comparable<HomeList> {

    private String groupID;
    private String groupName;
    private String message;
    private String messageTime;

    public HomeList(String groupID,String groupName, String message, String messageTime) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.message = message;
        this.messageTime = messageTime;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public int compareTo(HomeList o) {
        return this.getMessageTime().compareTo(o.getMessageTime());
    }
}
