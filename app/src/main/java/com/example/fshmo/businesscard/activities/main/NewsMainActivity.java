package com.example.fshmo.businesscard.activities.main;

import android.os.Bundle;

import com.example.fshmo.businesscard.data.NewsItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.fshmo.businesscard.R;

public class NewsMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news_main);

        initializeFragments();
    }

    private void initializeFragments() {

        NewsItem newsItem; //fixme подставить актуальные данные
        NewsDetailsFragment detailsFragment = NewsDetailsFragment.newInstance(newsItem);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.news_main_frame, detailsFragment)
                .commit();
    }

}
