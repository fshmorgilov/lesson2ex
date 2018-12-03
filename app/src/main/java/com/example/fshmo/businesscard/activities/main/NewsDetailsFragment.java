package com.example.fshmo.businesscard.activities.main;

import android.app.Activity;
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
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.AppDatabase;
import com.example.fshmo.businesscard.utils.NewsItemHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

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


    public static void newInstance(@NonNull int newsItemId){
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TEXT, newsItemId);
        fragment.setArguments(bundle);
    }

    public static void start(@NonNull Activity activity,
                             @NonNull NewsItem newsItem) {
        Intent intent = new Intent(activity, NewsDetailsFragment.class);
        intent.putExtra(KEY_TEXT, newsItem);
        activity.startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO переделать в кнопку
        switch (item.getItemId()) {
            case (R.id.delete_news_item):
                if (newsItem != null) {
                    deleteNewsItem(newsItem.getId());
                } else {
                    Log.i(TAG, "onOptionsItemSelected: newsItemIsNull");
                    getFragmentManager().popBackStack();
                }
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMainView = inflater.inflate(R.layout.activity_news_details, container, false);
        initializeViews();
        findNewsItem();
        return fragmentMainView;
    }

    private void deleteNewsItem(int id) {
        deleteNewsItemDisposable = Maybe.just(true)
                .doOnSuccess(boo -> AppDatabase.getInstance(getContext()).newsDao().deleteById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aBoolean -> Log.i(TAG, "onOptionsItemSelected: deleted item: " + id),
                        (e) -> Log.e(TAG, "onOptionsItemSelected: error deleting item" + e.getMessage())
                );
        Log.i(TAG, "onOptionsItemSelected: item deleted: " + newsItem.getTitle());
        getFragmentManager().popBackStack();
    }

    private void initializeViews() {
        this.webView = fragmentMainView.findViewById(R.id.web_view_details);
        this.imageView = fragmentMainView.findViewById(R.id.image_nd);
        this.fullTextView = fragmentMainView.findViewById(R.id.full_text_nd);
        this.publishDateView = fragmentMainView.findViewById(R.id.publish_date_nd);
        this.titleView = fragmentMainView.findViewById(R.id.title_nd);
        this.toolbar = fragmentMainView.findViewById(R.id.my_toolbar);
        this.errorImageView = fragmentMainView.findViewById(R.id.error_image_view);
        this.newsItemDetailsScroll = fragmentMainView.findViewById(R.id.news_item_details);
//        fragmentMainView.setSupportActionBar(this.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
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
        findNewsItemDisposable.dispose();
        deleteNewsItemDisposable.dispose();
        super.onDestroy();
    }

    private void showState(State state) {
        switch (state) {
            case HasData:
                webView.setVisibility(View.GONE);
                newsItemDetailsScroll.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);
                break;

            case HasNoData:
                webView.setVisibility(View.GONE);
                newsItemDetailsScroll.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);

                Glide.with(getActivity())
                        .load("https://www.oddee.com/wp-content/uploads/_media/imgs/articles2/a96984_e1.jpg")
                        .into(errorImageView);
                break;

            case PartiallyMissingData:
                webView.setVisibility(View.VISIBLE);
                newsItemDetailsScroll.setVisibility(View.GONE);
                errorImageView.setVisibility(View.GONE);
                break;
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
