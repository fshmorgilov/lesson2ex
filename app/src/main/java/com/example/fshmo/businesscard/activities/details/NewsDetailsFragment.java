package com.example.fshmo.businesscard.activities.details;

import android.content.Context;
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

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.activities.common.State;
import com.example.fshmo.businesscard.activities.feed.MainFragmentListener;
import com.example.fshmo.businesscard.activities.feed.NewsMainActivity;
import com.example.fshmo.businesscard.data.NewsItem;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class NewsDetailsFragment extends Fragment implements NewsDetailsView {

    private static final String TAG = NewsDetailsFragment.class.getName();
    private static final String KEY_ID = "KEY_News_item_id";

    private final String DATE_FORMAT = "HH:MM, EEEE, dd MMMM, yyyy";

    @InjectPresenter
    NewsDetailsPresenter presenter;

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
    private MainFragmentListener mainFragmentListener;
    private TextView selectItem;

    public NewsDetailsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static NewsDetailsFragment newInstance(@NonNull int newsItemId) {
        NewsDetailsFragment fragment = new NewsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, newsItemId);
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
                delete();
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
        presenter.findNewsItem(savedInstanceState.getInt(KEY_ID));
        return fragmentMainView;
    }

    public int delete() {
        presenter.deleteNewsItem(newsItem.getId());
        Log.i(TAG, "onOptionsItemSelected: item deleted: " + newsItem.getTitle());
        return newsItem.getId();
    }

    public void goBack() {
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

    public void showState(State state) {
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

    public void displayNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
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
