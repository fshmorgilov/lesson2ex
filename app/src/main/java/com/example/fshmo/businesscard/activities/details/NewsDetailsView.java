package com.example.fshmo.businesscard.activities.details;

import com.arellomobile.mvp.MvpView;
import com.example.fshmo.businesscard.activities.common.State;
import com.example.fshmo.businesscard.data.NewsItem;

interface NewsDetailsView extends MvpView {
    void displayNewsItem(NewsItem newsItem);

    void showState(State state);

    void goBack();
}
