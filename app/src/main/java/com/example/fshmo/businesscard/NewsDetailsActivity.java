package com.example.fshmo.businesscard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsDetailsActivity extends AppCompatActivity {

    private static final String KEY_TEXT = "KEY_TEXT";
    private ImageView imageView;
    private TextView titleView;
    private TextView publishDateView;
    private TextView fullTextView;
    private Toolbar toolbar;


    public static void start(@NonNull Activity activity, NewsItem newsItem) {
        Intent intent = new Intent(activity, NewsDetailsActivity.class);
        intent.putExtra(KEY_TEXT, newsItem);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);


        NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra(KEY_TEXT);
        this.imageView = findViewById(R.id.image_nd);
        this.fullTextView = findViewById(R.id.full_text_nd);
        this.publishDateView = findViewById(R.id.publish_date_nd);
        this.titleView = findViewById(R.id.title_nd);
        this.toolbar = findViewById(R.id.my_toolbar);
        this.toolbar.setTitle(newsItem.getCategory().getName());
        setSupportActionBar(this.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        Glide.with(this)
                .load(newsItem.getImageUrl())
                .into(imageView);
        titleView.append(newsItem.getTitle());

        publishDateView.append(new SimpleDateFormat("HH:MM, EEEE, dd MMMM, yyyy", Locale.ENGLISH)
                .format(newsItem.getPublishDate()));
        fullTextView.append(newsItem.getFullText());
    }

}
