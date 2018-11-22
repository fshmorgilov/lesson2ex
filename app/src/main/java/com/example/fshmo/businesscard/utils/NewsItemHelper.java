package com.example.fshmo.businesscard.utils;

import androidx.annotation.NonNull;

import android.util.Log;

import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.data.model.NewsEntity;
import com.example.fshmo.businesscard.web.topstories.dto.MultimediaDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResponseDTO;
import com.example.fshmo.businesscard.web.topstories.dto.ResultsDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.fshmo.businesscard.data.NewsItem.PLACEHOLDER_IMG;

public abstract class NewsItemHelper {

    private static final String TAG = NewsItemHelper.class.getName();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(NewsItem.DATE_FORMAT, Locale.ENGLISH);

    public static NewsEntity convertDtoToDao(@NonNull ResultsDTO newsItemDto) {
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle(newsItemDto.getTitle());
        newsEntity.setCategory(newsItemDto.getSection());
        newsEntity.setFullText(newsItemDto.getShortDescription());
        newsEntity.setNewsItemUrl(newsItemDto.getUrl());
        newsEntity.setPreviewText(newsItemDto.getShortDescription());
        newsEntity.setPublishDate(String.valueOf(newsItemDto.getDatePublished()));
        List<MultimediaDTO> mediaList = newsItemDto.getMultimedia();
        if (mediaList.isEmpty()) {
            newsEntity.setImageUrl(PLACEHOLDER_IMG);
        } else if (mediaList.size() > 1) {
            String url = mediaList.get(0).getUrl();
            if (!"".equals(url) && url != null)
                newsEntity.setImageUrl(mediaList.get(0).getUrl());
            else
                newsEntity.setImageUrl(PLACEHOLDER_IMG);
            newsEntity.setImageLargeUrl(mediaList.get((mediaList.size()) - 1).getUrl());
            Log.i(TAG, "convertDtoToDao: image " + newsEntity.getImageUrl());
            Log.i(TAG, "convertDtoToDao: imageLarge " + newsEntity.getImageLargeUrl());
        } else {
            String url = mediaList.get(0).getUrl();
            if (!"".equals(url) && url != null)
                newsEntity.setImageUrl(mediaList.get(0).getUrl());
            else
                newsEntity.setImageUrl(PLACEHOLDER_IMG);
        }
        Log.i(TAG, "Converted: " + newsEntity.getTitle());
        return newsEntity;
    }

    public static NewsItem convertDaoToDomain(@NonNull NewsEntity entity) {
        return new NewsItem(entity);
    }

    public static List<NewsItem> convertDaoListoToDomain(@NonNull List<NewsEntity> newsEntities) {
        List<NewsItem> newsItems = new ArrayList<>();
        for (NewsEntity entity : newsEntities) {
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

    public static NewsEntity[] parseToDaoArray(@NonNull ResponseDTO dto) {
        NewsEntity[] newsEntitiesArray = new NewsEntity[dto.getResults().size()];
        List<NewsEntity> entities = parseToDaoList(dto);
        entities.toArray(newsEntitiesArray);
        Log.d(TAG, "parseToDaoArray: Array size: " + newsEntitiesArray.length);
        return newsEntitiesArray;
    }
}
