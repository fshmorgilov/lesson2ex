package com.example.fshmo.businesscard.utils;

import androidx.annotation.NonNull;
import android.util.Log;

import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.NewsEntity;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class NewsItemHelper {

    private static final String TAG = NewsItemHelper.class.getName();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(NewsItem.DATE_FORMAT, Locale.ENGLISH);

    public static NewsEntity convertDtoToDao(@NonNull ResultsDTO newsItemDto) {
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle(newsItemDto.getTitle());
        newsEntity.setCategory(newsItemDto.getSection());
        newsEntity.setFullText(newsItemDto.getShortDescription());
        newsEntity.setImageUrl(newsItemDto.getMultimedia().get(0).getUrl());
        newsEntity.setNewsItemUrl(newsItemDto.getUrl());
        newsEntity.setPreviewText(newsItemDto.getShortDescription());
        newsEntity.setPublishDate(String.valueOf(newsItemDto.getDatePublished()));
        Log.i(TAG, "Converted: " + newsEntity.getTitle());
        return newsEntity;
    }

    public static NewsItem convertDaoToDomain(@NonNull NewsEntity entity) {
        return new NewsItem(entity);
    }

    public static List<NewsItem> convertDaoListoToDomain(@NonNull List<NewsEntity> newsEntities) {
        List<NewsItem> newsItems = new ArrayList<>();
        for (NewsEntity entity : newsEntities ) {
            newsItems.add(new NewsItem(entity));
        }
        return newsItems;
    }

    public static List<NewsEntity> parseToDaoList(@NonNull ResponseDTO responseDTO) {
        Log.i(TAG, "Decomposing reply - Quantity: " + String.valueOf(responseDTO.getResultCnt()));
        List<NewsEntity> newsEntities = new ArrayList<>();
        for (ResultsDTO result : responseDTO.getResults()) {
            NewsEntity newsEntity = NewsItemHelper.convertDtoToDao(result);
            Log.i(TAG, "Parsing: " + result.getTitle());
            newsEntities.add(newsEntity);
        }
        Log.i(TAG, "parseToDaoList: newsEntities Qntty " + newsEntities.size());
        return newsEntities;
    }

    public static NewsEntity[] parseToDaoArray(@NonNull ResponseDTO dto){
       List<NewsEntity> newsEntities = parseToDaoList(dto) ;
        return (NewsEntity[]) newsEntities.toArray();
    }
}
