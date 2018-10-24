package com.example.fshmo.businesscard.web.topstories;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ResponseDTO {

    @SerializedName("last_updated")
    private Date lastUpdated;
    @SerializedName("num_results")
    private int resultCnt;
    @SerializedName("results")
    private List<ResultsDTO> results;
}
