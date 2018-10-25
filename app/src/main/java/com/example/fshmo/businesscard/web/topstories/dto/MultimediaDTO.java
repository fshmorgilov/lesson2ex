package com.example.fshmo.businesscard.web.topstories.dto;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class MultimediaDTO {

    @SerializedName("url")
    private URL url;
    @SerializedName("caption")
    private String caption;
    @SerializedName("copyright")
    private String copyright;

    public String getUrl() {
        return url.toString();
    }

    public String getCaption() {
        return caption;
    }

    public String getCopyright() {
        return copyright;
    }
}
