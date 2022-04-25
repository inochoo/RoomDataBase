package com.sibaamap.roomdatabase.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sibaamap.roomdatabase.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "user.db";
    private static UserDatabase instance;
    //synchronized : tuần tự
    public static synchronized UserDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),UserDatabase.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract UserDao userDao();

}
