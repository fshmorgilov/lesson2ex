package com.example.fshmo.businesscard;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

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

    public void bind(final NewsItem newsItem, RequestManager glide, NewsFeedAdapter.OnItemClickListener onItemClickListener) {
        categoryTextView.setText(newsItem.getCategory().getName());
        headerTextView.setText(newsItem.getTitle());
        textTextView.setText(newsItem.getPreviewText());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        dateTextView.setText(sdf.format(newsItem.getPublishDate()));
        glide.load(newsItem.getImageUrl()).into(photo);
        view.setOnClickListener(v -> onItemClickListener.onItemClick(newsItem));
    }
}

