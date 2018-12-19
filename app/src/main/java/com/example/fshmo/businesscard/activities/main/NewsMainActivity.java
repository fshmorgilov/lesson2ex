package com.example.fshmo.businesscard.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.about.AboutActivity;
import com.example.fshmo.businesscard.activities.main.exceptions.DetailsFragmentIsEmptyException;
import com.example.fshmo.businesscard.data.NewsItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class NewsMainActivity extends AppCompatActivity implements MainFragmentListener {

    private static final String TAG = NewsMainActivity.class.getName();

    private static final String FEED_TAG = "feedFragment";
    private static final String DETAILS_TAG = "detailsFragment";

    private TextView instructionTextView;
    Toolbar toolbar;


    public static void start(@NonNull Activity activity) {
        Intent intent = new Intent(activity, NewsMainActivity.class);
        activity.startActivity(intent);
    }

    public static boolean isTablet(@NonNull Context context) {
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
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_main_frame, NewsFeedFragment.newInstance())
                    .addToBackStack(FEED_TAG)
                    .commit();
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
        if (isTablet(this)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_additional_frame, detailsFragment, DETAILS_TAG)
                    .addToBackStack(DETAILS_TAG)
                    .commit();
            if (instructionTextView != null)
                showAdditionalFrameState(State.HasData);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.news_main_frame, detailsFragment, DETAILS_TAG)
                    .addToBackStack(DETAILS_TAG)
                    .commit();
        }
    }

    @Override
    public void goToFeed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();
    }

    private void initializeFragments(boolean isTablet) {
        initializeFeedFragment();
        if (isTablet) {
            instructionTextView = findViewById(R.id.additional_frame_empty_text);
            instructionTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initializeFeedFragment() {
        NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.news_main_frame, newsFeedFragment, FEED_TAG)
                .commit();
        Log.i(TAG, "FM backstack count: " + getSupportFragmentManager().getBackStackEntryCount());
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
                    showAdditionalFrameState(State.HasNoData);
                } catch (DetailsFragmentIsEmptyException e) {
                    showAdditionalFrameState(State.HasNoData);
                }
                break;
        }
        Log.i(TAG, "FM backstack count: " + getSupportFragmentManager().getBackStackEntryCount());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tablet_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: back pressed");
        Log.i(TAG, "FM backstack count: " + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof NewsDetailsFragment)
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                getSupportFragmentManager().popBackStack();
                Log.i(TAG, "onBackPressed: details fragment removed");
            }
        } else finish();
    }

    private void showAdditionalFrameState(State state) {
        switch (state) {
            case HasData:
                this.instructionTextView.setVisibility(View.GONE);
                break;
            case HasNoData:
                this.instructionTextView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
