package com.rapida.messenger;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insertMessage (Message a);

    @Query("select message,messageTime,messageType from message where groupID = :gid order by messageTime desc limit 1")
    MessageAndTime getLatestMessage(String gid);

    @Query("select * from message where groupID=:gid")
    List<Message> getAllMessages(String gid);

    @Query("select * from message where id = :mid")
    Message checkIfExists(String mid);
}
