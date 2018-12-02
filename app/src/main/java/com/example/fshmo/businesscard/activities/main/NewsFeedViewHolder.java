package com.example.fshmo.businesscard.activities.main;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

class NewsFeedViewHolder extends RecyclerView.ViewHolder {
    private TextView categoryTextView;
    private TextView headerTextView;
    private TextView textTextView;
    private TextView dateTextView;
    private ImageView photo;
    private View view;
    private final String DATE_FORMAT = "HH:mm, EEEE";

    public NewsFeedViewHolder(View v) {
        super(v);
        this.view = v;
        categoryTextView = v.findViewById(R.id.category);
        headerTextView = v.findViewById(R.id.header);
        textTextView = v.findViewById(R.id.text);
        dateTextView = v.findViewById(R.id.date);
        photo = v.findViewById(R.id.photo);
    }

    public void bind(@NonNull final NewsItem newsItem,
                     @NonNull RequestManager glide,
                     @NonNull NewsFeedAdapter.OnItemClickListener onItemClickListener) {
        if (newsItem.getCategory() != null)
            categoryTextView.setText(newsItem.getCategory().getName());
        else
            categoryTextView.setVisibility(View.GONE);
        if (newsItem.getTitle() != null && !"".equals(newsItem.getTitle()))
            headerTextView.setText(newsItem.getTitle());
        else
            headerTextView.setVisibility(View.GONE);

        if (newsItem.getPreviewText() != null && !"".equals(newsItem.getPreviewText()))
            textTextView.setText(newsItem.getPreviewText());
        else
            textTextView.setVisibility(View.GONE);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        if (newsItem.getPublishDate() != null)
            dateTextView.setText(sdf.format(newsItem.getPublishDate()));
        else
            dateTextView.setVisibility(View.GONE);
        String url = newsItem.getImageUrl();
        glide.load(url)
                .into(photo);
        view.setOnClickListener(v -> onItemClickListener.onItemClick(newsItem));
    }
}

