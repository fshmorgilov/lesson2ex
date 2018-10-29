package com.example.fshmo.businesscard.data;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.fshmo.businesscard.web.topstories.dto.MultimediaDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NewsItem implements Serializable {

    private static final String LTAG = NewsItem.class.getName();

    private final String title;
    private final String imageUrl;
    private String imageUrlLarge;
    private final Category category;
    private final Date publishDate;
    private final String previewText;
    private final String fullText;
    private final String PLACEHOLDER_IMG = "https://www.google.ru/search?q=news+place+holder+image&newwindow=1&tbm=isch&source=iu&ictx=1&fir=EfhmYKY75BM0sMhttps://www.google.ru/imgres?imgurl=http%3A%2F%2Fwww.asanet.org%2Fsites%2Fdefault%2Ffiles%2Fdefault_images%2Fplaceholder-news.jpg&imgrefurl=http%3A%2F%2Fwww.asanet.org%2Ffiles%2Fnews-placeholder&docid=jrPflO7vu5YFXM&tbnid=jOKqZCHNXXZbPM%3A&vet=10ahUKEwimoInPiKLeAhVnhosKHYYED_cQMwg4KAEwAQ..i&w=384&h=288&bih=716&biw=1371&q=news%20place%20holder%20image&ved=0ahUKEwimoInPiKLeAhVnhosKHYYED_cQMwg4KAEwAQ&iact=mrc&uact=8";

    public NewsItem(String title, String imageUrl, Category category, Date publishDate, String previewText, String fullText) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.category = category;
        this.publishDate = publishDate;
        this.previewText = previewText;
        this.fullText = fullText;
    }

    public NewsItem(ResultsDTO dto) {
        List<MultimediaDTO> mediaList = dto.getMultimedia();
        if (mediaList.isEmpty()) {
            this.imageUrl = PLACEHOLDER_IMG;
        } else if (mediaList.size() > 1) {
            //FIXME выпилить в отдельный метод
            String url = mediaList.get(0).getUrl();
            if (!"".equals(url) && url != null)
                this.imageUrl = mediaList.get(0).getUrl();
            else
                this.imageUrl = PLACEHOLDER_IMG;
            this.imageUrlLarge = mediaList.get((mediaList.size()) - 1).getUrl();
        } else {
            String url = mediaList.get(0).getUrl();
            if (!"".equals(url) && url != null)
                this.imageUrl = mediaList.get(0).getUrl();
            else
                this.imageUrl = PLACEHOLDER_IMG;
            Log.e(LTAG, imageUrl);
        }
        this.title = dto.getTitle();
        this.category = new Category(6, dto.getSection());
        this.publishDate = dto.getDatePublished();
        this.previewText = dto.getShortDescription();
        this.fullText = dto.getShortDescription();
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public String getPreviewText() {
        return previewText;
    }

    public String getFullText() {
        return fullText;
    }

    public String getImageUrlLarge() {
        return imageUrlLarge;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof NewsItem && getTitle().equals(((NewsItem) obj).getTitle()));
    }
}
