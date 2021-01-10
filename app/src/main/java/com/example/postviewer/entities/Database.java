package com.example.postviewer.entities;

import android.content.Context;
import android.widget.Toast;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.postviewer.MainActivity;

@androidx.room.Database(entities = {Post.class, Author.class}, version = 1)
public abstract class Database extends RoomDatabase {
    private static Database instance;
    private static  final String DB_NAME = "htec_db";
    public static Database getInstance(Context context){
       if(instance==null)
           instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, DB_NAME)
            .allowMainThreadQueries().build();
        return instance;
    }

    public abstract DAO_Post getDAO_Post();
    public abstract DAO_Author getDAO_Author();

}
