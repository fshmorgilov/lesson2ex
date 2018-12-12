package com.example.fshmo.businesscard.activities.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fshmo.businesscard.activities.about.AboutActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.decorators.GridSpaceItemDecoration;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.AppDatabase;
import com.example.fshmo.businesscard.data.model.NewsDao;
import com.example.fshmo.businesscard.utils.NewsItemHelper;
import com.example.fshmo.businesscard.web.NewsTypes;
import com.example.fshmo.businesscard.web.topstories.TopStoriesApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsFeedFragment extends Fragment {

    private static final String TAG = NewsFeedFragment.class.getCanonicalName();

    @BindView(R.id.recycler_frame)
    FrameLayout recyclerFrame;
    @BindView(R.id.view_error)
    View errorView;
    @BindView(R.id.view_no_data)
    View errorNoData;
    @BindView(R.id.btn_retry_error)
    Button retryBtn;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.feed_toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View fragmentMainView;
    private NewsFeedAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.ItemDecoration decoration;
    private AlertDialog.Builder alertBuilder;

    private List<NewsItem> newsItems = new ArrayList<>();

    private String categoryName = String.valueOf(NewsTypes.home);
    private int progressBarProgress = 0;
    private int orientation;
    private int progressStep;
    private Disposable disposable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NewsDao newsDao;
    private MainFragmentListener listener;
    private Unbinder unbinder;

    public static Fragment newInstance() {
        return new NewsFeedFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof MainFragmentListener) {
            listener = (MainFragmentListener) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void selectCategory() {
        alertBuilder.show();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onAttach: reattached...");
//        observeDb();
        super.onResume();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMainView = inflater.inflate(R.layout.activity_news_feed, container, false);
        newsDao = AppDatabase.getInstance(getContext()).newsDao();
        unbinder = ButterKnife.bind(this, fragmentMainView);
        initialize();
        configuresViews();
        showState(State.HasData);
        observeDb();
        return fragmentMainView;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        compositeDisposable.dispose();
        super.onDestroyView();
    }

    private void observeDb() {
        compositeDisposable.add(
                Observable.just(true) //FIXME observable.fromCallable()
                        .map(aBoolean -> newsDao.getAll())
                        .map(NewsItemHelper::convertDaoListoToDomain)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::resetDataset)
        );
        Log.i(TAG, "observeDb: Displaying news");
    }

    private void initialize() {
        Log.i(TAG, "Initializing...");
        alertBuilder = new AlertDialog.Builder(getContext());
        progressBar.setProgress(0);

        orientation = this.getResources().getConfiguration().orientation;
        RequestManager glide = Glide.with(getContext());
        adapter = new NewsFeedAdapter(
                newsItems,
                glide,
                item -> {
                    Log.e(TAG, "News item selected: " + item.getTitle());
                    listener.onClicked(item);
                });
        decoration = new GridSpaceItemDecoration(4, 4);
        Log.i(TAG, "Initializing done");
    }

    private void configuresViews() {
        Log.i(TAG, "Configuring...");

        recyclerView.setHasFixedSize(true);
        if (orientation == Configuration.ORIENTATION_PORTRAIT || NewsMainActivity.isTablet(getContext())) {
            layoutManager = new LinearLayoutManager(getActivity());
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String[] categoryNames = NewsTypes.getNames(NewsTypes.class);
        alertBuilder.setTitle("Choose category")
                .setItems(
                        R.array.categories_array,
                        (dialog, which) -> {
                            this.categoryName = categoryNames[which];
                            toolbar.setTitle(this.categoryName.toUpperCase());
                            displayNews();
                        }
                );

        retryBtn.setOnClickListener(v -> displayNews());
        fab.setOnClickListener(v -> displayNews());

        toolbar.setTitle(this.categoryName.toUpperCase());
//        setSupportActionBar(toolbar); fixme тулбар

        Log.i(TAG, "Configuring done");
    }

    private void loadToDb() {
        progressBarProgress = 0;
        progressBar.setProgress(progressBarProgress);
        compositeDisposable.add(
                TopStoriesApi.getInstance()
                        .topStories().get(NewsTypes.valueOf(categoryName))
                        .map(NewsItemHelper::parseToDaoArray)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                newsEntities -> {
                                    newsDao.deleteAll();
                                    progressStep = 100 / newsEntities.length;
                                    newsDao.insertAll(newsEntities);
                                },
                                this::logItemError
                        ));
        Log.i(TAG, "Writing items to Database");
    }

    private void logItemError(@Nullable Throwable err) {
        Log.e(TAG, err.getMessage());
        if (err instanceof IOException) {
            showState(State.HasNoData);
        }
    }

    private void showState(@NonNull State state) {
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
        manageFabState(state);
        manageToolbarState();
        Log.i(TAG, "Showing state: " + state.name());
    }

    private void manageFabState(State state) {
        if (NewsMainActivity.isTablet(getContext())) {
            fab.setEnabled(false);
            fab.setClickable(false);
            fab.setAlpha(0.0f);
        } else {
            if (state == State.HasData
                    || state == State.NetworkError) {
                fab.setEnabled(true);
                fab.setClickable(true);
                fab.setAlpha(1.0f);
            } else {
                fab.setEnabled(false);
                fab.setClickable(false);
                fab.setAlpha(0.3f);
            }
        }
    }

    private void manageToolbarState() {
        if (NewsMainActivity.isTablet(getContext()))
            toolbar.setVisibility(View.GONE);
    }

    private void resetDataset(@NonNull List<NewsItem> newsItems) {
        if (!this.newsItems.isEmpty()) {
            clearNewsItems();
        }
        adapter.setDataset(newsItems);
        adapter.notifyItemRangeChanged(0, newsItems.size());
    }

    private void displayNews() {
        showState(State.Loading);
        loadToDb();
        observeDb();
        Toast.makeText(getContext(), "Displaying news", Toast.LENGTH_LONG).show();
        showState(State.HasData);
    }

    public void reload(int id) {
        showState(State.Loading);
        adapter.notifyItemRemoved(id);
        Log.i(TAG, "reload: removed item" + id);
        observeDb();
        Toast.makeText(getContext(), "Renewing...", Toast.LENGTH_LONG).show();
        showState(State.HasData);
    }


    private void clearNewsItems() {
        if (newsItems != null) {
            int size = newsItems.size();
            newsItems.clear();
            adapter.notifyItemRangeRemoved(0, size);
            Log.i(TAG, "clearNewsItems: cleared " + String.valueOf(size) + " items");
        }
    }

    //TODO MEnu inflater

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category_selector:
                alertBuilder.show();
                return true;
            case R.id.about:
                AboutActivity.start(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
