package com.example.fshmo.businesscard.web.topstories;


import android.support.annotation.NonNull;

import com.example.fshmo.businesscard.web.topstories.interceptors.ApiKeyInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class TopStoriesApi {

    private static final String url = "http://api.nytimes.com/svc/topstories/v2/";
    private static final String API_KEY = "258d5b758edc4dce91a904e111f29f41";

    private static final int TIMEOUT_SECONDS = 2;

    private final TopStoriesEndpoint endpoint;
    private final OkHttpClient client;

    private static TopStoriesApi api;

    public static synchronized TopStoriesApi getInstance() {
        if (api == null) {
            api = new TopStoriesApi();
        }
        return api;
    }

    private TopStoriesApi() {
        final Retrofit retrofit;
        client = buildOkHttpClient();
        retrofit = buildRetrofit();

        endpoint = retrofit.create(TopStoriesEndpoint.class);
    }

    @NonNull
    private Retrofit buildRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @NonNull
    private OkHttpClient buildOkHttpClient() {
        final HttpLoggingInterceptor networkLoggingInterceptor = new HttpLoggingInterceptor();
        networkLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        final ApiKeyInterceptor apiKeyInterceptor = new ApiKeyInterceptor(API_KEY);

        return new OkHttpClient.Builder()
                .addInterceptor(networkLoggingInterceptor)
                .addInterceptor(apiKeyInterceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public TopStoriesEndpoint topStories() {
        return endpoint;
    }

}
