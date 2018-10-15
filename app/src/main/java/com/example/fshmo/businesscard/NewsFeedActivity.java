package com.example.fshmo.businesscard;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
    private Disposable observable_closed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        int orientation = this.getResources().getConfiguration().orientation;
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

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
        } else
            layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        getStarredNews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressBarProgress = 0;
        observable_closed.dispose();
    }

    private void getStarredNews() {
        Observable<? extends Long> timer = Observable.interval(2, TimeUnit.SECONDS);
        observable_closed = Observable.zip(timer,
                Observable.fromIterable(DataUtils.generateNews()),
                (o, newsItem) -> newsItem)
                .doOnNext(item -> Log.e(LTAG, Thread.currentThread().getName()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsItem -> {
                    adapter.addItem(newsItem);
                    progressBarProgress = progressBarProgress + 20;
                    if (progressBarProgress > 100) progressBarProgress = 100;
                    progressBar.setProgress(progressBarProgress);
                });
    }
}
