package com.rapida.messenger;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class Group implements Comparable<Group> {

    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String adminPhone;
    private String timeCreated;

    public Group(String id, String name, String adminPhone, String timeCreated) {
        this.id = id;
        this.name = name;
        this.adminPhone = adminPhone;
        this.timeCreated = timeCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public int compareTo(Group o) {
        return this.getTimeCreated().compareTo(o.getTimeCreated());
    }
}
