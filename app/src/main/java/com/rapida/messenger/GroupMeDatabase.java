package com.rapida.messenger;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Group.class,Message.class},version = 1, exportSchema = false)
public abstract class GroupMeDatabase extends RoomDatabase {
    private static GroupMeDatabase instance;
    public abstract GroupDao groupDao();
    public abstract MessageDao messageDao();
    public static synchronized GroupMeDatabase  getInstance(Context context)
    {
        if(instance == null) {

            instance = Room.databaseBuilder(context,GroupMeDatabase.class,"GroupMeDatabase").build();
        }
        return instance;
    }
}
