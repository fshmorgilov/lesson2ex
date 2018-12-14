package com.example.fshmo.businesscard.data;

import android.util.Log;

import com.example.fshmo.businesscard.App;
import com.example.fshmo.businesscard.data.exceptions.CacheIsEmptyException;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCache {

    private static final App achor = App.INSTANCE;
    private static final String LTAG = DataCache.class.getName();
    private static volatile List<NewsItem> newsCache = new ArrayList<>();

    public static synchronized void addToNewsCache(NewsItem newsItem) {
        newsCache.add(newsItem);
        Log.i(LTAG, "Item added");
    }

    public static synchronized void invalidateNewsCache() {
        newsCache.clear();
        Log.i(LTAG, "Cache invalidated");
    }

    public static List<NewsItem> getNewsCache() throws CacheIsEmptyException {
        if (!newsCache.isEmpty()) {
            Log.i(LTAG, "Cache retrieved");
            return newsCache;
        } else {
            Log.i(LTAG, "Cache is empty");
            throw new CacheIsEmptyException();
        }
    }
}
