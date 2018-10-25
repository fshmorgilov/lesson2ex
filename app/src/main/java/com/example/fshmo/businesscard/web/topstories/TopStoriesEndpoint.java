package com.example.fshmo.businesscard.web.topstories;

import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface TopStoriesEndpoint {

    @GET
    Single<ResponseDTO> get();
}
