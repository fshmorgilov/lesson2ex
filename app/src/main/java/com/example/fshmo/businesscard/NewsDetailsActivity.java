package com.example.fshmo.businesscard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class NewsDetailsActivity extends AppCompatActivity {

    private static final String KEY_TEXT = "KEY_TEXT";
    private ImageView imageView;
    private TextView titleView;
    private TextView publishDateView;
    private TextView fullTextView;

    public static void start(@NonNull Activity activity, NewsItem newsItem) {
        Intent intent = new Intent(activity, NewsDetailsActivity.class);
        intent.putExtra(KEY_TEXT, newsItem);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        NewsItem newsItems  = (NewsItem) getIntent().getSerializableExtra(KEY_TEXT);
        this.imageView = findViewById(R.id.image_nd);
        this.fullTextView = findViewById(R.id.full_text_nd);
        this.publishDateView = findViewById(R.id.publish_date_nd);
        this.titleView = findViewById(R.id.title_nd);

        Glide.with(this)
                .load(newsItems.getImageUrl())
                .into(imageView);
        titleView.append(newsItems.getTitle());
        publishDateView.append(newsItems.getPublishDate().toString());
        fullTextView.append(newsItems.getFullText());
    }

}
