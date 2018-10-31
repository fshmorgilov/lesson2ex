package com.example.fshmo.businesscard.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.decorators.GridSpaceItemDecoration;
import com.example.fshmo.businesscard.data.DataCache;
import com.example.fshmo.businesscard.web.NewsTypes;
import com.example.fshmo.businesscard.web.topstories.TopStoriesApi;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsFeedActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private static final String LTAG = NewsFeedActivity.class.getCanonicalName();
    private View errorView;
    private View errorNoData;
    private Button retryBtn;
    private RecyclerView recyclerView;
    private NewsFeedAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration;
    private List<NewsItem> newsItems = new ArrayList<>();
    private AlertDialog.Builder alertBuilder;
    private int progressBarProgress = 0;
    private int orientation;
    private Disposable disposable;
    private String categoryName = "home";
    private int progressStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        initializeViews();
        configuresViews();
        fillViews();
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
        errorNoData = findViewById(R.id.view_no_data);
        errorView = findViewById(R.id.view_error);
        retryBtn = findViewById(R.id.btn_retry_error);
        retryBtn.setOnClickListener(v -> fillViews());
        alertBuilder = new AlertDialog.Builder(this);
        //TODO допилить просто error

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

        String[] categoryNames = NewsTypes.getNames(NewsTypes.class);
        alertBuilder.setTitle("Choose category")
                .setItems(
                        R.array.categories_array,
                        (dialog, which) -> categoryName = categoryNames[which]
                );
        Log.i(LTAG, "Configuring done");
    }

    private void fillViews() {
        showState(State.Loading);
        Log.i(LTAG, "Filling News");
        disposable =
                TopStoriesApi.getInstance()
                        .topStories().get(NewsTypes.valueOf(categoryName))
                        .flatMapObservable(this::makeNewsItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::showItem,
                                this::logItemError,
                                this::cacheAndComplete
                        );
        Log.i(LTAG, "Filling News done");
    }

    private Observable<NewsItem> makeNewsItem(@NonNull ResponseDTO responseDTO) {
        List<NewsItem> newsItems = new ArrayList<>();
        this.progressStep = 100/newsItems.size();
        for (ResultsDTO result : responseDTO.getResults()) {
            NewsItem newsItem = new NewsItem(result);
            newsItems.add(newsItem);
        }
        return Observable.fromIterable(newsItems);
    }

    private void logItemError(@Nullable Throwable err) {
        Log.e(LTAG, err.getMessage());
        if (err instanceof IOException) {
            showState(State.HasNoData);
        }
    }

    public void showState(@NonNull State state) {
        switch (state) {
            case HasData:
                recyclerView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                break;

            case HasNoData:
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                errorNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                break;

            case NetworkError:
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                break;

            case ServerError:
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                break;

            case Loading:
                recyclerView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;

            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    private void cacheAndComplete() {
        for (NewsItem newsItem : this.newsItems) {
            DataCache.addToNewsCache(newsItem);
        }
        this.showState(State.HasData);
        //TODO HAS NO DATA
    }

    private void showItem(NewsItem newsItem) {
        adapter.addItem(newsItem);
        progressBarProgress = progressBarProgress + progressStep;
        progressBar.setProgress(progressBarProgress);
    }
}
