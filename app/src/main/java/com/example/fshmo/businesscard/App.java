package com.example.fshmo.businesscard;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.fshmo.businesscard.utils.NetworkUtils;
import com.facebook.stetho.Stetho;

public class App extends Application {

    public static App INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Stetho.initializeWithDefaults(this);

        registerReceiver(NetworkUtils.getInstance().getReceiver(),
                new IntentFilter((ConnectivityManager.CONNECTIVITY_ACTION)));
    }

    public synchronized boolean isNetworkAvaliable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
