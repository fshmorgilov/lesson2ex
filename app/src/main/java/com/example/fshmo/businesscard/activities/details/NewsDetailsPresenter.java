package com.example.fshmo.businesscard.activities.details;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.example.fshmo.businesscard.activities.common.State;
import com.example.fshmo.businesscard.base.BasePresenter;
import com.example.fshmo.businesscard.data.repository.NewsRepository;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@InjectViewState
public class NewsDetailsPresenter extends BasePresenter<NewsDetailsView> {

    private static final String TAG = NewsDetailsPresenter.class.getName();

    void findNewsItem(@NonNull Integer newsItemId) {
        Log.i(TAG, "findNewsItem: newsItem id:" + String.valueOf(newsItemId));
        Disposable disposable = NewsRepository.getNewsItemById(newsItemId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        entity -> {
                            getViewState().showState(State.HasData);
                            getViewState().displayNewsItem(entity);
                            Log.i(TAG, "findNewsItem: displaying news item: " + entity.getTitle());
                        },
                        e -> {
                            getViewState().showState(State.HasNoData);
                            Log.e(TAG, "Error in finding the item: " + e.getMessage());
                        });
    }

    void deleteNewsItem(@NonNull Integer newsItemId){
        NewsRepository.delete(newsItemId);
        getViewState().goBack();
    }
}
