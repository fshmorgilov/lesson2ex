package com.example.fshmo.businesscard.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.main.exceptions.DetailsFragmentIsEmptyException;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.AppDatabase;
import com.example.fshmo.businesscard.services.NewsRequestService;
import com.example.fshmo.businesscard.utils.NewsItemHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsFragment extends Fragment {

    private static final String TAG = NewsDetailsFragment.class.getName();
    private static final String KEY_TEXT = "KEY_TEXT";

    private final String DATE_FORMAT = "HH:MM, EEEE, dd MMMM, yyyy";

    private View fragmentMainView;
    private ImageView imageView;
    private ImageView errorImageView;
    private TextView titleView;
    private TextView publishDateView;
    private TextView fullTextView;
    private Toolbar toolbar;
    private WebView webView;
    private NewsItem newsItem;
    private ScrollView newsItemDetailsScroll;
    private Disposable findNewsItemDisposable;
    private Disposable deleteNewsItemDisposable;
    private MainFragmentListener mainFragmentListener;
    private TextView selectItem;
    private MainFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(context);
        if (getActivity() instanceof MainFragmentListener) {
            listener = (MainFragmentListener) getActivity();
        }
    }

    static NewsDetailsFragment newInstance(@NonNull int newsItemId) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TEXT, newsItemId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.delete_news_item):
                try {
                    delete();
                } catch (DetailsFragmentIsEmptyException e) {
                    Log.e(TAG, "onOptionsItemSelected: newsItemIsnull");
                }
                goBack();
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMainView = inflater.inflate(R.layout.activity_news_details, container, false);
        mainFragmentListener = (MainFragmentListener) getActivity();
        initializeViews();
        findNewsItem();
        getActivity().startService(new Intent(getActivity(), NewsRequestService.class));
        return fragmentMainView;
    }

    public int delete() throws DetailsFragmentIsEmptyException {
        if (newsItem == null) {
            Log.e(TAG, "delete: no news item to delete");
            throw new DetailsFragmentIsEmptyException();
        } else {
            int id = newsItem.getId();
            deleteNewsItemDisposable = Maybe.just(true)
                    .doOnSuccess(boo -> AppDatabase.getInstance(getContext()).newsDao().deleteById(id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            aBoolean -> Log.i(TAG, "onOptionsItemSelected: deleted item: " + id),
                            (e) -> Log.e(TAG, "onOptionsItemSelected: error deleting item" + e.getMessage())
                    );
            Log.i(TAG, "onOptionsItemSelected: item deleted: " + newsItem.getTitle());
            return id;
        }
    }

    private void goBack() {
        Log.i(TAG, "goBack: backstack triggered");
        mainFragmentListener.goToFeed();
    }

    private void initializeViews() {
        this.webView = fragmentMainView.findViewById(R.id.web_view_details);
        this.imageView = fragmentMainView.findViewById(R.id.image_nd);
        this.fullTextView = fragmentMainView.findViewById(R.id.full_text_nd);
        this.publishDateView = fragmentMainView.findViewById(R.id.publish_date_nd);
        this.titleView = fragmentMainView.findViewById(R.id.title_nd);
        this.errorImageView = fragmentMainView.findViewById(R.id.error_image_view);
        this.newsItemDetailsScroll = fragmentMainView.findViewById(R.id.news_item_details);
        this.selectItem = fragmentMainView.findViewById(R.id.select_item);
        this.toolbar = fragmentMainView.findViewById(R.id.details_toolbar);
        if (!NewsMainActivity.isTablet(getContext())) {
            setHasOptionsMenu(true);
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
            toolbar.setNavigationOnClickListener(v -> goBack());
        } else
            toolbar.setVisibility(View.GONE);
    }

    private void findNewsItem() {
        int newsItemId;
        newsItemId = getArguments().getInt(KEY_TEXT);
        Log.i(TAG, "findNewsItem: newsItem id:" + String.valueOf(newsItemId));
        findNewsItemDisposable = AppDatabase.getInstance(getActivity()).newsDao().findById(newsItemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entity -> {
                            newsItem = NewsItemHelper.convertDaoToDomain(entity);
                            showState(State.HasData);
                            displayNewsItem();
                            Log.i(TAG, "findNewsItem: displaying news item: " + newsItem.getTitle());
                        },
                        e -> {
                            showState(State.HasNoData);
                            Log.e(TAG, "Error in finding the item: " + e.getMessage());
                        });
    }

    @Override
    public void onDestroy() {
        if (findNewsItemDisposable != null) findNewsItemDisposable.dispose();
        if (deleteNewsItemDisposable != null) deleteNewsItemDisposable.dispose();
        super.onDestroy();
    }

    private void showState(State state) {
        switch (state) {
            case HasData:
                webView.setVisibility(View.GONE);
                newsItemDetailsScroll.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);
                selectItem.setVisibility(View.GONE);
                break;

            case HasNoData:
                webView.setVisibility(View.GONE);
                newsItemDetailsScroll.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                selectItem.setVisibility(View.GONE);

                Glide.with(getActivity())
                        .load("https://www.oddee.com/wp-content/uploads/_media/imgs/articles2/a96984_e1.jpg")
                        .into(errorImageView);
                break;

            case PartiallyMissingData:
                webView.setVisibility(View.VISIBLE);
                newsItemDetailsScroll.setVisibility(View.GONE);
                errorImageView.setVisibility(View.GONE);
                selectItem.setVisibility(View.GONE);
                break;

            case Loading:
                webView.setVisibility(View.GONE);
                newsItemDetailsScroll.setVisibility(View.GONE);
                errorImageView.setVisibility(View.GONE);
                selectItem.setVisibility(View.VISIBLE);

        }
    }

    private void displayNewsItem() {
        webView.loadUrl(newsItem.getNewsItemUrl());

        toolbar.setTitle(newsItem.getCategory().getName());
        String url;
        if (newsItem.getImageUrlLarge() != null)
            url = newsItem.getImageUrlLarge();
        else
            url = newsItem.getImageUrl();

        Glide.with(getActivity())
                .load(url)
                .into(imageView);

        titleView.append(newsItem.getTitle());
        publishDateView.append(new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
                .format(newsItem.getPublishDate()));
        fullTextView.append(newsItem.getFullText());
    }
}
