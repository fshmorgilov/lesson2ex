package com.example.fshmo.businesscard.data.repository;

import android.util.Log;

import com.example.fshmo.businesscard.App;
import com.example.fshmo.businesscard.data.model.AppDatabase;
import com.example.fshmo.businesscard.data.model.NewsDao;
import com.example.fshmo.businesscard.data.model.NewsEntity;
import com.example.fshmo.businesscard.utils.NewsItemHelper;
import com.example.fshmo.businesscard.web.NewsTypes;
import com.example.fshmo.businesscard.web.topstories.TopStoriesApi;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class NewsRepository {

    private static final String TAG = NewsRepository.class.getName();

    private static NewsDao newsDao = AppDatabase.getInstance(App.INSTANCE).newsDao();
//
    @NonNull
    public static Single<NewsEntity[]> downloadUpdates() {
        Log.i(TAG, "Writing items to Database");
        return TopStoriesApi.getInstance()
                .topStories().get(NewsTypes.automobiles)
                .map(NewsItemHelper::parseToDaoArray)
                .subscribeOn(Schedulers.io());
    }

    public static void saveToDb(NewsEntity[] newsEntities){
        newsDao.insertAll(newsEntities);
        Log.i(TAG, "saveToDb: inserted "  + newsEntities.length +" items");
    }

}
