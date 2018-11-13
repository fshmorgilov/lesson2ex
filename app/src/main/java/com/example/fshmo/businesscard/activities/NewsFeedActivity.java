package com.example.fshmo.businesscard.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.decorators.GridSpaceItemDecoration;
import com.example.fshmo.businesscard.data.DataCache;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.AppDatabase;
import com.example.fshmo.businesscard.data.model.NewsDao;
import com.example.fshmo.businesscard.utils.NewsItemHelper;
import com.example.fshmo.businesscard.web.NewsTypes;
import com.example.fshmo.businesscard.web.topstories.TopStoriesApi;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsFeedActivity extends AppCompatActivity {

    private static final String LTAG = NewsFeedActivity.class.getCanonicalName();

    private ProgressBar progressBar;
    private FrameLayout recyclerFrame;
    private View errorView;
    private View errorNoData;
    private Button retryBtn;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private NewsFeedAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration;
    private Toolbar toolbar;
    private AlertDialog.Builder alertBuilder;

    private List<NewsItem> newsItems = new ArrayList<>();

    private String categoryName = String.valueOf(NewsTypes.home);
    private int progressBarProgress = 0;
    private int orientation;
    private int progressStep;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NewsDao newsDao;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NewsFeedActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        newsDao = AppDatabase.getInstance(this).newsDao();
        initializeViews();
        configuresViews();
        observeDb();
    }

    private void observeDb() {

        compositeDisposable.add(
//                Observable.just(newsDao.getAll())
                Observable.just(true)
                        .map(aBoolean -> newsDao.getAll())
                        .map(NewsItemHelper::convertDaoListoToDomain)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showItems)
        );
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
        fab = findViewById(R.id.fab);
        recyclerFrame = findViewById(R.id.recycler_frame);
        alertBuilder = new AlertDialog.Builder(this);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        toolbar = findViewById(R.id.feed_toolbar);

        orientation = this.getResources().getConfiguration().orientation;
        RequestManager glide = Glide.with(this);
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
                        (dialog, which) -> {
                            this.categoryName = categoryNames[which];
                            toolbar.setTitle(this.categoryName.toUpperCase());
                            int current_size = newsItems.size();
                            this.newsItems.clear();
                            adapter.notifyItemRangeRemoved(0, current_size);
                            loadToDb();
                        }
                );

        retryBtn.setOnClickListener(v -> loadToDb());
        fab.setOnClickListener(v -> loadToDb());

        toolbar.setTitle(this.categoryName.toUpperCase());
        setSupportActionBar(toolbar);

        Log.i(LTAG, "Configuring done");
    }

    private void loadToDb() {
        showState(State.Loading);
        compositeDisposable.add(
                TopStoriesApi.getInstance()
                        .topStories().get(NewsTypes.valueOf(categoryName))
                        .map(NewsItemHelper::parseToDaoArray)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                newsDao::insertAll,
                                this::logItemError
                        ));
        Log.i(LTAG, "Writing items to Database");
    }

    private Observable<NewsItem> makeNewsItem(@NonNull ResponseDTO responseDTO) {
        List<NewsItem> newsItems = new ArrayList<>();
        for (ResultsDTO result : responseDTO.getResults()) {
            NewsItem newsItem = new NewsItem(result);
            newsItems.add(newsItem);
        }
        this.progressStep = 100 / newsItems.size();
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
                toolbar.setVisibility(View.VISIBLE);
                break;

            case HasNoData:
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                errorNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                break;

            case NetworkError:
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                break;

            case ServerError:
                recyclerView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                break;

            case Loading:
                recyclerView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                errorNoData.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);
                break;

            default:
                throw new IllegalArgumentException("Unknown state: " + state);

        }
        Log.i(LTAG, "Showing state: " + state.name());
    }


    private void cacheAndComplete() {
        if (this.newsItems.isEmpty()) {
            showState(State.HasNoData);
        } else {
            for (NewsItem newsItem : this.newsItems) {
                DataCache.addToNewsCache(newsItem);
            }
            showState(State.HasData);
        }
    }

    private void showItems(@NonNull List<NewsItem> newsItems) {
        //TODO при первом запуске showNoData
        adapter.setDataset(newsItems);
        showState(State.HasData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category_selector:
                alertBuilder.show();
                return true;
            case R.id.about:
                AboutActivity.start(NewsFeedActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
