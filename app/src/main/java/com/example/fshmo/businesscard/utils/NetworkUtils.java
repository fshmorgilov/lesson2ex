package com.example.fshmo.businesscard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.fshmo.businesscard.App;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkUtils {

    private static NetworkUtils networkUtils;
    private NetworkReceiver receiver = new NetworkReceiver();
    private Subject<Boolean> networkState = BehaviorSubject.createDefault(isNetworkAvailable());


    public static NetworkUtils getInstance() {
        synchronized (App.class) {
            if (networkUtils == null)
                synchronized (App.class) {
                    networkUtils = new NetworkUtils();
                }
            return networkUtils;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.INSTANCE
                .getApplicationContext()
                .getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
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
            networkState.onNext(isNetworkAvailable());
        }
    }
}
