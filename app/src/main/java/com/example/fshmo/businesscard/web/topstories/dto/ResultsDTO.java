package com.example.fshmo.businesscard.web.topstories.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ResultsDTO {

    @SerializedName("section")
    private String section;
    @SerializedName("title")
    private String title;
    @SerializedName("abstract")
    private String shortDescription;
    @SerializedName("published_date")
    private Date datePublished;
    @SerializedName("multimedia")
    private List<MultimediaDTO> multimedia;

    public String getSection() {
        return section;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public List<MultimediaDTO> getMultimedia() {
        return multimedia;
    }
}
