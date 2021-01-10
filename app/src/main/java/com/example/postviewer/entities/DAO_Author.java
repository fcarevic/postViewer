package com.example.postviewer.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DAO_Author {
    @Query("SELECT  * from author where id = :id")
    Author getById(int id);


    @Insert
    void insertALl(Author... authors);

    @Query("DELETE FROM author")
    void deleteAll();
}