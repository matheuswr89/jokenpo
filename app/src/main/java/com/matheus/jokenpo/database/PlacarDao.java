package com.matheus.jokenpo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.matheus.jokenpo.model.Placar;

import java.util.List;

@Dao
public interface PlacarDao {

    @Query("SELECT * FROM placar")
    List<Placar> getAll();

    @Insert
    void insert(Placar placar);

    @Query("DELETE FROM placar")
    void nukeTable();
}
