package com.example.fshmo.businesscard.data.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface NewsDao {

    @Query("SELECT * FROM news")
    List<NewsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(NewsEntity ... newsEntities);
//    void insertAll(List<NewsEntity> newsEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsEntity newsEntity);

    @Delete
    void delete(NewsEntity newsEntity);

    @Query("DELETE FROM news")
    void deleteAll();

    @Query("SELECT * FROM news WHERE title LIKE :title LIMIT 1")
    Single<NewsEntity> findByTitle(String title);

    @Query("SELECT * FROM news WHERE id = :id")
    Single<NewsEntity> findById(int id);

    @Query("SELECT * FROM news WHERE title IN (:titles)")
    List<NewsEntity> loadAllByTitle(String[] titles);


}
