package com.example.fshmo.businesscard.data;

import android.util.Log;

import com.example.fshmo.businesscard.NewsItem;
import com.example.fshmo.businesscard.data.exceptions.CacheIsEmptyException;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCache {

    private static final String LTAG = DataCache.class.getName();

    private static List<NewsItem> newsCache = new ArrayList<>();

    public static synchronized void addToNewsCache(NewsItem newsItem) {
        newsCache.add(newsItem);
        Log.i(LTAG, "Item Added");
    }

    public static synchronized void invalidateNewsCache() {
        newsCache.clear();
        Log.i(LTAG, "Cache invalidated");
    }

    public static List<NewsItem> getNewsCache() throws CacheIsEmptyException {
        Log.i(LTAG, "Cache retrieved");
        if (!newsCache.isEmpty())
            return newsCache;
        else throw new CacheIsEmptyException();
    }
}
