package com.example.fshmo.businesscard.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.about.AboutActivity;
import com.example.fshmo.businesscard.activities.main.exceptions.DetailsFragmentIsEmptyException;
import com.example.fshmo.businesscard.data.NewsItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;

public class NewsMainActivity extends AppCompatActivity implements MainFragmentListener {

    private static final String FEED_TAG = "feedFragment";
    private static final String DETAILS_TAG = "detailsFragment";

    private TextView textView;
    private Unbinder unbinder;
    Toolbar toolbar;


    public static void start(Activity activity) {
        Intent intent = new Intent(activity, NewsMainActivity.class);
        activity.startActivity(intent);
    }

    public static boolean isTablet(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        double wInches = displayMetrics.widthPixels / (double) displayMetrics.densityDpi;
        double hInches = displayMetrics.heightPixels / (double) displayMetrics.densityDpi;

        double screenDiagonal = Math.sqrt(Math.pow(wInches, 2) + Math.pow(hInches, 2));
        return (screenDiagonal >= 7.0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);
        if (isTablet(this)) {
            toolbar = findViewById(R.id.main_feed_toolbar);
            setSupportActionBar(toolbar);
            manageToolbar(isTablet(this));
        }
        initializeFragments(isTablet(getApplicationContext()));
    }

    private void manageToolbar(boolean isTablet) {
        if (isTablet) {
            toolbar.setVisibility(View.VISIBLE);
        } else toolbar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClicked(@NonNull NewsItem newsItem) {
        NewsDetailsFragment detailsFragment = NewsDetailsFragment.newInstance(newsItem.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.news_additional_frame, detailsFragment, DETAILS_TAG)
                .commit();
        if (textView != null)
            textView.setVisibility(View.GONE);
    }

    @Override
    public void goToFeed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();
    }

    private void initializeFragments(boolean isTablet) {
        initializeFeedFragment();
        if (isTablet) {
            textView = findViewById(R.id.additional_frame_empty_text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void initializeFeedFragment() {
        NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.news_main_frame, newsFeedFragment, FEED_TAG)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NewsDetailsFragment detailsFragment = (NewsDetailsFragment) getSupportFragmentManager().findFragmentByTag(DETAILS_TAG);
        NewsFeedFragment feedFragment = (NewsFeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_TAG);
        switch (item.getItemId()) {
            case R.id.tablet_about:
                AboutActivity.start(this);
                break;
            case R.id.tablet_category_selector:
                feedFragment.selectCategory();
                break;
            case R.id.tablet_delete_news_item:
                try {
                    int id = detailsFragment.delete();
                    getSupportFragmentManager().beginTransaction()
                            .remove(detailsFragment)
                            .commit();
                    feedFragment.reload(id);
                    this.textView.setVisibility(View.VISIBLE);
                } catch (DetailsFragmentIsEmptyException e) {

                    //TODO show state
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tablet_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showAdditionalFrameState(State state) {
        switch (state) {
            case HasData:
                break;
            case HasNoData:
                break;
        }
    }
}
