package com.example.fshmo.businesscard.activities;

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
import com.example.fshmo.businesscard.Category;
import com.example.fshmo.businesscard.NewsItem;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.decorators.GridSpaceItemDecoration;
import com.example.fshmo.businesscard.data.DataCache;
import com.example.fshmo.businesscard.data.DataUtils;
import com.example.fshmo.businesscard.web.NewsTypes;
import com.example.fshmo.businesscard.web.topstories.TopStoriesApi;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.util.ArrayList;
import java.util.List;

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
//        try {
//            this.newsItems.addAll(DataCache.getNewsCache());
//        } catch (CacheIsEmptyException e) {
            fillViews();
//        }
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
        disposable =
                TopStoriesApi.getInstance(NewsTypes.arts)
                        .topStories().get(NewsTypes.arts)
                        .flatMapObservable(this::makeNewsItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (newsItem) -> {
                                    adapter.addItem(newsItem);
                                    progressBarProgress = progressBarProgress + progressStep;
                                    progressBar.setProgress(progressBarProgress);
                                },
                                this::logItemError,
                                this::cacheAndComplete
                                );
        Log.i(LTAG, "Filling News done");
    }

    private Observable<NewsItem> makeNewsItem(ResponseDTO responseDTO) {
        List<NewsItem> newsItems = new ArrayList<>();
        for (ResultsDTO result : responseDTO.getResults()) {
            String url;
            if (result.getMultimedia().isEmpty())
                url = null;
            else
                url = result.getMultimedia().get(0).getUrl();
            NewsItem newsItem = new NewsItem(
                    result.getTitle(),
                    url,
                    new Category(5, result.getSection()),//fixme
                    result.getDatePublished(),
                    result.getShortDescription(),//fixme
                    result.getShortDescription()//fixme
            );
            newsItems.add(newsItem);
        }
        return Observable.fromIterable(newsItems);
    }

    private void logItemError(Throwable err){
        Log.e(LTAG, err.getMessage());
    }

    private void cacheAndComplete(){
        for (NewsItem newsItem : this.newsItems) {
            DataCache.addToNewsCache(newsItem);
        }
        this.progressBar.setVisibility(View.GONE);
    }
}
