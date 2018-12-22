package com.example.fshmo.businesscard.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.data.repository.NewsRepository;
import com.example.fshmo.businesscard.utils.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.reactivex.disposables.Disposable;

public class NewsRequestService extends Service {

    private static final String TAG = NewsRequestService.class.getName();

    private NotificationManager notificationManager;

    private Disposable downloadDisposable;
    private NotificationCompat.Builder notification;

    private static final String errorMessage = "Cant download news";
    private static final String happyMessage = "Downloaded news update successfully;";
    private static final int notificationId = 123;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: service starting");
        downloadDisposable = NetworkUtils.getInstance().getOnlineNetwork()
                .timeout(1, TimeUnit.MINUTES)
                .flatMap(aLong -> NewsRepository.downloadUpdates())
                .subscribe(
                        newsEntities -> {
                            NewsRepository.saveToDb(newsEntities);
                            makeNotification(happyMessage, true);
                        },
                        this::logError
                );
        stopSelf();
        Log.i(TAG, "onStartCommand: service stopped");
        return START_NOT_STICKY;
    }

    private void logError(Throwable throwable) {
        if (throwable instanceof IOException) {
            Log.e(TAG, "logError: " + throwable.getMessage());
        } else
            Log.e(TAG, "logError: stopped unexpectedly : \n" + throwable.getMessage());
        makeNotification(errorMessage, false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void makeNotification(String message, boolean happy) {
        Notification notification;
        if (happy)
            notification = new NotificationCompat.Builder(this, "abcde")
                    .setSmallIcon(R.drawable.ic_stat_archive)
                    .setContentTitle("News app")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();
        else
            notification = new NotificationCompat.Builder(this, "abcde")
                    .setSmallIcon(R.drawable.ic_stat_archive)
                    .setContentTitle("Download failed")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onDestroy() {
        if (downloadDisposable != null && !downloadDisposable.isDisposed())
            downloadDisposable.dispose();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
