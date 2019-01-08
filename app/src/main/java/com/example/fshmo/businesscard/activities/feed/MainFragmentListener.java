package com.example.fshmo.businesscard.activities.feed;

import com.example.fshmo.businesscard.data.NewsItem;

public interface MainFragmentListener {

    void onClicked(NewsItem newsItem);

    void goToFeed();
}
