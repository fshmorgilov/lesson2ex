package com.example.fshmo.businesscard.web.topstories;

import android.support.annotation.NonNull;

import com.example.fshmo.businesscard.web.NewsTypes;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryInterceptor implements Interceptor {

    private NewsTypes newsType;
    private final String RESPONSE_FORMAT_JSON = ".json";

    public CategoryInterceptor(@NonNull NewsTypes newsType) {
        this.newsType = newsType;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final HttpUrl url = chain.request().url().newBuilder()
                .addPathSegment(this.newsType.toString() + RESPONSE_FORMAT_JSON)
                .build();

        final Request requestWithCategory = chain.request().newBuilder()
                .url(url)
                .build();

        return chain.proceed(requestWithCategory);
    }
}
