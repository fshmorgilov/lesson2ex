package com.example.fshmo.businesscard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fshmo.businesscard.App;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class NetworkUtils {

    private static NetworkUtils networkUtils;
    private NetworkReceiver receiver;
    private Subject<Boolean> networkState = BehaviorSubject.createDefault(App.INSTANCE.isNetworkAvaliable());


    public static NetworkUtils getInstance() {
        synchronized (App.class) {
            if (networkUtils == null)
                synchronized (App.class) {
                    networkUtils = new NetworkUtils();
                }
            return networkUtils;
        }
    }

    public NetworkReceiver getReceiver() {
        return receiver;
    }

    public Single<Boolean> getOnlineNetwork() {
        return networkState.subscribeOn(Schedulers.io())
                .filter(online -> online)
                .firstOrError();
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            networkState.onNext(App.INSTANCE.isNetworkAvaliable());
        }
    }
}
