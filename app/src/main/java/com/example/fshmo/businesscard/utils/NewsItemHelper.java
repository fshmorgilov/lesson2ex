package com.example.fshmo.businesscard.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.fshmo.businesscard.data.Category;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.NewsEntity;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;

public abstract class NewsItemHelper {

    private static final String LTAG = NewsItemHelper.class.getName();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(NewsItem.DATE_FORMAT, Locale.ENGLISH);

    public static NewsEntity convertDtoToDao(@NonNull ResultsDTO newsItemDto) {
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setCategory(newsItemDto.getSection());
        newsEntity.setFullText(newsItemDto.getShortDescription());
        newsEntity.setImageUrl(newsItemDto.getMultimedia().get(0).getUrl());
        newsEntity.setNewsItemUrl(newsItemDto.getUrl());
        newsEntity.setPreviewText(newsItemDto.getShortDescription());
        newsEntity.setPublishDate(String.valueOf(newsItemDto.getDatePublished()));
        Log.i(LTAG, "Converted: " + newsEntity.getTitle());
        return newsEntity;
    }

    public static NewsItem convertDaoToDomain(@NonNull NewsEntity entity) {
        return new NewsItem(entity);
    }

    public static Observable<NewsEntity> parseToDaoArray(@NonNull ResponseDTO responseDTO) {
        Log.i(LTAG, "Decomposing reply - Quantity: " + String.valueOf(responseDTO.getResultCnt()));
        List<NewsEntity> newsEntities = new ArrayList<>();
        for (ResultsDTO result : responseDTO.getResults()) {
            NewsEntity newsEntity = NewsItemHelper.convertDtoToDao(result);
            Log.i(LTAG, "Parsing: " + result.getTitle());
            newsEntities.add(newsEntity);
        }
        return Observable.fromIterable(newsEntities);
    }
}
