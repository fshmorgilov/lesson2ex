package com.example.fshmo.businesscard.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.fshmo.businesscard.data.model.NewsEntity;
import com.example.fshmo.businesscard.web.topstories.dto.MultimediaDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsItem implements Serializable {

    public static final String DATE_FORMAL_LONG = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String DATE_FORMAT = "HH:mm, EE";

    private static final String LTAG = NewsItem.class.getName();

    private final String title;
    private final String imageUrl;
    private final Category category;
    private final String previewText;
    private final String fullText;
    private String imageUrlLarge;
    private Date publishDate;
    private String newsItemUrl;


    private final String PLACEHOLDER_IMG = "https://www.google.ru/search?q=news+place+holder+image&newwindow=1&tbm=isch&source=iu&ictx=1&fir=EfhmYKY75BM0sMhttps://www.google.ru/imgres?imgurl=http%3A%2F%2Fwww.asanet.org%2Fsites%2Fdefault%2Ffiles%2Fdefault_images%2Fplaceholder-news.jpg&imgrefurl=http%3A%2F%2Fwww.asanet.org%2Ffiles%2Fnews-placeholder&docid=jrPflO7vu5YFXM&tbnid=jOKqZCHNXXZbPM%3A&vet=10ahUKEwimoInPiKLeAhVnhosKHYYED_cQMwg4KAEwAQ..i&w=384&h=288&bih=716&biw=1371&q=news%20place%20holder%20image&ved=0ahUKEwimoInPiKLeAhVnhosKHYYED_cQMwg4KAEwAQ&iact=mrc&uact=8";

    public NewsItem(@NonNull String title,
                    @NonNull String imageUrl,
                    @Nullable Category category,
                    @Nullable Date publishDate,
                    @Nullable String previewText,
                    @Nullable String fullText) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.category = category;
        this.publishDate = publishDate;
        this.previewText = previewText;
        this.fullText = fullText;
    }

    public NewsItem(@NonNull ResultsDTO dto) {
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
        this.newsItemUrl = dto.getUrl();
    }

    public NewsItem(@NonNull NewsEntity newsEntity) {
        this.title = newsEntity.getTitle();
        this.imageUrl = newsEntity.getImageUrl();
        this.category = new Category(0, newsEntity.getCategory());
        this.previewText = newsEntity.getPreviewText();
        this.fullText = newsEntity.getPreviewText();
        try {
            this.publishDate = new SimpleDateFormat(DATE_FORMAL_LONG, Locale.ENGLISH)
                    .parse(newsEntity.getPublishDate());
            Log.i(LTAG, "Parsed: " + newsEntity.getPublishDate());
        } catch (ParseException e) {
            this.publishDate = new Date();
            Log.e(LTAG, e.getMessage());
        }
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

    public String getNewsItemUrl() {
        return newsItemUrl;
    }

    public String getImageUrlLarge() {
        return imageUrlLarge;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof NewsItem && getTitle().equals(((NewsItem) obj).getTitle()));
    }
}
