package com.rapida.messenger;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class Message {

    @NonNull
    @PrimaryKey
    private String id;
    private String messageType;
    private String message;
    private String groupID;
    private String senderPhone;
    private String messageTime;

    public Message(@NonNull String id, String messageType, String message, String groupID, String senderPhone, String messageTime) {
        this.id = id;
        this.messageType = messageType;
        this.message = message;
        this.messageTime = messageTime;
        this.senderPhone = senderPhone;
        this.groupID = groupID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }
}
