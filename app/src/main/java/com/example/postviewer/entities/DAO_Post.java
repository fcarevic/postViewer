package com.example.postviewer.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO_Post {
    @Query("SELECT * from post")
    List<Post> getAllPosts();
    @Query("SELECT * from post where id = :id")
    Post getById(int id);
    @Insert
    void insertAll(Post... posts);

    @Delete
    void delete(Post post);

    @Query("DELETE FROM post")
    void deleteAll();
}
