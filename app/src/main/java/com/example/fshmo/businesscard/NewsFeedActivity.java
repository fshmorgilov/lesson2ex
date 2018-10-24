package com.example.fshmo.businesscard;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.fshmo.businesscard.data.DataCache;
import com.example.fshmo.businesscard.data.DataUtils;
import com.example.fshmo.businesscard.data.exceptions.CacheIsEmptyException;
import com.example.fshmo.businesscard.decorators.GridSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsFeedActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private static final String LTAG = NewsFeedActivity.class.getCanonicalName();
    private RecyclerView recyclerView;
    private NewsFeedAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration;
    private List<NewsItem> newsItems = new ArrayList<>();
    private int progressBarProgress = 0;
    private int orientation;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        initializeViews();
        configuresViews();
        try {
            this.newsItems.addAll(DataCache.getNewsCache());
        } catch (CacheIsEmptyException e) {
            fillViews();
        }
    }

    @Override
    protected void onDestroy() {
        progressBarProgress = 0;
        if (disposable != null)
            disposable.dispose();
        DataCache.invalidateNewsCache();
        super.onDestroy();
    }

    private void initializeViews() {
        Log.i(LTAG, "Initializing...");
        orientation = this.getResources().getConfiguration().orientation;
        RequestManager glide = Glide.with(this);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new NewsFeedAdapter(
                newsItems,
                glide,
                item -> {
                    Log.e(LTAG, item.getTitle());
                    NewsDetailsActivity.start(NewsFeedActivity.this, item);
                });
        decoration = new GridSpaceItemDecoration(4, 4);
        Log.i(LTAG, "Initializing done");
    }

    private void configuresViews() {
        Log.i(LTAG, "Configuring...");
        recyclerView.setHasFixedSize(true);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
        } else
            layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Log.i(LTAG, "Configuring done");
    }

    private void fillViews() {
        Log.i(LTAG, "Filling News");
        int progressStep = 100 / (DataUtils.generateNews().size());
        Observable<? extends Long> disposableTimer =
                Observable.interval(2, TimeUnit.SECONDS);
        disposable =
                Observable.zip(
                        disposableTimer,
                        Observable.fromIterable(DataUtils.generateNews()),
                        (o, newsItem) -> newsItem
                )
                        .doOnNext(item -> Log.e(LTAG, Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (newsItem) -> {
                                    adapter.addItem(newsItem);
                                    progressBarProgress = progressBarProgress + progressStep;
                                    progressBar.setProgress(progressBarProgress);
                                },
                                (err) -> Log.e(LTAG, err.getMessage()),
                                () -> {
                                    for (NewsItem newsItem : this.newsItems) {
                                        DataCache.addToNewsCache(newsItem);
                                    }
                                    this.progressBar.setVisibility(View.GONE);
                                });
        Log.i(LTAG, "Filling News done");
    }
}
