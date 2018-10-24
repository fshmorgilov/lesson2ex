package com.example.fshmo.businesscard.web.topstories;


import android.support.annotation.NonNull;

import com.example.fshmo.businesscard.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public final class TopStoriesApi {

    private static final String url = ""; //fixme
    private static final String API_KEY = "258d5b758edc4dce91a904e111f29f41";
    private static final int TIMEOUT_SECONDS = 2;
    private static OkHttpClient client;

    private static TopStoriesApi api;

    private static synchronized TopStoriesApi getInstance() {
        if (api == null) {
            api = new TopStoriesApi();
        }
        return api;
    }

    public TopStoriesApi() {
    }

    @NonNull
    private Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        //TODO
    }

    @NonNull
    private OkHttpClient buildOkHttpClient() {
        final HttpLoggingInterceptor networkLoggingInterceptor = new HttpLoggingInterceptor();
        networkLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
                .addInterceptor(networkLoggingInterceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }
}
