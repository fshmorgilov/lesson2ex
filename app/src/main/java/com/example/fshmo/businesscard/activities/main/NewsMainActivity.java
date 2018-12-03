package com.example.fshmo.businesscard.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.fshmo.businesscard.data.NewsItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fshmo.businesscard.R;

public class NewsMainActivity extends AppCompatActivity implements MainFragmentListener {

    private static final String FEED_TAG = "News Feed Main Fragment";

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NewsMainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news_main);
//        if (savedInstanceState != null)
            initializeFeedFragment();
    }

    private void initializeFeedFragment() {
        NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.news_main_frame, newsFeedFragment)
                .commit();
    }

    @Override
    public void onClicked(@NonNull NewsItem newsItem) {
        NewsDetailsFragment detailsFragment = NewsDetailsFragment.newInstance(newsItem.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.news_main_frame, detailsFragment)
                .commit();
    }

    @Override
    public void goToFeed() {
        if(getSupportFragmentManager().getBackStackEntryCount() >1)
            getSupportFragmentManager().popBackStackImmediate();
    }
}
