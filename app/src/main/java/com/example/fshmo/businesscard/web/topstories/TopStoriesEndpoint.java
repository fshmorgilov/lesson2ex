package com.example.fshmo.businesscard.web.topstories;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TopStoriesEndpoint {

    @GET()
    Single<ResponseDTO> search(@Query("api_key") String apiKey,
                               @Query("q") String search);
}
