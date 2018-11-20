package com.example.fshmo.businesscard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.AppDatabase;
import com.example.fshmo.businesscard.utils.NewsItemHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsActivity extends AppCompatActivity {

    private static final String TAG = NewsDetailsActivity.class.getName();
    private static final String KEY_TEXT = "KEY_TEXT";
    private final String DATE_FORMAT = "HH:MM, EEEE, dd MMMM, yyyy";
    private ImageView imageView;
    private ImageView errorImageView;
    private TextView titleView;
    private TextView publishDateView;
    private TextView fullTextView;
    private Toolbar toolbar;
    private WebView webView;
    private NewsItem newsItem;

    public static void start(@NonNull Activity activity,
                             @NonNull NewsItem newsItem) {
        Intent intent = new Intent(activity, NewsDetailsActivity.class);
        intent.putExtra(KEY_TEXT, newsItem);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        initializeViews();
        findNewsItem();
    }

    private void initializeViews() {
        this.webView = findViewById(R.id.web_view_details);
        this.imageView = findViewById(R.id.image_nd);
        this.fullTextView = findViewById(R.id.full_text_nd);
        this.publishDateView = findViewById(R.id.publish_date_nd);
        this.titleView = findViewById(R.id.title_nd);
        this.toolbar = findViewById(R.id.my_toolbar);
        this.errorImageView = findViewById(R.id.error_image_view);
        setSupportActionBar(this.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void findNewsItem() {
        int newsItemId;
        newsItemId = ((NewsItem) getIntent().getSerializableExtra(KEY_TEXT)).getId();
        Log.i(TAG, "findNewsItem: newsItem id:" + String.valueOf(newsItemId));
        Disposable disposable = AppDatabase.getInstance(this).newsDao().findById(newsItemId)
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

    private void showState(State state) {
        switch (state) {
            case HasData:
                webView.setVisibility(View.VISIBLE);
                errorImageView.setVisibility(View.GONE);
                break;
            case HasNoData:
                webView.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load("https://www.oddee.com/wp-content/uploads/_media/imgs/articles2/a96984_e1.jpg")
                        .into(errorImageView);
                break;
        }
    }

    private void displayNewsItem() {
        toolbar.setTitle(newsItem.getCategory().getName());
        webView.loadUrl(newsItem.getNewsItemUrl());
    }
}
