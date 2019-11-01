package com.example.groupme;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GroupDao {

    @Insert
    void insertGroup(Group group);

    @Query("select * from `group`")
    List<Group> getAllGroups();

    @Query("select * from `group` where id = :gid")
    Group checkIfExists(String gid);
}
