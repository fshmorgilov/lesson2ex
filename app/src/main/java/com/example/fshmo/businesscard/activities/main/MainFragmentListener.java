package com.example.fshmo.businesscard.activities.main;

import com.example.fshmo.businesscard.data.NewsItem;

interface MainFragmentListener {

    void onClicked(NewsItem newsItem);

    void goToFeed();
}
