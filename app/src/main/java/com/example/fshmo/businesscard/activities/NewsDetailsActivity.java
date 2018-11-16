package com.example.fshmo.businesscard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsDetailsActivity extends AppCompatActivity {

    private static final String KEY_TEXT = "KEY_TEXT";
    private ImageView imageView;
    private TextView titleView;
    private TextView publishDateView;
    private TextView fullTextView;
    private Toolbar toolbar;
    private WebView webView ;
    private final String DATE_FORMAT = "HH:MM, EEEE, dd MMMM, yyyy";

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


        NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra(KEY_TEXT);
        this.webView = findViewById(R.id.web_view_details);
        this.imageView = findViewById(R.id.image_nd);
        this.fullTextView = findViewById(R.id.full_text_nd);
        this.publishDateView = findViewById(R.id.publish_date_nd);
        this.titleView = findViewById(R.id.title_nd);
        this.toolbar = findViewById(R.id.my_toolbar);
        this.toolbar.setTitle(newsItem.getCategory().getName());
        setSupportActionBar(this.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        webView.loadUrl(newsItem.getNewsItemUrl());

        String url;
        if (newsItem.getImageUrlLarge() != null)
            url = newsItem.getImageUrlLarge();
        else
            url = newsItem.getImageUrl();
        Glide.with(this)
                .load(url)
                .into(imageView);

        titleView.append(newsItem.getTitle());
        publishDateView.append(new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
                .format(newsItem.getPublishDate()));
        fullTextView.append(newsItem.getFullText());
    }
}
