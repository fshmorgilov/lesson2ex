package com.example.fshmo.businesscard.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.example.fshmo.businesscard.data.NewsItem;
import com.example.fshmo.businesscard.R;

import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedViewHolder> {
    private List<NewsItem> dataset;
    private OnItemClickListener onItemClickListener;
    private RequestManager glide;
    private static final String LTAG = NewsFeedAdapter.class.getName();

    public interface OnItemClickListener {
        void onItemClick(@NonNull NewsItem item);
    }

    public NewsFeedAdapter(@NonNull List<NewsItem> newsItems,
                           @NonNull RequestManager glide, OnItemClickListener clickListener) {
        this.glide = glide;
        this.dataset = newsItems;
        this.onItemClickListener = clickListener;
    }

    @NonNull
    @Override
    public NewsFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsFeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsFeedViewHolder holder, int position) {
        holder.bind(dataset.get(position), glide, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addItem(@NonNull NewsItem item){
        this.dataset.add(item);
        notifyItemInserted(dataset.indexOf(item));
    }

    public void setDataset(@NonNull List<NewsItem> newsItems){
        int currSize = this.dataset.size();
        this.dataset.addAll(newsItems);
        notifyItemRangeInserted(currSize + 1, newsItems.size());
        Log.i(LTAG, "Dataset changed. Added: " + newsItems.size() + " items");

    }
}