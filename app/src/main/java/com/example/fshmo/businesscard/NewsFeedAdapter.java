package com.example.fshmo.businesscard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedViewHolder> {
    private List<NewsItem> dataset;
    private OnItemClickListener onItemClickListener;
    private RequestManager glide;

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

    public void setDataset(List<NewsItem> dataset) {
        if (dataset == null){
            return;
        }
        dataset.clear();
        this.dataset.addAll(dataset);
    }

    public void addItem(@NonNull NewsItem item){
        this.dataset.add(item);
        notifyItemInserted(dataset.indexOf(item));
    }
}