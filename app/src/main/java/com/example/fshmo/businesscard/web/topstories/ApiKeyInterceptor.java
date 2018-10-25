package com.example.fshmo.businesscard.web.topstories;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyInterceptor implements Interceptor {
    private static final String PARAM_API_KEY = "api_key";
    private final String apiKey;

    public ApiKeyInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request requestWithoutApiKey = chain.request();

        final HttpUrl url = requestWithoutApiKey.url()
                .newBuilder()
                .addQueryParameter(PARAM_API_KEY, apiKey)
                .build();

        final Request requestWithApiKey = requestWithoutApiKey.newBuilder()
                .url(url)
                .build();

        return chain.proceed(requestWithApiKey);
    }
}
