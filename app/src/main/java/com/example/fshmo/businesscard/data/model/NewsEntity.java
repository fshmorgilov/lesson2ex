package com.example.fshmo.businesscard.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fshmo.businesscard.data.NewsItem;

@Entity(tableName = "news",
        indices = @Index(value = "id", unique = true))
public class NewsEntity {
    @PrimaryKey
//    @NonNull
    @ColumnInfo(name = "id")
    private int id;

//    @NonNull
    @ColumnInfo(name = "title")
    private String title;

//    @NonNull
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

//    @NonNull
    @ColumnInfo(name = "category")
    private String category;

//    @Nullable
    @ColumnInfo(name = "publishDate")
    private String publishDate;

//    @NonNull
    @ColumnInfo(name = "previewText")
    private String previewText;

//    @Nullable
    @ColumnInfo(name = "fullText")
    private String fullText;

    public NewsEntity(@NonNull NewsItem newsItem){
        this.category = newsItem.getCategory().getName();
        this.imageUrl = newsItem.getImageUrl();
        this.title = newsItem.getTitle();
        this.publishDate = newsItem.getPublishDate().toString();
        this.newsItemUrl = newsItem.getNewsItemUrl();
        this.fullText = newsItem.getFullText();
        this.previewText = newsItem.getPreviewText();
    }


    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public int getId() {
        return id;
    }

    @NonNull
    public String getTitle() {

        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(@Nullable String publishDate) {
        this.publishDate = publishDate;
    }

    @NonNull
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    @NonNull
    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(@NonNull String previewText) {
        this.previewText = previewText;
    }

    @Nullable
    public String getFullText() {
        return fullText;
    }

    public void setFullText(@Nullable String fullText) {
        this.fullText = fullText;
    }

    @Nullable
    public String getNewsItemUrl() {
        return newsItemUrl;
    }

    public void setNewsItemUrl(@Nullable String newsItemUrl) {
        this.newsItemUrl = newsItemUrl;
    }

    @Nullable
    @ColumnInfo(name = "newsItemUrl")
    private String newsItemUrl;


    public NewsEntity(){}

}
