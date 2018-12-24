package com.example.fshmo.businesscard.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fshmo.businesscard.R;
import com.example.fshmo.businesscard.data.repository.NewsRepository;
import com.example.fshmo.businesscard.utils.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.disposables.Disposable;

import static com.example.fshmo.businesscard.utils.NetworkUtils.CancelReceiver.ACTION_CANCEL;

public class NewsRequestService extends Worker {

    public static final String WORK_TAG = "News download";
    private static final String TAG = NewsRequestService.class.getName();

    private NotificationManager notificationManager;

    private Disposable downloadDisposable;

    private static final String errorMessage = "Cant download news"; // TODO: 12/24/2018 Export to string both
    private static final String happyMessage = "Downloaded news update successfully;";
    private static final int notificationId = 123;

    public NewsRequestService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    private void logError(Throwable throwable) {
        if (throwable instanceof IOException) {
            Log.e(TAG, "logError: " + throwable.getMessage());
        } else
            Log.e(TAG, "logError: stopped unexpectedly : \n" + throwable.getMessage());
        makeNotification(errorMessage, false);
    }

    private void makeNotification(String message, boolean happy) {
        Intent cancelIntent = new Intent(getApplicationContext(), NetworkUtils.CancelReceiver.class);
        cancelIntent.setAction(ACTION_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, 0);
        Notification notification;
        if (happy)
            notification = new NotificationCompat.Builder(getApplicationContext(), "abcde")
                    .setSmallIcon(R.drawable.ic_stat_archive)
                    .setContentTitle("News app")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_stat_cancel, getApplicationContext().getString(R.string.cancel_work), pendingIntent)
                    .build();
        else
            notification = new NotificationCompat.Builder(getApplicationContext(), "abcde")
                    .setSmallIcon(R.drawable.ic_stat_archive)
                    .setContentTitle("Download failed")
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();
        if (notificationManager == null)
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onStopped() {
        if (downloadDisposable != null && !downloadDisposable.isDisposed())
            downloadDisposable.dispose();
        super.onStopped();
    }

    @NonNull
    @Override
    public Result doWork() {
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
        Log.i(TAG, "onStartCommand: service stopped");
        return Result.success();
    }

}
