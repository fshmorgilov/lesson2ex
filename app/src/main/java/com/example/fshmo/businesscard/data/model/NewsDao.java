package com.example.fshmo.businesscard.data.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.fshmo.businesscard.data.NewsItem;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface NewsDao {

    @Query("SELECT * FROM news")
    List<NewsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NewsEntity> newsEntities);

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
