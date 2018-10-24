package com.example.fshmo.businesscard.web.topstories;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

class MultimediaDTO {
    @SerializedName("url")
    private URL url;
    @SerializedName("caption")
    private String caption;
    @SerializedName("copyright")
    private String copyright;
}
