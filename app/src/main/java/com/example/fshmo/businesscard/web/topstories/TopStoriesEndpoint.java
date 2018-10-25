package com.example.fshmo.businesscard.web.topstories;

import com.example.fshmo.businesscard.web.NewsTypes;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TopStoriesEndpoint {

    @GET("{category}.json")
    Single<ResponseDTO> get(@Path("category") NewsTypes newsType);
}
