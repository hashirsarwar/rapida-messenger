package com.rapida.messenger;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

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
