package com.example.fshmo.businesscard.web.topstories;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ResultsDTO {
    @SerializedName("section")
    private String section;
    @SerializedName("title")
    private String title;
    @SerializedName("abstract")
    private String shortDescription; //abstract
    @SerializedName("published_date")
    private Date datePublished;
    @SerializedName("multimedia")
    private MultimediaDTO multimedia;
}
