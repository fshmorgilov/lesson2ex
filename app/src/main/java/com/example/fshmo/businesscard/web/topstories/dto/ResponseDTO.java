package com.example.fshmo.businesscard.web.topstories.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ResponseDTO {

    @SerializedName("last_updated")
    private Date lastUpdated;
    @SerializedName("num_results")
    private int resultCnt;
    @SerializedName("results")
    private List<ResultsDTO> results;

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public int getResultCnt() {
        return resultCnt;
    }

    public List<ResultsDTO> getResults() {
        return results;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "lastUpdated=" + lastUpdated +
                ", resultCnt=" + resultCnt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseDTO that = (ResponseDTO) o;
        return resultCnt == that.resultCnt &&
                Objects.equals(lastUpdated, that.lastUpdated) &&
                Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lastUpdated, resultCnt, results);
    }
}
